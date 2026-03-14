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
public class BuildingAssignment {

    private UUID id;
    private UUID companyId;
    private UUID buildingId;
    private UUID userId;
    private Instant createdAt;
}
