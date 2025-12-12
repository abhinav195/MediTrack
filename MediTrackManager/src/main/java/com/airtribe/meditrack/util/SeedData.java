package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.DoctorType;
// import com.airtribe.meditrack.enums.GENDER; // Use String if your Person class uses String
import com.airtribe.meditrack.enums.GENDER;
import com.airtribe.meditrack.service.DoctorService;
import com.airtribe.meditrack.service.PatientService;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SeedData {

    public static void load(DoctorService ds, PatientService ps) {
        System.out.println("--- Seeding Data ---");

        List<DayOfWeek> weekdays = Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

        // --- DOCTORS ---

        // 1. Cardiologist
        Doctor d1 = Doctor.builder()
                .id("DOC001") // 6 chars, alphanumeric
                .name("Dr. Strange")
                .age(45)
                .gender(GENDER.MALE)
                .contactNumber("1234567890")
                .email("strange@med.com")
                .address("177A Bleecker St")
                .doctorType(DoctorType.CARDIOLOGIST)
                .qualification("MD, PhD")
                .yearsOfExperience(15)
                .opdRoom("101")
                .availableFrom(LocalTime.of(9, 0))
                .availableTo(LocalTime.of(17, 0))
                .availableDays(weekdays)
                .build();
        ds.addDoctor(d1);

        // 2. Neurologist
        Doctor d2 = Doctor.builder()
                .id("DOC002")
                .name("Dr. House")
                .age(50)
                .gender(GENDER.MALE)
                .contactNumber("1234567891")
                .email("house@med.com")
                .address("221B Baker St")
                .doctorType(DoctorType.NEUROLOGIST)
                .qualification("MD")
                .yearsOfExperience(20)
                .opdRoom("102")
                .availableFrom(LocalTime.of(14, 0))
                .availableTo(LocalTime.of(22, 0))
                .availableDays(weekdays)
                .build();
        ds.addDoctor(d2);

        // 3. Dentist
        Doctor d3 = Doctor.builder()
                .id("DOC003")
                .name("Dr. Crentist")
                .age(35)
                .gender(GENDER.MALE)
                .contactNumber("1234567892")
                .email("crentist@med.com")
                .address("Scranton, PA")
                .doctorType(DoctorType.DENTIST)
                .qualification("DDS")
                .yearsOfExperience(8)
                .opdRoom("103")
                .availableFrom(LocalTime.of(8, 0))
                .availableTo(LocalTime.of(12, 0))
                .availableDays(weekdays)
                .build();
        ds.addDoctor(d3);

        System.out.println("Seeded Doctors (IDs: DOC001, DOC002, DOC003).");

        // --- PATIENTS ---

        // NOTE: MRN must be 6-12 chars, alphanumeric only (No hyphens!)

        Patient p1 = Patient.builder()
                .mrn("MRN001") // Fixed: Removed hyphen
                .name("Tony Stark")
                .age(40)
                .gender(GENDER.MALE)
                .contactNumber("9876543210")
                .email("ironman@avengers.com")
                .address("Malibu Point")
                .emergencyContact("1122334455")
                .bloodGroup("A+") // Must match: A+, A-, B+, B-, AB+, AB-, O+, O-
                .knownAllergies(Collections.singletonList("Shrapnel"))
                .chronicConditions(Collections.singletonList("Anxiety"))
                .currentMedications(Collections.singletonList("None"))
                .build();
        ps.addPatient(p1);

        Patient p2 = Patient.builder()
                .mrn("MRN002") // Fixed: Removed hyphen
                .name("Natasha Romanoff")
                .age(32)
                .gender(GENDER.FEMALE)
                .contactNumber("9876543211")
                .email("widow@avengers.com")
                .address("Unknown")
                .emergencyContact("1122334466")
                .bloodGroup("O+")
                .knownAllergies(Collections.singletonList("None"))
                .chronicConditions(Collections.singletonList("None"))
                .currentMedications(Collections.singletonList("None"))
                .build();
        ps.addPatient(p2);

        System.out.println("Seeded Patients (MRNs: MRN001, MRN002).");
        System.out.println("--------------------");
    }
}
