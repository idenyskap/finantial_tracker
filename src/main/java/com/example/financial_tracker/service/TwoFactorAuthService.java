package com.example.financial_tracker.service;

import com.example.financial_tracker.dto.TwoFactorAuthDTO;
import com.example.financial_tracker.dto.TwoFactorSetupDTO;
import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.exception.BusinessLogicException;
import com.example.financial_tracker.repository.UserRepository;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TwoFactorAuthService {

  private final UserRepository userRepository;
  private final SecretGenerator secretGenerator = new DefaultSecretGenerator(32);
  private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
  private final CodeVerifier codeVerifier = new DefaultCodeVerifier(
    new DefaultCodeGenerator(),
    new SystemTimeProvider()
  );
  private final RecoveryCodeGenerator recoveryCodeGenerator = new RecoveryCodeGenerator();

  @Value("${app.name:Financial Tracker}")
  private String appName;

  public TwoFactorSetupDTO setupTwoFactorAuth(User user) {
    log.info("Setting up 2FA for user: {}", user.getEmail());

    // Generate secret
    String secret = secretGenerator.generate();

    // Generate QR code
    String qrCodeUrl = generateQRCodeDataURL(user.getEmail(), secret);

    // Generate recovery codes
    Set<String> recoveryCodes = generateRecoveryCodes();

    // Don't save to user yet - wait for verification
    return TwoFactorSetupDTO.builder()
      .secret(secret)
      .qrCode(qrCodeUrl)
      .recoveryCodes(recoveryCodes)
      .build();
  }

  public void enableTwoFactorAuth(User user, String secret, String verificationCode, Set<String> recoveryCodes) {
    log.info("Enabling 2FA for user: {}", user.getEmail());

    // Verify the code first
    if (!verifyCode(secret, verificationCode)) {
      throw new BusinessLogicException("Invalid verification code");
    }

    // Update user
    user.setTwoFactorEnabled(true);
    user.setTwoFactorSecret(secret);
    user.setRecoveryCodes(String.join(",", recoveryCodes));

    userRepository.save(user);
    log.info("2FA enabled successfully for user: {}", user.getEmail());
  }

  public void disableTwoFactorAuth(User user, String password) {
    log.info("Disabling 2FA for user: {}", user.getEmail());

    user.setTwoFactorEnabled(false);
    user.setTwoFactorSecret(null);
    user.setRecoveryCodes(null);

    userRepository.save(user);
    log.info("2FA disabled successfully for user: {}", user.getEmail());
  }

  public boolean verifyTwoFactorCode(User user, String code) {
    if (!user.isTwoFactorEnabled()) {
      return true; // 2FA not enabled
    }

    // Check if it's a recovery code
    if (isRecoveryCode(user, code)) {
      consumeRecoveryCode(user, code);
      return true;
    }

    // Otherwise verify TOTP code
    return verifyCode(user.getTwoFactorSecret(), code);
  }

  private boolean verifyCode(String secret, String code) {
    return codeVerifier.isValidCode(secret, code);
  }

  private String generateQRCodeDataURL(String email, String secret) {
    try {
      QrData data = new QrData.Builder()
        .label(email)
        .secret(secret)
        .issuer(appName)
        .algorithm(HashingAlgorithm.SHA1)
        .digits(6)
        .period(30)
        .build();

      byte[] imageData = qrGenerator.generate(data);
      String base64 = Base64.getEncoder().encodeToString(imageData);
      return "data:image/png;base64," + base64;

    } catch (QrGenerationException e) {
      log.error("Failed to generate QR code", e);
      throw new BusinessLogicException("Failed to generate QR code");
    }
  }

  private Set<String> generateRecoveryCodes() {
    // Generate 8 recovery codes
    String[] codes = recoveryCodeGenerator.generateCodes(8);
    return Arrays.stream(codes)
      .map(code -> {
        // Format as XXXX-XXXX if code is 8 characters
        if (code.length() >= 8) {
          return code.substring(0, 4) + "-" + code.substring(4, 8);
        } else {
          return code;
        }
      })
      .collect(Collectors.toSet());
  }

  private boolean isRecoveryCode(User user, String code) {
    if (user.getRecoveryCodes() == null || user.getRecoveryCodes().isEmpty()) {
      return false;
    }

    String[] codesArray = user.getRecoveryCodes().split(",");
    Set<String> codes = new HashSet<>(Arrays.asList(codesArray));
    return codes.contains(code);
  }

  private void consumeRecoveryCode(User user, String code) {
    if (user.getRecoveryCodes() == null || user.getRecoveryCodes().isEmpty()) {
      return;
    }

    String[] codesArray = user.getRecoveryCodes().split(",");
    Set<String> codes = Arrays.stream(codesArray)
      .filter(c -> !c.equals(code))
      .collect(Collectors.toSet());

    user.setRecoveryCodes(String.join(",", codes));
    userRepository.save(user);

    log.info("Recovery code consumed for user: {}", user.getEmail());
  }

  public Set<String> regenerateRecoveryCodes(User user, String password) {
    log.info("Regenerating recovery codes for user: {}", user.getEmail());

    Set<String> newCodes = generateRecoveryCodes();
    user.setRecoveryCodes(String.join(",", newCodes));
    userRepository.save(user);

    return newCodes;
  }

  public TwoFactorAuthDTO getTwoFactorStatus(User user) {
    int codesRemaining = 0;
    if (user.getRecoveryCodes() != null && !user.getRecoveryCodes().isEmpty()) {
      codesRemaining = user.getRecoveryCodes().split(",").length;
    }

    return TwoFactorAuthDTO.builder()
      .enabled(user.isTwoFactorEnabled())
      .recoveryCodesRemaining(codesRemaining)
      .build();
  }
}
