package com.platform.iam.adapters.in.web;

public record LoginResponse(String accessToken, String tokenType, long expiresIn) {}
