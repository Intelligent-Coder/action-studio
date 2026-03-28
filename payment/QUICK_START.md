# Payment Service - Quick Start Guide

## 📋 What's Included

A **production-ready Spring Boot Payment Service** with:

✅ Full REST API for user and payment management
✅ Swagger UI for interactive API testing
✅ Multiple DTOs for request/response handling
✅ Comprehensive exception handling
✅ H2 database (no setup required!)
✅ MySQL support (optional)
✅ Transaction tracking
✅ Payment status management

---

## 🚀 Get Started in 3 Steps

### Step 1: Build the Project
```bash
cd /Users/harik/Dev/java/payment
mvn clean install
```

### Step 2: Run the Application
```bash
mvn spring-boot:run
```

You'll see:
```
...
2024-03-28 10:30:00.123  INFO 12345 --- [main] org.example.Main : Started Main in 3.456 seconds
```

### Step 3: Access the APIs

**Swagger UI:** http://localhost:8080/swagger-ui.html
**API Docs:** http://localhost:8080/v3/api-docs
**H2 Console:** http://localhost:8080/h2-console

---

## 📦 Project Structure

```
payment-service/
├── pom.xml                          # Maven configuration with Spring Boot dependencies
├── README.md                        # Detailed documentation
├── API_TESTING_GUIDE.md            # Ready-to-use curl commands
├── QUICK_START.md                  # This file
│
└── src/main/java/org/example/
    ├── Main.java                   # Spring Boot application entry point
    │
    ├── controller/                 # REST API endpoints
    │   ├── UserController.java
    │   ├── PaymentController.java
    │   └── TransactionController.java
    │
    ├── service/                    # Business logic
    │   ├── UserService.java
    │   ├── PaymentService.java
    │   └── TransactionService.java
    │
    ├── entity/                     # JPA entities
    │   ├── User.java
    │   ├── Payment.java
    │   └── Transaction.java
    │
    ├── repository/                 # Data access layer
    │   ├── UserRepository.java
    │   ├── PaymentRepository.java
    │   └── TransactionRepository.java
    │
    ├── dto/                        # Data transfer objects
    │   ├── UserDto.java
    │   ├── CreateUserDto.java
    │   ├── PaymentRequestDto.java
    │   ├── PaymentResponseDto.java
    │   └── TransactionDto.java
    │
    ├── exception/                  # Exception handling
    │   ├── GlobalExceptionHandler.java
    │   ├── ResourceNotFoundException.java
    │   ├── DuplicateResourceException.java
    │   ├── InvalidPaymentException.java
    │   ├── ErrorResponse.java
    │   └── ValidationErrorResponse.java
    │
    └── config/                     # Configuration
        └── SwaggerConfig.java

└── src/main/resources/
    ├── application.properties       # H2 configuration (default)
    └── application-mysql.properties # MySQL configuration (optional)
```

---

## 🔨 API Overview

### User Management
| Operation | Endpoint | Method |
|-----------|----------|--------|
| Create User | `/api/users` | POST |
| Get User | `/api/users/{id}` | GET |
| List Users | `/api/users` | GET |
| Update User | `/api/users/{id}` | PUT |
| Delete User | `/api/users/{id}` | DELETE |

### Payment Management
| Operation | Endpoint | Method |
|-----------|----------|--------|
| Create Payment | `/api/payments` | POST |
| Process Payment | `/api/payments/{id}/process` | POST |
| Complete Payment | `/api/payments/{id}/complete` | POST |
| Refund Payment | `/api/payments/{id}/refund` | POST |
| Get Payment | `/api/payments/{id}` | GET |
| List Payments | `/api/payments` | GET |
| Get User Payments | `/api/payments/user/{userId}` | GET |

### Transaction Tracking
| Operation | Endpoint | Method |
|-----------|----------|--------|
| Get Transaction | `/api/transactions/{id}` | GET |
| List Transactions | `/api/transactions` | GET |
| Get Payment Transactions | `/api/transactions/payment/{paymentId}` | GET |

---

## 💡 Quick Test

### 1. Create a User (copy-paste into terminal)
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "address": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001"
  }'
