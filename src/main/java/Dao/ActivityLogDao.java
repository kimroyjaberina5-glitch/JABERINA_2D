package dao;

import model.ActivityLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    // INSERT NEW ACTIVITY LOG
    public void logAction(
            int userId,
            String actionType,
            String description) {

        String sql = """
                INSERT INTO activity_logs
                (user_id, action_type, description)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, actionType);
            ps.setString(3, description);

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Logging error: " + e.getMessage());
        }
    }

    // GET ALL LOGS
    public List<ActivityLog> getAllLogs() {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT * FROM activity_logs
                ORDER BY timestamp DESC, log_id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                logs.add(mapLog(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving logs: " + e.getMessage());
        }

        return logs;
    }

    // GET LOGS BY USER ID
    public List<ActivityLog> getLogsByUserId(int userId) {

        List<ActivityLog> logs = new ArrayList<>();

        String sql = """
                SELECT * FROM activity_logs
                WHERE user_id = ?
                ORDER BY timestamp DESC, log_id DESC
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapLog(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error filtering logs: " + e.getMessage());
        }

        return logs;
    }

    // CLEAR HISTORICAL LOGS
    public boolean clearAllLogs() {

        String sql = "DELETE FROM activity_logs";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error clearing logs: " + e.getMessage());
            return false;
        }
    }

    // CONVERT RESULTSET INTO ACTIVITYLOG
    private ActivityLog mapLog(ResultSet rs) throws SQLException {

        int id = rs.getInt("user_id");
        Integer userId = rs.wasNull() ? null : id;

        return new ActivityLog(
                rs.getInt("log_id"),
                userId,
                rs.getString("action_type"),
                rs.getString("description"),
                rs.getString("timestamp")
        );
    }
}