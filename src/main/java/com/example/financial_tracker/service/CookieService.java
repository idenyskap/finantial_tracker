package com.example.financial_tracker.service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CookieService {

  @Value("${app.cookie.max-age:86400}")
  private int maxAge;

  @Value("${app.cookie.secure:false}")
  private boolean secure;

  @Value("${app.cookie.path:/}")
  private String path;

  @Value("${app.cookie.http-only:true}")
  private boolean httpOnly;

  @Value("${app.cookie.same-site:Lax}")
  private String sameSite;

  private static final String AUTH_COOKIE_NAME = "token";

  public void setAuthCookie(HttpServletResponse response, String token) {
    ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, token)
        .httpOnly(httpOnly)
        .secure(secure)
        .path(path)
        .maxAge(maxAge)
        .sameSite(sameSite)
        .build();
    response.addHeader("Set-Cookie", cookie.toString());

    log.debug("Auth cookie set with maxAge: {}, secure: {}, httpOnly: {}, sameSite: {}", maxAge, secure, httpOnly, sameSite);
  }

  public void clearAuthCookie(HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from(AUTH_COOKIE_NAME, "")
        .httpOnly(httpOnly)
        .secure(secure)
        .path(path)
        .maxAge(0)
        .sameSite(sameSite)
        .build();
    response.addHeader("Set-Cookie", cookie.toString());

    log.debug("Auth cookie cleared");
  }
}
