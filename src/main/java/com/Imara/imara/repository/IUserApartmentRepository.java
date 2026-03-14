package com.Imara.imara.repository;

import com.Imara.imara.model.UserApartment;

import java.util.List;
import java.util.UUID;

public interface IUserApartmentRepository {

    UserApartment save(UserApartment userApartment);

    UserApartment findById(UUID id);

    List<UserApartment> findAll();

    List<UserApartment> findAllByCompanyId(UUID companyId);

    List<UserApartment> findAllByUserId(UUID userId);

    List<UserApartment> findAllByApartmentId(UUID apartmentId);

    int update(UserApartment userApartment);

    int deleteById(UUID id);
}
