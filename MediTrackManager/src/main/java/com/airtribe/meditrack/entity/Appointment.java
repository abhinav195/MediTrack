package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.util.IdGenerator;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment implements Serializable {
    @lombok.Builder.Default
    private String appointmentId = IdGenerator.generateId();

    private String doctorId;
    private String patientId;
    private LocalDateTime timeSlot;

    @lombok.Builder.Default
    private AppointmentStatus status = AppointmentStatus.CONFIRMED;

    private String consultationType; // e.g. "Consultation", "Follow-up"

    @Override
    public String toString() {
        return "Appointment [ID=" + appointmentId + ", Status=" + status +
                ", DoctorID=" + doctorId + ", PatientID=" + patientId + "]";
    }
}
