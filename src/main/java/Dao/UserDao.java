package dao;

import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private final ActivityLogDao logDao = new ActivityLogDao();

    // REGISTER USER
    public boolean registerUser(
            String username,
            String password,
            String role) {

        String sql = """
                INSERT INTO users (username, password, roles, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    // LOGIN
    public User login(String username, String password) {

        String sql = """
                SELECT * FROM users
                WHERE username = ? AND password = ? AND status = 'ACTIVE'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("roles"),
                            rs.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }

    // VIEW ALL USERS
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        String sql = "SELECT * FROM users ORDER BY id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("roles"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }

        return users;
    }

    // APPROVE ACCOUNT
    public boolean approveUser(int adminId, int userId) {

        String sql = """
                UPDATE users
                SET status = 'ACTIVE'
                WHERE id = ? AND status = 'PENDING'
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, userId);

            int result = ps.executeUpdate();

            if (result > 0) {
                logDao.logAction(
                        adminId,
                        "USER_APPROVED",
                        "Admin ID " + adminId +
                                " approved account for User ID " + userId
                );

                return true;
            }

        } catch (SQLException e) {
            System.out.println("Approval error: " + e.getMessage());
        }

        return false;
    }

    // UPDATE ROLE AND STATUS
    public boolean updateUser(
            int adminId,
            int userId,
            String newRole,
            String newStatus) {

        if (!newRole.matches("ADMIN|RECEPTIONIST|DOCTOR")) {
            System.out.println("Invalid role.");
            return false;
        }

        if (!newStatus.matches("ACTIVE|PENDING|INACTIVE")) {
            System.out.println("Invalid status.");
            return false;
        }

        String sql = """
                UPDATE users
                SET roles = ?, status = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, newRole);
            ps.setString(2, newStatus);
            ps.setInt(3, userId);

            int result = ps.executeUpdate();

            if (result > 0) {
                logDao.logAction(
                        adminId,
                        "USER_UPDATED",
                        "Admin ID " + adminId +
                                " updated User ID " + userId +
                                " role to " + newRole +
                                " and status to " + newStatus
                );

                return true;
            }

        } catch (SQLException e) {
            System.out.println("Update error: " + e.getMessage());
        }

        return false;
    }
}