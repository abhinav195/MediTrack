package org.meditrack.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.meditrack.app.entity.Doctor;
import org.meditrack.app.entity.Paitent;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public class DoctorService {
    List<Doctor> doctors;
    public DoctorService() {
        this.doctors = new ArrayList<>();
    }
    public List<Doctor> getDoctors() {
        return doctors;
    }
    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }
    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }
    public void removeDoctor(Doctor doctor) {
        doctors.remove(doctor);
    }
    public void updateDoctor(String doctorId, String doctorObject) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Doctor updatedDoctor = mapper.readValue(doctorObject,Doctor.class);
        Doctor doctor = getDoctorById(doctorId);
        doctor.setName(updatedDoctor.getName());
        doctor.setAge(updatedDoctor.getAge());
        doctor.setAddress(updatedDoctor.getAddress() == null ? null : updatedDoctor.getAddress());
        doctor.setContactNo(updatedDoctor.getContactNo());
        doctor.setAddress(updatedDoctor.getAddress());
        doctor.setDoctorType(updatedDoctor.getDoctorType());
        doctor.setDoctorId(updatedDoctor.getDoctorId());
    }
    public Doctor getDoctorById(String doctorId) {
        for (Doctor doctor : doctors) {
            if(doctor.getDoctorId().equals(doctorId))
                return doctor;
        }
        return null;
    }
}
