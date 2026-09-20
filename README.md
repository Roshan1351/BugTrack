# BugTrack - Bug Tracking System

BugTrack is a role-based bug and project management system built with Spring Boot. It helps development teams report, assign, track, and manage software bugs throughout their lifecycle.

## Problem Statement

In software development teams, bugs are often difficult to track when they are managed through scattered messages, spreadsheets, or manual processes. This can lead to unclear ownership, missed deadlines, duplicate bug reports, and poor visibility into bug progress.

BugTrack provides a centralized system for managing projects, bugs, assignments, communication, attachments, and SLA deadlines with role-based access control.

## Tech Stack

* **Backend:** Java, Spring Boot, Spring Security, JWT
* **Database:** MySQL
* **Persistence:** Spring Data JPA, Hibernate
* **Frontend:** HTML, CSS, JavaScript, Bootstrap
* **Tools/Libraries:** Maven, Lombok, Apache POI, OpenPDF, Postman

## Key Features

* JWT authentication and role-based authorization
* Admin, Project Manager, Developer, and Tester roles
* Project and bug management
* Bug assignment and status tracking
* Bug history, comments, and notifications
* Duplicate bug detection
* SLA-based deadline tracking
* File attachments
* Pagination and sorting
* PDF and Excel reports
* REST APIs

## Project Structure

```text
src/main/java/com/bugtrack/bugtrack
│
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
├── service
└── util
```

The project follows a layered architecture:

```text
Controller → Service → Repository → Database
```

## Bug Workflow

```text
Tester → Report Bug → Assign → Developer → Fix → Tester → Close
```

## Run Locally

```bash
git clone https://github.com/Roshan1351/BugTrack.git
cd BugTrack
mvnw.cmd spring-boot:run
```

Configure MySQL and the required environment variables before running the application.

## Author

**Roshan Giri**

GitHub: https://github.com/Roshan1351
