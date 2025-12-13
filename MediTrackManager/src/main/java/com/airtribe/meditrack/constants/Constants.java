package com.airtribe.meditrack.constants;

import java.io.File;

import static com.airtribe.meditrack.util.CSVUtil.getResourceDirectory;

public class Constants {
    // In Constants.java
    private static final String RESOURCE_DIR = getResourceDirectory(); // Now returns ".../MediTrackManager/data"
    public static final String DOCTOR_CSV = RESOURCE_DIR + File.separator + "doctor_data.csv";
    public static final String PATIENT_CSV = RESOURCE_DIR + File.separator + "patient_data.csv";
    public static final String APPOINTMENT_CSV = RESOURCE_DIR + File.separator + "appointment_data.csv";
// verify if you have bill_data.csv defined here too


    public static final double TAX_RATE = 0.18;

    public static final int SLOT_DURATION_MINUTES = 30;

    public static final String PATIENT_NOT_FOUND = "Patient not found";
    public static final String DOCTOR_NOT_FOUND = "Doctor not found";

    public static final String DATE_FORMAT_DISPLAY = "E, dd MMM yyyy hh:mm a";
    public static final String DATE_FORMAT_INPUT = "yyyy-MM-dd HH:mm";
}
