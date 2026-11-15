package ui;

import dao.ActivityLogDao;
import model.User;

import java.util.Scanner;

public class DoctorMenu {

    private final Scanner scanner;
    private final ActivityLogDao logDao = new ActivityLogDao();

    public DoctorMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void showMenu(User doctor) {

        while (true) {

            System.out.println("\n====================================");
            System.out.println("          DOCTOR DASHBOARD");
            System.out.println("====================================");
            System.out.println("[1] View Assigned Appointments");
            System.out.println("[2] Record Consultation / Diagnosis");
            System.out.println("[3] Update Appointment Status");
            System.out.println("[0] Logout");

            System.out.print("Enter choice: ");

            String choice = scanner.nextLine();

            switch (choice) {

                case "1":
                    viewAppointments(doctor);
                    break;

                case "2":
                    recordConsultation(doctor);
                    break;

                case "3":
                    updateAppointmentStatus(doctor);
                    break;

                case "0":
                    logDao.logAction(
                            doctor.id(),
                            "USER_LOGOUT",
                            "Doctor " + doctor.username() + " logged out."
                    );
                    return;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void viewAppointments(User doctor) {

        System.out.println("\n========== ASSIGNED APPOINTMENTS ==========");
        System.out.println("Appointment viewing activity selected.");

        logDao.logAction(
                doctor.id(),
                "APPOINTMENTS_VIEWED",
                "Doctor " + doctor.username() +
                        " accessed assigned appointments."
        );
    }

    private void recordConsultation(User doctor) {

        System.out.println("\n========== CONSULTATION RECORD ==========");

        System.out.print("Appointment ID: ");
        String appointmentId = scanner.nextLine();

        System.out.print("Patient Name: ");
        String patientName = scanner.nextLine();

        System.out.print("Diagnosis: ");
        String diagnosis = scanner.nextLine();

        System.out.print("Treatment / Consultation Notes: ");
        String notes = scanner.nextLine();

        logDao.logAction(
                doctor.id(),
                "CONSULTATION_RECORDED",
                "Doctor recorded consultation for " +
                        patientName +
                        ", Appointment ID: " + appointmentId +
                        ", Diagnosis: " + diagnosis +
                        ", Notes: " + notes
        );

        System.out.println("Consultation activity recorded successfully.");
    }

    private void updateAppointmentStatus(User doctor) {

        System.out.print("Enter Appointment ID: ");
        String appointmentId = scanner.nextLine();

        System.out.println("[1] COMPLETED");
        System.out.println("[2] IN_PROGRESS");
        System.out.println("[3] CANCELLED");

        System.out.print("Select status: ");

        String choice = scanner.nextLine();

        String status = switch (choice) {
            case "1" -> "COMPLETED";
            case "2" -> "IN_PROGRESS";
            case "3" -> "CANCELLED";
            default -> null;
        };

        if (status == null) {
            System.out.println("Invalid status.");
            return;
        }

        logDao.logAction(
                doctor.id(),
                "APPOINTMENT_STATUS_UPDATED",
                "Doctor updated Appointment ID " +
                        appointmentId + " status to " + status
        );

        System.out.println("Appointment status activity recorded.");
    }
}