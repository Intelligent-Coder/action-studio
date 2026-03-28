# User Service - Testing Guide

This document provides comprehensive testing procedures for the User Service API.

## Prerequisites

- User Service running on `http://localhost:8081`
- `curl` or Postman for API testing
- (Optional) H2 Console for database verification

## Running Tests

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Code Coverage
```bash
mvn test jacoco:report
```

## Manual API Testing

### 1. Health Check

**Endpoint:**
```bash
GET http://localhost:8081/actuator/health
```

**Expected Response:** 200 OK
```json
{
  "status": "UP"
}
```

---

### 2. Create User

**Request:**
```bash
curl -X POST http://localhost:8081/api/users \
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

**Expected Response:** 201 Created
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001"
  },
  "createdAt": "2026-03-28T19:30:00",
  "updatedAt": "2026-03-28T19:30:00"
}
```

**Test Cases:**
- ✅ Valid user creation with all fields
- ❌ Duplicate email should return 409
- ❌ Duplicate phone should return 409
- ❌ Missing required fields should return 400
- ❌ Invalid email format should return 400

---

### 3. Get User by ID

**Request:**
```bash
curl http://localhost:8081/api/users/1
```

**Expected Response:** 200 OK
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001"
  },
  "createdAt": "2026-03-28T19:30:00",
  "updatedAt": "2026-03-28T19:30:00"
}
```

**Test Cases:**
- ✅ Get existing user
- ❌ Get non-existent user (ID: 999) should return 404

---

### 4. Get User by Email

**Request:**
```bash
curl http://localhost:8081/api/users/email/john.doe@example.com
```

**Expected Response:** 200 OK
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001"
  },
  "createdAt": "2026-03-28T19:30:00",
  "updatedAt": "2026-03-28T19:30:00"
}
```

**Test Cases:**
- ✅ Get existing user by email
- ❌ Get non-existent email should return 404

---

### 5. Get All Users

**Request:**
```bash
curl http://localhost:8081/api/users
```

**Expected Response:** 200 OK
```json
[
  {
    "id": 1,
    "email": "john.doe@example.com",
    "firstName": "John",
    ...
  },
  {
    "id": 2,
    "email": "jane.smith@example.com",
    "firstName": "Jane",
    ...
  }
]
```

**Test Cases:**
- ✅ Get all users (empty list if no users)
- ✅ Get all users after creating multiple

---

### 6. Update User

**Request:**
```bash
curl -X PUT http://localhost:8081/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.updated@example.com",
    "firstName": "John",
    "lastName": "Updated",
    "phoneNumber": "+1234567890",
    "address": {
      "street": "456 Oak Ave",
      "city": "Boston",
      "state": "MA",
      "zipCode": "02101"
    }
  }'
```

**Expected Response:** 200 OK
```json
{
  "id": 1,
  "email": "john.updated@example.com",
  "firstName": "John",
  "lastName": "Updated",
  "phoneNumber": "+1234567890",
  "address": {
    "street": "456 Oak Ave",
    "city": "Boston",
    "state": "MA",
    "zipCode": "02101"
  },
  "createdAt": "2026-03-28T19:30:00",
  "updatedAt": "2026-03-28T19:35:00"
}
```

**Test Cases:**
- ✅ Update existing user with valid data
- ❌ Update non-existent user should return 404
- ❌ Update with duplicate email should return 409
- ❌ Update with invalid data should return 400

---

### 7. Delete User

**Request:**
```bash
curl -X DELETE http://localhost:8081/api/users/1
```

**Expected Response:** 204 No Content

**Test Cases:**
- ✅ Delete existing user (204 response)
- ❌ Delete non-existent user should return 404
- ✅ Verify user is deleted (subsequent GET returns 404)

---

## Test Scenarios

### Scenario 1: Complete User Lifecycle

