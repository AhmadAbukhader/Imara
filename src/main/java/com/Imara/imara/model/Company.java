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
public class Company {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private Boolean isActive;
    private Instant createdAt;
    private Instant updatedAt;
}
