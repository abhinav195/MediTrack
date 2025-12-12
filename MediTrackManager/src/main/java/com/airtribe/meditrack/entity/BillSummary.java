package com.airtribe.meditrack.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


//Immutable class representing the final summary of a bill.
@Data
@Builder
@AllArgsConstructor
public final class BillSummary {
    private final String billId;
    private final String appointmentId;
    private final String patientName;
    private final double consultationFee;
    private final double medicationCost;
    private final double taxRate;
    private final double totalAmount;
    private final boolean isPaid;
    private final LocalDateTime generatedDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // Custom toString for nice printing
    @Override
    public String toString() {
        return String.format(
                "=== BILL SUMMARY ===\n" +
                        "ID: %s\n" +
                        "Patient: %s\n" +
                        "Appt ID: %s\n" +
                        "Fees: %.2f | Meds: %.2f | Tax: %.1f%%\n" +
                        "TOTAL: %.2f\n" +
                        "Status: %s\n" +
                        "Date: %s",
                billId, patientName, appointmentId, consultationFee, medicationCost,
                taxRate * 100, totalAmount, isPaid ? "PAID" : "UNPAID", generatedDate
        );
    }
}
