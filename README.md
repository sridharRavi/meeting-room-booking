A robust backend application built with Spring Boot for managing meeting rooms, bookings, and cancellations with strong guarantees around idempotency, conflict detection, and data consistency.

**Features:**
* Create and manage meeting rooms
* Book rooms for specific time slots
* Cancel existing bookings
* Idempotent APIs to prevent duplicate operations
* Conflict detection to avoid overlapping bookings
* Smart validation for time ranges and booking logic
* REST API testing with integration support

**Tech Stack**
Backend: Spring Boot
Language: Java
Build Tool: Maven / Gradle
Database: postgreSQL
Testing: MockMvc

**Repo structure**
meeting-room-booking/
│── src/
│   ├── controller/       # REST Controllers
│   ├── service/          # Business logic
│   ├── repository/       # Data access layer
│   ├── model/            # Entities / DTOs
│   ├── exception/        # Custom exceptions
│   └── config/           # Configurations (if any)
│
│── test/                 # Unit & integration tests

**Room Management**
Method	Endpoint	Description
POST	/rooms	Create a new room
GET	/rooms	List all rooms

**Booking Management**
POST	/bookings	Book a room
DELETE	/bookings/{id}	Cancel a booking
GET	/bookings	View bookings
│── pom.xml / build.gradle

**Special features**
To ensure safe retries:
APIs accept an Idempotency Key (via header or request)
Duplicate requests with the same key:
Return the same response
Do not create duplicate bookings
Also has Conflict checks to return error responses for bookings at the same time

**Running the Application**
Download the repo and run the following command at the root (where pom.xml is present). Make sure you have maven and Java and Spring boot setuo in your system
mvn spring-boot:run

**Running tests**
mvn test (some tests are flaky due to mismatch in SB version and MockIt versions)
