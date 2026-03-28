# Payment Service API - Quick Testing Guide

This guide provides ready-to-use curl commands to test the Payment Service API.

## Quick Start

1. **Build and run the application:**
```bash
cd /Users/harik/Dev/java/payment
mvn clean install
mvn spring-boot:run
```

2. **Open Swagger UI:** `http://localhost:8080/swagger-ui.html`

3. **Use curl commands below to test the API**

---


## Testing Workflow

### Step 1: Create a User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+1234567890",
    "address": "123 Main Street",
    "city": "New York",
    "state": "NY",
    "zipCode": "10001"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1234567890",
  "address": "123 Main Street",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "createdAt": "2024-03-28T10:30:00",
  "updatedAt": "2024-03-28T10:30:00"
}
```

Note the `id` (usually 1 for first user) - you'll need it for payment creation.

---

### Step 2: Get User Details

```bash
curl http://localhost:8080/api/users/1
```

Or by email:
```bash
curl http://localhost:8080/api/users/email/john.doe@example.com
```

---

### Step 3: Create a Payment

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 150.00,
    "currency": "USD",
    "paymentMethod": "CREDIT_CARD",
    "description": "Purchase of Premium Package",
    "referenceNumber": "REF2024-001"
  }'
```

**Expected Response:**
```json
{
  "id": 1,
  "transactionId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": 1,
  "amount": 150.00,
  "currency": "USD",
  "status": "PENDING",
  "paymentMethod": "CREDIT_CARD",
  "description": "Purchase of Premium Package",
  "referenceNumber": "REF2024-001",
  "createdAt": "2024-03-28T10:35:00",
  "updatedAt": "2024-03-28T10:35:00",
  "processedAt": null
}
```

Note the payment `id` - you'll need it for payment operations.

---

### Step 4: Process Payment

```bash
curl -X POST http://localhost:8080/api/payments/1/process
```

**Expected Response:**
```json
{
  "id": 1,
  "status": "PROCESSING",
  "updatedAt": "2024-03-28T10:36:00",
  ...
}
```

---

### Step 5: Complete Payment

```bash
curl -X POST http://localhost:8080/api/payments/1/complete
```

**Expected Response:**
```json
{
  "id": 1,
  "status": "COMPLETED",
  "processedAt": "2024-03-28T10:37:00",
  ...
}
```

---

### Step 6: Get Payment Details

```bash
curl http://localhost:8080/api/payments/1
```

Or by transaction ID:
```bash
curl http://localhost:8080/api/payments/transaction/550e8400-e29b-41d4-a716-446655440000
```

---

### Step 7: View Payment Transactions

```bash
curl http://localhost:8080/api/transactions/payment/1
```

---

### Step 8: Refund Payment

```bash
curl -X POST http://localhost:8080/api/payments/1/refund
```

**Expected Response:**
```json
{
  "id": 1,
  "status": "REFUNDED",
  ...
}
```

---

## All Available Payment Methods

When creating a payment, use one of these payment methods:

```
CREDIT_CARD
DEBIT_CARD
NET_BANKING
WALLET
UPI
CHEQUE
```

---

## All Payment Statuses

```
PENDING      - Payment waiting to be processed
PROCESSING   - Payment is being processed
COMPLETED    - Payment successfully completed
FAILED       - Payment processing failed
CANCELLED    - Payment was cancelled
REFUNDED     - Payment was refunded
```

---

## Useful Query Endpoints

### Get all users
```bash
curl http://localhost:8080/api/users
```

### Get all payments
```bash
curl http://localhost:8080/api/payments
```

### Get all payments for a user
```bash
curl http://localhost:8080/api/payments/user/1
```

### Get all payments by status
```bash
curl http://localhost:8080/api/payments/status/COMPLETED
```

### Get all transactions
```bash
curl http://localhost:8080/api/transactions
```

---

## Testing Multiple Users & Payments

### Create Second User
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "jane.smith@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "phoneNumber": "+1987654321",
    "address": "456 Oak Avenue",
    "city": "Los Angeles",
    "state": "CA",
    "zipCode": "90001"
  }'
```

### Create Multiple Payments
```bash
# Payment 2
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": 75.50,
    "currency": "USD",
    "paymentMethod": "DEBIT_CARD",
    "description": "Monthly Subscription",
    "referenceNumber": "SUB-001"
  }'

# Payment 3
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 2,
    "amount": 200.00,
    "currency": "USD",
    "paymentMethod": "UPI",
    "description": "Service Fee",
    "referenceNumber": "SVC-001"
  }'
```

---

## Error Scenarios to Test

### Create User with Duplicate Email
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "firstName": "Jane",
    "lastName": "Smith",
    "phoneNumber": "+1234567891",
    "address": "999 Main St",
    "city": "Boston",
    "state": "MA",
    "zipCode": "02101"
  }'
```

**Expected Error Response (409 Conflict):**
```json
{
  "status": 409,
  "message": "User with email already exists",
  "timestamp": "2024-03-28T10:40:00"
}
```

### Invalid Payment Amount
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "amount": -50.00,
    "currency": "USD",
    "paymentMethod": "CREDIT_CARD",
    "description": "Invalid Payment"
  }'
```

**Expected Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "amount": "Amount must be greater than 0"
  },
  "timestamp": "2024-03-28T10:41:00"
}
```

### Invalid User ID
```bash
curl http://localhost:8080/api/users/999
```

**Expected Error Response (404 Not Found):**
```json
{
  "status": 404,
  "message": "User not found with id: 999",
  "timestamp": "2024-03-28T10:42:00"
}
```

---

## H2 Database Console

You can also view the database directly:

**URL:** `http://localhost:8080/h2-console`

**Credentials:**
- JDBC URL: `jdbc:h2:mem:paymentdb`
- Username: `sa`
- Password: (leave blank)

---

## Tips for Testing

1. **Use Swagger UI** for a visual interface: `http://localhost:8080/swagger-ui.html`
2. **Check H2 Console** to view database tables and data
3. **Monitor Logs** - Check the console output for SQL queries and application logs
4. **Test Error Cases** - Try invalid inputs to see error handling
5. **Create Multiple Records** - Test with various users and payments

---

## Common Issues & Solutions

### `Port 8080 already in use`
Change the port in `application.properties`:
```properties
server.port=8081
```

### `H2 console not showing data`
- Verify H2 is enabled in `application.properties`
- Ensure JDBC URL in H2 console matches: `jdbc:h2:mem:paymentdb`

### `Payment processing fails with "invalid status"`
- Payments must follow the status flow: PENDING → PROCESSING → COMPLETED
- You cannot skip steps or go backwards

---

For more detailed information, refer to the [README.md](README.md) file.
