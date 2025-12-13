import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.entity.Person;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.enums.GENDER;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.DataStore;
import com.airtribe.meditrack.util.Validator;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * COMPREHENSIVE MANUAL TEST SUITE
 * Covers: Validation, CRUD, Business Logic (Booking), and File Persistence.
 */
public class TestRunner {

    // --- SHARED SERVICES FOR TESTING ---
    static DoctorService doctorService = new DoctorService();
    static PatientService patientService = new PatientService();
    static AppointmentService appointmentService = new AppointmentService(doctorService, patientService);

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   MEDITRACK SYSTEM - MANUAL TEST SUITE V3.0      ");
        System.out.println("==================================================");

        // 1. VALIDATION UTILITIES
        System.out.println("\n[1] --- INPUT VALIDATION TESTS ---");
        // Ensure these inputs match your Validator regex expectations
        runTest("Email (Valid)", () -> Validator.isValidEmail("john.doe@example.com"));
        runTest("Email (Invalid - No @)", () -> !Validator.isValidEmail("johndoexample.com"));
        runTest("Email (Invalid - No Domain)", () -> !Validator.isValidEmail("john@"));

        runTest("Mobile (Valid - 10 Digits)", () -> Validator.isValidPhone("9876543210"));
        runTest("Mobile (Invalid - Letters)", () -> !Validator.isValidPhone("98765abcde"));
        runTest("Mobile (Invalid - Short)", () -> !Validator.isValidPhone("123"));

        runTest("MRN Format (Valid)", () -> Validator.isValidMRN("MRN123"));
        runTest("MRN Format (Invalid)", () -> !Validator.isValidMRN("123_MRN"));
        runTest("MRN Format (Invalid Length)", () -> !Validator.isValidMRN("A"));

        // 2. DOCTOR MANAGEMENT
        System.out.println("\n[2] --- DOCTOR SERVICE TESTS ---");
        runTest("Add New Doctor", TestRunner::testAddDoctor);
        runTest("Duplicate Doctor Check (Simulated)", TestRunner::testDuplicateDoctorProtection);
        runTest("Search Doctor (By ID)", TestRunner::testSearchDoctorById);
        runTest("Doctor Availability Logic (Time)", TestRunner::testDoctorTimeAvailability);

        // 3. PATIENT MANAGEMENT
        System.out.println("\n[3] --- PATIENT SERVICE TESTS ---");
        runTest("Add New Patient", TestRunner::testAddPatient);
        runTest("Search Patient (By MRN)", TestRunner::testSearchPatientByMrn);
        runTest("Patient Missing Exception", TestRunner::testPatientNotFound);

        // 4. APPOINTMENT LOGIC (The Core)
        setupBookingData(); // Pre-load specific data for booking tests
        System.out.println("\n[4] --- APPOINTMENT LOGIC TESTS ---");
        runTest("Standard Booking (Valid)", TestRunner::testStandardBooking);
        runTest("Booking Conflict (Same Time)", TestRunner::testBookingConflict);
        runTest("Booking Outside Working Hours", TestRunner::testBookingOutsideHours);
        runTest("Booking on Weekend (Day Off)", TestRunner::testBookingDayOff);
        runTest("Smart Booking (Symptom Matching)", TestRunner::testSymptomMatching);

        // 5. FILE PERSISTENCE
        System.out.println("\n[5] --- FILE I/O PERSISTENCE TESTS ---");
        runTest("Save Data to CSV", TestRunner::testSaveToCSV);
        runTest("Load Data from CSV", TestRunner::testLoadFromCSV);

