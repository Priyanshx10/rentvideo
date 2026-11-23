# RentVideo – Video Rental Management System

A backend REST API built using **Java, Spring Boot, Spring Security, JWT, Spring Data JPA, and MySQL**. The application provides secure user authentication, role-based authorization, video inventory management, and video rental and return functionality.

## Features

### 1. Authentication and Authorization

- User registration and login.
- JWT-based stateless authentication.
- Password encryption using BCrypt.
- Role-based access control:
  - `CUSTOMER`
  - `ADMIN`
- Protected API endpoints using Spring Security.

### 2. User Management

- Register new customers.
- Authenticate users using email and password.
- Prevent duplicate email registrations.
- Public registration assigns the `CUSTOMER` role by default.
- Administrative users must be provisioned through a controlled process.

### 3. Video Management

- Create new videos.
- Retrieve all available videos.
- Retrieve a video by ID.
- Update video details.
- Delete videos.

**Access rules:**

- Customers can browse videos.
- Only administrators can create, update, and delete videos.

### 4. Rental Management

- Rent an available video.
- Return a rented video.
- Maintain rental history.
- Prevent customers from renting unavailable videos.
- Restrict customers to a maximum of two active rentals.
- Allow only the customer who rented a video to return it.
- Automatically make a video available after it is returned.

## Technology Stack

| Technology        | Purpose                          |
| ----------------- | -------------------------------- |
| Java 25           | Programming language             |
| Spring Boot 4.2.0 | Backend framework                |
| Spring Security   | Authentication and authorization |
| Spring Data JPA   | Database persistence             |
| Hibernate         | ORM                              |
| MySQL 8           | Relational database              |
| JWT (JJWT 0.13.0) | Token-based authentication       |
| BCrypt            | Password hashing                 |
| Maven             | Dependency management and build  |
| JUnit 5           | Unit testing                     |
| Mockito           | Mock-based testing               |
| Git & GitHub      | Version control                  |

## Project Architecture

The application follows a layered architecture to separate responsibilities.

```text
src
├── main
│   ├── java
│   │   └── com.rentvideo.rentvideo
│   │       ├── config
│   │       │   └── SecurityConfig.java
│   │       │
│   │       ├── controller
│   │       │   ├── AuthController.java
│   │       │   ├── VideoController.java
│   │       │   └── RentalController.java
│   │       │
│   │       ├── dto
│   │       │   ├── RegisterRequest.java
│   │       │   ├── LoginRequest.java
│   │       │   ├── AuthResponse.java
│   │       │   ├── UserResponse.java
│   │       │   ├── VideoRequest.java
│   │       │   ├── VideoResponse.java
│   │       │   └── RentalResponse.java
│   │       │
│   │       ├── entity
│   │       │   ├── User.java
│   │       │   ├── Role.java
│   │       │   ├── Video.java
│   │       │   └── Rental.java
│   │       │
│   │       ├── exception
│   │       │   └── GlobalExceptionHandler.java
│   │       │
│   │       ├── repository
│   │       │   ├── UserRepository.java
│   │       │   ├── VideoRepository.java
│   │       │   └── RentalRepository.java
│   │       │
│   │       ├── security
│   │       │   ├── JwtService.java
│   │       │   ├── JwtAuthenticationFilter.java
│   │       │   └── CustomUserDetailsService.java
│   │       │
│   │       ├── service
│   │       │   ├── UserService.java
│   │       │   ├── VideoService.java
│   │       │   └── RentalService.java
│   │       │
│   │       └── RentvideoApplication.java
│   │
│   └── resources
│       └── application.properties
│
└── test
    └── java
        └── com.rentvideo.rentvideo
            ├── controller
            │   └── AuthControllerTest.java
            └── service
                ├── UserServiceTest.java
                ├── VideoServiceTest.java
                └── RentalServiceTest.java
```

_The tree illustrates the implemented application structure; adjust filenames if your local project differs._

## Prerequisites

Install the following before running the application:

- Java JDK 25
- MySQL Server 8.0 or later
- Git
- Maven (optional, since the project includes Maven Wrapper)

Verify Java installation:

```bash
java -version
```

Verify Git installation:

```bash
git --version
```

## Database Configuration

### 1. Create the database

Open MySQL and execute:

```sql
CREATE DATABASE rentvideo_db;
```

### 2. Configure environment variables

The application reads database credentials and the JWT signing secret from environment variables.

**Windows PowerShell:**

```powershell
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "YOUR_MYSQL_PASSWORD"
```

Generate a 256-bit JWT secret for local development:

```powershell
[byte[]]$key = 1..32 | ForEach-Object { Get-Random -Maximum 256 }
$env:JWT_SECRET = [Convert]::ToBase64String($key)
```

Keep these values private. Do not commit credentials or signing secrets to GitHub.

### 3. Application properties

The application uses the following configuration in `src/main/resources/application.properties`:

```properties
spring.application.name=rentvideo

spring.datasource.url=jdbc:mysql://localhost:3306/rentvideo_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

app.jwt.secret=${JWT_SECRET}
app.jwt.expiration-ms=86400000

server.port=8080
```

The JWT expiration is 24 hours.

## Running the Application

Clone the repository:

```bash
git clone https://github.com/Priy anshx10/rentvideo.git
```

Change into the project directory:

