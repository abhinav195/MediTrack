# Design Decisions Document - MediTrack System

## 1. Architecture Overview
MediTrack follows a **Layered Architecture** pattern to ensure separation of concerns, maintainability, and scalability. The system is divided into distinct layers:

*   **Entity Layer (`com.airtribe.meditrack.entity`):** Defines the core data models (POJOs) representing the business domain.
*   **Service Layer (`com.airtribe.meditrack.service`):** Contains the business logic and orchestrates operations on entities.
*   **Utility Layer (`com.airtribe.meditrack.util`):** Provides reusable helper functions for validation, I/O, and data transformation.
*   **Presentation/Driver Layer (`Main.java`, `TestRunner.java`):** Handles user interaction via CLI and system initialization.

---

## 2. Key Design Choices

### 2.1 Inheritance for `Person` Hierarchy
**Decision:** Extract common attributes (`name`, `age`, `contact`, `email`, `address`) into an abstract base class `Person`.
*   **Reasoning:** Both `Doctor` and `Patient` share significant identity attributes. Using inheritance reduces code duplication and allows for polymorphic handling (e.g., generic `SearchById` methods returning a `Person`).
*   **Alternative Considered:** Composition (having a `PersonInfo` field inside Doctor/Patient).
*   **Why Rejected:** Inheritance felt more natural for an "is-a" relationship in this specific domain context.

### 2.2 In-Memory Data with CSV Persistence
**Decision:** The system loads data into memory (HashSets/Maps) at startup and writes back to CSV files on exit or specific save triggers.
*   **Reasoning:**
    *   **Performance:** In-memory operations (search, filter) are O(1) or O(n) and extremely fast compared to disk I/O.
    *   **Simplicity:** Avoids the overhead of setting up a full SQL database for this phase of the project.
    *   **Portability:** CSV files are human-readable and easy to debug.
*   **Trade-off:** Data is susceptible to loss if the application crashes before a save operation. This is mitigated by implementing `DataStore.save()` calls at critical transaction points.

### 2.3 Builder Pattern for Entity Creation
**Decision:** Use the **Builder Pattern** (via explicit inner classes or Lombok `@Builder`) for creating complex objects like `Doctor` and `Patient`.
*   **Reasoning:**
    *   Entities have many fields (10+), some of which are optional (e.g., `opdRoom`, `yearsOfExperience`).
    *   Constructors with many parameters are error-prone (e.g., accidentally swapping `email` and `address` strings).
    *   Builders provide a fluent, readable API: `.name("House").age(50).build()`.

### 2.4 "Smart Booking" Logic
**Decision:** The booking system handles unavailability by automatically searching for the **next available slot** or the **next available working day**.
*   **Reasoning:** Improves user experience. Instead of simply rejecting a request with "Slot Taken", the system proactively offers a solution.
*   **Implementation:** The logic iterates through time slots (e.g., +30 mins) or days until a free slot matching the doctor's working hours is found.

### 2.5 Centralized Validation
**Decision:** Use a standalone `Validator` utility class rather than validating inside entity setters.
*   **Reasoning:** Keeps entity classes pure (POJOs) and separates concerns. Validation logic (regex for emails, MRN formats, business rules like age limits) is often complex and should be reusable across different parts of the application (e.g., used by both `Main` menu and `SeedData`).

### 2.6 Enums for Strong Typing
**Decision:** Use Enums for fixed sets of constants (`DoctorType`, `AppointmentStatus`, `GENDER`).
*   **Reasoning:** Prevents "magic string" errors (e.g., "Cardio" vs "Cardiologist") and ensures type safety throughout the system logic.

---

## 3. Technology Stack & Libraries
*   **Java 21:** Leveraged for modern features like enhanced switch statements and better memory management.
*   **Lombok:** Used to reduce boilerplate code (Getters, Setters, Builders, Constructors).
*   **Java NIO (New I/O):** Used in `CSVUtil` for efficient file handling and robust path resolution across operating systems (Windows/Linux compatibility).

---

## 4. Future Improvements (Roadmap)
1.  **Concurrency Support:** Currently, the `Map` implementations are not thread-safe. Future versions should use `ConcurrentHashMap` to support multiple concurrent users.
2.  **Database Integration:** Replace CSV persistence with H2 or PostgreSQL for better query capabilities and ACID compliance.
3.  **Design Patterns:** Introduce Singleton for ID generation, Factory for object creation, and Observer for notifications to further decouple components.
