# Readme

## Hotel Management System

This project is a **Hotel Management System** built with **Spring Boot**. It includes user authentication, room management, reservation management, and more. The application uses **JWT and OAuth2 for token-based authentication**, **Spring Security** for securing endpoints, and **Kafka for event-driven processing**.

## Features

### Authentication

- **JWT Token-based Authentication**: The system uses JSON Web Tokens (JWT) for authenticating users.
- **OAuth2 with Google Authentication**: Users can log in via Google using `http://localhost:8081/oauth2/authorization/google`.
- **User Registration**: New users can be registered with a default 'USER' role if not provided.
- **Login**: Users can log in to receive a JWT token.


### Event-Driven Architecture with Kafka

- The system uses **Apache Kafka** to handle reservation events:
  - `reservation-email-topic`: Triggers email notifications when a reservation is made.
  - `reservation-cancellation-topic`: Triggers email notifications when a reservation is canceled.

### Scheduled Tasks

- The system has scheduled tasks using **Spring Scheduling** (`@EnableScheduling`).
- A **monthly revenue report** is generated and sent to the admin every **5th of the month at 10 AM**.

### Performance Enhancements

- **ThreadPoolTaskExecutor** is used for handling async tasks efficiently (`@EnableAsync`).
- **Redis Cache** is integrated for **faster data retrieval** and optimized performance (`@EnableCaching`).
- **Rate Limiting** is applied to prevent abuse, allowing only **5 requests per IP per endpoint**.

## Technologies Used

- **Spring Boot**: The main framework for building the application.
- **Spring Security**: For securing the endpoints and managing authentication.
- **JWT & OAuth2**: For token-based authentication.
- **Spring Data JPA**: For database interaction and CRUD operations.
- **Redis**: For faster retrieval and remote cache management.
- **Apache Kafka**: For event-driven communication.
- **Spring Scheduling**: For running scheduled tasks automatically.
- **Spring Async**: For executing background tasks asynchronously.
- **Junit & Mockito**: For unit and integration testing.
- **Lombok**: For reducing boilerplate code.
- **SLF4J**: For logging.
- **Online Database (MySQL)**: A cloud-based database is used for live data storage.


### Reservation Management

- **Create Reservation**: Allows guests to create room reservations.
- **Update Reservation**: Allows users to modify their reservations.
- **Cancel Reservation**: Allows users to cancel existing reservations.
- **Get All Reservations**: Fetches all reservations made in the system.
- **Get Reservation by ID**: Fetches a reservation by its ID.

### Room Management

- **Create Room**: Allows the creation of new rooms in the system.
- **Update Room**: Allows updating the details of existing rooms.
- **Delete Room**: Allows the deletion of rooms.
- **Get All Rooms**: Fetches all rooms available in the system.
- **Get Available Rooms**: Fetches only rooms that are available for booking.
- **Get Room by ID**: Fetches details of a specific room by its ID.
- **Get Booked Rooms**: Lists rooms that are currently booked.

## Prerequisites

To run this project, ensure you have the following installed:

- Java 8 or later
- Maven

## Setup Instructions

### Clone the Repository

```bash
git clone https://github.com/pranavchaudhari571/Hotel-Management-Using-Spring-boot-REST.git
cd Hotel-Management-Using-Spring-boot-REST.git
```

### Build and Run the Application

Use Maven to build the project:

```bash
mvn clean install
```

Run the Spring Boot application:

```bash
mvn spring-boot:run
```

The application will start on the default port **8081**. You can now access the API through `http://localhost:8081`.

## API Documentation

### Authentication Endpoints

#### **Login**

`POST /auth/login`: Logs in a user and returns a JWT token.

**Request Body:**

```json
{
  "username": "user",
  "password": "password"
}
```

**Response:**

```json
{
  "token": "your_jwt_token"
}
```

#### **User Registration**

`POST /auth/register`: Registers a new user with the system.

**Request Body:**

```json
{
  "username": "newuser",
  "password": "newpassword",
  "role": "USER"
}
```

### Reservation Endpoints

- `POST /hotel/reservations`: Creates a new reservation.
- `PUT /hotel/reservations`: Updates an existing reservation.
- `DELETE /hotel/reservations/{reservationId}`: Cancels a reservation.
- `GET /hotel/reservations`: Retrieves all reservations.
- `GET /hotel/reservations/{reservationId}`: Retrieves a specific reservation by ID.

### Room Endpoints

- `POST /hotel/rooms`: Creates a new room.
- `PUT /hotel/rooms`: Updates an existing room.
- `DELETE /hotel/rooms/{roomId}`: Deletes a room.
- `GET /hotel/rooms/all`: Retrieves all rooms.
- `GET /hotel/rooms/available`: Retrieves available rooms.
- `GET /hotel/rooms/{roomId}`: Retrieves a specific room by ID.
- `GET /hotel/rooms/booked`: Retrieves rooms that are currently booked.

## Major Annotations Used

- `@SpringBootApplication`: Marks the main class of a Spring Boot application.
- `@EnableCaching`: Enables caching in the application (used for Redis).
- `@EnableScheduling`: Enables scheduled tasks.
- `@EnableAsync`: Enables asynchronous method execution.
- `@Transactional`: Ensures transactional integrity.
- `@RestController`: Defines a controller with RESTful endpoints.
- `@Service`: Marks a service layer component.
- `@Repository`: Marks a repository for database interaction.
- `@Scheduled`: Defines scheduled tasks.
- `@Async`: Marks methods to be executed asynchronously.
- `@PreAuthorize`: Restricts access to endpoints based on roles.

## Exception Handling

The system uses a **global exception handler** to handle API errors gracefully.

- **Custom exceptions** include:
    - `ReservationNotFoundException`
    - `RoomNotAvailableException`
    - `UserAlreadyExistsException`
    - `InvalidJwtException`
- The global exception handler returns standardized error responses.

## Notes

- All requests that modify data (**POST, PUT, DELETE**) require authentication via JWT or OAuth2.
- API responses are in JSON format.
- The system is **fully cloud-based**, with an online **MySQL database** and **Redis caching**.
- **Apache Kafka** handles event-driven communication for reservation changes.
- **Rate Limiting** ensures protection against excessive API calls.
- **Comprehensive unit tests** ensure system reliability.

## License

This project is licensed under the **MIT License** - see the `LICENSE` file for details.

## Acknowledgements

- Inspired by various open-source hotel management systems.
- Special thanks to the creators of **Spring Boot, Redis, Apache Kafka**, and other technologies used.

