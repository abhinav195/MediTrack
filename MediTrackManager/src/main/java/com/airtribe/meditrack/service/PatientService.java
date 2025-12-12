package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Person;
import com.airtribe.meditrack.util.DataStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.exception.PatientNotFoundException;
import com.airtribe.meditrack.interfaces.Searchable;

import java.util.HashSet;

public class PatientService implements Searchable {

    HashSet<Patient> patients;

    public PatientService() {
        patients = new HashSet<>();
    }

    public HashSet<Patient> getPatients() {
        return patients;
    }

    public void setPatients(HashSet<Patient> Patients) {
        this.patients = Patients;
    }

    public void addPatient(Patient Patient) {
        patients.add(Patient);
    }

    public void deletePatient(String MRN) throws PatientNotFoundException {

        Person p = SearchById(MRN);
        if (p != null) {
            patients.remove(p);
        } else {
            throw new PatientNotFoundException("Patient with MRN: " + MRN + " does not exist");
        }
    }

    public void updatePatient(String MRN, String PatientObject) throws PatientNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Patient updatedPatient = mapper.readValue(PatientObject, Patient.class);

        Person patientInstance = SearchById(MRN);

        if (patientInstance instanceof Patient patient) {

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
        for (Patient patient : patients) {
            if (patient.getName().equalsIgnoreCase(name)) {
                return patient;
            }
        }
        throw new PatientNotFoundException("Patient with name: " + name + " does not exist");
    }

    @Override
    public Person SearchByAge(int age) throws PatientNotFoundException {
        for (Patient patient : patients) {
            if (patient.getAge() == age) {
                return patient;
            }
        }
        throw new PatientNotFoundException("Patient with age: " + age + " does not exist");
    }

    @Override
    public Person SearchById(String mrnId) throws PatientNotFoundException {
        for (Patient patient : patients) {
            if (patient.getMrn().equalsIgnoreCase(mrnId)) {
                return patient;
            }
        }
        throw new PatientNotFoundException("Patient with MRN: " + mrnId + " does not exist");
    }

    public void persistData() {
        try{
            DataStore<Patient> dataStore = new DataStore<>();
            dataStore.save(patients);
        } catch (Exception e){
            System.out.println("Error persisting patients data: " + e.getMessage());
        }

    }
}
