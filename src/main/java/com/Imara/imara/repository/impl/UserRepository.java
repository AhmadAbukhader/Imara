package com.Imara.imara.repository.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.exception.ExceptionMapper;
import com.Imara.imara.model.User;
import com.Imara.imara.repository.IUserRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository implements IUserRepository {

    private static final String INSERT = """
            INSERT INTO imara_schema.users (company_id, full_name, email, password_hash, role, is_active, deleted_at, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING *
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, company_id, full_name, email, password_hash, role, is_active, deleted_at, created_at, updated_at
            FROM imara_schema.users
            WHERE id = ?
            """;
    private static final String SELECT_BY_EMAIL = """
            SELECT id, company_id, full_name, email, password_hash, role, is_active, deleted_at, created_at, updated_at
            FROM imara_schema.users
            WHERE LOWER(email) = LOWER(?) AND deleted_at IS NULL
            """;
    private static final String SELECT_ALL = """
            SELECT id, company_id, full_name, email, password_hash, role, is_active, deleted_at, created_at, updated_at
            FROM imara_schema.users
            WHERE deleted_at IS NULL
            """;
    private static final String SELECT_BY_COMPANY_ID = """
            SELECT id, company_id, full_name, email, password_hash, role, is_active, deleted_at, created_at, updated_at
            FROM imara_schema.users
            WHERE company_id = ? AND deleted_at IS NULL
            """;
    private static final String UPDATE = """
            UPDATE imara_schema.users
            SET company_id = ?, full_name = ?, email = ?, password_hash = ?, role = ?, is_active = ?, deleted_at = ?, updated_at = ?
            WHERE id = ?
            """;
    private static final String DELETE = """
            DELETE FROM imara_schema.users WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> rowMapper = new UserRowMapper();

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User save(User user) {
        try {
            List<User> result = jdbcTemplate.query(INSERT, rowMapper,
                user.getCompanyId(),
                user.getFullName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getIsActive() != null ? user.getIsActive() : true,
                user.getDeletedAt(),
                user.getCreatedAt() != null ? user.getCreatedAt() : Instant.now(),
                user.getUpdatedAt() != null ? user.getUpdatedAt() : Instant.now()
        );
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("User save", ex);
        }
    }

    @Override
    public User findById(UUID id) {
        try {
            List<User> result = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "User not found with id: " + id);
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("User findById", ex);
        }
    }

    @Override
    public User findByEmail(String email) {
        try {
            List<User> result = jdbcTemplate.query(SELECT_BY_EMAIL, rowMapper, email);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "User not found with email");
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("User findByEmail", ex);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL, rowMapper);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("User findAll", ex);
        }
    }

    @Override
    public List<User> findAllByCompanyId(UUID companyId) {
        try {
            return jdbcTemplate.query(SELECT_BY_COMPANY_ID, rowMapper, companyId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("User findAllByCompanyId", ex);
        }
    }

    @Override
    public int update(User user) {
        try {
            return jdbcTemplate.update(UPDATE,
                user.getCompanyId(),
                user.getFullName(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getIsActive(),
                user.getDeletedAt(),
                Instant.now(),
                user.getId()
            );
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("User update", ex);
        }
    }

    @Override
    public int deleteById(UUID id) {
        try {
            return jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("User deleteById", ex);
        }
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                    .id(rs.getObject("id", UUID.class))
                    .companyId(rs.getObject("company_id", UUID.class))
                    .fullName(rs.getString("full_name"))
                    .email(rs.getString("email"))
                    .passwordHash(rs.getString("password_hash"))
                    .role(rs.getString("role"))
                    .isActive(rs.getBoolean("is_active"))
                    .deletedAt(RepositoryHelper.toInstant(rs.getTimestamp("deleted_at")))
                    .createdAt(RepositoryHelper.toInstant(rs.getTimestamp("created_at")))
                    .updatedAt(RepositoryHelper.toInstant(rs.getTimestamp("updated_at")))
                    .build();
        }
    }
}
