package ui;

import dao.ActivityLogDao;
import model.User;

import java.util.Scanner;

public class ReceptionistMenu {

    private final Scanner scanner;
    private final ActivityLogDao logDao = new ActivityLogDao();

    public ReceptionistMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu(User receptionist) {

        while (true) {

            System.out.println("\n====================================");
            System.out.println("       RECEPTIONIST DASHBOARD");
            System.out.println("====================================");
            System.out.println("[1] Register Patient");
            System.out.println("[2] Schedule Appointment");
            System.out.println("[3] Update Appointment");
            System.out.println("[4] Cancel Appointment");
            System.out.println("[0] Logout");

            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    registerPatient(receptionist);
                    break;

                case "2":
                    scheduleAppointment(receptionist);
                    break;

                case "3":
                    updateAppointment(receptionist);
                    break;

                case "4":
                    cancelAppointment(receptionist);
                    break;

                case "0":
                    logDao.logAction(
                            receptionist.id(),
                            "USER_LOGOUT",
                            "Receptionist " + receptionist.username() + " logged out."
                    );
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void registerPatient(User user) {

        System.out.println("\n========== REGISTER PATIENT ==========");

        System.out.print("Patient Name: ");
        String name = scanner.nextLine();

        System.out.print("Patient Contact Number: ");
        String contact = scanner.nextLine();

        logDao.logAction(
                user.id(),
                "PATIENT_REGISTERED",
                "Receptionist registered patient: " + name +
                        ", Contact: " + contact
        );

        System.out.println("Patient registration activity recorded.");
    }

    private void scheduleAppointment(User user) {

        System.out.println("\n========== SCHEDULE APPOINTMENT ==========");

        System.out.print("Patient Name: ");
        String patient = scanner.nextLine();

        System.out.print("Doctor Name: ");
        String doctor = scanner.nextLine();

        System.out.print("Appointment Date: ");
        String date = scanner.nextLine();

        System.out.print("Appointment Time: ");
        String time = scanner.nextLine();

        logDao.logAction(
                user.id(),
                "APPOINTMENT_SCHEDULED",
                "Appointment scheduled for " + patient +
                        " with Dr. " + doctor +
                        " on " + date + " at " + time
        );

        System.out.println("Appointment activity recorded.");
    }

    private void updateAppointment(User user) {

        System.out.print("Enter Appointment ID: ");
        String appointmentId = scanner.nextLine();

        System.out.print("Enter updated appointment details: ");
        String details = scanner.nextLine();

        logDao.logAction(
                user.id(),
                "APPOINTMENT_UPDATED",
                "Appointment ID " + appointmentId +
                        " updated: " + details
        );

        System.out.println("Appointment update activity recorded.");
    }

    private void cancelAppointment(User user) {

        System.out.print("Enter Appointment ID to cancel: ");
        String appointmentId = scanner.nextLine();

        logDao.logAction(
                user.id(),
                "APPOINTMENT_CANCELLED",
                "Appointment ID " + appointmentId + " cancelled."
        );

        System.out.println("Appointment cancellation activity recorded.");
    }
}