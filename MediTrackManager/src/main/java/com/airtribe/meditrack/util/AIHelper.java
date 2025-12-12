package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;

import java.time.LocalDateTime;
import java.util.List;

public class AIHelper {
    public static Doctor suggestDoctor(String symptom, DoctorService doctorService) {
        DoctorType type = mapSymptomToType(symptom);
        List<Doctor> doctors = doctorService.getDoctorsByType(type);
        return doctors.isEmpty() ? null : doctors.get(0);
    }

    private static DoctorType mapSymptomToType(String symptom) {
        symptom = symptom.toLowerCase();

        if (symptom.contains("heart") || symptom.contains("cardio")) return DoctorType.CARDIOLOGIST;
        if (symptom.contains("brain") || symptom.contains("neuro")) return DoctorType.NEUROLOGIST;
        if (symptom.contains("tooth") || symptom.contains("dental")) return DoctorType.DENTIST;
        if (symptom.contains("skin") || symptom.contains("rash")) return DoctorType.DERMATOLOGIST;
        if (symptom.contains("stomach") || symptom.contains("digestion") || symptom.contains("gut")) return DoctorType.GASTROENTEROLOGIST;
        if (symptom.contains("lung") || symptom.contains("breath") || symptom.contains("respiratory")) return DoctorType.PULMONOLOGIST;
        if (symptom.contains("ear") || symptom.contains("nose") || symptom.contains("throat")) return DoctorType.OTOLARYNGOLOGIST;
        if (symptom.contains("urine") || symptom.contains("kidney") || symptom.contains("bladder")) return DoctorType.UROLOGIST;
        if (symptom.contains("jaw") || symptom.contains("face") || symptom.contains("maxillofacial")) return DoctorType.MAXILLOFACIAL;
        if (symptom.contains("joint") || symptom.contains("arthritis") || symptom.contains("rheumatism")) return DoctorType.RHEUMATOLOGIST;
        if (symptom.contains("blood") || symptom.contains("lab") || symptom.contains("test")) return DoctorType.PATHOLOGIST;

        return DoctorType.GENERAL_PRACTITIONER; // default fallback
    }

    public static LocalDateTime suggestEarliestSlot(String doctorId, AppointmentService appointmentService) {
        return appointmentService.bookAppointmentByAI(doctorId);
    }
}
