package com.Imara.imara.repository.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.exception.ExceptionMapper;
import com.Imara.imara.model.Company;
import com.Imara.imara.repository.ICompanyRepository;
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
public class CompanyRepository implements ICompanyRepository {

    private static final String INSERT = """
            INSERT INTO imara_schema.companies (name, email, phone, is_active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING *
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, name, email, phone, is_active, created_at, updated_at
            FROM imara_schema.companies
            WHERE id = ?
            """;
    private static final String SELECT_BY_EMAIL = """
            SELECT id, name, email, phone, is_active, created_at, updated_at
            FROM imara_schema.companies
            WHERE LOWER(email) = LOWER(?)
            """;
    private static final String SELECT_ALL = """
            SELECT id, name, email, phone, is_active, created_at, updated_at
            FROM imara_schema.companies
            """;
    private static final String UPDATE = """
            UPDATE imara_schema.companies
            SET name = ?, email = ?, phone = ?, is_active = ?, updated_at = ?
            WHERE id = ?
            """;
    private static final String DELETE = """
            DELETE FROM imara_schema.companies WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Company> rowMapper = new CompanyRowMapper();

    public CompanyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Company save(Company company) {
        try {
            List<Company> result = jdbcTemplate.query(INSERT, rowMapper,
                    company.getName(),
                    company.getEmail(),
                    company.getPhone(),
                    company.getIsActive() != null ? company.getIsActive() : true,
                    company.getCreatedAt() != null ? company.getCreatedAt() : Instant.now(),
                    company.getUpdatedAt() != null ? company.getUpdatedAt() : Instant.now()
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Company save", ex);
        }
    }

    @Override
    public Company findById(UUID id) {
        try {
            List<Company> result = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "Company not found with id: " + id);
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Company findById", ex);
        }
    }

    @Override
    public Company findByEmail(String email) {
        try {
            List<Company> result = jdbcTemplate.query(SELECT_BY_EMAIL, rowMapper, email);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "Company not found with email");
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Company findByEmail", ex);
        }
    }

    @Override
    public List<Company> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL, rowMapper);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Company findAll", ex);
        }
    }

    @Override
    public int update(Company company) {
        try {
            return jdbcTemplate.update(UPDATE,
                    company.getName(),
                    company.getEmail(),
                    company.getPhone(),
                    company.getIsActive(),
                    Instant.now(),
                    company.getId()
            );
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Company update", ex);
        }
    }

    @Override
    public int deleteById(UUID id) {
        try {
            return jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Company deleteById", ex);
        }
    }

    private static class CompanyRowMapper implements RowMapper<Company> {
        @Override
        public Company mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Company.builder()
                    .id(rs.getObject("id", UUID.class))
                    .name(rs.getString("name"))
                    .email(rs.getString("email"))
                    .phone(rs.getString("phone"))
                    .isActive(rs.getBoolean("is_active"))
                    .createdAt(RepositoryHelper.toInstant(rs.getTimestamp("created_at")))
                    .updatedAt(RepositoryHelper.toInstant(rs.getTimestamp("updated_at")))
                    .build();
        }
    }
}
