# FESTPASS — College Fest Ticketing with QR Check-in

A simple, clean, and professional digital ticketing and entrance check-in web application for college fests and university events. FestPass replaces paper tickets with cryptographically unique digital tickets, instantaneous QR code verification at entrance gates, capacity safety guardrails, and real-time attendance analytics.

---

## 📌 Problem Statement

College festivals traditionally rely on physical paper passes or manual attendee lists, resulting in critical operational challenges:
1. **Ticket Duplication**: Paper passes and screenshots can be photocopied or duplicated.
2. **Unauthorized Resale & Reuse**: Without digital state tracking, the same ticket can be passed outside the gate and reused multiple times.
3. **Overcrowding & Safety Risks**: Venues have fixed fire and physical capacities. Without real-time capacity enforcement, venues can be dangerously overwhelmed.
4. **Blind Attendance Counts**: Organizers cannot reliably track how many attendees have arrived versus tickets sold.
5. **Slow Check-in Queues**: Manual lookup slows gate throughput and creates bottlenecks.

---

## 🚀 FestPass Solution & Key Features

- **College Fest Event Management**: Create, view, edit, and delete events with date, venue, capacity, ticket price, and status (`UPCOMING`, `ACTIVE`, `COMPLETED`, `CANCELLED`).
- **Student & Attendee Directory**: Register students with name, email, phone, college name, and unique student ID.
- **Digital Ticket Issuance**:
  - Automatically generates a unique ticket number (e.g. `FP-2026-000001`).
  - Generates a unique, non-guessable QR token (e.g. `FP-QR-XXXXXX`).
  - Strict Service-Layer **Capacity Enforcement** prevents overselling.
- **ZXing QR Code Engine**:
  - Dynamically encodes the unique ticket token into high-density QR code images using Google ZXing.
  - Generates inline Base64 data URIs and PNG image streams.
- **One-Time QR Check-in**:
  - Entrance validation verifies QR validity in real time.
  - Enforces strict one-time check-in: active tickets are marked as `USED` with timestamp, and duplicate check-in attempts are rejected with clear error feedback.
- **Real-Time Attendance Dashboard**:
  - Live metric cards (Total Events, Tickets Issued, Checked In, Available Seats).
  - Visual attendance progress bars showing `Checked-in / Capacity` and percentage.
  - Live recent check-ins log table.
- **Financial & Attendance Reports**:
  - Event-by-event breakdown of sold tickets, checked-in count, remaining capacity, attendance percentage, and event revenue.
  - Aggregated metrics: Total Tickets Sold, Average Ticket Price, Total Fest Revenue.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 / Java 27 compatible |
| **Framework** | Spring Boot 3.3.4 (Jakarta EE 10) |
| **Build Tool** | Apache Maven 3.9+ (includes `mvnw` wrapper) |
| **ORM & Data** | Spring Data JPA, Hibernate ORM |
| **Database** | MySQL 8.0+ (with H2 test database included) |
| **Validation** | Jakarta Bean Validation |
| **QR Code Engine**| ZXing Core & JavaSE 3.5.3 |
| **Frontend** | Semantic HTML5, Vanilla CSS, Vanilla JavaScript, Bootstrap 5.3, Bootstrap Icons |

---

## 🗄️ Database Design (`festpass_db`)

```
   +-------------------+              +----------------------+
   |    FestEvent      |              |       Attendee       |
   +-------------------+              +----------------------+
   | id (PK)           |              | id (PK)              |
   | event_name        |              | name                 |
   | description       |              | email                |
   | event_date        |              | phone                |
   | venue             |              | college_name         |
   | capacity          |              | student_id           |
   | ticket_price      |              | created_at           |
   | status            |              +----------------------+
   | created_at        |                         |
   +-------------------+                         | 1
             | 1                                 |
             |                                   | *
             | *                                 ↓
             +--------------> +----------------------+
                              |        Ticket        |
                              +----------------------+
                              | id (PK)              |
                              | ticket_number (UQ)   |
                              | qr_code (UQ)         |
                              | event_id (FK)        |
                              | attendee_id (FK)     |
                              | ticket_price         |
                              | status               |
                              | issued_at            |
                              | checked_in_at        |
                              +----------------------+
```

---

## ⚙️ MySQL Configuration & Setup

