import dao.ActivityLogDao;
import dao.DatabaseInitializer;
import dao.UserDao;

import model.User;

import ui.AdminMenu;
import ui.ReceptionistMenu;
import ui.DoctorMenu;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final UserDao userDao = new UserDao();
    private static final ActivityLogDao logDao = new ActivityLogDao();

    public static void main(String[] args) {

        DatabaseInitializer.initializeDatabase();

        createDefaultAdmin();

        while (true) {

            System.out.println("\n====================================");
            System.out.println("      CLINIC APPOINTMENT SYSTEM");
            System.out.println("====================================");
            System.out.println("[1] Login");
            System.out.println("[2] Register Account");
            System.out.println("[0] Exit");

            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    login();
                    break;

                case "2":
                    register();
                    break;

                case "0":
                    System.out.println("Thank you for using the system.");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // CREATE DEFAULT ADMIN ACCOUNT
    private static void createDefaultAdmin() {

        if (userDao.getAllUsers().isEmpty()) {

            boolean created = userDao.registerUser(
                    "admin",
                    "admin123",
                    "ADMIN"
            );

            if (created) {
                userDao.approveUser(0, 1);
                System.out.println("Default admin account created.");
                System.out.println("Username: admin");
                System.out.println("Password: admin123");
            }
        }
    }

    // REGISTER ACCOUNT
    private static void register() {

        System.out.println("\n========== ACCOUNT REGISTRATION ==========");

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.println("\nSelect Role:");
        System.out.println("[1] RECEPTIONIST");
        System.out.println("[2] DOCTOR");

        System.out.print("Enter choice: ");

        String choice = scanner.nextLine();

        String role = switch (choice) {
            case "1" -> "RECEPTIONIST";
            case "2" -> "DOCTOR";
            default -> null;
        };

        if (role == null) {
            System.out.println("Invalid role.");
            return;
        }

        if (userDao.registerUser(username, password, role)) {
            System.out.println("Account registered successfully.");
            System.out.println("Please wait for Admin approval.");
        } else {
            System.out.println("Registration failed.");
        }
    }

    // LOGIN
    private static void login() {

        System.out.println("\n========== LOGIN ==========");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = userDao.login(username, password);

        if (user == null) {
            System.out.println("Invalid login or account not approved.");
            return;
        }

        System.out.println("\nLogin successful!");
        System.out.println("Welcome, " + user.username());
        System.out.println("Role: " + user.role());

        logDao.logAction(
                user.id(),
                "USER_LOGIN",
                user.role() + " " + user.username() + " logged into the system."
        );

        openDashboard(user);
    }

    // ROLE-BASED DASHBOARD
    private static void openDashboard(User user) {

        switch (user.role()) {

            case "ADMIN":
                AdminMenu adminMenu = new AdminMenu(scanner);
                adminMenu.showMenu(user);
                break;

            case "RECEPTIONIST":
                ReceptionistMenu receptionistMenu =
                        new ReceptionistMenu(scanner);
                receptionistMenu.showMenu(user);
                break;

            case "DOCTOR":
                DoctorMenu doctorMenu = new DoctorMenu(scanner);
                doctorMenu.showMenu(user);
                break;

            default:
                System.out.println("Unauthorized role.");
        }
    }
}