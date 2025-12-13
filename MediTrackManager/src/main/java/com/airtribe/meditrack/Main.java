package com.airtribe.meditrack;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.DataStore; // Import DataStore
import com.airtribe.meditrack.util.SeedData;
import com.airtribe.meditrack.util.Validator;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DoctorService doctorService = new DoctorService();
    private static final PatientService patientService = new PatientService();
    private static final AppointmentService appointmentService = new AppointmentService(doctorService, patientService);

    // DataStore instances for saving/loading
    private static final DataStore<Doctor> doctorStore = new DataStore<>();
    private static final DataStore<Patient> patientStore = new DataStore<>();
    private static final DataStore<Appointment> appointmentStore = new DataStore<>();

    public static void main(String[] args) {
        System.out.println("Initializing MediTrack System...");

        // 1. INITIALIZE DATA (CSV with Seed Fallback)
        initializeData();

        // 2. Main Menu Loop
        boolean running = true;
        while (running) {
            System.out.println("\n=== MediTrack Management System ===");
            System.out.println("1.  [PATIENT] View All Patients");
            System.out.println("2.  [DOCTOR]  View All Doctors");
            System.out.println("3.  [BOOK]    Smart Booking (ID/Name/Symptom)");
            System.out.println("4.  [BOOK]    Auto-Match (Find Earliest by Type)");
            System.out.println("5.  [ADMIN]   View All Appointments");
            System.out.println("6.  Exit & Save");
            System.out.print(">> Enter choice: ");

            try {
                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine();
                    continue;
                }
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        printHeader("ALL PATIENTS");
                        patientService.getPatients().forEach(p -> System.out.println(p.getName() + " (MRN: " + p.getMrn() + ")"));
                        break;
                    case 2:
                        printHeader("ALL DOCTORS");
                        doctorService.getDoctors().forEach(d -> System.out.println(d.getName() + " [" + d.getDoctorType() + "] ID: " + d.getId()));
                        break;
                    case 3:
                        handleSmartBooking();
                        break;
                    case 4:
                        handleAutoMatchBooking();
                        break;
                    case 5:
                        printHeader("APPOINTMENTS");
                        appointmentService.getAllAppointments().forEach(System.out::println);
                        break;
                    case 6:
                        running = false;
                        saveData(); // Save before exit
                        System.out.println("Exiting System. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Try 1-6.");
                }
            } catch (Exception e) {
                System.out.println("CRITICAL ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void initializeData() {
        try {
            System.out.print("Loading data from CSV... ");

            // Load from CSV
            HashSet<Doctor> docs = DataStore.loadDoctors();
            HashSet<Patient> pats = DataStore.loadPatients();
            HashSet<Appointment> appts = DataStore.loadAppointments();

            // Check if empty -> Use SeedData
            if (docs.isEmpty() || pats.isEmpty()) {
                System.out.println("CSV Empty or Missing. Loading Seed Data...");
                SeedData.load(doctorService, patientService);

                // Save Seed Data immediately to CSV so next run uses CSV
                saveData();
            } else {
                // Populate Services with CSV Data
                doctorService.setDoctors(docs);
                patientService.setPatients(pats);
                // Note: AppointmentService needs a method to set appointments if you want persistence there too
                // appointmentService.setAppointments(appts); // You might need to add this method to AppointmentService
                System.out.println("SUCCESS. Loaded " + docs.size() + " Doctors and " + pats.size() + " Patients from CSV.");
            }
        } catch (IOException e) {
            System.out.println("Error loading CSV: " + e.getMessage());
            System.out.println("Falling back to Seed Data.");
            SeedData.load(doctorService, patientService);
        }
    }

    private static void saveData() {
        try {
            System.out.print("Saving data to CSV... ");
            doctorStore.save(doctorService.getDoctors());
            patientStore.save(patientService.getPatients());
            // appointmentStore.save(new HashSet<>(appointmentService.getAllAppointments())); // Uncomment if you want to save appointments
            System.out.println("DONE.");
        } catch (IOException e) {
            System.out.println("FAILED to save data: " + e.getMessage());
        }
    }

    // --- Scenario 1: Smart Booking  ---
    private static void handleSmartBooking() {
        printHeader("SMART BOOKING");
        System.out.println("You can enter a Doctor's Name, ID, or even a SYMPTOM (e.g., 'heart pain').");

        System.out.print("Enter Patient MRN: ");
        String patId = scanner.nextLine();

        System.out.print("Enter Doctor Name/ID/Symptom: ");
        String identifier = scanner.nextLine();

        System.out.print("Enter Preferred Time (yyyy-MM-dd HH:mm) [Press ENTER for Earliest Slot]: ");
        String timeStr = scanner.nextLine();

        try {
            LocalDateTime reqTime = null;
            if (Validator.isNonEmpty(timeStr)) {
                // Ideally use DateUtil.parse or similar
                reqTime = LocalDateTime.parse(timeStr.replace(" ", "T"));
            }

            Appointment app = appointmentService.bookAppointment(identifier, patId, reqTime);
            System.out.println("✅ SUCCESS! Appointment Confirmed: " + app.getAppointmentId());
            System.out.println("   Doctor: " + app.getDoctorId());
            System.out.println("   Time:   " + app.getTimeSlot());

            // Auto-save after booking (Optional)
            // saveData();

        } catch (Exception e) {
            System.out.println("❌ BOOKING FAILED: " + e.getMessage());
        }
    }

    // --- Scenario 2: Auto-Match by Type ---
    private static void handleAutoMatchBooking() {
        printHeader("AUTO-MATCH BOOKING");
        System.out.println("We will find the earliest available doctor for your need.");

        System.out.print("Enter Patient MRN: ");
        String patId = scanner.nextLine();

        System.out.println("Available Types: CARDIOLOGIST, DERMATOLOGIST, GENERAL_PRACTITIONER, etc.");
        System.out.print("Enter Specialist Type: ");
        String typeStr = scanner.nextLine().toUpperCase();

        try {
            DoctorType type = DoctorType.valueOf(typeStr);
            Appointment app = appointmentService.bookAppointmentByType(type, patId, null);

            System.out.println("✅ AUTO-MATCH SUCCESS! Assigned to Dr. " + app.getDoctorId());
            System.out.println("   Time Slot: " + app.getTimeSlot());

        } catch (IllegalArgumentException e) {
            System.out.println("❌ Invalid Doctor Type entered.");
        } catch (Exception e) {
            System.out.println("❌ BOOKING FAILED: " + e.getMessage());
        }
    }

    private static void printHeader(String title) {
        System.out.println("\n---------------- " + title + " ----------------");
    }
}
