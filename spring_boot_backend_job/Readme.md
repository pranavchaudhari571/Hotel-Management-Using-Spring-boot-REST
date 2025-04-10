
# Hotel Management System — Backend (Spring Boot)

This project is the **backend** component of a Hotel Management System built with Spring Boot. It provides user authentication, room and reservation management, and secure access control using **JWT**, **OAuth2**, **Kafka**, and **Redis**.

---

## 🔐 Authentication

- **JWT Token Authentication**: Stateless login for users and admins.
- **OAuth2 with Google**: Available at `/oauth2/authorization/google` for third-party login.
- **Role-Based Access**: Secured endpoints using `@PreAuthorize`.

---

## 📦 Features

### ✅ Authentication APIs
- `POST /auth/login` — JWT login
- `POST /auth/register` — user registration (default role: USER)

### 🏨 Room Management
- `POST /hotel/rooms` — create room
- `PUT /hotel/rooms` — update room
- `DELETE /hotel/rooms/{roomId}` — delete room
- `GET /hotel/rooms/all` — list all rooms
- `GET /hotel/rooms/available` — available rooms
- `GET /hotel/rooms/booked` — booked rooms
- `GET /hotel/rooms/{roomId}` — specific room

### 📆 Reservation Management
- `POST /hotel/reservations` — create reservation
- `PUT /hotel/reservations` — update reservation
- `DELETE /hotel/reservations/{id}` — cancel reservation
- `GET /hotel/reservations` — all reservations
- `GET /hotel/reservations/{id}` — reservation by ID

---

## ⚙️ Architecture

- **Spring Security**: Secures endpoints
- **JWT/OAuth2**: Dual authentication support
- **Spring Scheduling**: Monthly revenue report via scheduled tasks
- **Kafka**: Email triggers on reservation create/cancel
- **Redis Cache**: Optimized data retrieval
- **Rate Limiting**: Custom IP-based limits using Redis
- **Async Processing**: Background task execution
- **MySQL (Cloud)**: Production-ready relational DB

---

## 🧪 Testing & Reliability

- **Junit & Mockito**: For unit and integration testing
- **Global Exception Handling** with custom exceptions:
  - `ReservationNotFoundException`
  - `RoomNotAvailableException`
  - `InvalidJwtException`
  - `UserAlreadyExistsException`

---

## 🛠 Prerequisites

- Java 11+
- Maven
- MySQL (or any RDS)
- Redis
- Kafka (optional for email triggers)

---

## ▶️ Setup Instructions

```bash
# Clone
git clone https://github.com/pranavchaudhari571/Hotel-Management-Using-Spring-boot-REST.git
cd Hotel-Management-Using-Spring-boot-REST

# Build
mvn clean install

# Run
mvn spring-boot:run
```

App runs at: `http://localhost:8081`

---

## 🔄 Major Annotations Used

- `@SpringBootApplication`
- `@EnableCaching`
- `@EnableScheduling`
- `@EnableAsync`
- `@Transactional`
- `@PreAuthorize`
- `@RestController`, `@Service`, `@Repository`

---

## 📌 Notes

- All modifying APIs require **JWT** or **OAuth2** authentication
- API responses are in **JSON**
- CORS enabled via config
- Rate limiting allows **5 requests/IP/endpoint**
- Kafka topics:
  - `reservation-email-topic`
  - `reservation-cancellation-topic`

---

## 🪪 License

This project is licensed under the MIT License.

---

## 🙏 Acknowledgements

Thanks to the open-source community behind:
Spring Boot, Apache Kafka, Redis, MySQL, and OAuth2.

