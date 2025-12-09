package com.airtribe.meditrack;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.enums.GENDER;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.IdGenerator;

import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);

    // Services (State)
    private static final DoctorService doctorService = new DoctorService();
    private static final PatientService patientService = new PatientService();
    private static final AppointmentService appointmentService = new AppointmentService(doctorService, patientService);

    public static void main(String[] args) {
        // 1. Seeding some dummy data for easier testing
        seedData();

        // 2. Main Menu Loop
        boolean running = true;
        while (running) {
            System.out.println("\n=== MediTrack Management System ===");
            System.out.println("1. Patient Management (Add/View)");
            System.out.println("2. Doctor Management (Add/View)");
            System.out.println("3. Book Appointment");
            System.out.println("4. View All Appointments");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            try {
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
                        System.out.println("Exiting MediTrack. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }


    private static void handleBooking() {
        System.out.println("\n--- Book Appointment ---");
        System.out.print("Enter Doctor ID: ");
        String docId = scanner.nextLine();
        System.out.print("Enter Patient ID: ");
        String patId = scanner.nextLine();

        System.out.print("Enter Consultation Type: ");
        String type = scanner.nextLine();

        // Simple flow: Auto-schedule (pass null for time)
        try {
            appointmentService.bookAppointment(docId, patId, null, type);
        } catch (Exception e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }
    }

    private static void handlePatientMenu() {
        // Implement Add/View logic here
        System.out.println("1. Add Patient\n2. View All");
        int c = scanner.nextInt(); scanner.nextLine();
        if (c == 2) {
            // Access patientService.getAll() (if you added that method)
            // or print using iterator
            System.out.println(patientService.getPatients());
        }
    }

    private static void handleDoctorMenu() {
        System.out.println("1. Add Doctor\n2. View All");
        int c = scanner.nextInt(); scanner.nextLine();
        if (c == 2) {
            System.out.println(doctorService.getDoctors());
        }
    }

    private static void seedData() {
        // Create dummy doctor
        Doctor d1 = Doctor.builder()
                .name("Dr. Strange")
                .doctorType(DoctorType.NEUROLOGIST)
                .qualification("MD")
                .email("strange@marvel.com")
                .build(); // Add other fields as needed
        doctorService.addDoctor(d1);
        System.out.println("Seeded Doctor ID: " + d1.getId());

        // Create dummy patient
        Patient p1 = Patient.builder()
                .name("SpiderMan")
                .email("spidey@marvel.com")
                .gender(GENDER.MALE)
                .mrn(IdGenerator.generateId())
                .build();

        patientService.addPatient(p1);
        System.out.println("Seeded Patient ID: " + p1.getId());
        System.out.println("Seeded Patient [MRN]: " + p1.getMrn());
    }
}
