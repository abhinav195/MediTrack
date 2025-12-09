package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.exception.DoctorNotFoundException;
import com.airtribe.meditrack.exception.PatientNotFoundException;
import com.airtribe.meditrack.exception.PersonNotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    private List<Appointment> appointments;
    private final DoctorService doctorService;
    private final PatientService patientService;

    // Configuration
    private static final LocalTime START_TIME = LocalTime.of(9, 0);
    private static final LocalTime END_TIME = LocalTime.of(19, 0);
    private static final int SLOT_DURATION_MINUTES = 30;

    public AppointmentService(DoctorService doctorService, PatientService patientService) {
        appointments = new ArrayList<>();
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public Appointment bookAppointment(String doctorId, String patientId, LocalDateTime requestedTime, String consultationType) throws Exception {
        // 1. Validation
        if (doctorService.SearchById(doctorId) == null) throw new DoctorNotFoundException("Doctor not found: " + doctorId);
        if (patientService.SearchById(patientId) == null) throw new PatientNotFoundException("Patient not found: " + patientId);

        // 2. Determine Final Time Slot
        LocalDateTime finalSlot;

        if (requestedTime != null) {
            // User requested a specific time
            if (isSlotAvailable(doctorId, requestedTime)) {
                finalSlot = requestedTime;
            } else {
                System.out.println("Requested slot " + requestedTime + " is unavailable or invalid. Finding closest next slot...");
                finalSlot = findNextAvailableSlot(doctorId, requestedTime);
            }
        } else {
            // Auto-assign: Start searching from NOW (or next working start)
            finalSlot = findNextAvailableSlot(doctorId, LocalDateTime.now());
        }

        if (finalSlot == null) {
            throw new Exception("No available slots found for Doctor in the near future.");
        }

        // 3. Create Appointment
        Appointment appointment = Appointment.builder()
                .doctorId(doctorId)
                .patientId(patientId)
                .timeSlot(finalSlot)
                .consultationType(consultationType)
                .status(AppointmentStatus.CONFIRMED)
                .build();

        appointments.add(appointment);
        System.out.println("Appointment booked: " + appointment.getAppointmentId() + " at " + finalSlot);
        return appointment;
    }

//    Checks if a slot is within working hours (Mon-Fri, 9-7) AND not overlapping with existing appointments.
    private boolean isSlotAvailable(String doctorId, LocalDateTime slot) {
        // A. Check Working Hours / Days
        DayOfWeek day = slot.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) return false;

        LocalTime time = slot.toLocalTime();
        if (time.isBefore(START_TIME) || time.isAfter(END_TIME.minusMinutes(SLOT_DURATION_MINUTES))) return false;

        // B. Check Overlap with existing CONFIRMED appointments - assuming fixed 30min slots for simplicity
        return appointments.stream()
                .filter(a -> a.getDoctorId().equals(doctorId))
                .filter(a -> a.getStatus() == AppointmentStatus.CONFIRMED)
                .noneMatch(a -> a.getTimeSlot().isEqual(slot));
    }

//  Finds the next available 30-min slot.
    private LocalDateTime findNextAvailableSlot(String doctorId, LocalDateTime fromTime) {
        if (fromTime.isBefore(LocalDateTime.now())) {
            fromTime = LocalDateTime.now();
        }

        LocalDateTime candidate = fromTime.truncatedTo(ChronoUnit.MINUTES);

        int minute = candidate.getMinute();
        if (minute > 0 && minute < 30) candidate = candidate.withMinute(30);
        else if (minute > 30) candidate = candidate.plusHours(1).withMinute(0);

        // Searching for next 10 days
        for (int i = 0; i < 48 * 10; i++) { // 48 slots per day * 10 days
            if (isSlotAvailable(doctorId, candidate)) {
                return candidate;
            }
            candidate = candidate.plusMinutes(SLOT_DURATION_MINUTES);
        }
        return null; // Should not happen unless completely booked for 10 days
    }


    public void cancelAppointment(String appointmentId) throws Exception {
        Appointment appointment = getAppointmentById(appointmentId);
        appointment.setStatus(AppointmentStatus.CANCELLED);
    }

    public Appointment getAppointmentById(String id) throws Exception {
        return appointments
                .stream()
                .filter(
                        a -> a.getAppointmentId()
                                .equals(id)
                ).findFirst()
                .orElse(null);
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }
}
