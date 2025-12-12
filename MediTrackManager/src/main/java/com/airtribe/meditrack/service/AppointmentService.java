package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.entity.Person;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.DoctorNotFoundException;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.exception.PersonNotFoundException;
import com.airtribe.meditrack.util.AIHelper;
import com.airtribe.meditrack.util.DateUtil;
import com.airtribe.meditrack.util.Validator;

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
        this.appointments = new ArrayList<>();
    }
    public Appointment bookAppointment(String doctorIdentifierOrSymptom, String patientId, LocalDateTime requestedTime) throws Exception {
        // Validate patientId
        if (!Validator.isValidId(patientId))
            throw new IllegalArgumentException("Patient ID cannot be null or empty");
        Doctor doctor = resolveDoctor(doctorIdentifierOrSymptom);
        if (doctor == null) {
            doctor = AIHelper.suggestDoctor(doctorIdentifierOrSymptom, doctorService);
            if (doctor == null)
                throw new DoctorNotFoundException("No doctor found for symptom: " + doctorIdentifierOrSymptom);
        }
        // Validate doctor object
        Validator.validateDoctor(doctor);
        // Validate patient object
        Person person = patientService.SearchById(patientId);
        if (!(person instanceof Patient patient))
            throw new PersonNotFoundException("Patient not found: " + patientId);
        Validator.validatePatient(patient);
        // Resolve appointment slot
        LocalDateTime finalSlot = (requestedTime != null) ?
                resolveTimeSlot(doctor.getId(), requestedTime) :
                AIHelper.suggestEarliestSlot(doctor.getId(), this);
        if (finalSlot == null)
            throw new AppointmentNotFoundException("No available slot for Doctor: " + doctor.getName());
        Appointment appointment = createAndSaveAppointment(doctor.getId(), patientId, finalSlot);
        Validator.validateAppointment(appointment);
        return appointment;
    }
    // Helper to find doctor by ID first, then Name
    private Doctor resolveDoctor(String identifier) throws DoctorNotFoundException {
        // Try ID
        try {
            Person p = doctorService.SearchById(identifier);
            if (p instanceof Doctor) return (Doctor) p;
        } catch (Exception e) {
            // proceed to try with name
        }
        // Try Name
        try {
            Person p = doctorService.SearchByName(identifier);
            if (p instanceof Doctor) return (Doctor) p;
        } catch (Exception e) {
            // proceed to try with symptom
        }
        return null;
    }
    //  Path 2: Book by Doctor Type (Finds earliest available doctor)
    public Appointment bookAppointmentByType(DoctorType type, String patientId, LocalDateTime requestedTime) throws Exception {
        if (patientService.SearchById(patientId) == null)
            throw new PersonNotFoundException("Patient not found: " + patientId);
        // 1. Get Candidates
        List<Doctor> candidates = doctorService.getDoctorsByType(type);
        if (candidates.isEmpty()) throw new Exception("No doctors found for specialization: " + type);
        // 2. Earliest Slot
        Doctor bestDoctor = null;
        LocalDateTime bestSlot = null;
        // If user didn't request a time, start searching from NOW
        LocalDateTime searchStart = (requestedTime != null) ? requestedTime : LocalDateTime.now();
        for (Doctor doc : candidates) {
            Validator.validateDoctor(doc);
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
    private Appointment createAndSaveAppointment(String docId, String patId, LocalDateTime slot) throws PersonNotFoundException {
        if (!Validator.isValidId(docId)) throw new DoctorNotFoundException("Doctor ID invalid");
        if (!Validator.isValidId(patId)) throw new PersonNotFoundException("Patient ID invalid");
        Appointment appointment = Appointment.builder()
                .doctorId(docId)
                .patientId(patId)
                .timeSlot(slot)
                .status(AppointmentStatus.CONFIRMED)
                .build();
        Validator.validateAppointment(appointment);
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
    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }
    public Appointment getAppointmentById(String id) throws AppointmentNotFoundException {
        if (!Validator.isValidId(id))
            throw new InvalidDataException("Appointment ID cannot be null or empty");
        return appointments.stream()
                .filter(a -> a.getAppointmentId().equals(id)).
                findFirst().
                orElseThrow(() -> new AppointmentNotFoundException("Appointment with ID " + id + " not found."));
    }
    public LocalDateTime bookAppointmentByAI(String doctorId) {
        LocalDateTime slot = findNextAvailableSlot(doctorId, LocalDateTime.now());
        if (!Validator.isValidAppointmentSlot(slot)) {
            throw new InvalidDataException("AI-suggested appointment slot is invalid or in the past");
        }
        return slot;
    }
}
