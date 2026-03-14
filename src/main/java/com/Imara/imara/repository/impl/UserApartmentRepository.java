package com.Imara.imara.repository.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.exception.ExceptionMapper;
import com.Imara.imara.model.UserApartment;
import com.Imara.imara.repository.IUserApartmentRepository;
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
public class UserApartmentRepository implements IUserApartmentRepository {

    private static final String INSERT = """
            INSERT INTO imara_schema.user_apartments (company_id, user_id, apartment_id, created_at)
            VALUES (?, ?, ?, ?)
            RETURNING *
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, company_id, user_id, apartment_id, created_at
            FROM imara_schema.user_apartments
            WHERE id = ?
            """;
    private static final String SELECT_ALL = """
            SELECT id, company_id, user_id, apartment_id, created_at
            FROM imara_schema.user_apartments
            """;
    private static final String SELECT_BY_COMPANY_ID = """
            SELECT id, company_id, user_id, apartment_id, created_at
            FROM imara_schema.user_apartments
            WHERE company_id = ?
            """;
    private static final String SELECT_BY_USER_ID = """
            SELECT id, company_id, user_id, apartment_id, created_at
            FROM imara_schema.user_apartments
            WHERE user_id = ?
            """;
    private static final String SELECT_BY_APARTMENT_ID = """
            SELECT id, company_id, user_id, apartment_id, created_at
            FROM imara_schema.user_apartments
            WHERE apartment_id = ?
            """;
    private static final String UPDATE = """
            UPDATE imara_schema.user_apartments
            SET company_id = ?, user_id = ?, apartment_id = ?
            WHERE id = ?
            """;
    private static final String DELETE = """
            DELETE FROM imara_schema.user_apartments WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<UserApartment> rowMapper = new UserApartmentRowMapper();

    public UserApartmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserApartment save(UserApartment userApartment) {
        try {
            List<UserApartment> result = jdbcTemplate.query(INSERT, rowMapper,
                    userApartment.getCompanyId(),
                    userApartment.getUserId(),
                    userApartment.getApartmentId(),
                    userApartment.getCreatedAt() != null ? userApartment.getCreatedAt() : Instant.now()
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("UserApartment save", ex);
        }
    }

    @Override
    public UserApartment findById(UUID id) {
        try {
            List<UserApartment> result = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "UserApartment not found with id: " + id);
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("UserApartment findById", ex);
        }
    }

    @Override
    public List<UserApartment> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL, rowMapper);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("UserApartment findAll", ex);
        }
    }

    @Override
    public List<UserApartment> findAllByCompanyId(UUID companyId) {
        try {
            return jdbcTemplate.query(SELECT_BY_COMPANY_ID, rowMapper, companyId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("UserApartment findAllByCompanyId", ex);
        }
    }

    @Override
    public List<UserApartment> findAllByUserId(UUID userId) {
        try {
            return jdbcTemplate.query(SELECT_BY_USER_ID, rowMapper, userId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("UserApartment findAllByUserId", ex);
        }
    }

    @Override
    public List<UserApartment> findAllByApartmentId(UUID apartmentId) {
        try {
            return jdbcTemplate.query(SELECT_BY_APARTMENT_ID, rowMapper, apartmentId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("UserApartment findAllByApartmentId", ex);
        }
    }

    @Override
    public int update(UserApartment userApartment) {
        try {
            return jdbcTemplate.update(UPDATE,
                    userApartment.getCompanyId(),
                    userApartment.getUserId(),
                    userApartment.getApartmentId(),
                    userApartment.getId()
            );
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("UserApartment update", ex);
        }
    }

    @Override
    public int deleteById(UUID id) {
        try {
            return jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("UserApartment deleteById", ex);
        }
    }

    private static class UserApartmentRowMapper implements RowMapper<UserApartment> {
        @Override
        public UserApartment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return UserApartment.builder()
                    .id(rs.getObject("id", UUID.class))
                    .companyId(rs.getObject("company_id", UUID.class))
                    .userId(rs.getObject("user_id", UUID.class))
                    .apartmentId(rs.getObject("apartment_id", UUID.class))
                    .createdAt(RepositoryHelper.toInstant(rs.getTimestamp("created_at")))
                    .build();
        }
    }
}
