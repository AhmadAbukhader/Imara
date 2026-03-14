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
public class Building {

    private UUID id;
    private UUID companyId;
    private String name;
    private String address;
    private String city;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
