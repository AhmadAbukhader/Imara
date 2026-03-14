package com.Imara.imara.service;

import com.Imara.imara.controller.dto.UpdateCompanyRequest;
import com.Imara.imara.model.Company;
import com.Imara.imara.security.UserPrincipal;

import java.util.List;
import java.util.UUID;

public interface ICompanyService {

    /**
     * List companies the user can access. For standard users, returns their company only.
     */
    List<Company> listForUser(UserPrincipal principal);

    /**
     * Get company by ID. User must belong to the company.
     */
    Company getById(UUID companyId, UserPrincipal principal);

    /**
     * Update company. User must belong to the company.
     */
    Company update(UUID companyId, UpdateCompanyRequest request, UserPrincipal principal);
}
