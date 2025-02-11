# Readme
## Hotel Management System

This project is a simple **Hotel Management System** built with **Spring Boot**. It includes user authentication, room management, reservation management, and more. The application uses **JWT for token-based authentication** and **Spring Security** for securing endpoints.

## Features

### Authentication

- **JWT Token-based Authentication**: The system uses JSON Web Tokens (JWT) for authenticating users.
- **User Registration**: New users can be registered with a default 'USER' role if not provided.
- **Login**: Users can log in to receive a JWT token.

### Reservation Management

- **Create Reservation**: Allows guests to create room reservations.
- **Update Reservation**: Allows users to modify their reservations.
- **Cancel Reservation**: Allows users to cancel existing reservations.
- **Get All Reservations**: Fetches all the reservations made in the system.
- **Get Reservation by ID**: Fetches a reservation by its ID.

### Room Management

- **Create Room**: Allows the creation of new rooms in the system.
- **Update Room**: Allows updating the details of existing rooms.
- **Delete Room**: Allows the deletion of rooms.
- **Get All Rooms**: Fetches all rooms available in the system.
- **Get Available Rooms**: Fetches only rooms that are available for booking.
- **Get Room by ID**: Fetches details of a specific room by its ID.
- **Get Booked Rooms**: Lists rooms that are currently booked.

## Technologies Used

- **Spring Boot**: The main framework for building the application.
- **Spring Security**: For securing the endpoints and managing authentication.
- **JWT (JSON Web Token)**: For token-based user authentication.
- **Spring Data JPA**: For database interaction and CRUD operations.
- **Redis**: For faster retrieval and online remote cache management.
- **Junit & Mockito**: For writing unit and integration tests.
- **Lombok**: For reducing boilerplate code with annotations like `@Getter`, `@Setter`, etc.
- **SLF4J**: For logging.
- **Online Database**: The system uses a remote online database, eliminating the need for local database installation.

## Prerequisites

To run this project, ensure you have the following installed:

- Java 8 or later
- Maven

## Setup Instructions

### Clone the Repository

```bash
git clone https://github.com/yourusername/hotel-management-system.git
cd hotel-management-system
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

## Notes

- All requests that modify data (**POST, PUT, DELETE**) require authentication via JWT token.
- The API responses are in JSON format.
- The system uses a **remote online database**, so no local installation of MySQL or any other database is required.
- Redis caching is used for optimizing performance and fast retrieval of data.
- Comprehensive **Junit and Mockito** test cases ensure the stability and correctness of the system.

## License

This project is licensed under the **MIT License** - see the `LICENSE` file for details.

## Acknowledgements

- Inspired by various open-source hotel management systems.
- Special thanks to the creators of Spring Boot, Redis, and other technologies used.

