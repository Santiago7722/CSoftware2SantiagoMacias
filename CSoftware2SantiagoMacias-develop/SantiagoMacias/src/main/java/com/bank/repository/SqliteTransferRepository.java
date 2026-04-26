package com.bank.repository;

import com.bank.config.DatabaseManager;
import com.bank.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteTransferRepository implements TransferRepository {

    private final Connection conn;

    public SqliteTransferRepository() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    @Override
    public void save(Transfer transfer) {
        try {
            if (transfer.getTransferId() == 0) insert(transfer); else update(transfer);
        } catch (SQLException e) { throw new RuntimeException("Error saving transfer", e); }
    }

    private void insert(Transfer transfer) throws SQLException {
        String sql = """
            INSERT INTO transfers (source_account, destination_account, amount, creation_date,
            execution_date, status, creator_user_id, approver_user_id) VALUES (?,?,?,?,?,?,?,?)
        """;
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, transfer.getSourceAccount());
        ps.setString(2, transfer.getDestinationAccount());
        ps.setDouble(3, transfer.getAmount().getAmount().doubleValue());
        ps.setString(4, transfer.getCreationDate().toString());
        ps.setString(5, transfer.getExecutionDate() != null ? transfer.getExecutionDate().toString() : null);
        ps.setString(6, transfer.getStatus().name());
        ps.setInt(7, transfer.getCreatorUserId());
        ps.setObject(8, transfer.getApproverUserId() > 0 ? transfer.getApproverUserId() : null);
        ps.executeUpdate();
        ResultSet keys = ps.getGeneratedKeys();
        if (keys.next()) transfer.setTransferId(keys.getInt(1));
    }

    private void update(Transfer transfer) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            "UPDATE transfers SET status=?, execution_date=?, approver_user_id=? WHERE transfer_id=?");
        ps.setString(1, transfer.getStatus().name());
        ps.setString(2, transfer.getExecutionDate() != null ? transfer.getExecutionDate().toString() : null);
        ps.setObject(3, transfer.getApproverUserId() > 0 ? transfer.getApproverUserId() : null);
        ps.setInt(4, transfer.getTransferId());
        ps.executeUpdate();
    }

    @Override
    public Optional<Transfer> findById(int transferId) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM transfers WHERE transfer_id=?");
            ps.setInt(1, transferId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException("Error finding transfer", e); }
    }

    @Override
    public List<Transfer> findBySourceOrDestinationAccount(String accountNumber) {
        try {
            List<Transfer> list = new ArrayList<>();
            PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM transfers WHERE source_account=? OR destination_account=?");
            ps.setString(1, accountNumber);
            ps.setString(2, accountNumber);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error finding transfers", e); }
    }

    @Override
    public List<Transfer> findByStatus(TransferStatus status) {
        try {
            List<Transfer> list = new ArrayList<>();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM transfers WHERE status=?");
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error finding transfers by status", e); }
    }

    @Override
    public List<Transfer> findAll() {
        try {
            List<Transfer> list = new ArrayList<>();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM transfers ORDER BY creation_date DESC");
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error listing transfers", e); }
    }

    private Transfer mapRow(ResultSet rs) throws SQLException {
        Transfer t = new Transfer();
        t.setTransferId(rs.getInt("transfer_id"));
        t.setSourceAccount(rs.getString("source_account"));
        t.setDestinationAccount(rs.getString("destination_account"));
        t.setAmount(new Money(BigDecimal.valueOf(rs.getDouble("amount")), "USD"));
        t.setCreationDate(LocalDateTime.parse(rs.getString("creation_date")));
        String ed = rs.getString("execution_date");
        if (ed != null) t.setExecutionDate(LocalDateTime.parse(ed));
        t.setStatus(TransferStatus.valueOf(rs.getString("status")));
        t.setCreatorUserId(rs.getInt("creator_user_id"));
        int approver = rs.getInt("approver_user_id");
        if (!rs.wasNull()) t.setApproverUserId(approver);
        return t;
    }
}
