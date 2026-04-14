package com.platform.iam.adapters.in.web;

import java.util.List;
import java.util.UUID;

/** Payload de autoconhecimento (GET /api/auth/me). */
public record MeResponse(UUID userId, UUID tenantId, String email, List<String> roles) {}
