# Hotel Management REST API

This repository contains a Spring Boot REST API for managing hotel reservations and rooms.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Endpoints](#endpoints)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Contributing](#contributing)
- [License](#license)

## Introduction

This project is a Spring Boot application that provides RESTful endpoints for creating and managing hotel reservations and rooms. It allows users to create new reservations, retrieve available rooms, and cancel existing reservations.

## Features

- Create new reservations
- Retrieve available rooms
- Cancel existing reservations

## Endpoints

- `POST /hotel/reservations`: Create a new reservation.
- `GET /hotel/rooms`: Retrieve available rooms.
- `DELETE /hotel/reservations/{reservationId}`: Cancel an existing reservation by ID.

## Technologies Used

- Spring Boot
- Spring Data JPA
- Maven
- Java

## Getting Started

To run this application locally, follow these steps:

1. Clone this repository to your local machine.
2. Open the project in your preferred IDE.
3. Make sure you have Java and Maven installed.
4. Configure the database connection in the `application.properties` file.
5. Run the application as a Spring Boot application.

## Contributing

Contributions are welcome! If you have any suggestions, bug reports, or feature requests, please open an issue or submit a pull request.


