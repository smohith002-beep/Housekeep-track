# HouseKeepTrack Hotel – Complete REST API CRUD & Postman Guide

**Base URL**: `http://localhost:8080`

The system provides complete, real-time MySQL-persisted CRUD (GET, POST, PUT, DELETE) operations across all 7 major entities, plus automated lifecycle workflows.

You can import `HouseKeepTrack.postman_collection.json` directly into Postman or use the specifications below.

---

## 1. Room CRUD (`/api/rooms`)

### 1.1 GET All Rooms
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/rooms`
* **Response**: `200 OK` (Array of Room objects)

### 1.2 GET Room by ID
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/rooms/1`
* **Response**: `200 OK`

### 1.3 POST Create Room
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/rooms`
* **Headers**: `Content-Type: application/json`
* **Request Body**:
```json
{
  "roomNumber": "205",
  "floor": 2,
  "roomType": "DELUXE",
  "status": "READY",
  "capacity": 2
}
```
* **Response**: `201 Created`
```json
{
  "success": true,
  "message": "Room created successfully",
  "data": {
    "id": 9,
    "roomNumber": "205",
    "floor": 2,
    "roomType": "DELUXE",
    "status": "READY",
    "capacity": 2,
    "pricePerNight": 18000.0
  }
}
```

### 1.4 PUT Update Room
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/rooms/1`
* **Headers**: `Content-Type: application/json`
* **Request Body**:
```json
{
  "roomNumber": "205",
  "floor": 2,
  "roomType": "SUITE",
  "status": "READY",
  "capacity": 3
}
```
* **Response**: `200 OK`
```json
{
  "success": true,
  "message": "Room updated successfully",
  "data": {
    "id": 1,
    "roomNumber": "205",
    "floor": 2,
    "roomType": "SUITE",
    "status": "READY",
    "capacity": 3
  }
}
```

### 1.5 DELETE Room
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/rooms/9`
* **Response**: `204 No Content`
* **Foreign-Key Protection**: If room has active bookings, cleaning tasks, or inspections:
  * **Response**: `409 Conflict`
  ```json
  {
    "status": 409,
    "message": "Cannot delete Room 102 because it has 1 associated booking(s) 1 cleaning task(s)... Please remove dependencies first."
  }
  ```

### 1.6 PUT Room Status Transition
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/rooms/2/status`
* **Headers**: `Content-Type: application/json`
* **Request Body**: `{"status": "CLEANING"}`
* **Response**: `200 OK` (or `400 Bad Request` if transition is invalid)

---

## 2. Housekeeper CRUD (`/api/housekeepers`)

### 2.1 GET All Housekeepers
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/housekeepers`
* **Response**: `200 OK`

### 2.2 GET Housekeeper by ID
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/housekeepers/1`
* **Response**: `200 OK`

### 2.3 POST Create Housekeeper
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/housekeepers`
* **Headers**: `Content-Type: application/json`
* **Request Body**:
```json
{
  "name": "Priya",
  "phone": "9876543210",
  "email": "priya@housekeeptrack.com",
  "status": "AVAILABLE"
}
```
* **Response**: `201 Created`

### 2.4 PUT Update Housekeeper
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/housekeepers/2`
* **Headers**: `Content-Type: application/json`
* **Request Body**:
```json
{
  "name": "Priya Sharma",
  "phone": "9876543210",
  "email": "priya.sharma@housekeeptrack.com",
  "status": "AVAILABLE"
}
```
* **Response**: `200 OK`

### 2.5 DELETE Housekeeper
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/housekeepers/5`
* **Response**: `204 No Content` (Protected against active task references)

---

## 3. Cleaning Task CRUD & Workflows (`/api/cleaning-tasks`)

### 3.1 GET All Tasks
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/cleaning-tasks`
* **Response**: `200 OK`

### 3.2 GET Task by ID
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/cleaning-tasks/1`
* **Response**: `200 OK`

### 3.3 POST Create Cleaning Task
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/cleaning-tasks`
* **Headers**: `Content-Type: application/json`
* **Request Body**:
```json
{
  "roomId": 1,
  "housekeeperId": 2,
  "status": "ASSIGNED",
  "notes": "Complete room cleaning"
}
```
* **Response**: `201 Created`

### 3.4 PUT Update Cleaning Task
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/cleaning-tasks/1`
* **Headers**: `Content-Type: application/json`
* **Request Body**: `{"notes": "Priority VIP cleaning required."}`
* **Response**: `200 OK`

### 3.5 DELETE Cleaning Task
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/cleaning-tasks/10`
* **Response**: `204 No Content`

### 3.6 Workflow Endpoints
* **Start Task**: `PUT http://localhost:8080/api/cleaning-tasks/{id}/start` $\rightarrow$ sets task to `IN_PROGRESS`, sets room to `CLEANING`.
* **Complete Task**: `PUT http://localhost:8080/api/cleaning-tasks/{id}/complete` $\rightarrow$ sets task to `COMPLETED`, sets room to `INSPECTED`.
* **Reopen Task**: `PUT http://localhost:8080/api/cleaning-tasks/{id}/reopen` $\rightarrow$ sets task to `REOPENED`, sets room to `CLEANING`.

---

## 4. Inspection CRUD & Workflows (`/api/inspections`)

### 4.1 GET All Inspections
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/inspections`
* **Response**: `200 OK`

### 4.2 GET Inspection by ID
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/inspections/1`
* **Response**: `200 OK`

