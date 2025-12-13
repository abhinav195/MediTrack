# MediTrack System - Setup & Usage Guide

## 1. Prerequisites
Before running the application, ensure you have the following installed:

*   **Java Development Kit (JDK):** Version 21 or higher.
    *   Verify with: `java -version`
*   **Build Tool:** Maven (optional, if running via command line) or an IDE with Maven support.
*   **IDE:** IntelliJ IDEA (Recommended), Eclipse, or VS Code.
    git clone <repository-url>

**OR**
*  extract the zip file
*  cd MediTrackManager
---

## 2. Installation & Setup

### Step 1: Clone or Extract the Project
Download the source code or clone the repository to your local machine.


### Step 2: Open in IDE
1.  Open **IntelliJ IDEA**.
2.  Select **File > Open**.
3.  Navigate to the `MediTrackManager` folder and select it.
4.  Allow the IDE to import Maven dependencies (defined in `pom.xml`).
    *   *Note: This project uses Lombok. Ensure "Annotation Processing" is enabled in your IDE settings.*

### Step 3: Verify Folder Structure
Ensure the project structure looks like this:

MediTrackManager/
├── data/
│   ├── doctor_data.csv
│   ├── patient_data.csv
│   └── appointment_data.csv
├── docs/               <-- Documentation
├── src/
│   ├── main/java/com/airtribe/meditrack/
│   │   ├── Main.java        <-- Entry point
│   │   └── TestRunner.java  <-- Manual Test Suite
└── pom.xml

---

## 3. Running the Application

### Option A: Running via IDE (IntelliJ)
1.  Navigate to `src/main/java/com/airtribe/meditrack/Main.java`.
2.  Right-click the file and select **Run 'Main.main()'**.
3.  The console will open the interactive menu:
    ```
    Initializing MediTrack System...
    Loading data from CSV...
    
    === MediTrack Management System ===
    1. [PATIENT] View All Patients
    2. [DOCTOR]  View All Doctors
    ...
    >> Enter choice:
    ```

### Option B: Running via Command Line (Maven)

* mvn clean compile exec:java -Dexec.mainClass="com.airtribe.meditrack.Main"
---

## 4. Running the Test Suite
The project includes a comprehensive manual test runner that validates the system's logic (Validation, CRUD, Booking logic, CSV Persistence).

1.  Navigate to `src/main/java/com/airtribe/meditrack/TestRunner.java`.
2.  Right-click and select **Run 'TestRunner.main()'**.
3.  Check the console output for **✅ PASS** or **❌ FAIL** indicators.

**Expected Output:**
* MEDITRACK SYSTEM - MANUAL TEST SUITE V3.0
* --- INPUT VALIDATION TESTS ---​
* Email (Valid) : ✅ PASS
* ...
* --- APPOINTMENT LOGIC TESTS ---
* Booking Conflict (Same Time) : ✅ PASS
* Smart Booking (Symptom Matching) : ✅ PASS

---

## 5. Troubleshooting

### Issue: "File not found" or "Illegal char <:> at index 2"
*   **Cause:** The system cannot locate the `data/` directory or handle the Windows path correctly.
*   **Fix:** Ensure the `data` folder exists in the project root (not inside `src`). The `CSVUtil` class has been configured to automatically find this folder.

### Issue: "Compilation Error: Cannot find symbol (Getter/Setter)"
*   **Cause:** Lombok is not processing annotations.
*   **Fix:**
    1.  Go to **Settings > Build, Execution, Deployment > Compiler > Annotation Processors**.
    2.  Check **"Enable annotation processing"**.
    3.  Rebuild the project.

### Issue: Data is lost after restart
*   **Cause:** The application exited without saving.
*   **Fix:** Always use **Option 6 (Exit)** from the main menu to ensure data is written back to the CSV files. Terminating the app via the "Stop" button may prevent saving.
