package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class DataStore<T> {

    public DataStore() {
    }

    private void saveDoctors(Set<Doctor> doctors) throws IOException {
        CSVUtil.writeDoctorsToCSV(new java.util.ArrayList<>(doctors));
    }

    private void savePatients(Set<Patient> patients) throws IOException {
        CSVUtil.writePatientsToCSV(new java.util.ArrayList<>(patients));
    }

    private void saveAppointments(Set<Appointment> appointments) throws IOException {
        CSVUtil.writeAppointmentsToCSV(new java.util.ArrayList<>(appointments));
    }

    public void save(Set<T> data) throws IOException {
        if (data.isEmpty()) {
            return;
        }

        // Determine the type of data and save accordingly
        Object firstElement = data.iterator().next();
        if (firstElement instanceof Doctor) {
            saveDoctors((Set<Doctor>) data);
        } else if (firstElement instanceof Patient) {
            savePatients((Set<Patient>) data);
        } else if (firstElement instanceof Appointment) {
            saveAppointments((Set<Appointment>) data);
        }
    }

    public static HashSet<Doctor> loadDoctors() throws IOException {
        List<Doctor> doctorList = CSVUtil.readDoctorsFromCSV();
        return new HashSet<>(doctorList);
    }

    public static HashSet<Patient> loadPatients() throws IOException {
        List<Patient> patientList = CSVUtil.readPatientsFromCSV();
        return new HashSet<>(patientList);
    }

    public static HashSet<Appointment> loadAppointments() throws IOException {
        List<Appointment> appointmentList = CSVUtil.readAppointmentsFromCSV();
        return new HashSet<>(appointmentList);
    }

}
