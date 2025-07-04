package com.example.financial_tracker.security.jwt;

import com.example.financial_tracker.entity.User;
import com.example.financial_tracker.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserService userService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain) throws ServletException, IOException {

    final String authHeader = request.getHeader("Authorization");
    final String clientIp = getClientIpAddress(request);

    log.info("=== JWT Filter triggered for: {} {} from IP: {}",
      request.getMethod(), request.getRequestURI(), clientIp);
    log.debug("Authorization header: {}", authHeader != null ? "Bearer [PRESENT]" : "null");

    // If no Authorization header present, continue filter chain
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.debug("No valid Authorization header found, continuing filter chain");
      filterChain.doFilter(request, response);
      return;
    }

    final String jwt = authHeader.substring(7);
    String username = null;

    try {
      // Try to extract username from token
      username = jwtService.extractUsername(jwt);
      log.debug("Extracted username from JWT: {}", username);
    } catch (ExpiredJwtException ex) {
      log.warn("JWT token expired for IP: {} - {}", clientIp, ex.getMessage());
      handleJwtException(response, request, "JWT token has expired", "TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED);
      return;
    } catch (JwtException ex) {
      log.warn("Invalid JWT token from IP: {} - {}", clientIp, ex.getMessage());
      handleJwtException(response, request, "Invalid JWT token", "INVALID_TOKEN", HttpStatus.UNAUTHORIZED);
      return;
    } catch (Exception ex) {
      log.error("Error processing JWT token from IP: {} - {}", clientIp, ex.getMessage());
      handleJwtException(response, request, "Error processing JWT token", "TOKEN_ERROR", HttpStatus.UNAUTHORIZED);
      return;
    }

    // If username extracted and user is not authenticated
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        // Get the actual User entity directly
        User user = userService.getUserByEmail(username);
        log.debug("Found user: {} (ID: {})", user.getEmail(), user.getId());

        if (jwtService.isTokenValid(jwt, user)) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            user, // Use User entity directly as principal
            null,
            user.getAuthorities()
          );
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);

          log.info("Successfully authenticated user: {} from IP: {}", username, clientIp);
        } else {
          log.warn("Invalid JWT token for user: {} from IP: {}", username, clientIp);
          handleJwtException(response, request, "Invalid JWT token", "INVALID_TOKEN", HttpStatus.UNAUTHORIZED);
          return;
        }
      } catch (Exception ex) {
        log.error("Error loading user details for: {} from IP: {} - {}", username, clientIp, ex.getMessage());
        handleJwtException(response, request, "Error authenticating user", "AUTHENTICATION_ERROR", HttpStatus.UNAUTHORIZED);
        return;
      }
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Handle JWT-related exceptions by writing error response directly to HTTP response
   */
  private void handleJwtException(HttpServletResponse response,
                                  HttpServletRequest request,
                                  String message,
                                  String errorCode,
                                  HttpStatus status) throws IOException {

    log.warn("JWT Security Event - {} - {} {} - IP: {} - Error: {}",
      errorCode, request.getMethod(), request.getRequestURI(),
      getClientIpAddress(request), message);

    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("message", message);
    errorResponse.put("error", errorCode);
    errorResponse.put("status", status.value());
    errorResponse.put("timestamp", LocalDateTime.now().toString());
    errorResponse.put("path", request.getRequestURI());

    String jsonResponse = objectMapper.writeValueAsString(errorResponse);
    response.getWriter().write(jsonResponse);
    response.getWriter().flush();
  }

  private String getClientIpAddress(HttpServletRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isEmpty()) {
      return xRealIp;
    }

    return request.getRemoteAddr();
  }
}
