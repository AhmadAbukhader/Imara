package com.Imara.imara.repository;

import com.Imara.imara.model.Apartment;

import java.util.List;
import java.util.UUID;

public interface IApartmentRepository {

    Apartment save(Apartment apartment);

    Apartment findById(UUID id);

    List<Apartment> findAll();

    List<Apartment> findAllByCompanyId(UUID companyId);

    List<Apartment> findAllByBuildingId(UUID buildingId);

    int update(Apartment apartment);

    int deleteById(UUID id);
}
