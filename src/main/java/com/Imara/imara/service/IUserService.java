package com.Imara.imara.service;

import com.Imara.imara.security.UserPrincipal;

import java.util.UUID;

public interface IUserService {

    /**
     * Load user by ID for JWT authentication context.
     * Returns null if user not found or deleted (for JWT filter to skip auth).
     */
    UserPrincipal loadUserById(UUID userId);

    /**
     * Authenticate user with email and password. Returns UserPrincipal if valid.
     */
    UserPrincipal authenticate(String email, String rawPassword);

    /**
     * Encode password for storage.
     */
    String encodePassword(String rawPassword);
}
