package com.Imara.imara.repository;

import com.Imara.imara.model.BuildingServiceType;

import java.util.List;
import java.util.UUID;

public interface IBuildingServiceTypeRepository {

    BuildingServiceType save(BuildingServiceType buildingServiceType);

    BuildingServiceType findById(UUID id);

    List<BuildingServiceType> findAll();

    List<BuildingServiceType> findAllByCompanyId(UUID companyId);

    List<BuildingServiceType> findAllByBuildingId(UUID buildingId);

    int update(BuildingServiceType buildingServiceType);

    int deleteById(UUID id);
}
