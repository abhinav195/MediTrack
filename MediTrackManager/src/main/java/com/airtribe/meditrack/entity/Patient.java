package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.GENDER;
import com.airtribe.meditrack.util.IdGenerator;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Patient extends Person {

    @Builder.Default
    private String mrn= IdGenerator.generateMRN();
    private String emergencyContact;
    private String bloodGroup;
    private List<String> knownAllergies;
    private List<String> chronicConditions;
    private List<String> currentMedications;

    public Patient(String name, int age, String address, String contactNumber, String email, GENDER gender,
                   String bloodGroup, String emergencyContact) {
        this.setId(IdGenerator.generateId());
        this.setName(name);
        this.setAge(age);
        this.setAddress(address);
        this.setContactNumber(contactNumber);
        this.setEmail(email);
        this.setGender(gender);
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
        this.knownAllergies = new ArrayList<>();
        this.chronicConditions = new ArrayList<>();
        this.currentMedications = new ArrayList<>();
    }

    public Patient(Patient patient) {
        this.setId(IdGenerator.generateId()); // New ID
        this.setName(patient.getName());
        this.setAge(patient.getAge());
        this.setAddress(patient.getAddress());
        this.setContactNumber(patient.getContactNumber());
        this.setEmail(patient.getEmail());
        this.setGender(patient.getGender());

        this.mrn = IdGenerator.generateId(); // New MRN
        this.emergencyContact = patient.getEmergencyContact();
        this.bloodGroup = patient.getBloodGroup();


        this.knownAllergies = (patient.getKnownAllergies() != null)
                ? new ArrayList<>(patient.getKnownAllergies()) : new ArrayList<>();
        this.chronicConditions = (patient.getChronicConditions() != null)
                ? new ArrayList<>(patient.getChronicConditions()) : new ArrayList<>();
        this.currentMedications = (patient.getCurrentMedications() != null)
                ? new ArrayList<>(patient.getCurrentMedications()) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Patient [ID=" + getId() + ", Name=" + getName() + ", MRN=" + mrn + "]";
    }
}
