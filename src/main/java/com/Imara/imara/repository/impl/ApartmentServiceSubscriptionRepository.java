package com.Imara.imara.repository.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.exception.ExceptionMapper;
import com.Imara.imara.model.ApartmentServiceSubscription;
import com.Imara.imara.repository.IApartmentServiceSubscriptionRepository;
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
public class ApartmentServiceSubscriptionRepository implements IApartmentServiceSubscriptionRepository {

    private static final String INSERT = """
            INSERT INTO imara_schema.apartment_service_subscriptions (company_id, apartment_id, building_service_type_id, created_at)
            VALUES (?, ?, ?, ?)
            RETURNING *
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, company_id, apartment_id, building_service_type_id, created_at
            FROM imara_schema.apartment_service_subscriptions
            WHERE id = ?
            """;
    private static final String SELECT_ALL = """
            SELECT id, company_id, apartment_id, building_service_type_id, created_at
            FROM imara_schema.apartment_service_subscriptions
            """;
    private static final String SELECT_BY_COMPANY_ID = """
            SELECT id, company_id, apartment_id, building_service_type_id, created_at
            FROM imara_schema.apartment_service_subscriptions
            WHERE company_id = ?
            """;
    private static final String SELECT_BY_APARTMENT_ID = """
            SELECT id, company_id, apartment_id, building_service_type_id, created_at
            FROM imara_schema.apartment_service_subscriptions
            WHERE apartment_id = ?
            """;
    private static final String UPDATE = """
            UPDATE imara_schema.apartment_service_subscriptions
            SET company_id = ?, apartment_id = ?, building_service_type_id = ?
            WHERE id = ?
            """;
    private static final String DELETE = """
            DELETE FROM imara_schema.apartment_service_subscriptions WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ApartmentServiceSubscription> rowMapper = new ApartmentServiceSubscriptionRowMapper();

    public ApartmentServiceSubscriptionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ApartmentServiceSubscription save(ApartmentServiceSubscription subscription) {
        try {
            List<ApartmentServiceSubscription> result = jdbcTemplate.query(INSERT, rowMapper,
                    subscription.getCompanyId(),
                    subscription.getApartmentId(),
                    subscription.getBuildingServiceTypeId(),
                    subscription.getCreatedAt() != null ? subscription.getCreatedAt() : Instant.now()
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ApartmentServiceSubscription save", ex);
        }
    }

    @Override
    public ApartmentServiceSubscription findById(UUID id) {
        try {
            List<ApartmentServiceSubscription> result = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "ApartmentServiceSubscription not found with id: " + id);
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ApartmentServiceSubscription findById", ex);
        }
    }

    @Override
    public List<ApartmentServiceSubscription> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL, rowMapper);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ApartmentServiceSubscription findAll", ex);
        }
    }

    @Override
    public List<ApartmentServiceSubscription> findAllByCompanyId(UUID companyId) {
        try {
            return jdbcTemplate.query(SELECT_BY_COMPANY_ID, rowMapper, companyId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ApartmentServiceSubscription findAllByCompanyId", ex);
        }
    }

    @Override
    public List<ApartmentServiceSubscription> findAllByApartmentId(UUID apartmentId) {
        try {
            return jdbcTemplate.query(SELECT_BY_APARTMENT_ID, rowMapper, apartmentId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ApartmentServiceSubscription findAllByApartmentId", ex);
        }
    }

    @Override
    public int update(ApartmentServiceSubscription subscription) {
        try {
            return jdbcTemplate.update(UPDATE,
                    subscription.getCompanyId(),
                    subscription.getApartmentId(),
                    subscription.getBuildingServiceTypeId(),
                    subscription.getId()
            );
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ApartmentServiceSubscription update", ex);
        }
    }

    @Override
    public int deleteById(UUID id) {
        try {
            return jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("ApartmentServiceSubscription deleteById", ex);
        }
    }

    private static class ApartmentServiceSubscriptionRowMapper implements RowMapper<ApartmentServiceSubscription> {
        @Override
        public ApartmentServiceSubscription mapRow(ResultSet rs, int rowNum) throws SQLException {
            return ApartmentServiceSubscription.builder()
                    .id(rs.getObject("id", UUID.class))
                    .companyId(rs.getObject("company_id", UUID.class))
                    .apartmentId(rs.getObject("apartment_id", UUID.class))
                    .buildingServiceTypeId(rs.getObject("building_service_type_id", UUID.class))
                    .createdAt(RepositoryHelper.toInstant(rs.getTimestamp("created_at")))
                    .build();
        }
    }
}
