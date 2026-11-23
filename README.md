# RentVideo – Online Video Rental System

A RESTful backend application built using **Java, Spring Boot, Spring Security, Spring Data JPA, and MySQL** to manage an online video rental catalogue.

The application provides user registration, HTTP Basic Authentication, role-based authorization, and video management functionality.

## Features

### Authentication & Authorization

- HTTP Basic Authentication using Spring Security.
- Two user roles: `CUSTOMER` and `ADMIN`.
- Public user registration.
- BCrypt password encryption.
- Default role is `CUSTOMER` when no role is provided.
- Role-based access control for protected endpoints.

### User Management

- Register users with email, password, first name, last name, and role.
- Validate registration details.
- Prevent duplicate email registrations.
- Store encrypted passwords in the database.

### Video Management

- Create videos (ADMIN only).
- Update video details (ADMIN only).
- Delete videos (ADMIN only).
- Browse available videos (authenticated users).
- Newly created videos are available by default.

### Exception Handling

- Centralized exception handling using `@RestControllerAdvice`.
- Validation error handling.
- Appropriate HTTP status codes for common errors.

## Technology Stack

| Technology        | Purpose                          |
| ----------------- | -------------------------------- |
| Java 25           | Programming language             |
| Spring Boot 4.2.0 | Backend framework                |
| Spring Security   | Authentication and authorization |
| Spring Data JPA   | Database persistence             |
| Hibernate         | ORM                              |
| MySQL 8           | Relational database              |
| Maven             | Build and dependency management  |
| BCrypt            | Password hashing                 |
| REST API          | Client-server communication      |

## Project Structure

```text
rentvideo/
├── src/
│   ├── main/
│   │   ├── java/com/rentvideo/rentvideo/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   └── VideoController.java
│   │   │   ├── dto/
│   │   │   │   ├── RegisterRequest.java
│   │   │   │   ├── UserResponse.java
│   │   │   │   ├── VideoRequest.java
│   │   │   │   └── VideoResponse.java
│   │   │   ├── entity/
│   │   │   │   ├── Role.java
│   │   │   │   ├── User.java
│   │   │   │   └── Video.java
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── VideoRepository.java
│   │   │   └── service/
│   │   │       ├── CustomUserDetailsService.java
│   │   │       ├── UserService.java
│   │   │       └── VideoService.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Prerequisites

Make sure the following are installed:

- Java Development Kit (JDK) 25
- MySQL 8
- Git
- Maven (or use the included Maven Wrapper)

## Installation and Setup

### 1. Clone the repository

```bash
git clone https://github.com/Priyanshx10/rentvideo.git
cd rentvideo
```

### 2. Create the MySQL database

Open MySQL and execute:

```sql
CREATE DATABASE rentvideo_db;
```

### 3. Configure database credentials

Configure your `src/main/resources/application.properties` file to connect to MySQL.

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/rentvideo_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

Set your database credentials as environment variables in PowerShell:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
```

Do not commit database passwords or other secrets to GitHub.

### 4. Run the application

Using the Maven Wrapper on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The application will start at:

```text
http://localhost:8080
```

## API Documentation

Base URL:

```text
http://localhost:8080
```

### 1. User Registration

**Endpoint:** `POST /api/auth/register`

**Access:** Public

Request body:

```json
{
  "email": "admin@rentvideo.com",
  "password": "Admin12345!",
  "firstName": "Rent",
  "lastName": "Admin",
  "role": "ADMIN"
}
```

For a CUSTOMER account, omit the role:

```json
{
  "email": "customer@rentvideo.com",
  "password": "Customer123!",
  "firstName": "Test",
  "lastName": "Customer"
}
```

The role defaults to `CUSTOMER`.

Successful response: `201 Created`

### 2. Browse Available Videos

**Endpoint:** `GET /api/videos`

**Access:** CUSTOMER, ADMIN

Returns all available videos.

### 3. Get Video by ID

**Endpoint:** `GET /api/videos/{id}`

**Access:** CUSTOMER, ADMIN

Example:

```text
GET /api/videos/1
```

### 4. Create a Video

**Endpoint:** `POST /api/videos`

**Access:** ADMIN only

Request body:

```json
{
  "title": "The Dark Knight",
  "director": "Christopher Nolan",
  "genre": "Action"
}
```

Successful response: `201 Created`

New videos are available by default.

### 5. Update a Video

**Endpoint:** `PUT /api/videos/{id}`

**Access:** ADMIN only

Request body:

```json
{
  "title": "The Dark Knight Rises",
  "director": "Christopher Nolan",
  "genre": "Action"
}
```

Successful response: `200 OK`

### 6. Delete a Video

**Endpoint:** `DELETE /api/videos/{id}`

**Access:** ADMIN only

Example:

```text
DELETE /api/videos/1
```

Successful response: `204 No Content`

## Authentication

The application uses HTTP Basic Authentication.

For protected endpoints, send the registered email and password through the Authorization header.

Example:

```http
Authorization: Basic <base64-encoded-email-and-password>
```

In Postman:

1. Select the request.
2. Open the Authorization tab.
3. Select Basic Auth.
4. Enter the registered email as the username.
5. Enter the password.
6. Send the request.

Spring Security authenticates the credentials and checks the user's role before allowing access.

## Authorization Rules

| Operation        | CUSTOMER | ADMIN  |
| ---------------- | -------- | ------ |
| Register         | Public   | Public |
| Browse videos    | Yes      | Yes    |
| View video by ID | Yes      | Yes    |
| Create video     | No       | Yes    |
| Update video     | No       | Yes    |
| Delete video     | No       | Yes    |

## HTTP Status Codes

| Status Code      | Description                                    |
| ---------------- | ---------------------------------------------- |
| 200 OK           | Request completed successfully                 |
| 201 Created      | Resource created successfully                  |
| 204 No Content   | Resource deleted successfully                  |
| 400 Bad Request  | Invalid request or validation error            |
| 401 Unauthorized | Authentication required or invalid credentials |
| 403 Forbidden    | User does not have the required role           |
| 404 Not Found    | Requested resource does not exist              |
| 409 Conflict     | Duplicate email registration                   |

## Testing

The following API scenarios were manually verified using PowerShell:

- ADMIN registration and authentication.
- CUSTOMER registration with default role.
- ADMIN video creation.
- CUSTOMER video browsing.
- CUSTOMER blocked from creating and updating videos.
- ADMIN video update and deletion.
- Invalid video ID returning `404`.
- Duplicate email registration returning `409`.
- Unauthenticated access returning `401`.

## Future Enhancements

- Automated integration and unit tests.
- API documentation using Swagger/OpenAPI.
- Pagination and sorting for video listings.
- More advanced search and filtering.
- Deployment to AWS.

## Author

**Priyansh Yadav**

GitHub: [Priyanshx10](https://github.com/Priyanshx10)

## License

This project was developed as part of a backend development assignment.
