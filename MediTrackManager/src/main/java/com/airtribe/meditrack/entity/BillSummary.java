package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
    private final String doctorName;
    private final PaymentType paymentType;

    public void printBillSummary() {
        String divider = "----------------------------------------";

        String summary =
                divider + "\n" +
                        "              BILL SUMMARY\n" +
                        divider + "\n" +
                        " Bill ID       : " + billId + "\n" +
                        " Patient Name  : " + patientName + "\n" +
                        "doctor Name    : " + doctorName + "\n" +
                        " Generated On  : " + generatedDate + "\n" +
                        "Payment Type: "    + paymentType + "\n" +
                        " Total Amount  : ₹ " + String.format("%.2f", totalAmount) + "\n" +
                        divider;

        System.out.println(summary);
    }
}
