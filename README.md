# 🏥 MediTrack Manager

**MediTrack Manager** is a comprehensive, Java-based command-line application designed to streamline clinic operations. It handles patient management, doctor scheduling, and "smart" appointment booking logic with persistent CSV storage.

---

## 🚀 Key Features

*   **Smart Appointment Booking:**
    *   Automatically finds the next available slot if the requested time is busy.
    *   "Symptom Matching" (AI Helper stub) to route patients to the correct specialist (e.g., "Heart pain" → Cardiologist).
    *   Handles weekend and after-hours logic by rescheduling to the next working day.
*   **Entity Management:** Full CRUD operations for **Doctors** and **Patients**.
*   **Data Persistence:**
    *   Loads data from CSV files (`data/`) on startup.
    *   Saves all changes back to CSV on exit.
    *   Robust error handling for file I/O.
*   **Validation:** Centralized validation for Emails, Mobile Numbers, and Medical Record Numbers (MRN).
*   **Comprehensive Testing:** Includes a standalone `TestRunner.java` suite covering validation, CRUD, booking logic, and persistence.

---

## 🛠️ Tech Stack

*   **Language:** Java 21
*   **Build Tool:** Maven
*   **Libraries:**
    *   **Lombok:** Reduces boilerplate (Getters/Setters/Builders).
    *   **Java NIO:** Efficient file handling.
*   **Architecture:** Layered (Entities -> Services -> Utilities -> Main Driver).

---

## 📂 Project Structure

MediTrackManager/
├── data/ <-- Persistence Layer (CSV Files)
│ ├── doctor_data.csv
│ ├── patient_data.csv
│ └── appointment_data.csv
├── docs/ <-- Documentation (Diagrams, Decisions)
├── src/main/java/com/airtribe/meditrack/
│ ├── entity/ <-- POJOs (Doctor, Patient, Appointment)
│ ├── service/ <-- Business Logic
│ ├── util/ <-- Helpers (CSVUtil, Validator)
│ ├── Main.java <-- Application Entry Point
│ └── TestRunner.java <-- Manual Test Suite
└── pom.xml


---

## ⚡ Getting Started

### Prerequisites
*   Java JDK 21+
*   Maven (optional, wrapper included in IDEs)

### Installation
1.  **Clone the repo:**
    ```
    git clone https://github.com/YOUR_USERNAME/MediTrackManager.git
    cd MediTrackManager
    ```
2.  **Open in IntelliJ IDEA** (or Eclipse/VS Code).
3.  **Enable Annotation Processing** (for Lombok support).

### Running the App
Run the `Main.java` file. You will see an interactive menu:

=== MediTrack Management System ===

1. [PATIENT] View All Patients

2. [DOCTOR] View All Doctors

3. [BOOK] Smart Booking

4. [BOOK] Auto-Match Specialist

5. [ADMIN] View All Appointments

**Exit & Save**


### Running Tests
Run the `TestRunner.java` file to execute the manual test suite. It verifies:
*   ✅ Input Validation (Email/MRN regex)
*   ✅ Booking Logic (Conflict resolution)
*   ✅ File Persistence (Save/Load)

---

## 📖 Documentation
Detailed documentation can be found in the `docs/` folder:
*   [**Setup Instructions**](docs/Setup_Instructions.md): Step-by-step guide to running the app.
*   [**Design Decisions**](docs/Design_Decisions.md): Architecture choices and trade-offs.
*   [**Class Diagram**](docs/meditrack_diagram.puml): UML representation of the system.

---

## 🤝 Contributing
Contributions are welcome! Please fork the repository and submit a Pull Request.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

**Developed by Abhinav Pathak, Bhaskar Pandey, Guransh Dua, Drishti Sharma**
