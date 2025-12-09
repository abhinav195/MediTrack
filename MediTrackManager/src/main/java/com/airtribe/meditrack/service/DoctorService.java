package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.exception.DoctorNotFoundException;
import com.airtribe.meditrack.interfaces.Searchable;

import java.util.HashSet;

public class DoctorService implements Searchable {
    HashSet<Doctor> doctors;

    public DoctorService() {
        this.doctors = new HashSet<>();
    }

    public HashSet<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(HashSet<Doctor> doctors) {
        this.doctors = doctors;
    }

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }

    public void removeDoctor(Doctor doctor) {
        doctors.remove(doctor);
    }

    public void updateDoctor(String doctorId, String doctorObject) throws DoctorNotFoundException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Doctor updatedDoctor = mapper.readValue(doctorObject, Doctor.class);

        Person doctorInstance = SearchById(doctorId);

        if (doctorInstance instanceof Doctor doctor) {

            doctor.setName(updatedDoctor.getName());
            doctor.setAge(updatedDoctor.getAge());
            doctor.setAddress(updatedDoctor.getAddress());
            doctor.setContactNumber(updatedDoctor.getContactNumber());
            doctor.setDoctorType(updatedDoctor.getDoctorType());

            doctor.setId(updatedDoctor.getId());
        } else {
            throw new DoctorNotFoundException("Entity found is not a Doctor.");
        }
    }

    @Override
    public Person SearchByName(String name) throws DoctorNotFoundException {
        for (Doctor doctor : doctors) {
            if (doctor.getName().equalsIgnoreCase(name))
                return doctor;
        }
        throw new DoctorNotFoundException("Doctor with name: " + name + " does not exist");
    }

    @Override
    public Person SearchByAge(int age) throws DoctorNotFoundException {
        for (Doctor doctor : doctors) {
            if (doctor.getAge() == age)
                return doctor;
        }
        throw new DoctorNotFoundException("Doctor with age: " + age + " does not exist");
    }

    @Override
    public Person SearchById(String id) throws DoctorNotFoundException {
        for (Doctor doctor : doctors) {
            if (doctor.getId().equals(id))
                return doctor;
        }
        throw new DoctorNotFoundException("Doctor with id: " + id + " does not exist");
    }
}
