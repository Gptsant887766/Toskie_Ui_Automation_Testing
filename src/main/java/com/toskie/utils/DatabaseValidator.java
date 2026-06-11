package com.toskie.utils;

import com.aventstack.extentreports.Status;
import com.toskie.utils_Layer.ConfigManager;
import com.toskie.utils_Layer.ReportManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database validation utilities for verifying data persistence.
 * Configure DB connection in config.properties:
 *   db.url=jdbc:postgresql://host:5432/toskie
 *   db.user=testuser
 *   db.password=testpass
 */
public class DatabaseValidator {

    private Connection connection;

    // ─── Connection management ────────────────────────────────────────────────

    public void connect() {
        String url      = ConfigManager.get("db.url");
        String user     = ConfigManager.get("db.user");
        String password = ConfigManager.get("db.password");

        if (url == null || url.trim().isEmpty()) {
            ReportManager.getTest().log(Status.WARNING,
                "[DB] db.url not configured in config.properties — DB validation skipped.");
            return;
        }
        try {
            connection = DriverManager.getConnection(url, user, password);
            ReportManager.getTest().log(Status.PASS, "[DB] Connected to database.");
        } catch (SQLException e) {
            ReportManager.getTest().log(Status.FAIL,
                "[DB] Connection failed: " + e.getMessage());
            throw new RuntimeException("DB connection failed", e);
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                ReportManager.getTest().log(Status.INFO, "[DB] Disconnected.");
            }
        } catch (SQLException e) {
            ReportManager.getTest().log(Status.WARNING, "[DB] Disconnect error: " + e.getMessage());
        }
    }

    private boolean isConnected() {
        try { return connection != null && !connection.isClosed(); }
        catch (Exception e) { return false; }
    }

    // ─── User validation ──────────────────────────────────────────────────────

    public void assertUserExistsInDB(String mobile) {
        if (!isConnected()) { skipLog("assertUserExistsInDB"); return; }
        try {
            String sql = "SELECT COUNT(*) FROM users WHERE phone_number = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, mobile);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count > 0) {
                ReportManager.getTest().log(Status.PASS,
                    "[DB] User with mobile " + mobile + " exists in DB.");
            } else {
                ReportManager.getTest().log(Status.FAIL,
                    "[DB] User with mobile " + mobile + " NOT found in DB.");
            }
        } catch (SQLException e) {
            ReportManager.getTest().log(Status.FAIL, "[DB] Query error: " + e.getMessage());
        }
    }

    public void assertProfileCreatedInDB(String mobile, String firstName) {
        if (!isConnected()) { skipLog("assertProfileCreatedInDB"); return; }
        try {
            String sql = "SELECT u.id, p.first_name FROM users u "
                + "LEFT JOIN profiles p ON u.id = p.user_id "
                + "WHERE u.phone_number = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, mobile);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbFirstName = rs.getString("first_name");
                if (firstName.equalsIgnoreCase(dbFirstName)) {
                    ReportManager.getTest().log(Status.PASS,
                        "[DB] Profile for " + mobile + " created with firstName=" + firstName);
                } else {
                    ReportManager.getTest().log(Status.FAIL,
                        "[DB] Profile firstName mismatch: expected=" + firstName + " actual=" + dbFirstName);
                }
            } else {
                ReportManager.getTest().log(Status.FAIL,
                    "[DB] No profile found for mobile: " + mobile);
            }
        } catch (SQLException e) {
            ReportManager.getTest().log(Status.FAIL, "[DB] Profile query error: " + e.getMessage());
        }
    }

    public void assertUserEmailVerified(String email) {
        if (!isConnected()) { skipLog("assertUserEmailVerified"); return; }
        try {
            String sql = "SELECT email_verified FROM users WHERE email = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean verified = rs.getBoolean("email_verified");
                if (verified) {
                    ReportManager.getTest().log(Status.PASS,
                        "[DB] Email " + email + " is verified in DB.");
                } else {
                    ReportManager.getTest().log(Status.FAIL,
                        "[DB] Email " + email + " is NOT verified in DB.");
                }
            }
        } catch (SQLException e) {
            ReportManager.getTest().log(Status.FAIL, "[DB] Email verification query error: " + e.getMessage());
        }
    }

    // ─── Session / Token validation ───────────────────────────────────────────

    public void assertSessionStoredInDB(String mobile) {
        if (!isConnected()) { skipLog("assertSessionStoredInDB"); return; }
        try {
            String sql = "SELECT COUNT(*) FROM sessions WHERE user_id = "
                + "(SELECT id FROM users WHERE phone_number = ?) "
                + "AND expires_at > NOW()";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, mobile);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            if (count > 0) {
                ReportManager.getTest().log(Status.PASS,
                    "[DB] Active session found for mobile: " + mobile);
            } else {
                ReportManager.getTest().log(Status.FAIL,
                    "[DB] No active session for mobile: " + mobile);
            }
        } catch (SQLException e) {
            ReportManager.getTest().log(Status.FAIL, "[DB] Session query error: " + e.getMessage());
        }
    }

    // ─── Generic query ────────────────────────────────────────────────────────

    public List<String> executeQuery(String sql, String columnName) {
        List<String> results = new ArrayList<>();
        if (!isConnected()) { skipLog("executeQuery"); return results; }
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                results.add(rs.getString(columnName));
            }
        } catch (SQLException e) {
            ReportManager.getTest().log(Status.FAIL, "[DB] Query error: " + e.getMessage());
        }
        return results;
    }

    private void skipLog(String method) {
        ReportManager.getTest().log(Status.WARNING,
            "[DB] " + method + " skipped — no DB connection.");
    }
}