```bash
# 1. Create user
USER_ID=$(curl -s -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "phoneNumber": "+1111111111",
    "address": {
      "street": "Test St",
      "city": "Test City",
      "state": "TC",
      "zipCode": "12345"
    }
  }' | jq '.id')

echo "Created user with ID: $USER_ID"

# 2. Retrieve user
curl http://localhost:8081/api/users/$USER_ID | jq

# 3. Update user
curl -X PUT http://localhost:8081/api/users/$USER_ID \
  -H "Content-Type: application/json" \
  -d '{
    "email": "updated@example.com",
    "firstName": "Updated",
    "lastName": "User",
    "phoneNumber": "+1111111111",
    "address": {
      "street": "New Address",
      "city": "New City",
      "state": "NC",
      "zipCode": "54321"
    }
  }' | jq

# 4. Delete user
curl -X DELETE http://localhost:8081/api/users/$USER_ID

# 5. Verify deletion
curl http://localhost:8081/api/users/$USER_ID
```

### Scenario 2: Duplicate Prevention

```bash
# 1. Create first user
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "duplicate@example.com",
    "firstName": "User",
    "lastName": "One",
    "phoneNumber": "+2222222222"
  }'

# 2. Try to create with same email (should fail with 409)
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "duplicate@example.com",
    "firstName": "User",
    "lastName": "Two",
    "phoneNumber": "+3333333333"
  }'
```

### Scenario 3: Error Handling

```bash
# 1. Missing required field (400)
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe"
  }'

# 2. Non-existent user (404)
curl http://localhost:8081/api/users/99999

# 3. Invalid email format (400)
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "invalid-email",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890"
  }'
```

---

## Database Verification

### Access H2 Console
```
http://localhost:8081/h2-console
```

**Credentials:**
- JDBC URL: `jdbc:h2:mem:users`
- Username: `sa`
- Password: (empty)

### SQL Queries

**View all users:**
```sql
SELECT * FROM users;
```

**Count users:**
```sql
SELECT COUNT(*) FROM users;
```

**Check unique constraints:**
```sql
SELECT email, COUNT(*) FROM users GROUP BY email HAVING COUNT(*) > 1;
SELECT phone_number, COUNT(*) FROM users GROUP BY phone_number HAVING COUNT(*) > 1;
```

---

## Performance Testing

### Load Test with Apache Bench

```bash
# Create 100 concurrent requests
ab -n 100 -c 10 http://localhost:8081/api/users

# With POST data (requires ab plugin)
ab -n 100 -c 10 -p payload.json -T application/json http://localhost:8081/api/users
```

### Load Test with WRK (Lua scripting)

```bash
wrk -t4 -c100 -d30s http://localhost:8081/api/users
```

---

## Swagger UI Testing

Access the interactive API documentation:
```
http://localhost:8081/swagger-ui.html
```

Features:
- View all endpoints
- Try out API calls directly
- See request/response examples
- Automatic schema validation

---

## Logging and Debugging

### Enable Debug Logging

Update `application.yml`:
```yaml
logging:
  level:
    root: INFO
    org.springframework.web: DEBUG
    com.ecart.user: DEBUG
```

### View Application Logs

```bash
# Tail logs during execution
tail -f /var/log/application.log

# Filter specific logs
grep "UserService" /var/log/application.log
```

---

## Test Checklist

- [ ] Create user (valid data)
- [ ] Create user (duplicate email) → 409
- [ ] Create user (duplicate phone) → 409
- [ ] Create user (invalid email) → 400
- [ ] Create user (missing fields) → 400
- [ ] Get user by ID
- [ ] Get user by ID (non-existent) → 404
- [ ] Get user by email
- [ ] Get user by email (non-existent) → 404
- [ ] Get all users
- [ ] Update user (valid)
- [ ] Update user (non-existent) → 404
- [ ] Update user (duplicate email) → 409
- [ ] Delete user
- [ ] Delete user (non-existent) → 404
- [ ] Verify deleted user returns 404
- [ ] Health check endpoint
- [ ] Swagger UI accessible
- [ ] API docs JSON accessible
- [ ] H2 Console accessible

---

## Known Issues

None currently documented.

---

## Useful Commands

### Run specific test class
```bash
mvn test -Dtest=UserServiceTest
```

### Run specific test method
```bash
mvn test -Dtest=UserServiceTest#testCreateUser
```

### Skip tests during build
```bash
mvn clean install -DskipTests
```

### Generate test coverage report
```bash
mvn clean test jacoco:report
# View report at: target/site/jacoco/index.html
```