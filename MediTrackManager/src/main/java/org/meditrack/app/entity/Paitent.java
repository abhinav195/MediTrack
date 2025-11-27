package org.meditrack.app.entity;

import org.meditrack.app.enums.GENDER;
import org.meditrack.app.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

public class Paitent extends Person {

    String paitentId;
    String MRN;
    String emergencyContact;
    List<String> knownAllergies;
    List<String> chronicConditions;
    String bloodGroup;
    List<String> CurrentMedications;


    public Paitent(String name, int age, String address, String ContactNo, String email, GENDER gender, String bloodGroup,
                   String emergencyContact) {
        super(name, age, address, ContactNo, email, gender);
        this.paitentId = IdGenerator.generateId();
        this.MRN = IdGenerator.generateId();
        this.emergencyContact = emergencyContact;
        this.knownAllergies = new ArrayList<>();
        this.chronicConditions = new ArrayList<>();
        this.bloodGroup = bloodGroup;
        this.CurrentMedications = new ArrayList<>();
    }

    public Paitent(Paitent existingPaitent) {
        super(existingPaitent.getPatientName(), existingPaitent.getAge(), existingPaitent.getAddress(),
                existingPaitent.getContactNo(), existingPaitent.getEmail(), existingPaitent.getGender());
        this.paitentId = IdGenerator.generateId();
        this.MRN = IdGenerator.generateId();
        this.emergencyContact = existingPaitent.getEmergencyContact();
        this.knownAllergies = new ArrayList<>(existingPaitent.getKnownAllergies());
        this.chronicConditions = new ArrayList<>(existingPaitent.getChronicConditions());
        this.bloodGroup = existingPaitent.getBloodGroup();
        this.CurrentMedications = new ArrayList<>(existingPaitent.getCurrentMedications());
    }

    public void setPatientName(String patientName) {
        super.setName(patientName);
    }

    public String getPatientName() {
        return super.getName();
    }

    public void setAge(int age) {
        super.setAge(age);
    }
    public int getAge() {
        return super.getAge();
    }
    public void setAddress(String address) {
        super.setAddress(address);
    }
    public String getAddress() {
        return super.getAddress();
    }
    public String getContactNo() {
        return super.getContactNo();
    }

    public void setContactNo(String contactNo) {
        super.setContactNo(contactNo);
    }

    public String getEmail() {
        return super.getEmail();
    }
    public void setEmail(String email) {
        super.setEmail(email);
    }

    public GENDER getGender() {
        return super.getGender();
    }
    public void setGender(GENDER gender) {
        super.setGender(gender);
    }

    public String getMRN()
    {
        return MRN;
    }
    public String getEmergencyContact()
    {
        return emergencyContact;
    }
    public List<String> getKnownAllergies()
    {
        return knownAllergies;
    }

    public boolean isAllergicto(String allergy)
    {
        return knownAllergies.contains(allergy);
    }

    public String getBloodGroup()
    {
        return bloodGroup;
    }
    public List<String> getCurrentMedications()
    {
        return CurrentMedications;
    }
    public void addMedication(String medication)
    {
        CurrentMedications.add(medication);
    }
    public void  removeMedication(String medication)
    {
        CurrentMedications.remove(medication);
    }
    public List<String> getChronicConditions()
    {
        return chronicConditions;
    }
    public void addChronicCondition(String condition)
    {
        chronicConditions.add(condition);
    }

    public void removeChronicCondition(String condition)
    {
        chronicConditions.remove(condition);
    }

    public void setBloodGroup(String bloodGroup)
    {
        this.bloodGroup = bloodGroup;
    };

    public void setChronicConditions(List<String> chronicConditions)
    {
        this.chronicConditions = chronicConditions;
    }
    public void setCurrentMedications(List<String> currentMedications)
    {
        CurrentMedications = currentMedications;
    }
    public  void setKnownAllergies(List<String> knownAllergies)
    {
        this.knownAllergies = knownAllergies;
    }
}
