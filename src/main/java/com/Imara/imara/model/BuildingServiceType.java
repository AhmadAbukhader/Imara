package com.Imara.imara.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingServiceType {

    private UUID id;
    private UUID companyId;
    private UUID buildingId;
    private UUID serviceTypeId;
    private BigDecimal cost;
    private Boolean isOptional;
    private String billingPeriod;
    private Instant deletedAt;
    private Instant createdAt;
    private Instant updatedAt;
}
