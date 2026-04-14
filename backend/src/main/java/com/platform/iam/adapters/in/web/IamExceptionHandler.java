package com.platform.iam.adapters.in.web;

import com.platform.iam.application.InvalidAuthenticatedTokenException;
import com.platform.iam.domain.InvalidCredentialsException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class IamExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<IamApiErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(IamApiErrorResponse.invalidCredentials());
    }

    @ExceptionHandler(InvalidAuthenticatedTokenException.class)
    public ResponseEntity<IamApiErrorResponse> handleInvalidAuthenticatedToken(
            InvalidAuthenticatedTokenException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(IamApiErrorResponse.invalidAuthenticatedToken());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<IamApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var violations =
            ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new IamApiErrorResponse.FieldViolation(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
        return ResponseEntity.badRequest()
            .body(IamApiErrorResponse.validation("Request validation failed", violations));
    }
}
