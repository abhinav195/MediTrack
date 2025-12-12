package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;

public class BillingService {
    Appointment appointment;
    Bill bill;

    // looks like this might be needed for generating a bill using the appointment,
    // since current appointment is not composed of doctor and patient instance and doesn't have methods to
    // access required details to be added to the bill, thus kept this class can be deleted if not required.

}
