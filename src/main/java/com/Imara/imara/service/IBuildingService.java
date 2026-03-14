package com.Imara.imara.service;

import com.Imara.imara.controller.dto.CreateBuildingRequest;
import com.Imara.imara.controller.dto.UpdateBuildingRequest;
import com.Imara.imara.model.Building;
import com.Imara.imara.security.UserPrincipal;

import java.util.List;
import java.util.UUID;

public interface IBuildingService {

    /**
     * List buildings in company. User must belong to the company.
     */
    List<Building> listByCompany(UUID companyId, UserPrincipal principal);

    /**
     * Get building by ID. User must belong to the company; building must belong to the company.
     */
    Building getById(UUID companyId, UUID buildingId, UserPrincipal principal);

    /**
     * Create building. User must belong to the company.
     */
    Building create(UUID companyId, CreateBuildingRequest request, UserPrincipal principal);

    /**
     * Update building. User must belong to the company; building must belong to the company.
     */
    Building update(UUID companyId, UUID buildingId, UpdateBuildingRequest request, UserPrincipal principal);

    /**
     * Delete building. User must belong to the company; building must belong to the company.
     */
    void deleteById(UUID companyId, UUID buildingId, UserPrincipal principal);
}
