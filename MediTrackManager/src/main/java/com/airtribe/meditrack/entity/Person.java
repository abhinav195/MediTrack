package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.enums.GENDER;
import com.airtribe.meditrack.util.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person implements Serializable {

    // Centralized ID for all People (Doctors and Patients)
    @lombok.Builder.Default
    private String id = IdGenerator.generateMRN();

    private String name;
    private int age;
    private String address;
    private String contactNumber;
    private String email;
    private GENDER gender;

}
