package com.Imara.imara.repository;

import com.Imara.imara.model.ServiceType;

import java.util.List;
import java.util.UUID;

public interface IServiceTypeRepository {

    ServiceType save(ServiceType serviceType);

    ServiceType findById(UUID id);

    List<ServiceType> findAll();

    List<ServiceType> findAllByCompanyId(UUID companyId);

    int update(ServiceType serviceType);

    int deleteById(UUID id);
}
