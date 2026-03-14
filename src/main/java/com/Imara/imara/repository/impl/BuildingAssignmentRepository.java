package com.Imara.imara.repository.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.exception.ExceptionMapper;
import com.Imara.imara.model.BuildingAssignment;
import com.Imara.imara.repository.IBuildingAssignmentRepository;
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
public class BuildingAssignmentRepository implements IBuildingAssignmentRepository {

    private static final String INSERT = """
            INSERT INTO imara_schema.building_assignments (company_id, building_id, user_id, created_at)
            VALUES (?, ?, ?, ?)
            RETURNING *
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, company_id, building_id, user_id, created_at
            FROM imara_schema.building_assignments
            WHERE id = ?
            """;
    private static final String SELECT_ALL = """
            SELECT id, company_id, building_id, user_id, created_at
            FROM imara_schema.building_assignments
            """;
    private static final String SELECT_BY_COMPANY_ID = """
            SELECT id, company_id, building_id, user_id, created_at
            FROM imara_schema.building_assignments
            WHERE company_id = ?
            """;
    private static final String SELECT_BY_BUILDING_ID = """
            SELECT id, company_id, building_id, user_id, created_at
            FROM imara_schema.building_assignments
            WHERE building_id = ?
            """;
    private static final String SELECT_BY_USER_ID = """
            SELECT id, company_id, building_id, user_id, created_at
            FROM imara_schema.building_assignments
            WHERE user_id = ?
            """;
    private static final String UPDATE = """
            UPDATE imara_schema.building_assignments
            SET company_id = ?, building_id = ?, user_id = ?
            WHERE id = ?
            """;
    private static final String DELETE = """
            DELETE FROM imara_schema.building_assignments WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<BuildingAssignment> rowMapper = new BuildingAssignmentRowMapper();

    public BuildingAssignmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BuildingAssignment save(BuildingAssignment buildingAssignment) {
        try {
            List<BuildingAssignment> result = jdbcTemplate.query(INSERT, rowMapper,
                    buildingAssignment.getCompanyId(),
                    buildingAssignment.getBuildingId(),
                    buildingAssignment.getUserId(),
                    buildingAssignment.getCreatedAt() != null ? buildingAssignment.getCreatedAt() : Instant.now()
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingAssignment save", ex);
        }
    }

    @Override
    public BuildingAssignment findById(UUID id) {
        try {
            List<BuildingAssignment> result = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "BuildingAssignment not found with id: " + id);
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingAssignment findById", ex);
        }
    }

    @Override
    public List<BuildingAssignment> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL, rowMapper);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingAssignment findAll", ex);
        }
    }

    @Override
    public List<BuildingAssignment> findAllByCompanyId(UUID companyId) {
        try {
            return jdbcTemplate.query(SELECT_BY_COMPANY_ID, rowMapper, companyId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingAssignment findAllByCompanyId", ex);
        }
    }

    @Override
    public List<BuildingAssignment> findAllByBuildingId(UUID buildingId) {
        try {
            return jdbcTemplate.query(SELECT_BY_BUILDING_ID, rowMapper, buildingId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingAssignment findAllByBuildingId", ex);
        }
    }

    @Override
    public List<BuildingAssignment> findAllByUserId(UUID userId) {
        try {
            return jdbcTemplate.query(SELECT_BY_USER_ID, rowMapper, userId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingAssignment findAllByUserId", ex);
        }
    }

    @Override
    public int update(BuildingAssignment buildingAssignment) {
        try {
            return jdbcTemplate.update(UPDATE,
                    buildingAssignment.getCompanyId(),
                    buildingAssignment.getBuildingId(),
                    buildingAssignment.getUserId(),
                    buildingAssignment.getId()
            );
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingAssignment update", ex);
        }
    }

    @Override
    public int deleteById(UUID id) {
        try {
            return jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("BuildingAssignment deleteById", ex);
        }
    }

    private static class BuildingAssignmentRowMapper implements RowMapper<BuildingAssignment> {
        @Override
        public BuildingAssignment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return BuildingAssignment.builder()
                    .id(rs.getObject("id", UUID.class))
                    .companyId(rs.getObject("company_id", UUID.class))
                    .buildingId(rs.getObject("building_id", UUID.class))
                    .userId(rs.getObject("user_id", UUID.class))
                    .createdAt(RepositoryHelper.toInstant(rs.getTimestamp("created_at")))
                    .build();
        }
    }
}
