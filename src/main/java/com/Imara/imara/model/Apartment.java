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
public class Apartment {

    private UUID id;
    private UUID companyId;
    private UUID buildingId;
    private String number;
    private Integer floor;
    private BigDecimal area;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
