package com.Imara.imara.service.impl;

import com.Imara.imara.controller.dto.UpdateCompanyRequest;
import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.model.Company;
import com.Imara.imara.repository.ICompanyRepository;
import com.Imara.imara.security.UserPrincipal;
import com.Imara.imara.service.ICompanyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyService implements ICompanyService {

    private final ICompanyRepository companyRepository;

    public CompanyService(ICompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public List<Company> listForUser(UserPrincipal principal) {
        ensureAuthenticated(principal);
        Company company = companyRepository.findById(principal.getCompanyId());
        return List.of(company);
    }

    @Override
    public Company getById(UUID companyId, UserPrincipal principal) {
        ensureAuthenticated(principal);
        ensureAccessToCompany(companyId, principal);
        return companyRepository.findById(companyId);
    }

    @Override
    public Company update(UUID companyId, UpdateCompanyRequest request, UserPrincipal principal) {
        ensureAuthenticated(principal);
        ensureAccessToCompany(companyId, principal);

        Company existing = companyRepository.findById(companyId);

        Company updated = Company.builder()
                .id(existing.getId())
                .name(request.name() != null ? request.name() : existing.getName())
                .email(request.email() != null ? request.email() : existing.getEmail())
                .phone(request.phone() != null ? request.phone() : existing.getPhone())
                .isActive(request.isActive() != null ? request.isActive() : existing.getIsActive())
                .createdAt(existing.getCreatedAt())
                .updatedAt(existing.getUpdatedAt())
                .build();

        companyRepository.update(updated);
        return companyRepository.findById(companyId);
    }

    private void ensureAuthenticated(UserPrincipal principal) {
        if (principal == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }
    }

    private void ensureAccessToCompany(UUID companyId, UserPrincipal principal) {
        if (!principal.getCompanyId().equals(companyId)) {
            throw new ApplicationException(ErrorCode.FORBIDDEN, "Access denied to this company");
        }
    }
}
