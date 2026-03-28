# Payment Service API

A comprehensive Spring Boot application for managing payment transactions, users, and payment history with Swagger UI integration.

## Project Structure

```
src/main/java/org/example/
├── PaymentApplication.java            # Spring Boot application entry point
├── controller/
│   ├── PaymentController.java         # REST APIs for payment operations
│   └── TransactionController.java     # REST APIs for transaction tracking
├── service/
│   ├── PaymentService.java            # Business logic for payments
│   └── TransactionService.java        # Business logic for transactions
├── entity/
│   ├── Payment.java                   # Payment entity
│   └── Transaction.java               # Transaction entity
├── repository/
│   ├── PaymentRepository.java         # Data access for payments
│   └── TransactionRepository.java     # Data access for transactions
├── dto/
│   ├── PaymentRequestDto.java         # DTO for payment request
│   ├── PaymentResponseDto.java        # DTO for payment response
│   └── TransactionDto.java            # DTO for transaction response
├── client/
│   └── UserServiceClient.java         # Feign client for User Service
└── exception/
    ├── GlobalExceptionHandler.java    # Centralized exception handling
    ├── ResourceNotFoundException.java  # Custom exception
    ├── InvalidPaymentException.java    # Custom exception
    ├── ErrorResponse.java             # Error response DTO
    └── ValidationErrorResponse.java   # Validation error response DTO
```

## Features

- **Payment Processing**: Create, process, complete, refund, and fail payments
- **Transaction Tracking**: Track all transaction history for payments
- **Payment Status Management**: Monitor payment states (PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED)
- **Payment Methods**: Support for multiple payment methods (Credit Card, Debit Card, Net Banking, Wallet, UPI, Cheque)
- **User Integration**: Seamless integration with User Service microservice
- **Comprehensive Validation**: Input validation using Jakarta Bean Validation
- **Swagger UI**: Interactive API documentation and testing
- **Exception Handling**: Centralized error handling with meaningful error messages
- **Database Support**: H2 (default) for development, MySQL support available
- **Microservice Ready**: Service-to-service communication via Feign client

## Database Setup

### Option 1: H2 (In-Memory Database) - Recommended for Local Development

The application is configured to use H2 by default. No setup required!

**Features:**
- Embedded in-memory database
- Automatic schema creation
- Web console available at `http://localhost:8082/h2-console`

**H2 Console Credentials:**
```
JDBC URL: jdbc:h2:mem:paymentdb
Username: sa
Password: (leave blank)
```

### Option 2: MySQL - For Production/Persistent Storage

#### Prerequisites:
- MySQL Server 5.7 or higher
- MySQL Client

#### Setup Steps:

1. **Create Database and User:**
```sql
-- Create database
CREATE DATABASE payment_service;

-- Create user
CREATE USER 'payment_user'@'localhost' IDENTIFIED BY 'password123';

-- Grant privileges
GRANT ALL PRIVILEGES ON payment_service.* TO 'payment_user'@'localhost';
FLUSH PRIVILEGES;
```

2. **Update application.properties:**

```properties
# Comment out H2 configuration
# spring.datasource.url=jdbc:h2:mem:paymentdb
# spring.datasource.driverClassName=org.h2.Driver
# spring.h2.console.enabled=true

# Uncomment MySQL configuration
spring.datasource.url=jdbc:mysql://localhost:3306/payment_service
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=payment_user
spring.datasource.password=password123

# MySQL specific JPA configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

3. **Run the application** - tables will be created automatically

## Microservice Architecture

### Payment Service Dependencies

This service depends on:
- **User Service** (running on `http://localhost:8081`)

Ensure User Service is running before starting Payment Service.

### Service Startup Order
1. User Service (port 8081)
2. Payment Service (port 8082)

For detailed information on the User Service, refer to the [User Service README](../user/README.md).

## Running the Application

### Prerequisites:
- Java 21 or higher
- Maven 3.6 or higher
- User Service running on port 8081

### Steps:

1. **Navigate to project directory:**
```bash
cd /Users/harik/Dev/java/payment
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

## API Endpoints

### Payment Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments` | Create a new payment |
| POST | `/api/payments/{paymentId}/process` | Process payment |
| POST | `/api/payments/{paymentId}/complete` | Complete payment |
| POST | `/api/payments/{paymentId}/refund` | Refund payment |
| POST | `/api/payments/{paymentId}/fail` | Mark payment as failed |
| GET | `/api/payments/{paymentId}` | Get payment by ID |
| GET | `/api/payments/transaction/{transactionId}` | Get payment by transaction ID |
| GET | `/api/payments/user/{userId}` | Get user's payments |
| GET | `/api/payments/status/{status}` | Get payments by status |
| GET | `/api/payments` | Get all payments |

### Transaction Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/transactions/{transactionId}` | Get transaction by ID |
| GET | `/api/transactions/payment/{paymentId}` | Get payment's transactions |
| GET | `/api/transactions` | Get all transactions |

## Example Requests

### Create a Payment
```bash
curl -X POST http://localhost:8082/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 100.00,
    "currency": "USD",
    "paymentMethod": "CREDIT_CARD",
    "description": "Product purchase",
    "referenceNumber": "REF123"
  }'
```

### Process Payment
```bash
curl -X POST http://localhost:8082/api/payments/1/process
```

### Complete Payment
```bash
curl -X POST http://localhost:8082/api/payments/1/complete
```

### Get Payment by ID
```bash
curl http://localhost:8082/api/payments/1
```

## Payment Status Flow

```
PENDING → PROCESSING → COMPLETED
                    ↓
                 FAILED
                    ↓
                 REFUNDED
```

## Technologies Used

- **Spring Boot 3.5.12**: Modern Java web framework
- **Spring Data JPA**: ORM with Hibernate
- **Jakarta Bean Validation**: Input validation
- **Swagger 3.0 (springdoc-openapi)**: API documentation
- **Lombok**: Reduce boilerplate code
- **H2 Database**: In-memory database (development)
- **MySQL**: Relational database (production)
- **Maven**: Build automation tool

## Configuration

### application.properties
The application is configured in `src/main/resources/application.properties`. Key configurations:

```properties
# Application name
spring.application.name=payment-service

# Database
spring.datasource.url=jdbc:h2:mem:paymentdb

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=create-drop

# Swagger UI
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
```

## Exception Handling

The application includes comprehensive exception handling:

- **ResourceNotFoundException**: When a resource is not found (404)
- **DuplicateResourceException**: When trying to create duplicate resources (409)
- **InvalidPaymentException**: When payment operation is invalid (400)
- **MethodArgumentNotValidException**: When input validation fails (400)

All exceptions return structured error responses with timestamps.

## Project Dependencies

All dependencies are managed by Maven. Key dependencies:

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
```

## Future Enhancements

- Payment gateway integration (Stripe, PayPal, etc.)
- Caching layer (Redis)
- Async payment processing with message queues
- Authentication and authorization (JWT)
- Payment report generation
- Audit logging
- Multi-currency support
- Batch payment processing

## Troubleshooting

### Port Already in Use
If port 8080 is already in use, change it in application.properties:
```properties
server.port=8081
```

### H2 Console Not Accessible
Ensure H2 console is enabled:
```properties
spring.h2.console.enabled=true
```

### MySQL Connection Failed
- Verify MySQL is running
- Check connection credentials in application.properties
- Ensure database and user are created

## License

This project is open source and available under the MIT License.

## Support

For issues or questions, please check the project's issue tracker or contact the development team.
