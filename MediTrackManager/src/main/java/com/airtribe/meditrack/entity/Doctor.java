package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.DoctorType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

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
    private double consultationFee;

    @lombok.Builder.Default
    private List<DayOfWeek> availableDays = Arrays.asList(
            DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
    );

    @Override
    public String toString() {
        return "Doctor [ID=" + getId() + ", Name=" + getName() +
                ", Type=" + doctorType + ", Hours=" + availableFrom + "-" + availableTo + "]";
    }
}
