package com.Imara.imara.security;

import java.util.UUID;

/**
 * Claims extracted from a valid JWT token.
 */
public record JwtClaims(
        UUID userId,
        UUID companyId,
        String email,
        String role
) {}
