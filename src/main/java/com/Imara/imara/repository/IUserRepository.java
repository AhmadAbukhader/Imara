package com.Imara.imara.repository;

import com.Imara.imara.model.User;

import java.util.List;
import java.util.UUID;

public interface IUserRepository {

    User save(User user);

    User findById(UUID id);

    User findByEmail(String email);

    List<User> findAll();

    List<User> findAllByCompanyId(UUID companyId);

    int update(User user);

    int deleteById(UUID id);
}
