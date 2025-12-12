package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.enums.GENDER;
import com.airtribe.meditrack.service.AppointmentService;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashSet;

public class SeedData {

    public static void load(DoctorService ds, PatientService ps, AppointmentService as) throws IOException {
        System.out.println("--- Seeding Data ---");
        HashSet<Doctor> doctors = DataStore.loadDoctors();
        HashSet<Patient> patients = DataStore.loadPatients();
        HashSet<Appointment> appointments = DataStore.loadAppointments();

        if(!doctors.isEmpty()){
            ds.setDoctors(doctors);
            if(!patients.isEmpty()){
                ps.setPatients(patients);
                if(!appointments.isEmpty()){
                    as.setAppointments(appointments);
                }
            }
            return;
        }

        // Proceed with data creation if no data found in CSVs


        // --- DOCTORS ---

        // 1. Cardiologist (Standard Hours)
        Doctor d1 = Doctor.builder()
                .name("Dr. Strange")
                .doctorType(DoctorType.CARDIOLOGIST) // Assuming you have this enum
                .qualification("MD, PhD")
                .availableFrom(LocalTime.of(9, 0))
                .availableTo(LocalTime.of(17, 0)) // 5 PM
                .build();
        ds.addDoctor(d1);

        // 2. Neurologist (Late Shift)
        Doctor d2 = Doctor.builder()
                .name("Dr. House")
                .doctorType(DoctorType.NEUROLOGIST)
                .qualification("MD")
                .availableFrom(LocalTime.of(14, 0)) // 2 PM
                .availableTo(LocalTime.of(22, 0)) // 10 PM
                .build();
        ds.addDoctor(d2);

        // 3. Dentist (Morning Only)
        Doctor d3 = Doctor.builder()
                .name("Dr. Crentist")
                .doctorType(DoctorType.DENTIST)
                .availableFrom(LocalTime.of(8, 0))
                .availableTo(LocalTime.of(12, 0))
                .build();
        ds.addDoctor(d3);

        // 4. Another Neurologist (For conflict testing/auto-match)
        Doctor d4 = Doctor.builder()
                .name("Dr. Shepherd")
                .doctorType(DoctorType.NEUROLOGIST)
                .availableFrom(LocalTime.of(9, 0))
                .availableTo(LocalTime.of(17, 0))
                .build();
        ds.addDoctor(d4);

        System.out.println("Seeded 4 Doctors.");

        // --- PATIENTS ---

        Patient p1 = Patient.builder()
                .name("Tony Stark")
                .email("ironman@avengers.com")
                .gender(GENDER.MALE)
                .mrn("MRN-001") // Custom readable MRN for easier testing
                .build();
        ps.addPatient(p1);

        Patient p2 = Patient.builder()
                .name("Natasha Romanoff")
                .email("widow@avengers.com")
                .gender(GENDER.FEMALE)
                .mrn("MRN-002")
                .build();
        ps.addPatient(p2);

        System.out.println("Seeded 2 Patients. MRNs: MRN-001, MRN-002");
        System.out.println("--------------------");
    }
}
