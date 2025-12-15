# Proyecto-JDBC
[![Ask DeepWiki](https://devin.ai/assets/askdeepwiki.png)](https://deepwiki.com/JuanjoJmnz/Proyecto-JDBC)

This repository contains a data access project for a 2nd-year DAM (Desarrollo de Aplicaciones Multiplataforma) course. It's a console-based Java application that uses JDBC to manage a small-scale hospital system, handling data for doctors, speech therapists, patients, and their assignments.

## Features

The application provides a comprehensive command-line interface to interact with a MySQL database.

*   **Database Schema Management**:
    *   Automatically creates the required tables and stored procedures on startup.
    *   Provides options to drop individual tables (`pacienteAsignacion`, `doctores`, `logopedas`, `pacientes`) or reset the entire database.

*   **CRUD Operations**:
    *   **Create**: Add new doctors, speech therapists, and patients. User and role-specific data are created together in a single transaction to ensure data integrity. New patient-professional assignments can also be created.
    *   **Read**:
        *   View complete lists of doctors, speech therapists, patients, and assignments.
        *   Search for doctors by exact or partial specialty name.
        *   Filter patients by their primary diagnosis.
        *   Display patient login times converted to different time zones (e.g., `Asia/Tokyo`).
    *   **Update**:
        *   Modify patient diagnoses, doctor specialties, and speech therapist professional numbers using stored procedures.
        *   Discharge a patient by updating their status in the assignments table.
    *   **Delete**:
        *   Remove doctors, speech therapists, or patients. Associated user records are also deleted using `ON DELETE CASCADE` and transactions.
        *   Delete individual assignment records.

*   **Data Integrity and Safety**:
    *   Uses `PreparedStatement` to prevent SQL injection attacks.
    *   Employs database transactions (`commit`, `rollback`) for multi-step operations like creating new users or deleting records to maintain consistency.

## Database Schema

The application models a simple clinical management system with the following tables:

*   `users`: A central table containing common user data (email, password, name) and a role (`Doctor`, `Logopeda`, `Paciente`).
*   `doctores`: Stores doctor-specific details, linked to a user via a foreign key.
*   `logopedas`: Stores speech therapist-specific details (e.g., professional number), linked to a user.
*   `pacientes`: Contains patient-specific information like date of birth, diagnosis, and medical history.
*   `pacienteAsignacion`: A linking table that connects patients with their assigned doctors and speech therapists, including session details and status.

## Technologies Used

*   **Java**: Core programming language for the application logic.
*   **JDBC (Java Database Connectivity)**: To connect and execute queries against the database.
*   **MySQL**: The relational database management system.
*   **Maven**: For managing project dependencies, specifically the MySQL Connector/J driver.

## Setup and Usage

### Prerequisites

*   Java Development Kit (JDK)
*   Apache Maven

### Running the Application

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/JuanjoJmnz/Proyecto-JDBC.git
    cd Proyecto-JDBC
    ```

2.  **Database Connection:**
    The database connection credentials are hardcoded in the `src/main/java/ConexionBBDD.java` file. The application is configured to connect to a specific remote MySQL instance.

3.  **Compile the project:**
    Use Maven to compile the source code and download dependencies.
    ```sh
    mvn clean install
    ```

4.  **Run the main class:**
    Execute the `Main` class to start the application. Upon starting, it will automatically drop existing tables, create the full schema, and insert sample data for testing.
    ```sh
    java -cp target/ProyectoJDBC-1.0-SNAPSHOT.jar Main
    ```

5.  **Interact with the application:**
    Follow the on-screen menu prompts to use the application's features.

## Code Structure

*   `Main.java`: The entry point of the application. It contains the main loop and the primary navigation menu.
*   `ConexionBBDD.java`: The core class responsible for all database interactions. It manages the database connection, schema creation/deletion, CRUD operations, and execution of stored procedures.
*   `Funciones.java`: A helper class that handles user input validation and orchestrates calls to the `ConexionBBDD` class based on user menu selections.
*   `pom.xml`: The Maven Project Object Model file, which defines the project's dependencies and build settings.