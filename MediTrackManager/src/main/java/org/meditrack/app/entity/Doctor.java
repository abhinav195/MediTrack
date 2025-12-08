package org.meditrack.app.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.meditrack.app.enums.DoctorType;
import org.meditrack.app.enums.GENDER;

import java.time.LocalTime;

@SuperBuilder
@Getter
@Setter
public class Doctor extends Person{
    private DoctorType doctorType;
    private String doctorId;
    private String qualification;
    private int yearsOfExperience;
    private String opdRoom;
    private LocalTime availableFrom;
    private LocalTime availableTo;


//    public Doctor(String name, int age, String address, String ContactNo, String email, GENDER gender) {
//        super(name, age, address, ContactNo, email, gender);
//    }

    public Doctor(Doctor existingDoctor) {
        super(existingDoctor.name, existingDoctor.age, existingDoctor.address, existingDoctor.ContactNo,
                existingDoctor.email, existingDoctor.gender);
        this.doctorId = existingDoctor.getDoctorId();
        this.qualification = existingDoctor.getQualification();
        this.yearsOfExperience = existingDoctor.getYearsOfExperience();
        this.opdRoom = existingDoctor.getOpdRoom();
        this.availableFrom = existingDoctor.getAvailableFrom();
        this.availableTo = existingDoctor.getAvailableTo();

    }
}
