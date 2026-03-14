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
public class ApartmentServiceSubscription {

    private UUID id;
    private UUID companyId;
    private UUID apartmentId;
    private UUID buildingServiceTypeId;
    private Instant createdAt;
}