```bash
cd rentvideo
```

Set the environment variables described above, then run:

```powershell
.\mvnw.cmd spring-boot:run
```

The application starts at:

```text
http://localhost:8080
```

## REST API Documentation

Base URL:

```text
http://localhost:8080
```

### Authentication APIs

| Method | Endpoint             | Description                  | Access |
| ------ | -------------------- | ---------------------------- | ------ |
| POST   | `/api/auth/register` | Register a user              | Public |
| POST   | `/api/auth/login`    | Authenticate and receive JWT | Public |

**Register request:**

```json
{
  "email": "customer@example.com",
  "password": "Customer123!",
  "firstName": "John",
  "lastName": "Doe"
}
```

New public registrations receive the `CUSTOMER` role.

**Login request:**

```json
{
  "email": "customer@example.com",
  "password": "Customer123!"
}
```

**Login response:**

```json
{
  "token": "your-jwt-token",
  "tokenType": "Bearer"
}
```

Use the returned token in the Authorization header for protected endpoints:

```text
Authorization: Bearer <JWT_TOKEN>
```

### Video APIs

| Method | Endpoint                | Description               | Access        |
| ------ | ----------------------- | ------------------------- | ------------- |
| GET    | `/api/videos`           | Retrieve available videos | Authenticated |
| GET    | `/api/videos/{videoId}` | Retrieve video by ID      | Authenticated |
| POST   | `/api/videos`           | Create a video            | ADMIN         |
| PUT    | `/api/videos/{videoId}` | Update a video            | ADMIN         |
| DELETE | `/api/videos/{videoId}` | Delete a video            | ADMIN         |

**Create video request:**

```json
{
  "title": "Inception",
  "description": "A science-fiction thriller",
  "available": true
}
```

### Rental APIs

| Method | Endpoint                      | Description           | Access   |
| ------ | ----------------------------- | --------------------- | -------- |
| POST   | `/api/videos/{videoId}/rent`  | Rent a video          | CUSTOMER |
| POST   | `/api/books/{videoId}/return` | Return a rented video | CUSTOMER |

**Rental business rules:**

1. A customer can have a maximum of two active rentals.
2. Only available videos can be rented.
3. A rented video cannot be rented again.
4. A customer can return only their own active rental.
5. Returning a video updates its availability.
6. Rental history is retained through the rental records.

## Security Implementation

The application uses Spring Security with JWT-based stateless authentication.

Authentication flow:

```text
Client
  |
  | Email + Password
  v
AuthController
  |
  v
AuthenticationManager
  |
  v
Spring Security
  |
  v
JWT Service
  |
  v
JWT Token returned to Client
```

For protected requests:

```text
Client Request + Bearer Token
  |
  v
JwtAuthenticationFilter
  |
  v
Validate JWT
  |
  v
Load User Details
  |
  v
Set Security Context
  |
  v
Controller
  |
  v
Service
  |
  v
MySQL Database
```

### Authorization Rules

| Operation         | CUSTOMER | ADMIN                                    |
| ----------------- | -------- | ---------------------------------------- |
| Register          | Yes      | Public registration does not grant ADMIN |
| Login             | Yes      | Yes                                      |
| Browse videos     | Yes      | Yes                                      |
| Create videos     | No       | Yes                                      |
| Update videos     | No       | Yes                                      |
| Delete videos     | No       | Yes                                      |
| Rent videos       | Yes      | No                                       |
| Return own rental | Yes      | No                                       |

## Error Handling

The application includes centralized exception handling through `GlobalExceptionHandler`.

Common HTTP responses:

| Status           | Meaning                                       |
| ---------------- | --------------------------------------------- |
| 200 OK           | Request completed successfully                |
| 201 Created      | Resource created successfully                 |
| 400 Bad Request  | Invalid request data                          |
| 401 Unauthorized | Missing or invalid authentication             |
| 403 Forbidden    | Authenticated user lacks required permission  |
| 404 Not Found    | Requested resource does not exist             |
| 409 Conflict     | Rental or registration business-rule conflict |

## Testing

The project includes unit tests using JUnit 5 and Mockito.

Run all tests:

```powershell
.\mvnw.cmd clean test
```

The current test suite contains **23 passing tests**:

| Test class                |  Tests |
| ------------------------- | -----: |
| AuthControllerTest        |      4 |
| RentalServiceTest         |      6 |
| UserServiceTest           |      3 |
| VideoServiceTest          |      9 |
| RentvideoApplicationTests |      1 |
| **Total**                 | **23** |

These tests cover authentication, registration, video operations, rental rules, and application context loading.

## Future Improvements

Potential enhancements include:

- Integration tests for complete JWT-protected API workflows.
- API documentation using Swagger / OpenAPI.
- Docker and Docker Compose deployment.
- Pagination and sorting for video listings.
- Rental history and customer-specific rental endpoints.
- Production-grade logging and monitoring.
- CI/CD pipeline.
- Cloud deployment using AWS.

## Author

**Priyansh Yadav**

Software Engineer | Java Backend Development

GitHub: [Priyanshx10](https://github.com/Priyanshx10)

Repository: [RentVideo](https://github.com/Priyanshx10/rentvideo)

---

_Built as a backend development project to practice REST API design, Spring Boot, database persistence, JWT authentication, role-based authorization, and transactional business logic._
