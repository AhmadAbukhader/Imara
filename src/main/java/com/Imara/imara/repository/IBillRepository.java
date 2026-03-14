package com.Imara.imara.repository;

import com.Imara.imara.model.Bill;

import java.util.List;
import java.util.UUID;

public interface IBillRepository {

    Bill save(Bill bill);

    Bill findById(UUID id);

    List<Bill> findAll();

    List<Bill> findAllByCompanyId(UUID companyId);

    List<Bill> findAllByApartmentId(UUID apartmentId);

    int update(Bill bill);

    int deleteById(UUID id);
}
