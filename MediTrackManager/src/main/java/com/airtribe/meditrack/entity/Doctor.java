package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.DoctorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Doctor extends Person {

    private DoctorType doctorType;
    private String qualification;
    private int yearsOfExperience;
    private String opdRoom;
    private LocalTime availableFrom;
    private LocalTime availableTo;

    @Override
    public String toString() {
        return "Doctor [ID=" + getId() + ", Name=" + getName() +
                ", Type=" + doctorType + "]";
    }
}
