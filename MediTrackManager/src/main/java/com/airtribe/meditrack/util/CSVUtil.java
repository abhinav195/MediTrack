package com.airtribe.meditrack.util;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Doctor;
import com.airtribe.meditrack.entity.Patient;
import com.airtribe.meditrack.enums.AppointmentStatus;
import com.airtribe.meditrack.enums.DoctorType;
import com.airtribe.meditrack.enums.GENDER;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CSVUtil {

    private static final String RESOURCE_DIR = getResourceDirectory();
    private static final String PATIENT_CSV = RESOURCE_DIR + File.separator + "patient_data.csv";
    private static final String DOCTOR_CSV = RESOURCE_DIR + File.separator + "doctor_data.csv";
    private static final String APPOINTMENT_CSV = RESOURCE_DIR + File.separator + "appointment_data.csv";


    public static String getResourceDirectory() {
        // 1. Get the path where the code is running (e.g., target/classes)
        try {
            java.net.URI jarUri = CSVUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path classPath = Paths.get(jarUri).normalize();

            // 2. Navigate UP to the 'MediTrackManager' root folder
            // If running from IDE/Maven, classPath usually ends in 'target/classes' or just 'classes'
            Path projectRoot = classPath;
            while (projectRoot != null && !projectRoot.getFileName().toString().equals("MediTrackManager")) {
                projectRoot = projectRoot.getParent();
                // Safety check: if we hit root (null), stop
                if (projectRoot == null) break;
            }

            // Fallback: If we couldn't find "MediTrackManager" in path (e.g. jar is renamed),
            // assume we are 2 levels deep from root (standard maven: target/classes)
            if (projectRoot == null) {
                // Go up 2 levels from where the class is: classes -> target -> ROOT
                projectRoot = Paths.get(jarUri).getParent().getParent();
            }

            // 3. Point to the "data" folder in the root
            Path dataDir = projectRoot.resolve("data");

            // 4. Ensure it exists (though it should, based on your screenshot)
            if (!java.nio.file.Files.exists(dataDir)) {
                System.out.println("[WARNING] Data directory not found at: " + dataDir.toAbsolutePath());
                System.out.println("Creating it now...");
                java.nio.file.Files.createDirectories(dataDir);
            }

            return dataDir.toString();

        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve data directory path: " + e.getMessage(), e);
        }
    }


    private static final String[] PATIENT_HEADERS = {
            "id", "name", "age", "address", "contactNumber", "email", "gender",
            "mrn", "emergencyContact", "bloodGroup", "knownAllergies", "chronicConditions", "currentMedications"
    };

    private static final String[] DOCTOR_HEADERS = {
            "id", "name", "age", "address", "contactNumber", "email", "gender",
            "doctorType", "qualification", "yearsOfExperience", "opdRoom", "availableFrom", "availableTo", "availableDays"
    };

    private static final String[] APPOINTMENT_HEADERS = {
            "appointmentId", "doctorId", "patientId", "timeSlot", "status"
    };

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;


    public static void writePatientsToCSV(List<Patient> patients) throws IOException {
        Path path = Paths.get(PATIENT_CSV);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(PATIENT_HEADERS))) {

            for (Patient patient : patients) {
                csvPrinter.printRecord(
                        patient.getId(),
                        patient.getName(),
                        patient.getAge(),
                        patient.getAddress(),
                        patient.getContactNumber(),
                        patient.getEmail(),
                        patient.getGender() != null ? patient.getGender().name() : "",
                        patient.getMrn(),
                        patient.getEmergencyContact(),
                        patient.getBloodGroup(),
                        String.join(";", patient.getKnownAllergies() != null ? patient.getKnownAllergies() : new ArrayList<>()),
                        String.join(";", patient.getChronicConditions() != null ? patient.getChronicConditions() : new ArrayList<>()),
                        String.join(";", patient.getCurrentMedications() != null ? patient.getCurrentMedications() : new ArrayList<>())
                );
            }
            csvPrinter.flush();
        }
    }

    public static List<Patient> readPatientsFromCSV() throws IOException {
        List<Patient> patients = new ArrayList<>();
        Path path = Paths.get(PATIENT_CSV);

        if (!Files.exists(path)) {
            return patients;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            csvParser.forEach(record -> {
                Patient patient = Patient.builder()
                        .id(record.get("id"))
                        .name(record.get("name"))
                        .age(Integer.parseInt(record.get("age")))
                        .address(record.get("address"))
                        .contactNumber(record.get("contactNumber"))
                        .email(record.get("email"))
                        .gender(parseGender(record.get("gender")))
                        .mrn(record.get("mrn"))
                        .emergencyContact(record.get("emergencyContact"))
                        .bloodGroup(record.get("bloodGroup"))
                        .knownAllergies(parseList(record.get("knownAllergies")))
                        .chronicConditions(parseList(record.get("chronicConditions")))
                        .currentMedications(parseList(record.get("currentMedications")))
                        .build();
                patients.add(patient);
            });
        }
        return patients;
    }

    public static void writeDoctorsToCSV(List<Doctor> doctors) throws IOException {
        Path path = Paths.get(DOCTOR_CSV);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(DOCTOR_HEADERS))) {

            for (Doctor doctor : doctors) {
                csvPrinter.printRecord(
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getAge(),
                        doctor.getAddress(),
                        doctor.getContactNumber(),
                        doctor.getEmail(),
                        doctor.getGender() != null ? doctor.getGender().name() : "",
                        doctor.getDoctorType() != null ? doctor.getDoctorType().name() : "",
                        doctor.getQualification(),
                        doctor.getYearsOfExperience(),
                        doctor.getOpdRoom(),
                        doctor.getAvailableFrom() != null ? doctor.getAvailableFrom().format(TIME_FORMATTER) : "",
                        doctor.getAvailableTo() != null ? doctor.getAvailableTo().format(TIME_FORMATTER) : "",
                        formatDayOfWeekList(doctor.getAvailableDays())
                );
            }
            csvPrinter.flush();
        }
    }

    public static List<Doctor> readDoctorsFromCSV() throws IOException {
        List<Doctor> doctors = new ArrayList<>();
        Path path = Paths.get(DOCTOR_CSV);

        if (!Files.exists(path)) {
            return doctors;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            csvParser.forEach(record -> {
                Doctor doctor = Doctor.builder()
                        .id(record.get("id"))
                        .name(record.get("name"))
                        .age(Integer.parseInt(record.get("age")))
                        .address(record.get("address"))
                        .contactNumber(record.get("contactNumber"))
                        .email(record.get("email"))
                        .gender(parseGender(record.get("gender")))
                        .doctorType(parseDoctorType(record.get("doctorType")))
                        .qualification(record.get("qualification"))
                        .yearsOfExperience(Integer.parseInt(record.get("yearsOfExperience")))
                        .opdRoom(record.get("opdRoom"))
                        .availableFrom(parseLocalTime(record.get("availableFrom")))
                        .availableTo(parseLocalTime(record.get("availableTo")))
                        .availableDays(parseDayOfWeekList(record.get("availableDays")))
                        .build();
                doctors.add(doctor);
            });
        }
        return doctors;
    }

    public static void writeAppointmentsToCSV(List<Appointment> appointments) throws IOException {
        Path path = Paths.get(APPOINTMENT_CSV);
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(APPOINTMENT_HEADERS))) {

            for (Appointment appointment : appointments) {
                csvPrinter.printRecord(
                        appointment.getAppointmentId(),
                        appointment.getDoctorId(),
                        appointment.getPatientId(),
                        appointment.getTimeSlot() != null ? appointment.getTimeSlot().format(DATE_TIME_FORMATTER) : "",
                        appointment.getStatus() != null ? appointment.getStatus().name() : ""
                );
            }
            csvPrinter.flush();
        }
    }

    public static List<Appointment> readAppointmentsFromCSV() throws IOException {
        List<Appointment> appointments = new ArrayList<>();
        Path path = Paths.get(APPOINTMENT_CSV);

        if (!Files.exists(path)) {
            return appointments;
        }

        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            csvParser.forEach(record -> {
                Appointment appointment = Appointment.builder()
                        .appointmentId(record.get("appointmentId"))
                        .doctorId(record.get("doctorId"))
                        .patientId(record.get("patientId"))
                        .timeSlot(parseLocalDateTime(record.get("timeSlot")))
                        .status(parseAppointmentStatus(record.get("status")))
                        .build();
                appointments.add(appointment);
            });
        }
        return appointments;
    }

    // ============ Helper Methods ============

    private static GENDER parseGender(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return GENDER.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static DoctorType parseDoctorType(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return DoctorType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static AppointmentStatus parseAppointmentStatus(String value) {
        if (value == null || value.isEmpty()) {
            return AppointmentStatus.CONFIRMED;
        }
        try {
            return AppointmentStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return AppointmentStatus.CONFIRMED;
        }
    }

    private static LocalTime parseLocalTime(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return LocalTime.parse(value, TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }


    private static LocalDateTime parseLocalDateTime(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }
    }

    private static List<String> parseList(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(value.split(";"));
    }

    private static String formatDayOfWeekList(List<DayOfWeek> days) {
        if (days == null || days.isEmpty()) {
            return "";
        }
        return String.join(";", days.stream().map(DayOfWeek::name).toList());
    }

    private static List<DayOfWeek> parseDayOfWeekList(String value) {
        if (value == null || value.isEmpty()) {
            return new ArrayList<>();
        }
        List<DayOfWeek> days = new ArrayList<>();
        String[] dayStrings = value.split(";");
        for (String dayString : dayStrings) {
            try {
                days.add(DayOfWeek.valueOf(dayString.trim()));
            } catch (IllegalArgumentException e) {
                // Skip invalid day values
            }
        }
        return days;
    }
}
