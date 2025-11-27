package org.meditrack.app.entity;

import java.util.UUID;

public abstract class Person {
    String name;
    String age;
    String address;
    String ContactNo;
    String email;

    public Person(String name, String age, String address, String ContactNo, String email) {
        this.name = name;
        this.age = age;
        this.address = address;
        this.ContactNo = ContactNo;
        this.email = email;
    }

    public String getUniqueID()
    {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAge() {
        return age;
    }
    public void setAge(String age) {
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
}

