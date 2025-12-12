package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Person;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.util.Validator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.exception.DoctorNotFoundException;
import com.airtribe.meditrack.interfaces.Searchable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.airtribe.meditrack.constants.Constants.SLOT_DURATION_MINUTES;

public class DoctorService implements Searchable {
    HashSet<Doctor> doctors;
    public DoctorService() {
        this.doctors = new HashSet<>();
    }
    public void setDoctors(HashSet<Doctor> doctors) {
        if (doctors != null) {
            for (Doctor d : doctors) {
                Validator.validateDoctor(d);
            }
        }
        this.doctors = doctors;
    }
    public void addDoctor(Doctor doctor) {
        Validator.validateDoctor(doctor);
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
            Validator.validateDoctor(updatedDoctor);
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
    public boolean isDoctorWorking(String doctorId, LocalDateTime slot) {
        try {
            Doctor doc = (Doctor) SearchById(doctorId);
            // 1. Check Day
            if (!doc.getAvailableDays().contains(slot.getDayOfWeek())) {
                return false;
            }
            // 2. Check Time
            LocalTime time = slot.toLocalTime();
            // Assuming 30-min slots, ensures appointment finishes before shift end
            return !time.isBefore(doc.getAvailableFrom()) &&
                    !time.plusMinutes(SLOT_DURATION_MINUTES).isAfter(doc.getAvailableTo());

        } catch (DoctorNotFoundException e) {
            return false;
        }
    }
    public List<Doctor> getDoctorsByType(DoctorType type) {
        return doctors.stream()
                .filter(d -> d.getDoctorType() == type)
                .collect(Collectors.toList());
    }
    public HashSet<Doctor> getDoctors() {
        return this.doctors;
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
