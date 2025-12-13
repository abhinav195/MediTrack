package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.PaymentType;
import com.airtribe.meditrack.exception.DoctorNotFoundException;
import com.airtribe.meditrack.exception.PatientNotFoundException;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;
import com.airtribe.meditrack.util.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    @lombok.Builder.Default
    private String billId = IdGenerator.generateId();

    private Appointment appointment;
    private double consultationFee;
    private double medicationCost;
    private double taxRate;
    private double totalAmount;
    @lombok.Builder.Default
    private boolean isPaid = false;
    @lombok.Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
    private PaymentType paymentType;
    private DoctorService doctorService;
    private PatientService patientService;
    private AppointmentService appointmentService;
    String pName = "Unknown";
    String dName = "Unknown";

    public Bill update_bill() {

        this.totalAmount = (this.consultationFee + this.medicationCost)+
                (this.consultationFee + this.medicationCost) * this.taxRate;

        this.isPaid=false;
        return this;
    }

    public Bill processPayment() {
        this.isPaid = true;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public BillSummary generateBillSummary() {
        String pName = "Unknown";
        String dName = "Unknown";

        // In Bill.java
        try {
            if (this.patientService != null && this.appointment != null) {
                Person patient = this.patientService.SearchById(this.appointment.getPatientId());
                if (patient == null) {
                    throw new PatientNotFoundException("Patient not found with ID: " +
                            (this.appointment != null ? this.appointment.getPatientId() : "null"));
                }
                pName = patient.getName();
            }
        }catch (PatientNotFoundException e) {
            // Log the exception and continue with default "Unknown" name
            System.err.println("Warning: " + e.getMessage());
            // pName remains "Unknown"
        }

        try {
            if (this.doctorService != null && this.appointment != null) {
                Person doctor = this.doctorService.SearchById(this.appointment.getDoctorId());
                if (doctor == null) {
                    throw new DoctorNotFoundException("Doctor not found with ID: " +
                            (this.appointment != null ? this.appointment.getDoctorId() : "null"));
                }
                dName = doctor.getName();
            }
        }catch (DoctorNotFoundException e) {
            // Log the exception and continue with default "Unknown" name
            System.err.println("Warning: " + e.getMessage());
            // dName remains "Unknown"
        }

        return BillSummary.builder()
                .billId(this.billId)
                .appointmentId(this.appointment != null ? this.appointment.getAppointmentId() : "N/A")
                .consultationFee(this.consultationFee)
                .medicationCost(this.medicationCost)
                .taxRate(this.taxRate)
                .totalAmount(this.totalAmount)
                .isPaid(this.isPaid)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .patientName(pName)
                .doctorName(dName)
                .paymentType(this.paymentType)
                .build();
    }
}
