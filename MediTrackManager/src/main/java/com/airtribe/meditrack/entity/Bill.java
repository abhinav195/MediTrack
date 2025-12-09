package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.util.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    @Builder.Default
    private String billId = IdGenerator.generateId();

    private String appointmentId;
    private double consultationFee;
    private double medicationCost;
    private double taxRate;

    public double calculateTotal() {
        return (consultationFee + medicationCost) * (1 + taxRate);
    }
}
