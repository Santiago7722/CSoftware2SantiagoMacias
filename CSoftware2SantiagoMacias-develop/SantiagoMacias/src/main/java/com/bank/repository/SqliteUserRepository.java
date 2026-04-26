package com.bank.repository;

import com.bank.config.DatabaseManager;
import com.bank.model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteUserRepository implements UserRepository {

    private final Connection conn;

    public SqliteUserRepository() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    @Override
    public User save(User user) {
        try {
            if (user.getUserId() == 0) {
                String sql = "INSERT INTO users (related_entity_id, full_name, identification_number, email, phone, birth_date, address, role, status, password_hash, company_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getRelatedEntityId());
                ps.setString(2, user.getFullName());
                ps.setString(3, user.getIdentificationNumber());
                ps.setString(4, user.getEmail());
                ps.setString(5, user.getPhone());
                ps.setString(6, user.getBirthDate() != null ? user.getBirthDate().toString() : null);
                ps.setString(7, user.getAddress());
                ps.setString(8, user.getRole().name());
                ps.setString(9, user.getStatus().name());
                ps.setString(10, user.getPasswordHash());
                ps.setString(11, user.getCompanyId());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) user.setUserId(keys.getInt(1));
            } else {
                String sql = "UPDATE users SET related_entity_id=?, full_name=?, email=?, phone=?, address=?, role=?, status=?, company_id=? WHERE user_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, user.getRelatedEntityId());
                ps.setString(2, user.getFullName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPhone());
                ps.setString(5, user.getAddress());
                ps.setString(6, user.getRole().name());
                ps.setString(7, user.getStatus().name());
                ps.setString(8, user.getCompanyId());
                ps.setInt(9, user.getUserId());
                ps.executeUpdate();
            }
            return user;
        } catch (SQLException e) { throw new RuntimeException("Error saving user", e); }
    }

    @Override
    public Optional<User> findByIdentification(String idNumber) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE identification_number=?");
            ps.setString(1, idNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException("Error finding user", e); }
    }

    @Override
    public Optional<User> findById(int userId) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE user_id=?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException("Error finding user", e); }
    }

    @Override
    public List<User> findAll() {
        try {
            List<User> list = new ArrayList<>();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM users");
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error listing users", e); }
    }

    @Override
    public List<User> findByCompanyId(String companyId) {
        try {
            List<User> list = new ArrayList<>();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE company_id=?");
            ps.setString(1, companyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
            return list;
        } catch (SQLException e) { throw new RuntimeException("Error finding users by company", e); }
    }

    @Override
    public boolean existsByIdentification(String idNumber) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE identification_number=?");
            ps.setString(1, idNumber);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) { throw new RuntimeException("Error checking user existence", e); }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setRelatedEntityId(rs.getString("related_entity_id"));
        u.setFullName(rs.getString("full_name"));
        u.setIdentificationNumber(rs.getString("identification_number"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        String bd = rs.getString("birth_date"); if (bd != null) u.setBirthDate(LocalDate.parse(bd));
        u.setAddress(rs.getString("address"));
        u.setRole(UserRole.valueOf(rs.getString("role")));
        u.setStatus(UserStatus.valueOf(rs.getString("status")));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setCompanyId(rs.getString("company_id"));
        return u;
    }
}
