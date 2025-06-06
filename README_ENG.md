# Hospital and Health Services Management System - VidaPlus 

<a href="https://github.com/Caroline-Teixeira/vida_plus/blob/main/README.md"><img src="https://raw.githubusercontent.com/yammadev/flag-icons/refs/heads/master/png/BR%402x.png" alt="Portuguese" ></a>


Repository for HSHMS (Hospital and Health Services Management System), a multidisciplinary project developed for the Multidisciplinary Project course at UNINTER (2025). This system was created for the fictional institution VidaPlus, with the objective of centralizing registrations, appointments, telemedicine, and hospital administration, according to the provided case study.

**About the Project:** This is a back-end prototype, using Java Spring with MVC architecture and MySQL 8 database. The REST API implements the main system functionalities, meeting the functional and non-functional requirements of the case study.

## Features

- Patient registration and management (personal data, medical records)
- Appointment and surgery scheduling and cancellation
- Healthcare professional management (schedules)
- Hospital administration (bed control, log reports)

## Technologies Used

- **Back-end:** Java 17, Spring Boot, Spring MVC, Spring Data JPA, Spring Security
- **Database:** MySQL 8
- **Tools:** Maven (dependency management), Postman (API testing)

## Configuration

### Clone the repository:
```bash
git clone https://github.com/Caroline-Teixeira/vida_plus.git
```

### Navigate to the directory:
```bash
cd vida_plus
```

### Configure the database (MySQL):
```sql
CREATE DATABASE hospital_vidaplus;
```

### Update the application.properties file to:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_vidaplus
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### Compile and run:
```bash
mvn clean install
mvn spring-boot:run
```

## API Testing

Access http://localhost:8080 (or configured port). Use Postman or another platform to test endpoints.

The following links present the tests performed for the application:
- https://youtu.be/gLvQzmj5r5g
- https://youtu.be/iD2cYpGLDQs
- https://youtu.be/xapQgoiTB_U

<h2>Automatic Token Configuration - POSTMAN</h2>
Create an environment called ‘Hospital VidaPlus’. In the login session, use the script:

```
pm.environment.unset("hospital-token");
console.log("Token removed from environment variable");
```
For other requests:

In the `Header` section, create a key called `Authorization` and set the following value in `Value`: `Bearer {{hospital-token}}`.
The Token expires after 7 days.

## Test Users

| **ROLE** | **EMAIL** | **PASSWORD** |
|----------|-----------|--------------|
| ADMIN | fernanda.costa@example.com | fernanda022 |
| ADMIN | leon.s@example.com | senha123 |
| HEALTH_PROFESSIONAL | mariana.lopes@example.com | 123mariana |
| ATTENDANT | lucas.oliveira@example.com | lucas789 |
| ATTENDANT | sheila.s@example.com | 123senha |
| PATIENT | alice.costa@example.com | alice202 |
| PATIENT | clara.s@example.com | senha123 |

## API Documentation - HTTP Endpoints

### HTTP Method: POST

| URL | DESCRIPTION | PERMISSIONS |
|-----|-------------|-------------|
| /auth/login | Allows public access to authentication routes (e.g., login) | All (No Authentication) |
| /auth/logout | Logs out the authenticated user | Authenticated Users |
| /api/audit-records/filter | Filters audit records (by users) | Admin |
| /api/users | Creates a new user | Admin, Attendant |
| /api/appointments | Creates a new appointment | Admin, Attendant |
| /api/surgeries | Creates a new surgery record | Admin, Attendant |
| /api/medical-records/{patientId}/add-observations | Adds consultation observations to medical record | Admin, Health_Professional |
| /api/medical-records/{patientId}/add-surgery-observations | Adds surgery observations to medical record | Admin, Health_Professional |

### HTTP Method: PUT

| URL | DESCRIPTION | PERMISSIONS |
|-----|-------------|-------------|
| /api/users/{id} | Updates a specific user | Admin, Attendant |
| /api/appointments/{id} | Updates a specific appointment | Admin, Attendant |
| /api/appointments/{id}/status | Updates an appointment status | Admin, Attendant |
| /api/surgeries/{id} | Updates a specific surgery | Admin, Attendant |
| /api/surgeries/{id}/status | Updates a surgery status | Admin, Attendant |
| /api/medical-records/{patientId}/update-observations | Updates consultation observations in medical record | Admin, Health_Professional |
| /api/medical-records/{patientId}/update-surgery-observations | Updates surgery observations in medical record | Admin, Health_Professional |

### HTTP Method: GET

| URL | DESCRIPTION | PERMISSIONS |
|-----|-------------|-------------|
| /api/users | Lists all users | Admin, Attendant |
| /api/users/{id} | Gets details of a specific user | Admin, Attendant |
| /api/users/current | Gets authenticated user data | All (Authenticated User) |
| /api/appointments | Lists all appointments | Admin, Attendant |
| /api/appointments/{id} | Gets details of a specific appointment | Admin, Attendant, Health_Professional |
| /api/appointments/current | Lists current user's appointments | All (Authenticated User) |
| /api/appointments/patient/{patientId} | Lists appointments for a specific patient | Admin, Attendant, Health_Professional |
| /api/appointments/healthProfessional/{healthProfessionalId} | Lists appointments for a healthcare professional | Admin, Attendant, Health_Professional |
| /api/surgeries | Lists all surgeries | Admin, Attendant |
| /api/surgeries/{id} | Gets details of a specific surgery | Admin, Attendant, Health_Professional |
| /api/surgeries/current | Lists current user's surgeries | All (Authenticated User) |
| /api/surgeries/patient/{patientId} | Lists surgeries for a specific patient | Admin, Attendant, Health_Professional |
| /api/surgeries/healthProfessional/{healthProfessionalId} | Lists surgeries for a healthcare professional | Admin, Attendant, Health_Professional |
| /api/medical-records/current | Lists current user's medical record | All (Authenticated User) |
| /api/medical-records/patient/{patientId} | Lists medical record for a specific patient | Admin, Attendant, Health_Professional |
| /api/audit-records/all | Lists all audit records | Admin |
| /api/schedule/all-slots/{professionalId}/{date} | Lists all available slots for a professional | Admin, Attendant |
| /api/schedule/current/{date} | Gets current schedule for a specific date | Admin, Attendant, Health_Professional |
| /api/hospitalizations/active | Lists active hospitalizations | Admin |
| /api/hospitalizations/available-beds | Lists available beds | Admin |

### HTTP Method: DELETE

| URL | DESCRIPTION | PERMISSIONS |
|-----|-------------|-------------|
| /api/users/{id} | Removes a specific user | Admin |
| /api/appointments/{id} | Removes a specific appointment | Admin, Attendant |
| /api/surgeries/{id} | Removes a specific surgery | Admin, Attendant |
| /api/medical-records/{patientId}/remove-observations | Removes consultation observations from medical record | Admin, Health_Professional |
| /api/medical-records/{patientId}/remove-surgery-observations | Removes surgery observations from medical record | Admin, Health_Professional |
| /api/medical-records/{patientId} | Removes a specific medical record | Admin |
