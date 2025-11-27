package org.meditrack.app.entity;

import org.meditrack.app.enums.GENDER;

public abstract class Person {
    String name;
    int age;
    String address;
    String ContactNo;
    String email;
    GENDER gender;

    public Person(String name, int age, String address, String ContactNo, String email, GENDER gender) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.ContactNo = ContactNo;
        this.email = email;
        this.gender = gender;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getContactNo() {
        return ContactNo;
    }
    public void setContactNo(String contactNo) {
        ContactNo = contactNo;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public GENDER getGender() {
        return gender;
    }
    public void setGender(GENDER gender) {
        this.gender = gender;
    }
}