### 4.3 POST Create Inspection
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/inspections`
* **Headers**: `Content-Type: application/json`
* **Request Body**:
```json
{
  "roomId": 1,
  "supervisorName": "Manager",
  "inspectionStatus": "PASSED",
  "remarks": "Room is clean"
}
```
* **Response**: `201 Created`

### 4.4 PUT Update Inspection
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/inspections/1`
* **Headers**: `Content-Type: application/json`
* **Request Body**: `{"remarks": "Verified white-glove inspection passed."}`
* **Response**: `200 OK`

### 4.5 DELETE Inspection
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/inspections/10`
* **Response**: `204 No Content`

### 4.6 Pass / Fail Workflows
* **Pass Inspection**: `POST http://localhost:8080/api/inspections/{id}/pass`
  * Room $\rightarrow$ `READY`, CleaningTask $\rightarrow$ `COMPLETED`, Housekeeper $\rightarrow$ `AVAILABLE`.
* **Fail Inspection**: `POST http://localhost:8080/api/inspections/{id}/fail`
  * Room $\rightarrow$ `CLEANING`, CleaningTask $\rightarrow$ `REOPENED`, Housekeeper remains `BUSY`.

---

## 5. Guest CRUD (`/api/guests`)

### 5.1 GET All Guests
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/guests`
* **Response**: `200 OK`

### 5.2 GET Guest by ID
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/guests/1`
* **Response**: `200 OK`

### 5.3 POST Create Guest
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/guests`
* **Headers**: `Content-Type: application/json`
* **Request Body**:
```json
{
  "name": "Arun Kumar",
  "phone": "9876543210",
  "email": "arun@example.com",
  "address": "Coimbatore"
}
```
* **Response**: `201 Created`

### 5.4 PUT Update Guest
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/guests/1`
* **Headers**: `Content-Type: application/json`
* **Request Body**: `{"address": "Race Course Road, Coimbatore"}`
* **Response**: `200 OK`

### 5.5 DELETE Guest
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/guests/10`
* **Response**: `204 No Content` (Protected against active reservations)

---

## 6. Booking CRUD & Checkout (`/api/bookings`)

### 6.1 GET All Bookings
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/bookings`
* **Response**: `200 OK`

### 6.2 GET Booking by ID
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/bookings/1`
* **Response**: `200 OK`

### 6.3 POST Create Booking
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/bookings`
* **Headers**: `Content-Type: application/json`
* **Request Body**:
```json
{
  "guestId": 1,
  "roomId": 1,
  "checkInDate": "2026-09-29",
  "checkOutDate": "2026-10-01",
  "bookingStatus": "RESERVED"
}
```
* **Success Response**: `201 Created`
* **Non-Ready Room Conflict (Section 9)**:
  * When room status is `DIRTY`, `CLEANING`, or `INSPECTED`:
  * **Response**: `409 Conflict`
  ```json
  {
    "status": 409,
    "message": "Room 102 is not ready for booking"
  }
  ```

### 6.4 PUT Update Booking
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/bookings/1`
* **Headers**: `Content-Type: application/json`
* **Request Body**: `{"bookingStatus": "CHECKED_IN"}`
* **Response**: `200 OK`

### 6.5 DELETE Booking
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/bookings/10`
* **Response**: `204 No Content`

### 6.6 POST Checkout Guest (Section 10)
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/bookings/1/checkout`
* **Response**: `200 OK`
```json
{
  "message": "Checkout completed and cleaning task assigned (Housekeeper Rahul assigned successfully.)",
  "roomNumber": "202",
  "roomStatus": "DIRTY",
  "taskId": 5,
  "housekeeper": "Rahul",
  "taskStatus": "ASSIGNED"
}
```

---

## 7. Audit Log CRUD (`/api/audit-logs`)

### 7.1 GET All Audit Logs
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/audit-logs`
* **Response**: `200 OK`

### 7.2 GET Audit Log by ID
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/audit-logs/1`
* **Response**: `200 OK`

### 7.3 POST Create Audit Log
* **Method**: `POST`
* **URL**: `http://localhost:8080/api/audit-logs`
* **Headers**: `Content-Type: application/json`
* **Request Body**:
```json
{
  "entityType": "ROOM",
  "entityId": 101,
  "action": "MANUAL_AUDIT",
  "oldValue": "DIRTY",
  "newValue": "CLEANING",
  "changedBy": "ADMIN"
}
```
* **Response**: `201 Created`

### 7.4 PUT Update Audit Log
* **Method**: `PUT`
* **URL**: `http://localhost:8080/api/audit-logs/1`
* **Headers**: `Content-Type: application/json`
* **Request Body**: `{"action": "MANUAL_AUDIT_VERIFIED"}`
* **Response**: `200 OK`

### 7.5 DELETE Audit Log
* **Method**: `DELETE`
* **URL**: `http://localhost:8080/api/audit-logs/1`
* **Response**: `204 No Content`

---

## 8. Dashboard Statistics API (Section 20)

### 8.1 GET Dashboard Statistics
* **Method**: `GET`
* **URL**: `http://localhost:8080/api/dashboard/statistics`
* **Response**: `200 OK`
```json
{
  "totalRooms": 8,
  "readyRooms": 4,
  "dirtyRooms": 2,
  "cleaningRooms": 1,
  "inspectionRooms": 1,
  "availableHousekeepers": 1,
  "busyHousekeepers": 4,
  "activeTasks": 3,
  "completedTasks": 1
}
```
*(Also available at `GET http://localhost:8080/api/dashboard/stats` for front-end charts)*
