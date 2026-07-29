package com.queuectl.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Manages SQLite database connections, PRAGMA setup, and schema migrations.
 */
public class Database {

    private final String dbPath;

    public Database() {
        this("queuectl.db");
    }

    public Database(String dbPath) {
        this.dbPath = (dbPath != null && !dbPath.isBlank()) ? dbPath : "queuectl.db";
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }

    /**
     * Opens a new JDBC connection to the SQLite database and configures PRAGMAs.
     *
     * @return a new configured Connection
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL;");
            stmt.execute("PRAGMA busy_timeout=5000;");
            stmt.execute("PRAGMA foreign_keys=ON;");
            stmt.execute("PRAGMA synchronous=NORMAL;");
        }
        return conn;
    }

    /**
     * Performs schema migration if the database schema is not up to date.
     */
    public void migrate() {
        try (Connection conn = getConnection()) {
            int currentVersion = 0;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("PRAGMA user_version;")) {
                if (rs.next()) {
                    currentVersion = rs.getInt(1);
                }
            }

            if (currentVersion < 1) {
                System.out.println("Applying migration V1__init.sql...");
                String migrationSql = readResourceFile("db/migrations/V1__init.sql");
                String[] statements = migrationSql.split(";");

                conn.setAutoCommit(false);
                try (Statement stmt = conn.createStatement()) {
                    for (String sql : statements) {
                        String trimmed = sql.trim();
                        if (!trimmed.isEmpty()) {
                            stmt.execute(trimmed);
                        }
                    }
                    stmt.execute("PRAGMA user_version = 1;");
                    conn.commit();
                    System.out.println("Applied migration V1__init.sql successfully (version 1)");
                } catch (Exception e) {
                    conn.rollback();
                    throw new RuntimeException("Failed to apply migration V1__init.sql", e);
                } finally {
                    conn.setAutoCommit(true);
                }
            } else {
                System.out.println("Database schema up to date (version " + currentVersion + ")");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database migration failed", e);
        }
    }

    private String readResourceFile(String path) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        }
        if (is == null) {
            throw new IllegalStateException("Resource not found on classpath: " + path);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read resource file: " + path, e);
        }
    }
}
