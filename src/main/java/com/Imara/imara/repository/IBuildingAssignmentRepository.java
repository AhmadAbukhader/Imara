package com.Imara.imara.repository;

import com.Imara.imara.model.BuildingAssignment;

import java.util.List;
import java.util.UUID;

public interface IBuildingAssignmentRepository {

    BuildingAssignment save(BuildingAssignment buildingAssignment);

    BuildingAssignment findById(UUID id);

    List<BuildingAssignment> findAll();

    List<BuildingAssignment> findAllByCompanyId(UUID companyId);

    List<BuildingAssignment> findAllByBuildingId(UUID buildingId);

    List<BuildingAssignment> findAllByUserId(UUID userId);

    int update(BuildingAssignment buildingAssignment);

    int deleteById(UUID id);
}
