package org.meditrack.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.meditrack.app.entity.Doctor;
import org.meditrack.app.exceptions.DoctorNotFoundException;
import org.meditrack.app.interfaces.Searchable;

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
        Doctor updatedDoctor = mapper.readValue(doctorObject,Doctor.class);
        Doctor doctor = SearchById(doctorId);
        doctor.setName(updatedDoctor.getName());
        doctor.setAge(updatedDoctor.getAge());
        doctor.setAddress(updatedDoctor.getAddress() == null ? null : updatedDoctor.getAddress());
        doctor.setContactNo(updatedDoctor.getContactNo());
        doctor.setAddress(updatedDoctor.getAddress());
        doctor.setDoctorType(updatedDoctor.getDoctorType());
        doctor.setDoctorId(updatedDoctor.getDoctorId());
    }

    @Override
    public Doctor SearchByName(String name) throws DoctorNotFoundException {
        for (Doctor doctor : doctors) {
            if(doctor.getName().equalsIgnoreCase(name))
                return doctor;
        }
        throw new DoctorNotFoundException("Doctor with name: " + name + " does not exist");
    }

    @Override
    public Doctor SearchByAge(int age) throws DoctorNotFoundException {
        for( Doctor doctor : doctors) {
            if(doctor.getAge() == age)
                return doctor;
        }
        throw new DoctorNotFoundException("Doctor with age: " + age + " does not exist");
    }

    @Override
    public Doctor SearchById(String id) throws DoctorNotFoundException {
        for (Doctor doctor : doctors) {
            if(doctor.getDoctorId().equals(id))
                return doctor;
        }
        throw new DoctorNotFoundException("Doctor with id: " + id + " does not exist");
    }
}
