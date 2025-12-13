package com.airtribe.meditrack.interfaces;

import com.airtribe.meditrack.entity.BillSummary;
import com.airtribe.meditrack.exception.AppointmentNotFoundException;
import com.airtribe.meditrack.exception.DoctorNotFoundException;
import com.airtribe.meditrack.exception.PatientNotFoundException;

public interface Payable {
    BillSummary generateBillSummary() throws AppointmentNotFoundException, DoctorNotFoundException,
            PatientNotFoundException;

    double calculateTotal() throws AppointmentNotFoundException, DoctorNotFoundException;
}