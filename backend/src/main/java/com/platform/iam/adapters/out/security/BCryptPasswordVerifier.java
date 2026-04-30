package com.platform.iam.adapters.out.security;

import com.platform.iam.application.PasswordVerifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class BCryptPasswordVerifier implements PasswordVerifier {

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @Override
  public boolean matches(String rawPassword, String encodedHash) {
    if (rawPassword == null) {
      return false;
    }
    return encoder.matches(rawPassword, encodedHash);
  }
}
