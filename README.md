# Hotel Management System

This project is a **Hotel Management System** built using **Spring Boot** and RESTful APIs. It includes core features such as room and reservation management, along with advanced functionalities like Java Mail Service integration, Redis caching, and unit testing.

## Features

### 1. Room Management

- **Create Room**: Add new rooms to the system.
- **Update Room**: Modify details of existing rooms.
- **Delete Room**: Remove a room from the system.
- **Fetch All Rooms**: Retrieve a list of all rooms.
- **Fetch Available Rooms**: Retrieve a list of currently available rooms.
- **Fetch Booked Rooms**: Retrieve a list of rooms that are booked.
- **Fetch Room by ID**: Get details of a specific room.

### 2. Reservation Management

- **Create Reservation**: Book a room for a guest.
- **Update Reservation**: Modify an existing reservation.
- **Cancel Reservation**: Cancel a reservation by ID.
- **Fetch All Reservations**: Retrieve all reservations.
- **Fetch Reservation by ID**: Get details of a specific reservation.

### 3. Advanced Features

- **Java Mail Service**: Send email notifications for booking confirmations, updates, and cancellations.
- **Redis Cache**: Improve performance by caching frequently accessed data.
- **Unit Testing**: Comprehensive test cases to ensure reliability and correctness.

## Technology Stack

- **Backend**: Java, Spring Boot
- **Caching**: Redis
- **Testing**: JUnit, Mockito
- **Build Tool**: Maven
- **Database**: MySQL (or any other relational database)

## API Endpoints

### Reservation Controller

#### Base URL: `/hotel/reservations`

- **POST** `/` - Create a new reservation.
- **PUT** `/` - Update an existing reservation.
- **DELETE** `/{reservationId}` - Cancel a reservation by ID.
- **GET** `/` - Retrieve all reservations.
- **GET** `/{reservationId}` - Fetch reservation details by ID.

### Room Controller

#### Base URL: `/hotel/rooms`

- **POST** `/` - Create a new room.
- **PUT** `/` - Update room details.
- **DELETE** `/{roomId}` - Delete a room by ID.
- **GET** `/all` - Retrieve all rooms.
- **GET** `/available` - Fetch available rooms.
- **GET** `/{roomId}` - Fetch room details by ID.
- **GET** `/booked` - Fetch booked rooms.

## How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/pranavchaudhari571/Hotel-Management-Using-Spring-boot-REST.git
   ```
2. Navigate to the project directory:
   ```bash
   cd Hotel-Management-Using-Spring-boot-REST
   ```
3. Build the project using Maven:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot: run
   ```
5. Access the application via:
   ```bash
   
    http://localhost:8081/swagger-ui.html

   ```

## Unit Testing

Run unit tests with:

```bash
mvn test
```

## Contributions

Contributions are welcome! Feel free to submit a pull request or open an issue for suggestions or bug reports.

## License

This project is licensed under the MIT License. See the LICENSE file for details.

---

### Contact

For queries or suggestions, please reach out to pranav.chaudhari571@gmail.com.