```

Save the returned `id` (usually 1).

### 2. Create a Payment
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 99.99,
    "currency": "USD",
    "paymentMethod": "CREDIT_CARD",
    "description": "Test Payment",
    "referenceNumber": "REF-001"
  }'
```

Save the returned `id`.

### 3. Process & Complete Payment
```bash
# Process
curl -X POST http://localhost:8080/api/payments/1/process

# Complete
curl -X POST http://localhost:8080/api/payments/1/complete
```

### 4. View Payment Details
```bash
curl http://localhost:8080/api/payments/1
```

---

## 🗄️ Database

### Using H2 (Default - No Setup Required)

**Access H2 Console:**
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:paymentdb`
- Username: `sa`
- Password: (leave empty)

### Using MySQL (Optional)

1. Create database:
```sql
CREATE DATABASE payment_service;
CREATE USER 'payment_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON payment_service.* TO 'payment_user'@'localhost';
FLUSH PRIVILEGES;
```

2. Run with MySQL profile:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=mysql"
```

---

## 📚 Key Features

### ✨ Comprehensive DTOs
- **UserDto, CreateUserDto** - User data transfer
- **PaymentRequestDto, PaymentResponseDto** - Payment data transfer
- **TransactionDto** - Transaction data transfer

### 🛡️ Exception Handling
- **ResourceNotFoundException** - 404 errors
- **DuplicateResourceException** - 409 conflicts
- **InvalidPaymentException** - 400 bad requests
- **Validation errors** - Field-level validation responses

### 📊 Payment Status Flow
```
PENDING → PROCESSING → COMPLETED
             ↓
          FAILED
```

### 💳 Supported Payment Methods
- CREDIT_CARD
- DEBIT_CARD
- NET_BANKING
- WALLET
- UPI
- CHEQUE

---

## 🔧 Configuration

Edit `src/main/resources/application.properties` to customize:

```properties
# Application name
spring.application.name=payment-service

# Server port
server.port=8080

# Logging level
logging.level.root=INFO

# JPA/Hibernate
spring.jpa.show-sql=false
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## 📖 Documentation Files

- **README.md** - Full documentation with all details
- **API_TESTING_GUIDE.md** - Ready-to-use curl commands for all endpoints
- **QUICK_START.md** - This file

---

## 🆘 Troubleshooting

### Build Issues
```bash
# Clear Maven cache and rebuild
mvn clean install -U
```

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

### Database Issues
```bash
# For H2, data is recreated on startup (see hibernate.ddl-auto=create-drop)
# Just restart the application

# For MySQL, verify credentials and database exists
```

---

## 🎯 Next Steps

1. ✅ Build and run: `mvn clean install && mvn spring-boot:run`
2. ✅ Visit Swagger UI: http://localhost:8080/swagger-ui.html
3. ✅ Test with curl: See API_TESTING_GUIDE.md
4. ✅ Check H2 Console: http://localhost:8080/h2-console
5. ✅ Explore the code: Start with `Main.java` and `UserController.java`

---

## 💪 Customization Ideas

- Add authentication (Spring Security)
- Integrate with payment gateways (Stripe, PayPal)
- Add caching (Redis)
- Add async processing (RabbitMQ, Kafka)
- Add audit logging
- Add pagination for list endpoints
- Add search/filter capabilities

---

## 📋 Tech Stack

- **Java 21**
- **Spring Boot 3.5.12**
- **Spring Data JPA with Hibernate**
- **H2 Database** (development) / **MySQL** (production)
- **Swagger UI 3.0** with springdoc-openapi 2.7.0
- **Lombok** (reduce boilerplate)
- **Maven** (build tool)

---

## ✅ What You Have

- ✅ Production-ready Spring Boot application
- ✅ Complete REST API with 15+ endpoints
- ✅ 3 JPA entities with relationships
- ✅ 5 comprehensive DTOs
- ✅ 3 repository interfaces with custom queries
- ✅ 3 service classes with business logic
- ✅ 3 controller classes with REST endpoints
- ✅ Global exception handling
- ✅ Swagger/OpenAPI documentation
- ✅ H2 database (pre-configured)
- ✅ MySQL support (optional)
- ✅ Validation with meaningful error messages

---

**You're all set! Happy coding! 🎉**

For detailed information, check the README.md file.
