package com.Imara.imara.repository.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.exception.ExceptionMapper;
import com.Imara.imara.model.ServiceType;
import com.Imara.imara.repository.IServiceTypeRepository;
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
public class ServiceTypeRepository implements IServiceTypeRepository {

    private static final String INSERT = """
            INSERT INTO imara_schema.service_types (company_id, name, description, is_active, deleted_at, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING *
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, company_id, name, description, is_active, deleted_at, created_at, updated_at
            FROM imara_schema.service_types
            WHERE id = ?
            """;
    private static final String SELECT_ALL = """
            SELECT id, company_id, name, description, is_active, deleted_at, created_at, updated_at
            FROM imara_schema.service_types
            WHERE deleted_at IS NULL
            """;
    private static final String SELECT_BY_COMPANY_ID = """
            SELECT id, company_id, name, description, is_active, deleted_at, created_at, updated_at
            FROM imara_schema.service_types
            WHERE company_id = ? AND deleted_at IS NULL
            """;
    private static final String UPDATE = """
            UPDATE imara_schema.service_types
            SET company_id = ?, name = ?, description = ?, is_active = ?, deleted_at = ?, updated_at = ?
            WHERE id = ?
            """;
    private static final String DELETE = """
            DELETE FROM imara_schema.service_types WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ServiceType> rowMapper = new ServiceTypeRowMapper();

    public ServiceTypeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ServiceType save(ServiceType serviceType) {
        try {
            List<ServiceType> result = jdbcTemplate.query(INSERT, rowMapper,
                    serviceType.getCompanyId(),
                    serviceType.getName(),
                    serviceType.getDescription(),
                    serviceType.getIsActive() != null ? serviceType.getIsActive() : true,
                    serviceType.getDeletedAt(),
                    serviceType.getCreatedAt() != null ? serviceType.getCreatedAt() : Instant.now(),
                    serviceType.getUpdatedAt() != null ? serviceType.getUpdatedAt() : Instant.now()
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ServiceType save", ex);
        }
    }

    @Override
    public ServiceType findById(UUID id) {
        try {
            List<ServiceType> result = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "ServiceType not found with id: " + id);
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ServiceType findById", ex);
        }
    }

    @Override
    public List<ServiceType> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL, rowMapper);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ServiceType findAll", ex);
        }
    }

    @Override
    public List<ServiceType> findAllByCompanyId(UUID companyId) {
        try {
            return jdbcTemplate.query(SELECT_BY_COMPANY_ID, rowMapper, companyId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ServiceType findAllByCompanyId", ex);
        }
    }

    @Override
    public int update(ServiceType serviceType) {
        try {
            return jdbcTemplate.update(UPDATE,
                    serviceType.getCompanyId(),
                    serviceType.getName(),
                    serviceType.getDescription(),
                    serviceType.getIsActive(),
                    serviceType.getDeletedAt(),
                    Instant.now(),
                    serviceType.getId()
            );
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ServiceType update", ex);
        }
    }

    @Override
    public int deleteById(UUID id) {
        try {
            return jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ServiceType deleteById", ex);
        }
    }

    private static class ServiceTypeRowMapper implements RowMapper<ServiceType> {
        @Override
        public ServiceType mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ServiceType.builder()
                    .id(rs.getObject("id", UUID.class))
                    .companyId(rs.getObject("company_id", UUID.class))
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .isActive(rs.getBoolean("is_active"))
                    .deletedAt(RepositoryHelper.toInstant(rs.getTimestamp("deleted_at")))
                    .createdAt(RepositoryHelper.toInstant(rs.getTimestamp("created_at")))
                    .updatedAt(RepositoryHelper.toInstant(rs.getTimestamp("updated_at")))
                    .build();
        }
    }
}
