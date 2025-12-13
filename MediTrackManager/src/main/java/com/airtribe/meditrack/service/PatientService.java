package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Person;
import com.airtribe.meditrack.enums.GENDER;
import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.PatientNotFoundException;
import com.airtribe.meditrack.interfaces.Searchable;

import java.util.HashSet;
import java.util.Scanner;

public class PatientService implements Searchable {
    HashSet<Patient> Patients;
    public PatientService() {
        Patients = new HashSet<>();
    }
    public HashSet<Patient> getPatients() {
        return Patients;
    }
    public void setPatients(HashSet<Patient> Patients) {
        if (Patients != null) {
            for (Patient p : Patients) {
                Validator.validatePatient(p);
            }
        }
        this.Patients = Patients;
    }
    public void addPatient(Patient Patient) {
        Validator.validatePatient(Patient);

        Patients.add(Patient);
    }
    public void deletePatient(String MRN) throws PatientNotFoundException {

        Person p = SearchById(MRN);
        if (p != null) {
            Patients.remove(p);
        } else {
            throw new PatientNotFoundException("Patient with MRN: " + MRN + " does not exist");
        }
    }
    public void updatePatient(String MRN, String PatientObject) throws PatientNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Patient updatedPatient = mapper.readValue(PatientObject, Patient.class);
        Person patientInstance = SearchById(MRN);
        if (patientInstance instanceof Patient patient) {
            Validator.validatePatient(updatedPatient);

            patient.setName(updatedPatient.getName());
            patient.setAge(updatedPatient.getAge());
            patient.setAddress(updatedPatient.getAddress());
            patient.setContactNumber(updatedPatient.getContactNumber());

            patient.setBloodGroup(updatedPatient.getBloodGroup());
            patient.setKnownAllergies(updatedPatient.getKnownAllergies());
            patient.setChronicConditions(updatedPatient.getChronicConditions());
            patient.setCurrentMedications(updatedPatient.getCurrentMedications());
        }
    }
    @Override
    public Person SearchByName(String name) throws PatientNotFoundException {
        for (Patient patient : Patients) {
            if (patient.getName().equalsIgnoreCase(name)) {
                return patient;
            }
        }
        throw new PatientNotFoundException("Patient with name: " + name + " does not exist");
    }
    @Override
    public Person SearchByAge(int age) throws PatientNotFoundException {
        for (Patient patient : Patients) {
            if (patient.getAge() == age) {
                return patient;
            }
        }
        throw new PatientNotFoundException("Patient with age: " + age + " does not exist");
    }
    @Override
    public Person SearchById(String mrnId) throws PatientNotFoundException {
        if (!Validator.isValidMRN(mrnId)) {
            throw new InvalidDataException("Invalid MRN format. MRN must be 6–12 alphanumeric characters.");
        }
        for (Patient patient : Patients) {
            if (patient.getMrn().equalsIgnoreCase(mrnId)) {
                return patient;
            }
        }
        throw new PatientNotFoundException("Patient with MRN: " + mrnId + " does not exist");
    }


    public Patient createPatientInteractive(Scanner scanner) {
        System.out.print("Enter Patient Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Age: ");
        int age = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine();

        System.out.print("Enter Contact Number: ");
        String contactNumber = scanner.nextLine();

        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        System.out.print("Enter Gender (MALE/FEMALE): ");
        String genderStr = scanner.nextLine().toUpperCase();
        GENDER gender = GENDER.valueOf(genderStr);

        System.out.print("Enter Blood Group (e.g., O+, A-, B+, AB-): ");
        String bloodGroup = scanner.nextLine();

        System.out.print("Enter Emergency Contact: ");
        String emergencyContact = scanner.nextLine();

        // Create Patient using Lombok builder
        Patient patient = Patient.builder()
                .name(name)
                .age(age)
                .address(address)
                .contactNumber(contactNumber)
                .email(email)
                .gender(gender)
                .bloodGroup(bloodGroup)
                .emergencyContact(emergencyContact)
                .build();

        // Add to service
        this.addPatient(patient);

        return patient;
    }
}