### 1. Database Creation
Make sure MySQL 8.0 is running on `localhost:3306`. Create the database if needed:
```sql
CREATE DATABASE IF NOT EXISTS festpass_db;
```
*(Note: Spring Boot's connection string includes `createDatabaseIfNotExist=true` so it can also create the database automatically).*

### 2. Configure Database Password in `application.properties`
Open `src/main/resources/application.properties` and replace `YOUR_PASSWORD` with your local MySQL `root` password:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/festpass_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.open-in-view=false
```

---

## 💻 How to Open and Run in IntelliJ IDEA

1. **Launch IntelliJ IDEA**:
   - Click **File** ➔ **Open...**
   - Navigate to the `SEM - Java project` folder and click **OK**.
2. **Automatic Maven Import**:
   - IntelliJ IDEA will recognize `pom.xml`.
   - If prompted, click **Trust Project** and **Load Maven Project**.
   - Maven will automatically download all dependencies (Spring Boot, MySQL Connector, ZXing).
3. **Verify Project SDK**:
   - Go to **File** ➔ **Project Structure** (or press `Ctrl+Alt+Shift+S`).
   - Under **Project**, ensure SDK is set to **Java 21** or **Java 27**.
4. **Update MySQL Password**:
   - Open `src/main/resources/application.properties`.
   - Replace `YOUR_PASSWORD` with your MySQL password.
5. **Run the Application**:
   - Navigate to `src/main/java/com/festpass/FestPassApplication.java`.
   - Click the green **Run ▶** icon next to `public static void main(String[] args)`.
   - Once the console displays `Started FestPassApplication in X.XXX seconds`, the server is running.
6. **Access FestPass in Browser**:
   - Open your web browser and navigate to:
     ```
     http://localhost:8080
     ```

---

## 📦 Running via Command Line (Maven Wrapper)

You can also run FestPass directly using the bundled Maven wrapper without needing Maven pre-installed:

### Windows (PowerShell or CMD):
```powershell
# Run automated tests
.\mvnw test

# Start the Spring Boot application
.\mvnw spring-boot:run
```

### Linux / macOS:
```bash
./mvnw test
./mvnw spring-boot:run
```

---

## 🔒 Core Business Rules

### 1. Capacity Restriction (Service Layer)
- Enforced in `TicketService.java`:
  ```java
  long currentTicketsIssued = ticketRepository.countByEventIdAndStatusNot(event.getId(), TicketStatus.CANCELLED);
  if (currentTicketsIssued >= event.getCapacity()) {
      throw new EventCapacityExceededException("Ticket sales closed. Event capacity has been reached.");
  }
  ```
- Any attempt to issue a ticket once `currentTicketsIssued >= event.capacity` is rejected with HTTP 400 and clear error messaging.

### 2. One-Time Entrance Check-In (Service Layer)
- Enforced in `CheckInService.java`:
  ```java
  Ticket ticket = ticketRepository.findByQrCode(qrToken)
          .orElseThrow(() -> new InvalidQrCodeException("Invalid QR code. No valid ticket was found."));

  if (ticket.getStatus() == TicketStatus.USED) {
      throw new TicketAlreadyUsedException("Ticket has already been used at " + ...);
  }

  ticket.setStatus(TicketStatus.USED);
  ticket.setCheckedInAt(LocalDateTime.now());
  ticketRepository.save(ticket);
  ```
- A ticket can only be validated once. Subsequent scans will be rejected with `⚠ TICKET ALREADY USED`. Non-existent tokens are rejected with `✕ INVALID QR CODE`.

---

## 🌐 REST API Endpoints

### 1. Fest Events (`/api/events`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/events` | Create a new fest event | `201 CREATED` |
| `GET` | `/api/events` | List all fest events with attendance stats | `200 OK` |
| `GET` | `/api/events/{id}` | Get event details by ID | `200 OK` |
| `PUT` | `/api/events/{id}` | Update event details | `200 OK` |
| `DELETE`| `/api/events/{id}` | Delete event (if no tickets issued) | `200 OK` |
| `GET` | `/api/events/{id}/attendance` | Get real-time event attendance | `200 OK` |

### 2. Attendees (`/api/attendees`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/attendees` | Register an attendee | `201 CREATED` |
| `GET` | `/api/attendees` | List all registered attendees | `200 OK` |
| `GET` | `/api/attendees/{id}` | Get attendee details by ID | `200 OK` |

### 3. Tickets (`/api/tickets`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/tickets` | Issue ticket with capacity check & QR generation | `201 CREATED` |
| `GET` | `/api/tickets` | List all issued tickets | `200 OK` |
| `GET` | `/api/tickets/{id}` | Get ticket details by ID | `200 OK` |
| `GET` | `/api/tickets/number/{ticketNumber}` | Get ticket by ticket number | `200 OK` |
| `GET` | `/api/tickets/qr/{qrToken}` | Lookup ticket by QR token | `200 OK` |
| `GET` | `/api/tickets/{id}/qr-image` | Stream ZXing QR code image as PNG | `200 OK` |

### 4. QR Check-In (`/api/check-in`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `POST` | `/api/check-in` | Validate and check-in QR code token | `200 OK` / `400 BAD REQUEST` |
| `GET` | `/api/check-in/recent` | Get list of recent entrance check-ins | `200 OK` |

### 5. Reports & Analytics (`/api`)
| Method | Endpoint | Description | Status Code |
|---|---|---|---|
| `GET` | `/api/dashboard/stats` | Overall dashboard metrics & recent logs | `200 OK` |
| `GET` | `/api/reports` | Detailed event revenue & attendance report | `200 OK` |

---

## 🧪 Sample Pre-loaded Data

Upon initial startup, `DataInitializer` seeds:
- **Events**:
  1. `TechFest 2026` (Capacity: 500, Price: ₹150, Venue: Main Auditorium, Status: ACTIVE)
  2. `Cultural Fest 2026` (Capacity: 800, Price: ₹100, Venue: Open Air Theatre, Status: UPCOMING)
  3. `GameCon Arena 2026` (Capacity: 300, Price: ₹200, Venue: Indoor Sports Complex, Status: ACTIVE)
- **Attendees**: Rahul Kumar, Priya Sharma, Arun Kumar, Sneha Raj.
- **Sample Tickets**:
  - `FP-2026-000001` (Rahul Kumar - ACTIVE) ➔ Ready to test successful check-in.
  - `FP-2026-000002` (Priya Sharma - USED) ➔ Ready to test duplicate check-in rejection.
  - `FP-2026-000003` (Arun Kumar - ACTIVE)
- The database is not duplicated on server restart.

---

## ❓ Troubleshooting

1. **Access denied for user 'root'@'localhost'**:
   - Open `src/main/resources/application.properties` and ensure `spring.datasource.password` matches your MySQL root password.
2. **Port 8080 already in use**:
   - Change `server.port=8080` in `application.properties` to another port such as `server.port=8081`.
3. **Java SDK version error in IntelliJ**:
   - Open `File ➔ Project Structure ➔ Project` and set SDK to JDK 21 or JDK 27.
