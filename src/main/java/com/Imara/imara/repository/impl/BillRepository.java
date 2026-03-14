package com.Imara.imara.repository.impl;

import com.Imara.imara.exception.ApplicationException;
import com.Imara.imara.exception.ErrorCode;
import com.Imara.imara.exception.ExceptionMapper;
import com.Imara.imara.model.Bill;
import com.Imara.imara.repository.IBillRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class BillRepository implements IBillRepository {

    private static final String INSERT = """
            INSERT INTO imara_schema.bills (company_id, building_id, apartment_id, service_type_id, billing_period_start, amount, status, due_date, issued_at, paid_at, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            RETURNING *
            """;
    private static final String SELECT_BY_ID = """
            SELECT id, company_id, building_id, apartment_id, service_type_id, billing_period_start, amount, status, due_date, issued_at, paid_at, created_at, updated_at
            FROM imara_schema.bills
            WHERE id = ?
            """;
    private static final String SELECT_ALL = """
            SELECT id, company_id, building_id, apartment_id, service_type_id, billing_period_start, amount, status, due_date, issued_at, paid_at, created_at, updated_at
            FROM imara_schema.bills
            """;
    private static final String SELECT_BY_COMPANY_ID = """
            SELECT id, company_id, building_id, apartment_id, service_type_id, billing_period_start, amount, status, due_date, issued_at, paid_at, created_at, updated_at
            FROM imara_schema.bills
            WHERE company_id = ?
            """;
    private static final String SELECT_BY_APARTMENT_ID = """
            SELECT id, company_id, building_id, apartment_id, service_type_id, billing_period_start, amount, status, due_date, issued_at, paid_at, created_at, updated_at
            FROM imara_schema.bills
            WHERE apartment_id = ?
            """;
    private static final String UPDATE = """
            UPDATE imara_schema.bills
            SET company_id = ?, building_id = ?, apartment_id = ?, service_type_id = ?, billing_period_start = ?, amount = ?, status = ?, due_date = ?, issued_at = ?, paid_at = ?, updated_at = ?
            WHERE id = ?
            """;
    private static final String DELETE = """
            DELETE FROM imara_schema.bills WHERE id = ?
            """;

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Bill> rowMapper = new BillRowMapper();

    public BillRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Bill save(Bill bill) {
        try {
            List<Bill> result = jdbcTemplate.query(INSERT, rowMapper,
                    bill.getCompanyId(),
                    bill.getBuildingId(),
                    bill.getApartmentId(),
                    bill.getServiceTypeId(),
                    bill.getBillingPeriodStart(),
                    bill.getAmount(),
                    bill.getStatus(),
                    bill.getDueDate(),
                    bill.getIssuedAt(),
                    bill.getPaidAt(),
                    bill.getCreatedAt() != null ? bill.getCreatedAt() : Instant.now(),
                    bill.getUpdatedAt() != null ? bill.getUpdatedAt() : Instant.now()
            );
            return result.isEmpty() ? null : result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Bill save", ex);
        }
    }

    @Override
    public Bill findById(UUID id) {
        try {
            List<Bill> result = jdbcTemplate.query(SELECT_BY_ID, rowMapper, id);
            if (result.isEmpty()) {
                throw new ApplicationException(ErrorCode.DATA_NOT_FOUND, "Bill not found with id: " + id);
            }
            return result.get(0);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Bill findById", ex);
        }
    }

    @Override
    public List<Bill> findAll() {
        try {
            return jdbcTemplate.query(SELECT_ALL, rowMapper);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Bill findAll", ex);
        }
    }

    @Override
    public List<Bill> findAllByCompanyId(UUID companyId) {
        try {
            return jdbcTemplate.query(SELECT_BY_COMPANY_ID, rowMapper, companyId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Bill findAllByCompanyId", ex);
        }
    }

    @Override
    public List<Bill> findAllByApartmentId(UUID apartmentId) {
        try {
            return jdbcTemplate.query(SELECT_BY_APARTMENT_ID, rowMapper, apartmentId);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Bill findAllByApartmentId", ex);
        }
    }

    @Override
    public int update(Bill bill) {
        try {
            return jdbcTemplate.update(UPDATE,
                    bill.getCompanyId(),
                    bill.getBuildingId(),
                    bill.getApartmentId(),
                    bill.getServiceTypeId(),
                    bill.getBillingPeriodStart(),
                    bill.getAmount(),
                    bill.getStatus(),
                    bill.getDueDate(),
                    bill.getIssuedAt(),
                    bill.getPaidAt(),
                    Instant.now(),
                    bill.getId()
            );
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Bill update", ex);
        }
    }

    @Override
    public int deleteById(UUID id) {
        try {
            return jdbcTemplate.update(DELETE, id);
        } catch (DataAccessException ex) {
            throw ExceptionMapper.map("Bill deleteById", ex);
        }
    }

    private static class BillRowMapper implements RowMapper<Bill> {
        @Override
        public Bill mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Bill.builder()
                    .id(rs.getObject("id", UUID.class))
                    .companyId(rs.getObject("company_id", UUID.class))
                    .buildingId(rs.getObject("building_id", UUID.class))
                    .apartmentId(rs.getObject("apartment_id", UUID.class))
                    .serviceTypeId(rs.getObject("service_type_id", UUID.class))
                    .billingPeriodStart(RepositoryHelper.toLocalDate(rs.getDate("billing_period_start")))
                    .amount(rs.getBigDecimal("amount"))
                    .status(rs.getString("status"))
                    .dueDate(RepositoryHelper.toLocalDate(rs.getDate("due_date")))
                    .issuedAt(RepositoryHelper.toLocalDate(rs.getDate("issued_at")))
                    .paidAt(RepositoryHelper.toLocalDate(rs.getDate("paid_at")))
                    .createdAt(RepositoryHelper.toInstant(rs.getTimestamp("created_at")))
                    .updatedAt(RepositoryHelper.toInstant(rs.getTimestamp("updated_at")))
                    .build();
        }
    }
}
