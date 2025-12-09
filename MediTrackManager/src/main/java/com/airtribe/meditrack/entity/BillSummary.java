package com.airtribe.meditrack.entity;

//Immutable class representing the final summary of a bill.
public final class BillSummary {

    private final String billId;
    private final double totalAmount;
    private final String generatedDate;
    private final String patientName;

    public BillSummary(String billId, double totalAmount, String generatedDate, String patientName) {
        this.billId = billId;
        this.totalAmount = totalAmount;
        this.generatedDate = generatedDate;
        this.patientName = patientName;
    }

    public String getBillId() {
        return billId;
    }
    public double getTotalAmount() {
        return totalAmount;
    }
    public String getGeneratedDate() {
        return generatedDate;
    }
    public String getPatientName() {
        return patientName;
    }

    @Override
    public String toString() {
        return "BillSummary [ID=" + billId + ", Total=" + totalAmount +
                ", Date=" + generatedDate + ", Patient=" + patientName + "]";
    }
}
