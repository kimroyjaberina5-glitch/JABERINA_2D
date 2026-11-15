package ui;

import dao.ActivityLogDao;
import dao.UserDao;
import model.ActivityLog;
import model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final Scanner scanner;
    private final ActivityLogDao logDao = new ActivityLogDao();
    private final UserDao userDao = new UserDao();

    public AdminMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu(User admin) {

        while (true) {

            System.out.println("\n====================================");
            System.out.println("     CLINIC APPOINTMENT SYSTEM");
            System.out.println("====================================");
            System.out.println("[1] View All Users");
            System.out.println("[2] Approve User Account");
            System.out.println("[3] Update User Role and Status");
            System.out.println("[4] View System Audit Logs");
            System.out.println("[0] Logout");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    viewUsers();
                    break;

                case "2":
                    approveUser(admin);
                    break;

                case "3":
                    updateUser(admin);
                    break;

                case "4":
                    auditLogMenu();
                    break;

                case "0":
                    logDao.logAction(
                            admin.id(),
                            "USER_LOGOUT",
                            "Admin " + admin.username() + " logged out."
                    );
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void viewUsers() {

        System.out.println("\n========== USER LIST ==========");

        System.out.printf(
                "%-8s | %-20s | %-15s | %-12s%n",
                "ID", "USERNAME", "ROLE", "STATUS"
        );

        for (User user : userDao.getAllUsers()) {

            System.out.printf(
                    "%-8d | %-20s | %-15s | %-12s%n",
                    user.id(),
                    user.username(),
                    user.role(),
                    user.status()
            );
        }
    }

    private void approveUser(User admin) {

        System.out.print("Enter User ID to approve: ");
        int userId = readInt();

        if (userDao.approveUser(admin.id(), userId)) {
            System.out.println("Account approved successfully.");
        } else {
            System.out.println("Approval failed. Check User ID or account status.");
        }
    }

    private void updateUser(User admin) {

        System.out.print("Enter User ID: ");
        int userId = readInt();

        System.out.println("Available Roles:");
        System.out.println("[1] ADMIN");
        System.out.println("[2] RECEPTIONIST");
        System.out.println("[3] DOCTOR");

        System.out.print("Select role: ");

        String roleChoice = scanner.nextLine();

        String role = switch (roleChoice) {
            case "1" -> "ADMIN";
            case "2" -> "RECEPTIONIST";
            case "3" -> "DOCTOR";
            default -> null;
        };

        if (role == null) {
            System.out.println("Invalid role.");
            return;
        }

        System.out.println("\nAvailable Status:");
        System.out.println("[1] ACTIVE");
        System.out.println("[2] PENDING");
        System.out.println("[3] INACTIVE");

        System.out.print("Select status: ");

        String statusChoice = scanner.nextLine();

        String status = switch (statusChoice) {
            case "1" -> "ACTIVE";
            case "2" -> "PENDING";
            case "3" -> "INACTIVE";
            default -> null;
        };

        if (status == null) {
            System.out.println("Invalid status.");
            return;
        }

        if (userDao.updateUser(admin.id(), userId, role, status)) {
            System.out.println("User updated successfully.");
        } else {
            System.out.println("User update failed.");
        }
    }

    // AUDIT LOG MENU
    private void auditLogMenu() {

        while (true) {

            System.out.println("\n====================================");
            System.out.println("          SYSTEM AUDIT LOGS");
            System.out.println("====================================");
            System.out.println("[1] View All System Logs");
            System.out.println("[2] Filter Logs by User ID");
            System.out.println("[3] Clear Historical Logs");
            System.out.println("[0] Back");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    displayLogs(logDao.getAllLogs());
                    break;

                case "2":
                    System.out.print("Enter User ID: ");
                    int userId = readInt();

                    displayLogs(logDao.getLogsByUserId(userId));
                    break;

                case "3":
                    clearLogs();
                    break;

                case "0":
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // DISPLAY FORMATTED LOGS
    private void displayLogs(List<ActivityLog> logs) {

        System.out.println("\n================ SYSTEM LOG RECORDS ================");

        System.out.printf(
                "%-5s | %-8s | %-18s | %-35s | %-20s%n",
                "ID",
                "USER ID",
                "ACTION",
                "DESCRIPTION",
                "TIMESTAMP"
        );

        System.out.println("----------------------------------------------------------------------------------------------------");

        if (logs.isEmpty()) {
            System.out.println("No activity logs found.");
            return;
        }

        for (ActivityLog log : logs) {

            System.out.printf(
                    "%-5d | %-8s | %-18s | %-35s | %-20s%n",
                    log.logId(),
                    log.userId() == null ? "N/A" : log.userId(),
                    log.actionType(),
                    truncate(log.description(), 35),
                    log.timestamp()
            );
        }
    }

    // CLEAR LOGS WITH CONFIRMATION
    private void clearLogs() {

        System.out.print(
                "Are you sure you want to clear ALL historical logs? (YES/NO): "
        );

        String confirmation = scanner.nextLine();

        if (confirmation.equalsIgnoreCase("YES")) {

            if (logDao.clearAllLogs()) {
                System.out.println("Historical logs cleared successfully.");
            }

        } else {
            System.out.println("Clear operation cancelled.");
        }
    }

    private String truncate(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length - 3) + "...";
        }
        return text;
    }

    private int readInt() {

        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }
}