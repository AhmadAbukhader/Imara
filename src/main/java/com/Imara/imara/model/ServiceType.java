package com.Imara.imara.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceType {

    private UUID id;
    private UUID companyId;
    private String name;
    private String description;
    private Boolean isActive;
    private Instant deletedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
