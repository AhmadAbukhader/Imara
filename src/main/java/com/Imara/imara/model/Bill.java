package com.Imara.imara.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {

    private UUID id;
    private UUID companyId;
    private UUID buildingId;
    private UUID apartmentId;
    private UUID serviceTypeId;
    private LocalDate billingPeriodStart;
    private BigDecimal amount;
    private String status;
    private LocalDate dueDate;
    private LocalDate issuedAt;
    private LocalDate paidAt;
    private Instant createdAt;
    private Instant updatedAt;
}
