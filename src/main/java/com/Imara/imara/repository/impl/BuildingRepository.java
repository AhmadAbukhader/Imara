package com.Imara.imara.repository.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.exception.ExceptionMapper;
import com.Imara.imara.model.Building;
import com.Imara.imara.repository.IBuildingRepository;
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
public class BuildingRepository implements IBuildingRepository {

    private static final String INSERT = """
            INSERT INTO imara_schema.buildings (company_id, name, address, city, is_active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            RETURNING *
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, company_id, name, address, city, is_active, created_at, updated_at
            FROM imara_schema.buildings
            WHERE id = ?
            """;
    private static final String SELECT_ALL = """
            SELECT id, company_id, name, address, city, is_active, created_at, updated_at
            FROM imara_schema.buildings
            """;
    private static final String SELECT_BY_COMPANY_ID = """
            SELECT id, company_id, name, address, city, is_active, created_at, updated_at
            FROM imara_schema.buildings
            WHERE company_id = ?
            """;
    private static final String UPDATE = """
            UPDATE imara_schema.buildings
            SET company_id = ?, name = ?, address = ?, city = ?, is_active = ?, updated_at = ?
            WHERE id = ?
            """;
    private static final String DELETE = """
            DELETE FROM imara_schema.buildings WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Building> rowMapper = new BuildingRowMapper();

    public BuildingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Building save(Building building) {
        try {
            List<Building> result = jdbcTemplate.query(INSERT, rowMapper,
                    building.getCompanyId(),
                    building.getName(),
                    building.getAddress(),
                    building.getCity(),
                    building.getIsActive() != null ? building.getIsActive() : true,
                    building.getCreatedAt() != null ? building.getCreatedAt() : Instant.now(),
                    building.getUpdatedAt() != null ? building.getUpdatedAt() : Instant.now()
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Building save", ex);
        }
    }

    @Override
    public Building findById(UUID id) {
        try {
            List<Building> result = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "Building not found with id: " + id);
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Building findById", ex);
        }
    }

    @Override
    public List<Building> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL, rowMapper);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Building findAll", ex);
        }
    }

    @Override
    public List<Building> findAllByCompanyId(UUID companyId) {
        try {
            return jdbcTemplate.query(SELECT_BY_COMPANY_ID, rowMapper, companyId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Building findAllByCompanyId", ex);
        }
    }

    @Override
    public int update(Building building) {
        try {
            return jdbcTemplate.update(UPDATE,
                    building.getCompanyId(),
                    building.getName(),
                    building.getAddress(),
                    building.getCity(),
                    building.getIsActive(),
                    Instant.now(),
                    building.getId()
            );
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Building update", ex);
        }
    }

    @Override
    public int deleteById(UUID id) {
        try {
            return jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Building deleteById", ex);
        }
    }

    private static class BuildingRowMapper implements RowMapper<Building> {
        @Override
        public Building mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Building.builder()
                    .id(rs.getObject("id", UUID.class))
                    .companyId(rs.getObject("company_id", UUID.class))
                    .name(rs.getString("name"))
                    .address(rs.getString("address"))
                    .city(rs.getString("city"))
                    .isActive(rs.getBoolean("is_active"))
                    .createdAt(RepositoryHelper.toInstant(rs.getTimestamp("created_at")))
                    .updatedAt(RepositoryHelper.toInstant(rs.getTimestamp("updated_at")))
                    .build();
        }
    }
}
