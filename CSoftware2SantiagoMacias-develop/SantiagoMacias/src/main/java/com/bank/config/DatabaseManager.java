package com.bank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:bank.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                connection.createStatement().execute("PRAGMA foreign_keys = ON");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection: " + e.getMessage(), e);
        }
        return connection;
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage(), e);
        }
    }

    private void createTables() throws SQLException {
        Statement stmt = connection.createStatement();

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                related_entity_id TEXT,
                full_name TEXT NOT NULL,
                identification_number TEXT NOT NULL UNIQUE,
                email TEXT NOT NULL,
                phone TEXT NOT NULL,
                birth_date TEXT,
                address TEXT,
                role TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'ACTIVE',
                password_hash TEXT NOT NULL,
                company_id TEXT
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS bank_accounts (
                account_number TEXT PRIMARY KEY,
                account_type TEXT NOT NULL,
                owner_id TEXT NOT NULL,
                current_balance REAL NOT NULL DEFAULT 0.0,
                currency TEXT NOT NULL DEFAULT 'USD',
                status TEXT NOT NULL DEFAULT 'ACTIVE',
                opening_date TEXT NOT NULL
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS loans (
                loan_id INTEGER PRIMARY KEY AUTOINCREMENT,
                loan_type TEXT NOT NULL,
                client_id TEXT NOT NULL,
                requested_amount REAL NOT NULL,
                approved_amount REAL,
                interest_rate REAL,
                term_months INTEGER,
                status TEXT NOT NULL DEFAULT 'UNDER_REVIEW',
                approval_date TEXT,
                disbursement_date TEXT,
                disbursement_account TEXT,
                creator_user_id INTEGER NOT NULL,
                analyst_user_id INTEGER
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS transfers (
                transfer_id INTEGER PRIMARY KEY AUTOINCREMENT,
                source_account TEXT NOT NULL,
                destination_account TEXT NOT NULL,
                amount REAL NOT NULL,
                creation_date TEXT NOT NULL,
                approval_date TEXT,
                status TEXT NOT NULL,
                creator_user_id INTEGER NOT NULL,
                approver_user_id INTEGER
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS audit_log (
                log_id TEXT PRIMARY KEY,
                operation_type TEXT NOT NULL,
                operation_datetime TEXT NOT NULL,
                user_id INTEGER NOT NULL,
                user_role TEXT NOT NULL,
                affected_product_id TEXT,
                detail_data TEXT NOT NULL
            )
        """);

        stmt.close();
        System.out.println("[DB] Database tables initialized successfully.");
    }
}
