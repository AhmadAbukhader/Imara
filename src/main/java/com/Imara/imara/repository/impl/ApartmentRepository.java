package com.Imara.imara.repository.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.exception.ExceptionMapper;
import com.Imara.imara.model.Apartment;
import com.Imara.imara.repository.IApartmentRepository;
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
public class ApartmentRepository implements IApartmentRepository {

    private static final String INSERT = """
            INSERT INTO imara_schema.apartments (company_id, building_id, number, floor, area, is_active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING *
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, company_id, building_id, number, floor, area, is_active, created_at, updated_at
            FROM imara_schema.apartments
            WHERE id = ?
            """;
    private static final String SELECT_ALL = """
            SELECT id, company_id, building_id, number, floor, area, is_active, created_at, updated_at
            FROM imara_schema.apartments
            """;
    private static final String SELECT_BY_COMPANY_ID = """
            SELECT id, company_id, building_id, number, floor, area, is_active, created_at, updated_at
            FROM imara_schema.apartments
            WHERE company_id = ?
            """;
    private static final String SELECT_BY_BUILDING_ID = """
            SELECT id, company_id, building_id, number, floor, area, is_active, created_at, updated_at
            FROM imara_schema.apartments
            WHERE building_id = ?
            """;
    private static final String UPDATE = """
            UPDATE imara_schema.apartments
            SET company_id = ?, building_id = ?, number = ?, floor = ?, area = ?, is_active = ?, updated_at = ?
            WHERE id = ?
            """;
    private static final String DELETE = """
            DELETE FROM imara_schema.apartments WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Apartment> rowMapper = new ApartmentRowMapper();

    public ApartmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Apartment save(Apartment apartment) {
        try {
            List<Apartment> result = jdbcTemplate.query(INSERT, rowMapper,
                    apartment.getCompanyId(),
                    apartment.getBuildingId(),
                    apartment.getNumber(),
                    apartment.getFloor(),
                    apartment.getArea(),
                    apartment.getIsActive() != null ? apartment.getIsActive() : true,
                    apartment.getCreatedAt() != null ? apartment.getCreatedAt() : Instant.now(),
                    apartment.getUpdatedAt() != null ? apartment.getUpdatedAt() : Instant.now()
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Apartment save", ex);
        }
    }

    @Override
    public Apartment findById(UUID id) {
        try {
            List<Apartment> result = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "Apartment not found with id: " + id);
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Apartment findById", ex);
        }
    }

    @Override
    public List<Apartment> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL, rowMapper);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Apartment findAll", ex);
        }
    }

    @Override
    public List<Apartment> findAllByCompanyId(UUID companyId) {
        try {
            return jdbcTemplate.query(SELECT_BY_COMPANY_ID, rowMapper, companyId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Apartment findAllByCompanyId", ex);
        }
    }

    @Override
    public List<Apartment> findAllByBuildingId(UUID buildingId) {
        try {
            return jdbcTemplate.query(SELECT_BY_BUILDING_ID, rowMapper, buildingId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Apartment findAllByBuildingId", ex);
        }
    }

    @Override
    public int update(Apartment apartment) {
        try {
            return jdbcTemplate.update(UPDATE,
                    apartment.getCompanyId(),
                    apartment.getBuildingId(),
                    apartment.getNumber(),
                    apartment.getFloor(),
                    apartment.getArea(),
                    apartment.getIsActive(),
                    Instant.now(),
                    apartment.getId()
            );
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Apartment update", ex);
        }
    }

    @Override
    public int deleteById(UUID id) {
        try {
            return jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Apartment deleteById", ex);
        }
    }

    private static class ApartmentRowMapper implements RowMapper<Apartment> {
        @Override
        public Apartment mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Apartment.builder()
                    .id(rs.getObject("id", UUID.class))
                    .companyId(rs.getObject("company_id", UUID.class))
                    .buildingId(rs.getObject("building_id", UUID.class))
                    .number(rs.getString("number"))
                    .floor(rs.getObject("floor", Integer.class))
                    .area(rs.getObject("area", BigDecimal.class))
                    .isActive(rs.getBoolean("is_active"))
                    .createdAt(RepositoryHelper.toInstant(rs.getTimestamp("created_at")))
                    .updatedAt(RepositoryHelper.toInstant(rs.getTimestamp("updated_at")))
                    .build();
        }
    }
}
