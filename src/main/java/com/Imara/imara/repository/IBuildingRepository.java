package com.Imara.imara.repository;

import com.Imara.imara.model.Building;

import java.util.List;
import java.util.UUID;

public interface IBuildingRepository {

    Building save(Building building);

    Building findById(UUID id);

    List<Building> findAll();

    List<Building> findAllByCompanyId(UUID companyId);

    int update(Building building);

    int deleteById(UUID id);
}
