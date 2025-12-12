package com.airtribe.meditrack.entity;

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
//        return BillSummary.builder()
//                .billId(this.billId)
//                .appointmentId(this.appointment.getAppointmentId())
//                .consultationFee(this.consultationFee)
//                .medicationCost(this.medicationCost)
//                .taxRate(this.taxRate)
//                .totalAmount(this.totalAmount)
//                .isPaid(this.isPaid)
//                .createdAt(this.createdAt)
//                .updatedAt(this.updatedAt)
//                .build();
        return null;
    }
}
