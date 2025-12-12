import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.enums.GENDER;
import com.airtribe.meditrack.exception.*;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.Validator;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestRunner {

    // Shared Service Instances
    static DoctorService ds = new DoctorService();
    static PatientService ps = new PatientService();
    static AppointmentService as = new AppointmentService(ds, ps);

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("   MEDITRACK COMPLETE TEST SUITE V2.0    ");
        System.out.println("=========================================\n");

        // 1. Validator Tests (Unit Level)
        System.out.println("--- 1. VALIDATOR UNIT TESTS ---");
        runTest("Valid Email Check", () -> Validator.isValidEmail("test@test.com"));
        runTest("Invalid Email Check", () -> !Validator.isValidEmail("bad-email"));
        runTest("Valid MRN Check", () -> Validator.isValidMRN("MRN001"));
        runTest("Invalid MRN (Hyphen)", () -> !Validator.isValidMRN("MRN-001"));
        runTest("Age Validation (0)", () -> !Validator.isValidAge(0));
        runTest("Age Validation (150)", () -> !Validator.isValidAge(150));

        // 2. Patient Service Tests
        System.out.println("\n--- 2. PATIENT SERVICE TESTS ---");
        runTest("Add Valid Patient", TestRunner::testAddPatient);
        runTest("Add Invalid Patient (Bad Age)", TestRunner::testAddInvalidPatient);
        runTest("Search Patient by MRN", TestRunner::testSearchPatient);
        runTest("Search Non-Existent Patient", TestRunner::testSearchMissingPatient);

        // 3. Doctor Service Tests
        System.out.println("\n--- 3. DOCTOR SERVICE TESTS ---");
        runTest("Add Valid Doctor", TestRunner::testAddDoctor);
        runTest("Add Invalid Doctor (Missing Fields)", TestRunner::testAddInvalidDoctor);
        runTest("Doctor Working Hours Logic", TestRunner::testDoctorAvailabilityLogic);

        // 4. Appointment Logic Tests (The Core)
        setupAppointmentData(); // Pre-load data for complex tests
        System.out.println("\n--- 4. APPOINTMENT LOGIC TESTS ---");
        runTest("Book by ID (Happy Path)", TestRunner::testBookingById);
        runTest("Book by Name (Resolve Logic)", TestRunner::testBookingByName);
        runTest("AI Suggestion (Symptom -> Type)", TestRunner::testAiSymptomMatching);
        runTest("Conflict Resolution (Double Booking)", TestRunner::testDoubleBooking);
        runTest("Weekend Blocking Logic", TestRunner::testWeekendBlocking);
        runTest("Working Hours Blocking", TestRunner::testWorkingHoursBlocking);

        System.out.println("\n=========================================");
        System.out.println("   ALL TESTS COMPLETED                   ");
        System.out.println("=========================================");
    }

    // --- 2. PATIENT TESTS ---
    private static boolean testAddPatient() {
        try {
            Patient p = Patient.builder()
                    .mrn("MRN100").name("John Doe").age(30).gender(GENDER.MALE)
                    .contactNumber("1234567890").email("john@doe.com").address("123 St")
                    .emergencyContact("0987654321").bloodGroup("O+")
                    .knownAllergies(Collections.singletonList("None"))
                    .chronicConditions(Collections.singletonList("None"))
                    .currentMedications(Collections.singletonList("None"))
                    .build();
            ps.addPatient(p);
            return ps.SearchById("MRN100") != null;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private static boolean testAddInvalidPatient() {
        try {
            // Missing Name
            Patient p = Patient.builder().mrn("MRN101").age(30).build();
            ps.addPatient(p);
            return false; // Should have failed
        } catch (InvalidDataException e) {
            return true; // Pass (Expected Exception)
        }
    }

    private static boolean testSearchPatient() {
        try { return ps.SearchById("MRN100").getName().equals("John Doe"); }
        catch (Exception e) { return false; }
    }

    private static boolean testSearchMissingPatient() {
        try {
            ps.SearchById("MRN999");
            return false;
        } catch (PatientNotFoundException e) {
            return true;
        }
    }

    // --- 3. DOCTOR TESTS ---
    private static boolean testAddDoctor() {
        try {
            List<DayOfWeek> days = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY);
            Doctor d = Doctor.builder()
                    .id("DOC100").name("Dr. Valid").age(45).gender(GENDER.FEMALE)
                    .contactNumber("1122334455").email("doc@valid.com").address("Clinic 1")
                    .doctorType(DoctorType.CARDIOLOGIST).qualification("MD").yearsOfExperience(10)
                    .opdRoom("101").availableFrom(LocalTime.of(9,0)).availableTo(LocalTime.of(17,0))
                    .availableDays(days).build();
            ds.addDoctor(d);
            return ds.SearchById("DOC100") != null;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private static boolean testAddInvalidDoctor() {
        try {
            Doctor d = Doctor.builder().id("DOC101").build(); // No name, etc.
            ds.addDoctor(d);
            return false;
        } catch (InvalidDataException e) { return true; }
    }

    private static boolean testDoctorAvailabilityLogic() {
        try {
            // Dr. Valid works Mon-Tue, 9-5
            LocalDateTime mondayNoon = LocalDateTime.of(2025, 12, 15, 12, 0); // Mon
            LocalDateTime sunday = LocalDateTime.of(2025, 12, 14, 12, 0); // Sun

            boolean worksMon = ds.isDoctorWorking("DOC100", mondayNoon);
            boolean worksSun = ds.isDoctorWorking("DOC100", sunday);

            return worksMon && !worksSun;
        } catch (Exception e) { return false; }
    }

    // --- 4. APPOINTMENT TESTS ---
    private static void setupAppointmentData() {
        // Pre-load data specifically for appointment logic
        List<DayOfWeek> allWeek = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

        Doctor card = Doctor.builder()
                .id("DOC200").name("Dr. Heart").age(50).gender(GENDER.MALE)
                .contactNumber("9988776655").email("heart@med.com").address("Hosp A")
                .doctorType(DoctorType.CARDIOLOGIST).qualification("MD").yearsOfExperience(20)
                .opdRoom("202").availableFrom(LocalTime.of(9,0)).availableTo(LocalTime.of(17,0))
                .availableDays(allWeek).build();
        ds.addDoctor(card);

        Patient pat = Patient.builder()
                .mrn("MRN200").name("Jane Smith").age(25).gender(GENDER.FEMALE)
                .contactNumber("5544332211").email("jane@smith.com").address("456 Ave")
                .emergencyContact("1112223333").bloodGroup("B+")
                .knownAllergies(Collections.singletonList("None"))
                .chronicConditions(Collections.singletonList("None"))
                .currentMedications(Collections.singletonList("None")).build();
        ps.addPatient(pat);
    }

    private static boolean testBookingById() {
        try {
            Appointment appt = as.bookAppointment("DOC200", "MRN200", null);
            return appt != null && appt.getStatus() == AppointmentStatus.CONFIRMED;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    private static boolean testBookingByName() {
        try {
            Appointment appt = as.bookAppointment("Dr. Heart", "MRN200", null);
            return appt != null && appt.getDoctorId().equals("DOC200");
        } catch (Exception e) { return false; }
    }

    private static boolean testAiSymptomMatching() {
        try {
            // "heart pain" should map to CARDIOLOGIST
            Appointment appt = as.bookAppointment("I have severe heart pain", "MRN200", null);
            // Fetch the doctor who was booked
            Doctor bookedDoc = (Doctor) ds.SearchById(appt.getDoctorId());
            // PASS if the booked doctor is indeed a CARDIOLOGIST
            return bookedDoc.getDoctorType() == DoctorType.CARDIOLOGIST;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testDoubleBooking() {
        try {
            LocalDateTime slot = LocalDateTime.of(2025, 12, 17, 10, 0); // Wed 10:00

            // 1. First Booking
            as.bookAppointment("DOC200", "MRN200", slot);

            // 2. Second Booking (Same Time)
            Appointment appt2 = as.bookAppointment("DOC200", "MRN200", slot);

            // Should be moved to 10:30
            return appt2.getTimeSlot().getMinute() == 30;
        } catch (Exception e) { return false; }
    }

    private static boolean testWeekendBlocking() {
        try {
            // Dec 13, 2025 is Saturday
            LocalDateTime sat = LocalDateTime.of(2025, 12, 13, 10, 0);
            Appointment appt = as.bookAppointment("DOC200", "MRN200", sat);

            // Should NOT be Saturday
            return appt.getTimeSlot().getDayOfWeek() != DayOfWeek.SATURDAY;
        } catch (Exception e) { return false; }
    }

    private static boolean testWorkingHoursBlocking() {
        try {
            // 5 AM (Doc starts at 9 AM)
            LocalDateTime tooEarly = LocalDateTime.of(2025, 12, 17, 5, 0);
            Appointment appt = as.bookAppointment("DOC200", "MRN200", tooEarly);

            return appt.getTimeSlot().getHour() >= 9;
        } catch (Exception e) { return false; }
    }


    // --- HELPER METHOD ---
    private static void runTest(String name, TestSupplier test) {
        System.out.printf("%-45s : ", name);
        try {
            if (test.run()) System.out.println("✅ PASS");
            else System.out.println("❌ FAIL");
        } catch (Exception e) {
            System.out.println("❌ ERROR (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
        }
    }
    @FunctionalInterface interface TestSupplier { boolean run(); }
}
