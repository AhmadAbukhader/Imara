package com.Imara.imara.dto;

import com.Imara.imara.model.Company;

import java.time.Instant;
import java.util.UUID;

public record CompanyResponse(
        UUID id,
        String name,
        String email,
        String phone,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
    public static CompanyResponse from(Company company) {
        return new CompanyResponse(
                company.getId(),
                company.getName(),
                company.getEmail(),
                company.getPhone(),
                company.getIsActive(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}
