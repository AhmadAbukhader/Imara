package com.Imara.imara.dto;

import java.util.UUID;

public record LoginResponse(
        String token,
        String tokenType,
        UUID userId,
        UUID companyId,
        String email,
        String fullName,
        String role
) {
    public static LoginResponse of(String token, UUID userId, UUID companyId, String email, String fullName, String role) {
        return new LoginResponse(token, "Bearer", userId, companyId, email, fullName, role);
    }
}
