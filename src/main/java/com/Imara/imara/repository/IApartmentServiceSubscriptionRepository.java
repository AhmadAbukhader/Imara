package com.Imara.imara.repository;

import com.Imara.imara.model.ApartmentServiceSubscription;

import java.util.List;
import java.util.UUID;

public interface IApartmentServiceSubscriptionRepository {

    ApartmentServiceSubscription save(ApartmentServiceSubscription subscription);

    ApartmentServiceSubscription findById(UUID id);

    List<ApartmentServiceSubscription> findAll();

    List<ApartmentServiceSubscription> findAllByCompanyId(UUID companyId);

    List<ApartmentServiceSubscription> findAllByApartmentId(UUID apartmentId);

    int update(ApartmentServiceSubscription subscription);

    int deleteById(UUID id);
}
