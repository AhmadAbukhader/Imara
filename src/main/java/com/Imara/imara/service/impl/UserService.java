package com.Imara.imara.service.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.model.User;
import com.Imara.imara.repository.IUserRepository;
import com.Imara.imara.security.UserPrincipal;
import com.Imara.imara.service.IUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserPrincipal loadUserById(UUID userId) {
        try {
            User user = userRepository.findById(userId);
            if (user.getDeletedAt() != null) {
                return null;
            }
            return new UserPrincipal(user);
        } catch (ApplicationException e) {
            if (e.getErrorCode() == ErrorCode.DATA_NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    @Override
    public UserPrincipal authenticate(String email, String rawPassword) {
        User user;
        try {
            user = userRepository.findByEmail(email);
        } catch (ApplicationException e) {
            if (e.getErrorCode() == ErrorCode.DATA_NOT_FOUND) {
                throw new ApplicationException(ErrorCode.INVALID_CREDENTIALS);
            }
            throw e;
        }

        if (user.getDeletedAt() != null || !Boolean.TRUE.equals(user.getIsActive())) {
            throw new ApplicationException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new ApplicationException(ErrorCode.INVALID_CREDENTIALS);
        }

        return new UserPrincipal(user);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
