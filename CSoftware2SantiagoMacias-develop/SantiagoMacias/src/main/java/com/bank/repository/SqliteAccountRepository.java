package com.bank.repository;

import com.bank.config.DatabaseManager;
import com.bank.model.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * ADAPTADOR SQLite — Implementación del puerto AccountRepository.
 */
public class SqliteAccountRepository implements AccountRepository {

    private final Connection conn;

    public SqliteAccountRepository() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    @Override
    public void save(BankAccount account) {
        try {
            String sql = """
                INSERT OR REPLACE INTO bank_accounts
                (account_number, account_type, owner_id, current_balance, currency, status, opening_date)
                VALUES (?,?,?,?,?,?,?)
            """;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account.getAccountNumber());
            ps.setString(2, account.getAccountType().name());
            ps.setString(3, account.getOwnerId());
            ps.setDouble(4, account.getBalance().getAmount().doubleValue());
            ps.setString(5, account.getBalance().getCurrency());
            ps.setString(6, account.getStatus().name());
            ps.setString(7, account.getOpeningDate().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving account", e);
        }
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM bank_accounts WHERE account_number=?");
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error finding account", e);
        }
    }

    @Override
    public List<BankAccount> findByOwnerId(String ownerId) {
        try {
            List<BankAccount> list = new ArrayList<>();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM bank_accounts WHERE owner_id=?");
            ps.setString(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding accounts by owner", e);
        }
    }

    @Override
    public List<BankAccount> findAll() {
        try {
            List<BankAccount> list = new ArrayList<>();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM bank_accounts");
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error listing accounts", e);
        }
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM bank_accounts WHERE account_number=?");
            ps.setString(1, accountNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking account existence", e);
        }
    }

    private BankAccount mapRow(ResultSet rs) throws SQLException {
        BankAccount a = new BankAccount();
        a.setAccountNumber(rs.getString("account_number"));
        a.setAccountType(AccountType.valueOf(rs.getString("account_type")));
        a.setOwnerId(rs.getString("owner_id"));
        a.setBalance(new Money(BigDecimal.valueOf(rs.getDouble("current_balance")), rs.getString("currency")));
        a.setStatus(AccountStatus.valueOf(rs.getString("status")));
        a.setOpeningDate(LocalDate.parse(rs.getString("opening_date")));
        return a;
    }
}
