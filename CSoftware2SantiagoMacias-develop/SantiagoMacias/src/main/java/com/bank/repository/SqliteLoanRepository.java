package com.bank.repository;

import com.bank.config.DatabaseManager;
import com.bank.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteLoanRepository implements LoanRepository {

    private final Connection conn;

    public SqliteLoanRepository() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    @Override
    public void save(Loan loan) {
        try {
            if (loan.getLoanId() == 0) insert(loan); else update(loan);
        } catch (SQLException e) {
            throw new RuntimeException("Error saving loan", e);
        }
    }

    private void insert(Loan loan) throws SQLException {
        String sql = """
            INSERT INTO loans (loan_type, client_id, requested_amount, approved_amount, interest_rate,
            term_months, status, approval_date, disbursement_date, disbursement_account,
            creator_user_id, analyst_user_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
        """;
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, loan.getLoanType());
        ps.setString(2, loan.getClientId());
        ps.setDouble(3, loan.getRequestedAmount().getAmount().doubleValue());
        ps.setObject(4, loan.getApprovedAmount() != null ? loan.getApprovedAmount().getAmount().doubleValue() : null);
        ps.setObject(5, loan.getInterestRate() != null ? loan.getInterestRate().doubleValue() : null);
        ps.setObject(6, loan.getTermMonths() > 0 ? loan.getTermMonths() : null);
        ps.setString(7, loan.getStatus().name());
        ps.setString(8, loan.getApprovalDate() != null ? loan.getApprovalDate().toString() : null);
        ps.setString(9, loan.getDisbursementDate() != null ? loan.getDisbursementDate().toString() : null);
        ps.setString(10, loan.getDisbursementAccountNumber());
        ps.setInt(11, loan.getCreatorUserId());
        ps.setObject(12, loan.getAnalystUserId() > 0 ? loan.getAnalystUserId() : null);
        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) loan.setLoanId(keys.getInt(1));
    }

    private void update(Loan loan) throws SQLException {
        String sql = """
            UPDATE loans SET loan_type=?, approved_amount=?, interest_rate=?, term_months=?, status=?,
            approval_date=?, disbursement_date=?, disbursement_account=?, analyst_user_id=? WHERE loan_id=?
        """;
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, loan.getLoanType());
        ps.setObject(2, loan.getApprovedAmount() != null ? loan.getApprovedAmount().getAmount().doubleValue() : null);
        ps.setObject(3, loan.getInterestRate() != null ? loan.getInterestRate().doubleValue() : null);
        ps.setObject(4, loan.getTermMonths() > 0 ? loan.getTermMonths() : null);
        ps.setString(5, loan.getStatus().name());
        ps.setString(6, loan.getApprovalDate() != null ? loan.getApprovalDate().toString() : null);
        ps.setString(7, loan.getDisbursementDate() != null ? loan.getDisbursementDate().toString() : null);
        ps.setString(8, loan.getDisbursementAccountNumber());
        ps.setObject(9, loan.getAnalystUserId() > 0 ? loan.getAnalystUserId() : null);
        ps.setInt(10, loan.getLoanId());
        ps.executeUpdate();
    }

    @Override
    public Optional<Loan> findById(int loanId) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM loans WHERE loan_id=?");
            ps.setInt(1, loanId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException("Error finding loan", e); }
    }

    @Override
    public List<Loan> findByClientId(String clientId) {
        try {
            List<Loan> list = new ArrayList<>();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM loans WHERE client_id=?");
            ps.setString(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error finding loans", e); }
    }

    @Override
    public List<Loan> findByStatus(LoanStatus status) {
        try {
            List<Loan> list = new ArrayList<>();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM loans WHERE status=?");
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error finding loans by status", e); }
    }

    @Override
    public List<Loan> findAll() {
        try {
            List<Loan> list = new ArrayList<>();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM loans");
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error listing loans", e); }
    }

    private Loan mapRow(ResultSet rs) throws SQLException {
        Loan l = new Loan();
        l.setLoanId(rs.getInt("loan_id"));
        l.setLoanType(rs.getString("loan_type"));
        l.setClientId(rs.getString("client_id"));
        l.setRequestedAmount(new Money(BigDecimal.valueOf(rs.getDouble("requested_amount")), "USD"));
        double approved = rs.getDouble("approved_amount");
        if (!rs.wasNull()) l.setApprovedAmount(new Money(BigDecimal.valueOf(approved), "USD"));
        double rate = rs.getDouble("interest_rate");
        if (!rs.wasNull()) l.setInterestRate(BigDecimal.valueOf(rate));
        int term = rs.getInt("term_months");
        if (!rs.wasNull()) l.setTermMonths(term);
        l.setStatus(LoanStatus.valueOf(rs.getString("status")));
        String ad = rs.getString("approval_date"); if (ad != null) l.setApprovalDate(LocalDate.parse(ad));
        String dd = rs.getString("disbursement_date"); if (dd != null) l.setDisbursementDate(LocalDate.parse(dd));
        l.setDisbursementAccountNumber(rs.getString("disbursement_account"));
        l.setCreatorUserId(rs.getInt("creator_user_id"));
        int analyst = rs.getInt("analyst_user_id"); if (!rs.wasNull()) l.setAnalystUserId(analyst);
        return l;
    }
}
