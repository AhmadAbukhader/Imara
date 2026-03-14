package com.Imara.imara.service.impl;

import com.Imara.imara.controller.dto.JoinCompanyRequest;
import com.Imara.imara.controller.dto.RegisterRequest;
import com.Imara.imara.dto.LoginResponse;
import com.Imara.imara.dto.UserInfoResponse;
import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.model.Company;
import com.Imara.imara.model.User;
import com.Imara.imara.repository.ICompanyRepository;
import com.Imara.imara.repository.IUserRepository;
import com.Imara.imara.security.JwtUtils;
import com.Imara.imara.security.UserPrincipal;
import com.Imara.imara.service.IAuthService;
import com.Imara.imara.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService implements IAuthService {

    private static final String ROLE_COMPANY_OWNER = "COMPANY_OWNER";
    private static final String ROLE_MAINTAINER = "MAINTAINER";

    private final IUserService userService;
    private final ICompanyRepository companyRepository;
    private final IUserRepository userRepository;
    private final JwtUtils jwtUtils;

    public AuthService(IUserService userService, ICompanyRepository companyRepository, IUserRepository userRepository, JwtUtils jwtUtils) {
        this.userService = userService;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginResponse login(String email, String password) {
        UserPrincipal principal = userService.authenticate(email, password);

        String token = jwtUtils.generateToken(
                principal.getId(),
                principal.getCompanyId(),
                principal.getUsername(),
                principal.getRole()
        );

        return LoginResponse.of(
                token,
                principal.getId(),
                principal.getCompanyId(),
                principal.getUsername(),
                principal.getFullName(),
                principal.getRole()
        );
    }

    @Override
    public UserInfoResponse getCurrentUserInfo(UserPrincipal principal) {
        if (principal == null) {
            throw new ApplicationException(ErrorCode.UNAUTHORIZED);
        }
        return new UserInfoResponse(
                principal.getId(),
                principal.getCompanyId(),
                principal.getUsername(),
                principal.getFullName(),
                principal.getRole()
        );
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        ensureUserEmailNotTaken(request.email());
        ensureCompanyEmailNotTaken(request.companyEmail());

        Company company = Company.builder()
                .name(request.companyName())
                .email(request.companyEmail())
                .phone(request.companyPhone())
                .isActive(true)
                .build();
        company = companyRepository.save(company);

        User user = buildUser(
                company.getId(),
                request.fullName(),
                request.email(),
                request.password(),
                ROLE_COMPANY_OWNER
        );
        user = userRepository.save(user);

        return buildLoginResponse(user);
    }

    @Override
    @Transactional
    public LoginResponse joinCompany(JoinCompanyRequest request) {
        companyRepository.findById(request.companyId()); // throws DataNotFoundException if not found
        ensureUserEmailNotTaken(request.email());

        User user = buildUser(
                request.companyId(),
                request.fullName(),
                request.email(),
                request.password(),
                ROLE_MAINTAINER
        );
        user = userRepository.save(user);

        return buildLoginResponse(user);
    }

    private void ensureUserEmailNotTaken(String email) {
        try {
            userRepository.findByEmail(email);
            throw new ApplicationException(ErrorCode.DUPLICATE_EMAIL, "User with this email already exists");
        } catch (ApplicationException e) {
            if (e.getErrorCode() != ErrorCode.DATA_NOT_FOUND) throw e;
            // Email is available
        }
    }

    private void ensureCompanyEmailNotTaken(String email) {
        try {
            companyRepository.findByEmail(email);
            throw new ApplicationException(ErrorCode.DUPLICATE_COMPANY_EMAIL);
        } catch (ApplicationException e) {
            if (e.getErrorCode() != ErrorCode.DATA_NOT_FOUND) throw e;
            // Email is available
        }
    }

    private User buildUser(UUID companyId, String fullName, String email, String password, String role) {
        Instant now = Instant.now();
        return User.builder()
                .companyId(companyId)
                .fullName(fullName)
                .email(email)
                .passwordHash(userService.encodePassword(password))
                .role(role)
                .isActive(true)
                .deletedAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private LoginResponse buildLoginResponse(User user) {
        UserPrincipal principal = new UserPrincipal(user);
        String token = jwtUtils.generateToken(
                principal.getId(),
                principal.getCompanyId(),
                principal.getUsername(),
                principal.getRole()
        );
        return LoginResponse.of(
                token,
                principal.getId(),
                principal.getCompanyId(),
                principal.getUsername(),
                principal.getFullName(),
                principal.getRole()
        );
    }
}
