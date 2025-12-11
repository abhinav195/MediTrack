package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Person;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.DoctorNotFoundException;
import com.airtribe.meditrack.exception.PersonNotFoundException;
import com.airtribe.meditrack.util.DateUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    private List<Appointment> appointments;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public AppointmentService(DoctorService doctorService, PatientService patientService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.appointments  = new ArrayList<>();
    }

//    Path 1: Book with specific Doctor (ID) and name
    public Appointment bookAppointment(String doctorIdentifier, String patientId, LocalDateTime requestedTime) throws Exception {
        // 1. Resolve Doctor (ID vs Name)
        Doctor doctor = resolveDoctor(doctorIdentifier);
        if (doctor == null) {
            throw new PersonNotFoundException("Doctor not found with ID or Name: " + doctorIdentifier);
        }

        // 2. Validate Patient
        if (patientService.SearchById(patientId) == null) {
            throw new PersonNotFoundException("Patient not found: " + patientId);
        }

        // 3. Find Slot
        LocalDateTime finalSlot = resolveTimeSlot(doctor.getId(), requestedTime);
        if (finalSlot == null) throw new Exception("No slots available for Doctor " + doctor.getName());

        // 4. Create
        return createAndSaveAppointment(doctor.getId(), patientId, finalSlot);
    }

    // Helper to find doctor by ID first, then Name
    private Doctor resolveDoctor(String identifier) throws DoctorNotFoundException {
        // Try ID
        try {
            Person p = doctorService.SearchById(identifier);
            if (p instanceof Doctor) return (Doctor) p;
        } catch (Exception e) {
            throw new DoctorNotFoundException("Doctor not found with ID: " + identifier + " try with name now.");
        }
        // Try Name
        try {
            Person p = doctorService.SearchByName(identifier);
            if (p instanceof Doctor) return (Doctor) p;
        } catch (Exception e) {
            throw new DoctorNotFoundException("Doctor not found with Name: " + identifier);
        }

        return null;
    }

//  Path 2: Book by Doctor Type (Finds earliest available doctor)
    public Appointment bookAppointmentByType(DoctorType type, String patientId, LocalDateTime requestedTime) throws Exception {

        if (patientService.SearchById(patientId) == null) throw new PersonNotFoundException("Patient not found: " + patientId);

        // 1. Get Candidates
        List<Doctor> candidates = doctorService.getDoctorsByType(type);
        if (candidates.isEmpty()) throw new Exception("No doctors found for specialization: " + type);

        // 2. Earliest Slot
        Doctor bestDoctor = null;
        LocalDateTime bestSlot = null;

        // If user didn't request a time, start searching from NOW
        LocalDateTime searchStart = (requestedTime != null) ? requestedTime : LocalDateTime.now();

        for (Doctor doc : candidates) {
            LocalDateTime slot = findNextAvailableSlot(doc.getId(), searchStart);
            if (slot != null) {
                // If we found a slot, checks if it's better (sooner) than current best
                if (bestSlot == null || slot.isBefore(bestSlot)) {
                    bestSlot = slot;
                    bestDoctor = doc;
                }
            }
        }

        if (bestDoctor == null) throw new Exception("No available slots found for any " + type);

        System.out.println("Auto-Matched Doctor: " + bestDoctor.getName());

        // Create
        return createAndSaveAppointment(bestDoctor.getId(), patientId, bestSlot);
    }

    // --- Helpers ---
    private Appointment createAndSaveAppointment(String docId, String patId, LocalDateTime slot) {
        Appointment appointment = Appointment.builder()
                .doctorId(docId)
                .patientId(patId)
                .timeSlot(slot)
                .status(AppointmentStatus.CONFIRMED)
                .build();
        appointments.add(appointment);
        System.out.println("Appointment Booked: " + appointment.getAppointmentId() + " at " + DateUtil.format(slot));
        return appointment;
    }

    private LocalDateTime resolveTimeSlot(String doctorId, LocalDateTime requestedTime) {
        if (requestedTime != null) {
            if (isSlotAvailable(doctorId, requestedTime)) return requestedTime;
            System.out.println("Requested slot unavailable. Searching next...");
            return findNextAvailableSlot(doctorId, requestedTime);
        }
        return findNextAvailableSlot(doctorId, LocalDateTime.now());
    }

    private LocalDateTime findNextAvailableSlot(String doctorId, LocalDateTime fromTime) {
        if (fromTime.isBefore(LocalDateTime.now())) fromTime = LocalDateTime.now();
        LocalDateTime candidate = DateUtil.roundToNextSlot(fromTime);

        for (int i = 0; i < 48 * 10; i++) { // Search 10 days
            if (isSlotAvailable(doctorId, candidate)) return candidate;
            candidate = candidate.plusMinutes(DateUtil.SLOT_DURATION_MINUTES);
        }
        return null;
    }

    private boolean isSlotAvailable(String doctorId, LocalDateTime slot) {
        if (!doctorService.isDoctorWorking(doctorId, slot)) return false;

        return appointments.stream()
                .filter(a -> a.getDoctorId().equals(doctorId))
                .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED)
                .noneMatch(a -> a.getTimeSlot().isEqual(slot));
    }

    public List<Appointment> getAllAppointments() { return new ArrayList<>(appointments); }
    public Appointment getAppointmentById(String id) throws AppointmentNotFoundException {
        return appointments.stream()
                .filter(a->a.getAppointmentId().equals(id)).
                findFirst().
                orElseThrow(() -> new AppointmentNotFoundException("Appointment with ID " + id + " not found."));
    }
}
