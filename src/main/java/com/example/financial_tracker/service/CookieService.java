package com.example.financial_tracker.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

  private static final String AUTH_COOKIE_NAME = "token";

  public void setAuthCookie(HttpServletResponse response, String token) {
    Cookie cookie = new Cookie(AUTH_COOKIE_NAME, token);
    cookie.setHttpOnly(httpOnly);
    cookie.setSecure(secure);
    cookie.setPath(path);
    cookie.setMaxAge(maxAge);
    response.addCookie(cookie);

    log.debug("Auth cookie set with maxAge: {}, secure: {}, httpOnly: {}", maxAge, secure, httpOnly);
  }

  public void clearAuthCookie(HttpServletResponse response) {
    Cookie cookie = new Cookie(AUTH_COOKIE_NAME, null);
    cookie.setHttpOnly(httpOnly);
    cookie.setSecure(secure);
    cookie.setPath(path);
    cookie.setMaxAge(0);
    response.addCookie(cookie);

    log.debug("Auth cookie cleared");
  }
}
