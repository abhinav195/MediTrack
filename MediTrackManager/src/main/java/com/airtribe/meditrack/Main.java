package com.airtribe.meditrack;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.SeedData; // Import the new class
import com.airtribe.meditrack.util.DateUtil;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DoctorService doctorService = new DoctorService();
    private static final PatientService patientService = new PatientService();
    private static final AppointmentService appointmentService = new AppointmentService(doctorService, patientService);

    public static void main(String[] args) {
        try {
            // 1. Load Seed Data from external class
            SeedData.load(doctorService, patientService, appointmentService);

            // 2. Main Menu Loop
            boolean running = true;
            while (running) {
                System.out.println("\n=== MediTrack Management System ===");
                System.out.println("1. Patient Management (Add/View)");
                System.out.println("2. Doctor Management (Add/View)");
                System.out.println("3. Book Appointment (New!)");
                System.out.println("4. View All Appointments");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");

                try {
                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    switch (choice) {
                        case 1:
                            handlePatientMenu();
                            break;
                        case 2:
                            handleDoctorMenu();
                            break;
                        case 3:
                            handleBooking();
                            break;
                        case 4:
                            System.out.println(appointmentService.getAllAppointments());
                            break;
                        case 5:
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid choice.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    scanner.nextLine(); // Clear buffer if scanner crashed
                }
            }
        }
        catch (Exception e) {
            System.out.println("Runtime Error in Main: " + e.getMessage() + " StackTrace: " + Arrays.toString(e.getStackTrace()));
        }
        finally{
            doctorService.persistData();
            patientService.persistData();
            appointmentService.persistData();
            scanner.close();
        }
    }

    private static void handleBooking() {
        System.out.println("\n--- Book Appointment ---");
        System.out.println("1. Book by Doctor ID or Name");
        System.out.println("2. Auto-Match by Specialization");
        System.out.print("3. Return to Main Menu: ");
        System.out.print("Choice: ");
        int choice = scanner.nextInt(); scanner.nextLine();

        System.out.print("Enter Patient ID (MRN): ");
        String patId = scanner.nextLine();

        System.out.print("Enter Time (yyyy-MM-dd HH:mm) [Leave empty for Auto]: ");
        String timeStr = scanner.nextLine();
        LocalDateTime reqTime = LocalDateTime.now();
        if (!timeStr.trim().isEmpty()) {
            reqTime = DateUtil.parse(timeStr);
        }
        Appointment appointment = null;
        DataStore<Appointment> appointmentStore = new DataStore<>();

        try {
            switch (choice) {
                case 1:
                    System.out.print("Enter Doctor Name or ID: ");
                    String docInput = scanner.nextLine();
                    appointment = appointmentService.bookAppointment(docInput, patId, reqTime);
                    appointmentStore = new DataStore<>();
                    appointmentStore.save(new HashSet<>(Arrays.asList(appointment)));

                case 2:
                    System.out.println("Types: ");
                    for (DoctorType dt : DoctorType.values()) System.out.print(dt + " ");
                    System.out.print("\nEnter Type: ");
                    String typeStr = scanner.nextLine().toUpperCase();
                    DoctorType type = DoctorType.valueOf(typeStr);
                    appointment = appointmentService.bookAppointmentByType(type, patId, reqTime);
                    appointmentStore.save(new HashSet<>(Arrays.asList(appointment)));

                case 3:
                    return ;
            }

        } catch (Exception e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }
    }

    private static void handlePatientMenu() {
        System.out.println("1. Add Patient\n2. View All");
        int c = scanner.nextInt(); scanner.nextLine();
        if (c == 2) System.out.println(patientService.getPatients());
    }

    private static void handleDoctorMenu() {
        System.out.println("1. Add Doctor\n2. View All");
        int c = scanner.nextInt(); scanner.nextLine();
        if (c == 2) System.out.println(doctorService.getDoctors());
    }
}
