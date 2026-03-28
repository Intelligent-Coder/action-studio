# Product Service API

A comprehensive Spring Boot microservice for managing products in the e-commerce platform with Swagger UI integration.

## Project Structure

```
src/main/java/com/ecart/product/
├── ProductApplication.java               # Spring Boot application entry point
├── controller/
│   └── ProductController.java            # REST APIs for product management
├── service/
│   ├── ProductService.java               # Product service interface
│   └── ProductServiceImpl.java            # Business logic for product management
├── entity/
│   └── Product.java                      # Product entity
├── repository/
│   └── ProductRepository.java            # Data access for products
├── dto/
│   ├── ProductRequestDto.java            # DTO for product creation/update
│   └── ProductResponseDto.java           # DTO for product response
├── config/
│   └── SwaggerConfig.java                # Swagger/OpenAPI configuration
├── exception/
│   ├── GlobalExceptionHandler.java       # Centralized exception handling
│   ├── ResourceNotFoundException.java     # Custom exception
│   ├── InvalidPaymentException.java      # Custom exception
│   ├── ErrorResponse.java                # Error response DTO
│   └── ValidationErrorResponse.java      # Validation error response DTO
```

## Features

- **Product Management**: Create, retrieve, update, and delete products
- **Product Search**: Search products by keyword
- **Comprehensive Validation**: Input validation using Jakarta Bean Validation
- **Swagger UI**: Interactive API documentation and testing
- **Exception Handling**: Centralized error handling with meaningful error messages
- **Database Support**: H2 (development), PostgreSQL (production)
- **Spring Cloud Integration**: Config server integration for centralized configuration
- **Circuit Breaker**: Resilience4j integration for fault tolerance

## Database Setup

### Option 1: H2 (In-Memory Database) - Recommended for Local Development

The application is configured to use H2 by default. No setup required!

**Features:**
- Embedded in-memory database
- Automatic schema creation
- Web console available at `http://localhost:8082/h2-console`

**H2 Console Credentials:**
```
JDBC URL: jdbc:h2:mem:products
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
CREATE DATABASE ecart_product;

-- Create user
CREATE USER product_service WITH PASSWORD 'password123';

-- Grant privileges
ALTER ROLE product_service CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE ecart_product TO product_service;
```

2. **Update application.yml:**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecart_product
    username: product_service
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
cd /Users/harik/Dev/java/ecart-app/product
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

### Product Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/products` | Create a new product |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products` | Get all products |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |
| GET | `/api/products/search?keyword={keyword}` | Search products by keyword |

## Example Requests

### Get All Products
```bash
curl http://localhost:8082/api/products
```

**Response (200):**
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "quantity": 50
  }
]
```

### Get Product by ID
```bash
curl http://localhost:8082/api/products/1
```

**Response (200):**
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "quantity": 50
}
```

**Response (404):**
```json
{
  "timestamp": "2026-03-28T10:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 1",
  "path": "/api/products/1"
}
```

### Create a Product
```bash
curl -X POST http://localhost:8082/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "High-performance laptop with 16GB RAM",
    "price": 999.99,
    "quantity": 50
  }'
```

**Response (201):**
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "High-performance laptop with 16GB RAM",
  "price": 999.99,
  "quantity": 50
}
```

### Update Product
```bash
curl -X PUT http://localhost:8082/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop Pro",
    "description": "Professional laptop with 32GB RAM",
    "price": 1299.99,
    "quantity": 75
  }'
```

**Response (200):**
```json
{
  "id": 1,
  "name": "Laptop Pro",
  "description": "Professional laptop with 32GB RAM",
  "price": 1299.99,
  "quantity": 75
}
```

### Search Products by Keyword
```bash
curl http://localhost:8082/api/products/search?keyword=laptop
```

**Response (200):**
```json
[
  {
    "id": 1,
    "name": "Laptop Pro",
    "description": "Professional laptop with 32GB RAM",
    "price": 1299.99,
    "quantity": 75
  },
  {
    "id": 2,
    "name": "Gaming Laptop",
    "description": "High-end gaming laptop",
    "price": 1599.99,
    "quantity": 30
  }
]
```

### Delete Product
```bash
curl -X DELETE http://localhost:8082/api/products/1
```

**Response (204):** No content

**Response (404):**
```json
{
  "timestamp": "2026-03-28T10:30:00.000Z",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 1",
  "path": "/api/products/1"
}
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
- **Resilience4j**: Circuit breaker for fault tolerance

## Configuration

### application.yml
The application is configured in `src/main/resources/application.yml`.

Key configurations:
```yaml
spring:
  application:
    name: product-service
  datasource:
    url: jdbc:h2:mem:products
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

- **ResourceNotFoundException**: When a product is not found (404)
- **InvalidPaymentException**: When invalid product data is provided
- **MethodArgumentNotValidException**: When input validation fails (400)

All exceptions return structured error responses with timestamps.

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
    <version>${springdoc.version}</version>
</dependency>

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
</dependency>

<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
</dependency>
```

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

### Product Creation Failed
- Ensure all required fields are provided
- Verify data types match the schema
- Check for duplicate products if uniqueness constraints exist

## Microservice Endpoints

This service runs on **port 8082** and can be accessed at:
```
http://localhost:8082
```

For production deployments, the service can be registered with a service discovery mechanism (Eureka, Consul, etc.).

## Future Enhancements

- Product categories and tags
- Product reviews and ratings
- Inventory management
- Product images and media
- Bulk product operations
- Advanced filtering and sorting
- Product variants and options
- Pricing strategies and discounts
- Product audit trail

## License

This project is open source and available under the MIT License.

## Support

For issues or questions, please check the project's issue tracker or contact the development team.