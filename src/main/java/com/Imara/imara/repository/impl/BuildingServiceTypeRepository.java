package com.Imara.imara.repository.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.exception.ExceptionMapper;
import com.Imara.imara.model.BuildingServiceType;
import com.Imara.imara.repository.IBuildingServiceTypeRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class BuildingServiceTypeRepository implements IBuildingServiceTypeRepository {

    private static final String INSERT = """
            INSERT INTO imara_schema.building_service_types (company_id, building_id, service_type_id, cost, is_optional, billing_period, deleted_at, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING *
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, company_id, building_id, service_type_id, cost, is_optional, billing_period, deleted_at, created_at, updated_at
            FROM imara_schema.building_service_types
            WHERE id = ?
            """;
    private static final String SELECT_ALL = """
            SELECT id, company_id, building_id, service_type_id, cost, is_optional, billing_period, deleted_at, created_at, updated_at
            FROM imara_schema.building_service_types
            WHERE deleted_at IS NULL
            """;
    private static final String SELECT_BY_COMPANY_ID = """
            SELECT id, company_id, building_id, service_type_id, cost, is_optional, billing_period, deleted_at, created_at, updated_at
            FROM imara_schema.building_service_types
            WHERE company_id = ? AND deleted_at IS NULL
            """;
    private static final String SELECT_BY_BUILDING_ID = """
            SELECT id, company_id, building_id, service_type_id, cost, is_optional, billing_period, deleted_at, created_at, updated_at
            FROM imara_schema.building_service_types
            WHERE building_id = ? AND deleted_at IS NULL
            """;
    private static final String UPDATE = """
            UPDATE imara_schema.building_service_types
            SET company_id = ?, building_id = ?, service_type_id = ?, cost = ?, is_optional = ?, billing_period = ?, deleted_at = ?, updated_at = ?
            WHERE id = ?
            """;
    private static final String DELETE = """
            DELETE FROM imara_schema.building_service_types WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<BuildingServiceType> rowMapper = new BuildingServiceTypeRowMapper();

    public BuildingServiceTypeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BuildingServiceType save(BuildingServiceType buildingServiceType) {
        try {
            List<BuildingServiceType> result = jdbcTemplate.query(INSERT, rowMapper,
                    buildingServiceType.getCompanyId(),
                    buildingServiceType.getBuildingId(),
                    buildingServiceType.getServiceTypeId(),
                    buildingServiceType.getCost(),
                    buildingServiceType.getIsOptional() != null ? buildingServiceType.getIsOptional() : false,
                    buildingServiceType.getBillingPeriod(),
                    buildingServiceType.getDeletedAt(),
                    buildingServiceType.getCreatedAt() != null ? buildingServiceType.getCreatedAt() : Instant.now(),
                    buildingServiceType.getUpdatedAt() != null ? buildingServiceType.getUpdatedAt() : Instant.now()
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingServiceType save", ex);
        }
    }

    @Override
    public BuildingServiceType findById(UUID id) {
        try {
            List<BuildingServiceType> result = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "BuildingServiceType not found with id: " + id);
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingServiceType findById", ex);
        }
    }

    @Override
    public List<BuildingServiceType> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL, rowMapper);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingServiceType findAll", ex);
        }
    }

    @Override
    public List<BuildingServiceType> findAllByCompanyId(UUID companyId) {
        try {
            return jdbcTemplate.query(SELECT_BY_COMPANY_ID, rowMapper, companyId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingServiceType findAllByCompanyId", ex);
        }
    }

    @Override
    public List<BuildingServiceType> findAllByBuildingId(UUID buildingId) {
        try {
            return jdbcTemplate.query(SELECT_BY_BUILDING_ID, rowMapper, buildingId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingServiceType findAllByBuildingId", ex);
        }
    }

    @Override
    public int update(BuildingServiceType buildingServiceType) {
        try {
            return jdbcTemplate.update(UPDATE,
                    buildingServiceType.getCompanyId(),
                    buildingServiceType.getBuildingId(),
                    buildingServiceType.getServiceTypeId(),
                    buildingServiceType.getCost(),
                    buildingServiceType.getIsOptional(),
                    buildingServiceType.getBillingPeriod(),
                    buildingServiceType.getDeletedAt(),
                    Instant.now(),
                    buildingServiceType.getId()
            );
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingServiceType update", ex);
        }
    }

    @Override
    public int deleteById(UUID id) {
        try {
            return jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingServiceType deleteById", ex);
        }
    }

    private static class BuildingServiceTypeRowMapper implements RowMapper<BuildingServiceType> {
        @Override
        public BuildingServiceType mapRow(ResultSet rs, int rowNum) throws SQLException {
            return BuildingServiceType.builder()
                    .id(rs.getObject("id", UUID.class))
                    .companyId(rs.getObject("company_id", UUID.class))
                    .buildingId(rs.getObject("building_id", UUID.class))
                    .serviceTypeId(rs.getObject("service_type_id", UUID.class))
                    .cost(rs.getBigDecimal("cost"))
                    .isOptional(rs.getBoolean("is_optional"))
                    .billingPeriod(rs.getString("billing_period"))
                    .deletedAt(RepositoryHelper.toInstant(rs.getTimestamp("deleted_at")))
                    .createdAt(RepositoryHelper.toInstant(rs.getTimestamp("created_at")))
                    .updatedAt(RepositoryHelper.toInstant(rs.getTimestamp("updated_at")))
                    .build();
        }
    }
}