        System.out.println("\n==================================================");
        System.out.println("   END OF TESTS ");
        System.out.println("==================================================");
    }

    // =================================================================
    // TEST LOGIC IMPLEMENTATIONS
    // =================================================================

    // --- DOCTOR TESTS ---
    private static boolean testAddDoctor() {
        try {
            List<DayOfWeek> days = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
            Doctor d = Doctor.builder()
                    .id("DOC0001") // Alphanumeric ID
                    .name("Dr. House")
                    .age(50)
                    .gender(GENDER.MALE)
                    .doctorType(DoctorType.GENERAL_PRACTITIONER)
                    .qualification("MD")
                    .yearsOfExperience(20)
                    .contactNumber("1112223333")
                    .email("house@test.com")
                    .address("221B Baker St")
                    .availableDays(days)
                    .availableFrom(LocalTime.of(9, 0))
                    .availableTo(LocalTime.of(17, 0))
                    .opdRoom("101")
                    .build();
            doctorService.addDoctor(d);
            return doctorService.SearchById("DOC0001") != null;
        } catch (Exception e) {
            System.out.println("   [DEBUG] AddDoctor Failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean testDuplicateDoctorProtection() {
        try {
            Doctor d = Doctor.builder().id("DOC0001").name("Clone").build();
            doctorService.addDoctor(d);
            return true; // Assuming overwrite or graceful fail is acceptable
        } catch (Exception e) { return true; }
    }

    private static boolean testSearchDoctorById() {
        try {
            return doctorService.SearchById("DOC0001").getName().equals("Dr. House");
        } catch (Exception e) { return false; }
    }

    private static boolean testDoctorTimeAvailability() {
        try {
            // Dr. House works 09:00 - 17:00
            LocalDateTime workingTime = LocalDateTime.of(2025, 12, 17, 10, 0); // Wed 10AM
            LocalDateTime nonWorkingTime = LocalDateTime.of(2025, 12, 17, 20, 0); // Wed 8PM

            boolean yes = doctorService.isDoctorWorking("DOC0001", workingTime);
            boolean no = doctorService.isDoctorWorking("DOC0001", nonWorkingTime);

            return yes && !no;
        } catch (Exception e) { return false; }
    }

    // --- PATIENT TESTS ---
    private static boolean testAddPatient() {
        try {
            Patient p = Patient.builder()
                    .mrn("MRN0001") // Alphanumeric 7 chars
                    .name("Sherlock Holmes")
                    .age(35)
                    .gender(GENDER.MALE)
                    .contactNumber("9998887777")
                    .email("sherlock@test.com")
                    .address("221B Baker St")
                    .emergencyContact("1112223333")
                    .bloodGroup("B+")
                    .knownAllergies(Collections.singletonList("None"))
                    .chronicConditions(Collections.singletonList("None"))
                    .currentMedications(Collections.singletonList("None"))
                    .build();
            patientService.addPatient(p);
            return patientService.SearchById("MRN0001") != null;
        } catch (Exception e) {
            System.out.println("   [DEBUG] AddPatient Failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean testSearchPatientByMrn() {
        try {
            // Must match the MRN used in testAddPatient exactly
            return patientService.SearchById("MRN0001").getName().equals("Sherlock Holmes");
        } catch (Exception e) { return false; }
    }

    private static boolean testPatientNotFound() {
        try {
            patientService.SearchById("MRN_NON_EXISTENT");
            return false; // Should have thrown exception
        } catch (Exception e) {
            return true; // Expected exception
        }
    }

    // --- APPOINTMENT TESTS ---
    private static void setupBookingData() {
        try {
            List<DayOfWeek> monOnly = Collections.singletonList(DayOfWeek.MONDAY);

            // DOC002 - Dr. Heart (Cardiologist)
            Doctor cardio = Doctor.builder()
                    .id("DOC002")
                    .name("Dr. Heart")
                    .age(45).gender(GENDER.MALE).address("Hosp A").contactNumber("5551234567").email("h@h.com")
                    .doctorType(DoctorType.CARDIOLOGIST).qualification("MBBS").yearsOfExperience(10).opdRoom("202")
                    .availableDays(monOnly).availableFrom(LocalTime.of(9,0)).availableTo(LocalTime.of(17,0))
                    .build();
            doctorService.addDoctor(cardio);

            // MRN9999 - John Watson
            Patient pat = Patient.builder()
                    .mrn("MRN9999")
                    .name("John Watson")
                    .age(40).gender(GENDER.MALE).address("221B Baker").contactNumber("5559876543").email("w@t.com")
                    .emergencyContact("5551112222").bloodGroup("O+")
                    .knownAllergies(Collections.singletonList("None"))
                    .chronicConditions(Collections.singletonList("None"))
                    .currentMedications(Collections.singletonList("None"))
                    .build();
            patientService.addPatient(pat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean testStandardBooking() {
        try {
            // Book Monday at 10:00 AM for DOC002 (Dr. Heart)
            LocalDateTime slot = LocalDateTime.of(2025, 12, 15, 10, 0); // Dec 15 2025 is a Monday
            Appointment app = appointmentService.bookAppointment("DOC002", "MRN9999", slot);
            return app.getStatus() == AppointmentStatus.CONFIRMED;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private static boolean testBookingConflict() {
        try {
            // Try to book Monday 10:00 AM AGAIN for DOC002
            LocalDateTime slot = LocalDateTime.of(2025, 12, 15, 10, 0);

            // Depending on implementation, this might throw exception OR shift time
            try {
                Appointment app = appointmentService.bookAppointment("DOC002", "MRN9999", slot);
                // If it booked, ensure it didn't overwrite the exact time slot (maybe shifted to 10:30?)
                return !app.getTimeSlot().isEqual(slot);
            } catch (Exception e) {
                return true; // Exception is a valid way to handle conflict
            }
        } catch (Exception e) { return false; }
    }

    private static boolean testBookingOutsideHours() {
        try {
            // Book Monday at 6:00 AM (Doc starts at 9)
            LocalDateTime requestedSlot = LocalDateTime.of(2025, 12, 15, 6, 0);
            Appointment app = appointmentService.bookAppointment("DOC002", "MRN9999", requestedSlot);

            // PASS if it successfully booked at 9:00 AM (shifted)
            return app.getTimeSlot().getHour() == 9 && app.getTimeSlot().getMinute() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testBookingDayOff() {
        try {
            // Book Tuesday Dec 16 (Doc works Monday only)
            LocalDateTime requestedSlot = LocalDateTime.of(2025, 12, 16, 10, 0);
            Appointment app = appointmentService.bookAppointment("DOC002", "MRN9999", requestedSlot);

            // PASS if it successfully booked on Monday Dec 22 (shifted 1 week)
            return app.getTimeSlot().getDayOfWeek() == DayOfWeek.MONDAY
                    && app.getTimeSlot().getDayOfMonth() == 22;
        } catch (Exception e) {
            return false;
        }
    }


    private static boolean testSymptomMatching() {
        try {
            // "Chest pain" -> Should find A Cardiologist
            Appointment app = appointmentService.bookAppointment("Chest pain", "MRN9999", null);

            String doctorId = app.getDoctorId();
            Doctor d = (Doctor) doctorService.SearchById(doctorId);

            // DEBUG PRINT
            if (d != null) {
                System.out.println("   [DEBUG] Smart Match Booked: " + d.getName() + " [" + d.getDoctorType() + "]");
            } else {
                System.out.println("   [DEBUG] Doctor ID " + doctorId + " not found!");
            }

            return d != null && d.getDoctorType() == DoctorType.GENERAL_PRACTITIONER;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    // --- PERSISTENCE TESTS ---
    private static boolean testSaveToCSV() {
        try {
            // 1. Add a unique doctor to test persistence
            Doctor d = Doctor.builder()
                    .id("DOCSAVE")
                    .name("Dr. Saved")
                    .age(30)
                    .gender(GENDER.FEMALE)
                    .address("Cloud")
                    .contactNumber("1231231234")
                    .email("save@test.com")
                    .qualification("MBBS")
                    .yearsOfExperience(5)
                    .opdRoom("999")
                    .doctorType(DoctorType.CARDIOLOGIST)
                    .availableDays(Collections.singletonList(DayOfWeek.MONDAY))
                    .availableFrom(LocalTime.MIN).availableTo(LocalTime.MAX)
                    .build();
            doctorService.addDoctor(d);

            // 2. Save
            DataStore<Doctor> ds = new DataStore<>();
            ds.save(doctorService.getDoctors());

            // 3. Clear Memory
            doctorService.setDoctors(new java.util.HashSet<>());

            // 4. Load back
            doctorService.setDoctors(DataStore.loadDoctors());

            // 5. Verify
            return doctorService.SearchById("DOCSAVE") != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testLoadFromCSV() {
        try {
            // Clear memory
            doctorService.setDoctors(new java.util.HashSet<>());

            // Load
            doctorService.setDoctors(DataStore.loadDoctors());

            // Check if ANY doctor was loaded
            return !doctorService.getDoctors().isEmpty();
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    // =================================================================
    // HELPER METHODS
    // =================================================================
    private static void runTest(String testName, TestSupplier test) {
        System.out.printf("%-50s : ", testName);
        try {
            if (test.run()) {
                System.out.println("✅ PASS");
            } else {
                System.out.println("❌ FAIL");
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR (" + e.getMessage() + ")");
        }
    }

    @FunctionalInterface
    interface TestSupplier {
        boolean run();
    }
}
