package com.Imara.imara.repository;

import com.Imara.imara.model.Company;

import java.util.List;
import java.util.UUID;

public interface ICompanyRepository {

    Company save(Company company);

    Company findById(UUID id);

    Company findByEmail(String email);

    List<Company> findAll();

    int update(Company company);

    int deleteById(UUID id);
}
