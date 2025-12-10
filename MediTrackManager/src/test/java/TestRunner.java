
import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.enums.GENDER;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.DateUtil;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;

public class TestRunner {

    // Shared State for Tests
    static DoctorService ds = new DoctorService();
    static PatientService ps = new PatientService();
    static AppointmentService as = new AppointmentService(ds, ps);

    public static void main(String[] args) {
        System.out.println("=== Starting COMPLETE Manual Test Suite ===\n");
        // All Appointment test
        runTest("1. DateUtil - Parsing & Formatting", TestRunner::testDateUtilParsing);
        runTest("2. DateUtil - Rounding Logic (10:12 -> 10:30)", TestRunner::testDateUtilRounding);

        setupTestData(); // Pre-load specific data for logic tests

        runTest("3. Appointment - Book by ID (Happy Path)", TestRunner::testBookingById);
        runTest("4. Appointment - Book by Name (Resolving Logic)", TestRunner::testBookingByName);
        runTest("5. Smart Match - Auto-assign by Specialization", TestRunner::testAutoMatchSpecialization);
        runTest("6. Conflict Logic - Double Booking Same Slot", TestRunner::testDoubleBooking);
        runTest("7. Availability - Block Weekend Booking", TestRunner::testWeekendBlocking);
        runTest("8. Availability - Block Outside Working Hours", TestRunner::testWorkingHoursBlocking);
        runTest("9. Error Handling - Invalid Patient/Doctor", TestRunner::testInvalidEntities);
        runTest("10. Edge Case - Doctor Fully Booked (30 Days)", TestRunner::testFullyBookedDoctor);
        // Appointment test ends

        System.out.println("\n=== All Tests Completed ===");
    }

//    Appointment test starts
    // --- SETUP ---
    private static void setupTestData() {
        // Doc 1: Regular 9-5
        ds.addDoctor(Doctor.builder().name("Dr. Standard").doctorType(DoctorType.CARDIOLOGIST)
                .availableFrom(LocalTime.of(9,0)).availableTo(LocalTime.of(17,0)).build());

        // Doc 2: Fully Booked Simulation (Workaholic)
        ds.addDoctor(Doctor.builder().name("Dr. Busy").doctorType(DoctorType.DERMATOLOGIST)
                .availableFrom(LocalTime.of(9,0)).availableTo(LocalTime.of(17,0)).build());

        // Patient
        ps.addPatient(Patient.builder().name("Test Patient").mrn("MRN-TEST").gender(GENDER.MALE).build());
    }

    // --- TESTS ---

    private static boolean testDateUtilParsing() {
        String input = "2025-12-09 14:30";
        LocalDateTime ldt = DateUtil.parse(input);
        return ldt != null && ldt.getYear() == 2025 && ldt.getMinute() == 30;
    }

    private static boolean testDateUtilRounding() {
        // Case A: 10:00 -> 10:00
        LocalDateTime t1 = LocalDateTime.of(2025, 12, 10, 10, 0);
        if (!DateUtil.roundToNextSlot(t1).equals(t1)) return false;

        // Case B: 10:12 -> 10:30
        LocalDateTime t2 = LocalDateTime.of(2025, 12, 10, 10, 12);
        LocalDateTime expected = LocalDateTime.of(2025, 12, 10, 10, 30);
        return DateUtil.roundToNextSlot(t2).equals(expected);
    }

    private static boolean testBookingById() {
        try {
            Doctor d = (Doctor) ds.SearchByName("Dr. Standard");
            Appointment appt = as.bookAppointment(d.getId(), "MRN-TEST", null);
            return appt != null && appt.getStatus() == AppointmentStatus.CONFIRMED;
        } catch (Exception e) {
            e.printStackTrace(); return false;
        }
    }

    private static boolean testBookingByName() {
        try {
            // Passing NAME instead of ID
            Appointment appt = as.bookAppointment("Dr. Standard", "MRN-TEST", null);
            return appt != null && appt.getDoctorId() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testAutoMatchSpecialization() {
        try {
            // Should pick Dr. Standard (Cardiologist)
            Appointment appt = as.bookAppointmentByType(DoctorType.CARDIOLOGIST, "MRN-TEST", null);
            Doctor bookedDoc = (Doctor) ds.SearchById(appt.getDoctorId());
            return bookedDoc.getDoctorType() == DoctorType.CARDIOLOGIST;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testDoubleBooking() {
        try {
            Doctor d = (Doctor) ds.SearchByName("Dr. Standard");
            LocalDateTime slot = LocalDateTime.of(2025, 12, 15, 10, 0); // Future Monday

            // 1. Book Slot
            as.bookAppointment(d.getId(), "MRN-TEST", slot);

            // 2. Try booking same slot again
            Appointment appt2 = as.bookAppointment(d.getId(), "MRN-TEST", slot);

            // Should NOT be the same slot (Auto-scheduler should move it to 10:30)
            return !appt2.getTimeSlot().equals(slot) && appt2.getTimeSlot().isAfter(slot);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testWeekendBlocking() {
        try {
            Doctor d = (Doctor) ds.SearchByName("Dr. Standard");
            // Pick a known Saturday
            LocalDateTime sat = LocalDateTime.of(2025, 12, 13, 10, 0);

            Appointment appt = as.bookAppointment(d.getId(), "MRN-TEST", sat);
            // Result should NOT be Saturday (Auto-scheduler moves it to Monday)
            return appt.getTimeSlot().getDayOfWeek() != DayOfWeek.SATURDAY;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testWorkingHoursBlocking() {
        try {
            Doctor d = (Doctor) ds.SearchByName("Dr. Standard");
            // Pick 8:00 AM (Doc starts at 9:00)
            LocalDateTime tooEarly = LocalDateTime.of(2025, 12, 15, 8, 0);

            Appointment appt = as.bookAppointment(d.getId(), "MRN-TEST", tooEarly);
            // Should move to 9:00 AM
            return appt.getTimeSlot().getHour() >= 9;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testInvalidEntities() {
        try {
            as.bookAppointment("BAD_ID", "MRN-TEST", null);
            return false; // Fail if no exception
        } catch (Exception e) {
            return true; // Pass (Expected Exception)
        }
    }

    private static boolean testFullyBookedDoctor() {
        // HACK: Simulate Dr. Busy is unavailable only on Tuesdays (just to test logic)
        // Ideally we would fill 48 * 10 slots, but that's too slow for manual test.
        // Instead, we create a dummy doctor with NO working days.

        Doctor lazyDoc = Doctor.builder().name("Dr. Lazy").doctorType(DoctorType.DENTIST)
                .availableDays(Collections.emptyList()) // No working days
                .build();
        ds.addDoctor(lazyDoc);

        try {
            as.bookAppointment(lazyDoc.getId(), "MRN-TEST", null);
            return false; // Should fail as he never works
        } catch (Exception e) {
            System.out.print(" (Expected: " + e.getMessage() + ") ");
            return true;
        }
    }

    // --- HELPER ---
    private static void runTest(String name, TestSupplier test) {
        System.out.print("TEST: " + name + " ... ");
        try {
            if (test.run()) System.out.println("PASS");
            else System.out.println("FAIL");
        } catch (Exception e) {
            System.out.println("ERROR (" + e.getMessage() + ")");
        }
    }
//    Appointment Test ends

    @FunctionalInterface interface TestSupplier { boolean run(); }
}
