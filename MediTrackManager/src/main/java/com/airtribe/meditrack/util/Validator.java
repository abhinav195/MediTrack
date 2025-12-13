package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.exception.InvalidDataException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public class Validator {
    // BASIC VALIDATORS
    public static boolean isNonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    public static boolean isValidEmail(String email) {
        return isNonEmpty(email) &&
                email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    public static boolean isValidPhone(String contactNumber) {
        return isNonEmpty(contactNumber) &&
                contactNumber.matches("^[0-9]{10,15}$");
    }
    public static boolean isValidAge(int age) {
        return age > 0 && age < 120;
    }
    public static boolean isValidExperience(int years) {
        return years >= 0 && years <= 60;
    }
    public static boolean isValidAmount(double amount) {
        return amount >= 0;
    }
    public static boolean isValidList(List<String> list) {
        return list != null && list.stream().allMatch(Validator::isNonEmpty);
    }
    public static boolean isValidTimeRange(LocalTime from, LocalTime to) {
        return from != null && to != null && to.isAfter(from);
    }
    public static boolean isValidAppointmentSlot(LocalDateTime slot) {
        return slot != null && slot.isAfter(LocalDateTime.now());
    }
    public static boolean isValidDoctorQualification(String qualification) {
        return isNonEmpty(qualification);
    }
    public static boolean isValidDoctorType(DoctorType doctorType) {
        return doctorType != null;
    }
    public static boolean isValidOpdRoom(String opdRoom) {
        return isNonEmpty(opdRoom);
    }
    public static boolean isValidTaxRate(double taxRate) {
        return taxRate >= 0 && taxRate <= 50;
    }
    public static boolean isValidId(String id) {
        return isNonEmpty(id);
    }
    public static boolean isValidMRN(String mrn) {
        return isNonEmpty(mrn) && mrn.matches("^[A-Za-z0-9]{6,12}$");
    }

    // PATIENT VALIDATION
    public static void validatePatient(Patient p) {
        if (!isNonEmpty(p.getName()))
            throw new InvalidDataException("Patient name cannot be empty");
        if (!isValidAge(p.getAge()))
            throw new InvalidDataException("Patient age must be between 1 and 119");
        if (!isNonEmpty(p.getAddress()))
            throw new InvalidDataException("Address cannot be empty");
        if (!isValidPhone(p.getContactNumber()))
            throw new InvalidDataException("Invalid patient contact number");
        if (!isValidEmail(p.getEmail()))
            throw new InvalidDataException("Invalid patient email");
        if (p.getGender() == null)
            throw new InvalidDataException("Gender cannot be null");
        if (!isValidId(p.getMrn()))
            throw new InvalidDataException("MRN cannot be empty");
        if (!isValidPhone(p.getEmergencyContact()))
            throw new InvalidDataException("Invalid emergency contact number");
        if (p.getContactNumber().equals(p.getEmergencyContact()))
            throw new InvalidDataException("Emergency contact cannot be same as primary contact");
        Set<String> validGroups = Set.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
        if (!validGroups.contains(p.getBloodGroup()))
            throw new InvalidDataException("Invalid blood group");
        if (!isValidList(p.getKnownAllergies()))
            throw new InvalidDataException("Known allergies cannot be null or contain empty values");
        if (!isValidList(p.getChronicConditions()))
            throw new InvalidDataException("Chronic conditions cannot be null or contain empty values");
        if (!isValidList(p.getCurrentMedications()))
            throw new InvalidDataException("Medication list cannot be null or contain empty values");
    }

    // DOCTOR VALIDATION
    public static void validateDoctor(Doctor d) {
        if (!isNonEmpty(d.getName()))
            throw new InvalidDataException("Doctor name cannot be empty");
        if (!isValidAge(d.getAge()))
            throw new InvalidDataException("Doctor age must be between 1 and 119");
        if (!isNonEmpty(d.getAddress()))
            throw new InvalidDataException("Doctor address cannot be empty");
        if (!isValidPhone(d.getContactNumber()))
            throw new InvalidDataException("Invalid doctor contact number");
        if (!isValidEmail(d.getEmail()))
            throw new InvalidDataException("Invalid doctor email");
        if (d.getGender() == null)
            throw new InvalidDataException("Doctor gender cannot be null");
        if (!isValidDoctorType(d.getDoctorType()))
            throw new InvalidDataException("Doctor type cannot be null");
        if (!isValidDoctorQualification(d.getQualification()))
            throw new InvalidDataException("Qualification cannot be empty");
        if (!isValidExperience(d.getYearsOfExperience()))
            throw new InvalidDataException("Experience must be between 0 and 60 years");
        if (!isValidOpdRoom(d.getOpdRoom()))
            throw new InvalidDataException("OPD room cannot be empty");
        if (!isValidTimeRange(d.getAvailableFrom(), d.getAvailableTo()))
            throw new InvalidDataException("Available time range is invalid");
        if (d.getAvailableDays() == null || d.getAvailableDays().isEmpty())
            throw new InvalidDataException("Doctor must have at least one available day");
    }

    // APPOINTMENT VALIDATION
    public static void validateAppointment(Appointment a) {
        if (!isValidId(a.getAppointmentId()))
            throw new InvalidDataException("Appointment ID is invalid");
        if (!isValidId(a.getDoctorId()))
            throw new InvalidDataException("Doctor ID is invalid");
        if (!isValidId(a.getPatientId()))
            throw new InvalidDataException("Patient ID is invalid");
        if (!isValidAppointmentSlot(a.getTimeSlot()))
            throw new InvalidDataException("Appointment time must be in the future");
    }

    // BILL VALIDATION
    public static void validateBill(Bill bill) {
        if (!isValidId(bill.getBillId()))
            throw new InvalidDataException("Bill ID cannot be empty");
        if (bill.getAppointment() == null || !isValidId(bill.getAppointment().getAppointmentId()))
            throw new InvalidDataException("Appointment ID cannot be empty");
        if (!isValidAmount(bill.getConsultationFee()))
            throw new InvalidDataException("Consultation fee cannot be negative");
        if (!isValidAmount(bill.getMedicationCost()))
            throw new InvalidDataException("Medication cost cannot be negative");
        if (!isValidTaxRate(bill.getTaxRate()))
            throw new InvalidDataException("Tax rate must be between 0 and 50");
        if (!isValidAmount(bill.getTotalAmount()))
            throw new InvalidDataException("Total amount cannot be negative");
    }

}
