package com.Imara.imara.service;

import com.Imara.imara.controller.dto.JoinCompanyRequest;
import com.Imara.imara.controller.dto.RegisterRequest;
import com.Imara.imara.dto.LoginResponse;
import com.Imara.imara.dto.UserInfoResponse;
import com.Imara.imara.security.UserPrincipal;

public interface IAuthService {

    /**
     * Authenticate user and produce login response with JWT.
     */
    LoginResponse login(String email, String password);

    /**
     * Build user info response from authenticated principal.
     * @throws com.Imara.imara.exception.ApplicationException with ErrorCode.UNAUTHORIZED if principal is null
     */
    UserInfoResponse getCurrentUserInfo(UserPrincipal principal);

    /**
     * Register new company with first user (COMPANY_OWNER). Creates company and user atomically.
     * @throws com.Imara.imara.exception.ApplicationException with ErrorCode.DUPLICATE_EMAIL if user or company email already exists
     */
    LoginResponse register(RegisterRequest request);

    /**
     * Join existing company as apartment owner (MAINTAINER). Apartment info stays null until COMPANY_OWNER assigns it.
     * @throws com.Imara.imara.exception.ApplicationException with ErrorCode.DATA_NOT_FOUND if company not found
     * @throws com.Imara.imara.exception.ApplicationException with ErrorCode.DUPLICATE_EMAIL if user email already exists
     */
    LoginResponse joinCompany(JoinCompanyRequest request);
}
