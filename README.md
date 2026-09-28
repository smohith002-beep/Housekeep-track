# HouseKeepTrack – Hotel Housekeeping Task Assignment & Room Status Management System

**HouseKeepTrack** is an academic enterprise-grade Java / Spring Boot and MySQL web application designed to solve the real-world problem of hotel housekeeping task coordination and room lifecycle governance.

In many hotels, housekeeping staff clean rooms based on verbal or ad-hoc instructions, which frequently leads to guests checking in before a room has been properly sanitized and inspected. **HouseKeepTrack** eliminates this problem through a deterministic, transactional lifecycle engine:

$$\text{CHECKOUT} \longrightarrow \mathbf{DIRTY} \longrightarrow \mathbf{CLEANING} \longrightarrow \mathbf{INSPECTED} \longrightarrow \mathbf{READY} \longrightarrow \text{CHECK-IN}$$

If a supervisor inspection fails:
$$\mathbf{INSPECTED} \longrightarrow \mathbf{CLEANING} \longrightarrow \mathbf{INSPECTED} \longrightarrow \mathbf{READY}$$

---

## 1. Key Features & Business Rules Enforced

* **Rule 1: Deterministic Lifecycle Transitions**: Rooms cannot move randomly between statuses. Only valid state transitions are permitted:
  * `DIRTY` $\rightarrow$ `CLEANING`
  * `CLEANING` $\rightarrow$ `INSPECTED`
  * `INSPECTED` $\rightarrow$ `READY`
  * `INSPECTED` $\rightarrow$ `CLEANING` (if inspection fails)
  * `READY` $\rightarrow$ `DIRTY` (strictly upon guest checkout)
* **Rule 2: Booking Availability Verification**: Only chambers in `READY` status can be allocated to incoming guests. Attempting to book a `DIRTY`, `CLEANING`, or `INSPECTED` room throws a `RoomNotAvailableException` (HTTP 400).
* **Rule 3 & 4: Automated Checkout Pipeline**: When a guest checks out (`POST /api/bookings/{id}/checkout`):
  1. Booking status is set to `CHECKED_OUT`.
  2. Associated room transitions to `DIRTY`.
  3. A new `CleaningTask` is created automatically.
  4. An `AVAILABLE` housekeeper is identified and assigned.
  5. The housekeeper's status transitions to `BUSY`.
* **Rule 5: Single-Task Concurrency Control**: A housekeeper cannot receive a new assignment while actively working on a task.
* **Rule 6: Mandatory White-Glove Inspection**: A room cannot reach `READY` without passing a supervisor inspection.
* **Rule 7: Defect Remediation**: If inspection fails, the room reverts to `CLEANING` with supervisor remarks, and the cleaning task is marked `REOPENED`.
* **Rule 8: Transactional Integrity & Audit Trail**: All operations are secured with Spring `@Transactional` and recorded in the immutable `audit_logs` table.

---

## 2. Technology Stack

* **Backend**: Java 21 / 25, Spring Boot 3.3.4 (Spring Web, Spring Data JPA, Hibernate, Spring Security, Validation)
* **Database**: MySQL Server (`housekeeptrack` database)
* **Frontend**: Thymeleaf, Vanilla CSS (5-star luxury hotel design system), JavaScript (Fetch API, Chart.js)
* **Build Tool**: Apache Maven 3.9+

---

## 3. Database Schema

The database `housekeeptrack` contains the following tables:
1. `rooms` – Chamber inventory, floor, room type, status (`DIRTY`, `CLEANING`, `INSPECTED`, `READY`), price, capacity.
2. `housekeepers` – Staff roster, phone, email, duty status (`AVAILABLE`, `BUSY`, `OFF_DUTY`), current task ID.
3. `cleaning_tasks` – Task assignments, timestamps (`assigned_at`, `started_at`, `completed_at`), status (`ASSIGNED`, `IN_PROGRESS`, `COMPLETED`, `REOPENED`), notes.
4. `inspections` – Supervisor audit logs, room reference, supervisor name, status (`PASSED`, `FAILED`), defect remarks.
5. `guests` – Guest contact details and profile.
6. `bookings` – Reservations, check-in/out dates, status (`RESERVED`, `CHECKED_IN`, `CHECKED_OUT`, `CANCELLED`), room reference, total amount.
7. `audit_logs` – Entity change log (`entity_type`, `entity_id`, `action`, `old_value`, `new_value`, `changed_by`, `changed_at`).

---

## 4. Main Website Routes

* **Homepage**: `http://localhost:8080/` (Hero section, search panel, live database telemetry, room highlights, facilities)
* **Chambers Catalog**: `http://localhost:8080/rooms` (Filter by status, search by room number, add new room)
* **Visual Status Board**: `http://localhost:8080/room-status` (Visual color-coded room status cards: Green READY, Red DIRTY, Orange CLEANING, Blue INSPECTED)
* **Housekeeping Staff**: `http://localhost:8080/housekeepers` (Staff workload, duty status toggle, add staff)
* **Cleaning Tasks**: `http://localhost:8080/cleaning-tasks` (Start cleaning, complete cleaning, reopen tasks)
* **Supervisor Inspections**: `http://localhost:8080/inspections` (Audit pending rooms, pass/fail with remarks)
* **Guest Bookings**: `http://localhost:8080/bookings` (Booking form with READY validation, guest checkout action)
* **Executive Dashboard**: `http://localhost:8080/admin/dashboard` (10 KPI cards, interactive Chart.js charts, recent activity)
* **Turnaround Reports**: `http://localhost:8080/reports` (Average room turnaround time $completedAt - assignedAt$, workload balance)
* **System Audit Trail**: `http://localhost:8080/audit-logs` (Audit history)
* **Gallery & Contact**: `http://localhost:8080/gallery`, `http://localhost:8080/contact`
* **Staff Login**: `http://localhost:8080/login` (Includes 1-click test role credentials)

---

## 5. Security & Roles

Pre-configured in-memory authentication:
* **Admin**: `admin` / `admin123` (Full system access)
* **Supervisor**: `supervisor` / `supervisor123` (Rooms, cleaning tasks, inspections, reports)
* **Housekeeper**: `housekeeper` / `housekeeper123` (Cleaning tasks, start/complete tasks)
* **Receptionist**: `receptionist` / `receptionist123` (Rooms, bookings, guests, checkouts)

*Note: All `/api/**` REST endpoints have CSRF disabled to support frictionless Postman testing.*

---

## 6. How to Run the Application

### Prerequisites:
1. Ensure MySQL is running on port 3306 with credentials `root` / `root`.
2. Database `housekeeptrack` will be created or updated automatically by Hibernate.

### Build and Run:
```bash
mvn clean compile
mvn spring-boot:run
```

Access the application in your browser:
```
http://localhost:8080
```

---

## 7. Postman API Testing

Import the included `HouseKeepTrack.postman_collection.json` file into Postman or consult [POSTMAN_GUIDE.md](file:///c:/Users/MOHITH%20S/Downloads/house%20mentroing/POSTMAN_GUIDE.md) for full request specifications and sample payloads.
