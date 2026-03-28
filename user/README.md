# User Service API

A comprehensive Spring Boot microservice for managing user profiles, authentication, and user information in the e-commerce platform with Swagger UI integration.

## Project Structure

```
src/main/java/com/ecart/user/
├── UserApplication.java               # Spring Boot application entry point
├── controller/
│   └── UserController.java            # REST APIs for user management
├── service/
│   ├── UserService.java               # User service interface
│   └── UserServiceImpl.java            # Business logic for user management
├── entity/
│   ├── User.java                      # User entity
│   └── Address.java                   # Address embeddable value object
├── repository/
│   └── UserRepository.java            # Data access for users
├── dto/
│   ├── UserDto.java                   # DTO for user response
│   ├── CreateUserDto.java             # DTO for user creation/update
│   └── AddressDto.java                # DTO for address information
├── config/
│   └── SwaggerConfig.java             # Swagger/OpenAPI configuration
├── exception/
│   ├── GlobalExceptionHandler.java    # Centralized exception handling
│   ├── ResourceNotFoundException.java  # Custom exception
│   ├── DuplicateResourceException.java # Custom exception
│   ├── ErrorResponse.java             # Error response DTO
│   └── ValidationErrorResponse.java   # Validation error response DTO
└── client/
    └── ProviderHttpInterface.java     # HTTP interface for service communication
```

## Features

- **User Management**: Create, retrieve, update, and delete users
- **User Profile Management**: Store and manage user information (email, name, phone, address)
- **Duplicate Prevention**: Prevent duplicate email and phone number registrations
- **Comprehensive Validation**: Input validation using Jakarta Bean Validation
- **Swagger UI**: Interactive API documentation and testing
- **Exception Handling**: Centralized error handling with meaningful error messages
- **Database Support**: H2 (development), PostgreSQL (production)
- **Spring Cloud Integration**: Config server integration for centralized configuration
- **Microservice Ready**: Prepared for inter-service communication via HTTP interfaces

## Database Setup

### Option 1: H2 (In-Memory Database) - Recommended for Local Development

The application is configured to use H2 by default. No setup required!

**Features:**
- Embedded in-memory database
- Automatic schema creation
- Web console available at `http://localhost:8082/h2-console`

**H2 Console Credentials:**
```
JDBC URL: jdbc:h2:mem:users
Username: sa
Password: (leave blank)
```

### Option 2: PostgreSQL - For Production/Persistent Storage

#### Prerequisites:
- PostgreSQL 12 or higher
- PostgreSQL Client

#### Setup Steps:

1. **Create Database and User:**
```sql
-- Create database
CREATE DATABASE ecart_user;

-- Create user
CREATE USER user_service WITH PASSWORD 'password123';

-- Grant privileges
ALTER ROLE user_service CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE ecart_user TO user_service;
```

2. **Update application.yml:**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecart_user
    username: user_service
    password: password123
    driverClassName: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
```

3. **Run the application** - tables will be created automatically

## Running the Application

### Prerequisites:
- Java 21 or higher
- Maven 3.6 or higher

### Steps:

1. **Navigate to project directory:**
```bash
cd /Users/harik/Dev/java/ecart-app/user
```

2. **Build the project:**
```bash
mvn clean install
```

3. **Run the application:**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8082`

## API Documentation

### Swagger UI
Once the application is running, access the interactive API documentation:
```
http://localhost:8082/swagger-ui.html
```

### OpenAPI/JSON
```
http://localhost:8082/v3/api-docs
```

### H2 Console
```
http://localhost:8082/h2-console
```

## API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users` | Create a new user |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/email/{email}` | Get user by email |
| GET | `/api/users` | Get all users |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |

## Example Requests

### Create a User
```bash
curl -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "address": {
      "street": "123 Main St",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001"
    }
  }'
```

### Get User by ID
```bash
curl http://localhost:8082/api/users/1
```

### Get User by Email
```bash
curl http://localhost:8082/api/users/email/john.doe@example.com
```

### Update User
```bash
curl -X PUT http://localhost:8082/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "address": {
      "street": "456 Oak Ave",
      "city": "Boston",
      "state": "MA",
      "zipCode": "02101"
    }
  }'
```

### Delete User
```bash
curl -X DELETE http://localhost:8082/api/users/1
```

### Get All Users
```bash
curl http://localhost:8082/api/users
```

## Technologies Used

- **Spring Boot 3.5.12**: Modern Java web framework
- **Spring Cloud 2025.0.0**: Distributed systems support
- **Spring Data JPA**: ORM with Hibernate
- **Jakarta Bean Validation**: Input validation
- **Swagger 3.0 (springdoc-openapi)**: API documentation
- **Lombok**: Reduce boilerplate code
- **H2 Database**: In-memory database (development)
- **PostgreSQL**: Relational database (production)
- **Maven**: Build automation tool

## Configuration

### application.yml
The application is configured in `src/main/resources/application.yml`.

Key configurations:
```yaml
spring:
  application:
    name: user-service
  datasource:
    url: jdbc:h2:mem:users
  jpa:
    hibernate:
      ddl-auto: create-drop

server:
  port: 8082

springdoc:
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
```

## Exception Handling

The application includes comprehensive exception handling:

- **ResourceNotFoundException**: When a user is not found (404)
- **DuplicateResourceException**: When trying to create duplicate users (409)
- **MethodArgumentNotValidException**: When input validation fails (400)

All exceptions return structured error responses with timestamps.

## Inter-Service Communication

The user service is prepared for communication with other microservices using Spring HTTP interfaces:

### Payment Service Integration
```java
@HttpExchange
public interface PaymentServiceClient {
    @GetExchange("/api/payments/user/{userId}")
    List<PaymentDto> getUserPayments(@PathVariable Long userId);
}
```

## Project Dependencies

Key dependencies managed by Maven:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

## Future Enhancements

- User authentication and authorization (JWT)
- Role-based access control (RBAC)
- User profile pictures/avatars
- Email verification
- Two-factor authentication
- User activity logging
- Password reset functionality
- Account deactivation/deletion policies
- User preferences and settings management

## Troubleshooting

### Port Already in Use
If port 8082 is already in use, change it in application.yml:
```yaml
server:
  port: 8083
```

### H2 Console Not Accessible
Ensure H2 console is enabled:
```yaml
spring:
  h2:
    console:
      enabled: true
```

### PostgreSQL Connection Failed
- Verify PostgreSQL is running
- Check connection credentials in application.yml
- Ensure database and user are created

### Duplicate User Creation Failed
Ensure email and phone number are unique:
- Email must be unique per user
- Phone number must be unique per user

## Microservice Endpoints

This service runs on **port 8082** and can be accessed at:
```
http://localhost:8082
```

For production deployments, the service can be registered with a service discovery mechanism (Eureka, Consul, etc.).

## License

This project is open source and available under the MIT License.

## Support

For issues or questions, please check the project's issue tracker or contact the development team.
