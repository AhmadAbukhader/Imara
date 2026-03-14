package com.Imara.imara.dto;

import java.util.UUID;

public record UserInfoResponse(
        UUID userId,
        UUID companyId,
        String email,
        String fullName,
        String role
) {}
