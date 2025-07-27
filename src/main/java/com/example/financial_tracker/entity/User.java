package com.example.financial_tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_user")
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String email;

  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(role);
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private NotificationSettings notificationSettings;

  public NotificationSettings getNotificationSettings() {
    return notificationSettings;
  }

  @Column(name = "email_verified")
  private boolean emailVerified = false;

  @Column(name = "verification_token")
  private String verificationToken;

  @Column(name = "verification_token_expires_at")
  private LocalDateTime verificationTokenExpiresAt;

  @Column(name = "reset_password_token")
  private String resetPasswordToken;

  @Column(name = "reset_password_token_expires_at")
  private LocalDateTime resetPasswordTokenExpiresAt;

  @Column(name = "new_email")
  private String newEmail;

  @Column(name = "new_email_token")
  private String newEmailToken;

  @Column(name = "new_email_token_expires_at")
  private LocalDateTime newEmailTokenExpiresAt;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = LocalDateTime.now();
    }
  }
}
