package com.Imara.imara.controller;

import com.Imara.imara.controller.dto.UpdateCompanyRequest;
import com.Imara.imara.dto.CompanyResponse;
import com.Imara.imara.security.UserPrincipal;
import com.Imara.imara.service.ICompanyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final ICompanyService companyService;

    public CompanyController(ICompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(companyService.listForUser(principal).stream()
                .map(CompanyResponse::from)
                .toList());
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> getById(
            @PathVariable UUID companyId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(CompanyResponse.from(companyService.getById(companyId, principal)));
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponse> update(
            @PathVariable UUID companyId,
            @Valid @RequestBody UpdateCompanyRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(CompanyResponse.from(companyService.update(companyId, request, principal)));
    }
}
