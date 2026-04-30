package com.platform.iam.adapters.in.web;

import com.platform.iam.application.CurrentUserProvider;
import com.platform.iam.application.LoginUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final LoginUseCase loginUseCase;
  private final CurrentUserProvider currentUserProvider;

  public AuthController(LoginUseCase loginUseCase, CurrentUserProvider currentUserProvider) {
    this.loginUseCase = loginUseCase;
    this.currentUserProvider = currentUserProvider;
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    LoginUseCase.LoginResult result = loginUseCase.execute(request.email(), request.password());
    return new LoginResponse(result.accessToken(), result.tokenType(), result.expiresIn());
  }

  @GetMapping("/me")
  public MeResponse me() {
    return currentUserProvider
        .currentUser()
        .map(u -> new MeResponse(u.userId(), u.tenantId(), u.email(), u.roles()))
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
  }
}
