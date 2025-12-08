package org.meditrack.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.meditrack.app.entity.Patient;
import org.meditrack.app.exceptions.PatientNotFoundException;
import org.meditrack.app.interfaces.Searchable;

import java.util.HashSet;

public class PatientService implements Searchable {

    HashSet<Patient> Patients;
    public PatientService() {
        Patients = new HashSet<>();
    }
    public HashSet<Patient> getPatients() {
        return Patients;
    }
    public void setPatients(HashSet<Patient> Patients) {
        this.Patients = Patients;
    }

    public void addPatient(Patient Patient) {
        Patients.add(Patient);
    }

    public void deletePatient(String MRN) throws PatientNotFoundException {
        try {
            Patients.remove(SearchById(MRN));
        } catch (PatientNotFoundException e) {
            throw new PatientNotFoundException("Patient with MRN: " + MRN + "does not exist");
        }
    }

    public void updatePatient(String MRN, String PatientObject) throws PatientNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Patient updatedPatient = mapper.readValue(PatientObject,Patient.class);
        Patient existingPatient = SearchById(MRN);

        // Now update fields — only the ones you want to change
        existingPatient.setName(updatedPatient.getName());
        existingPatient.setAge(updatedPatient.getAge());
        existingPatient.setAddress(updatedPatient.getAddress() == null ? null : updatedPatient.getAddress());
        existingPatient.setContactNo(updatedPatient.getContactNo());
        existingPatient.setAddress(updatedPatient.getAddress());
        existingPatient.setBloodGroup(updatedPatient.getBloodGroup());
        existingPatient.setKnownAllergies(updatedPatient.getKnownAllergies());
        existingPatient.setChronicConditions(updatedPatient.getChronicConditions());
        existingPatient.setCurrentMedications(updatedPatient.getCurrentMedications());
    }

    @Override
    public Patient SearchByName(String name) throws PatientNotFoundException{
        for (Patient patient: Patients) {
            if(patient.getName().equalsIgnoreCase(name)){
                return patient;
            }
        }
        throw new PatientNotFoundException("Patient with name: " + name + " does not exist");
    }

    @Override
    public Patient SearchByAge(int age) throws PatientNotFoundException{
        for (Patient patient: Patients) {
            if(patient.getAge() == age){
                return patient;
            }
        }
        throw new PatientNotFoundException("Patient with age: " + age + " does not exist");
    }

    @Override
    public Patient SearchById(String id) throws PatientNotFoundException {
        for( Patient patient: Patients) {
            if(patient.getMRN().equalsIgnoreCase(id)){
                return patient;
            }
        }
        throw new PatientNotFoundException("Patient with MRN: " + id + " does not exist");
    }
}
