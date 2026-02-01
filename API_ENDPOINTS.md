# API Endpoints Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication

All endpoints except `/api/auth/**` require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## 1. Authentication Endpoints

### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "9876543210",
  "role": "CUSTOMER",
  "address": "123 Main St",
  "city": "Mumbai",
  "pincode": "400001"
}
```

**Response:**
```json
{
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phone": "9876543210",
  "role": "CUSTOMER",
  "active": true
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["CUSTOMER"]
}
```

### Refresh Token
```http
POST /api/auth/refresh?refreshToken=<refresh-token>
```

---

## 2. User Endpoints

### Get Current User Profile
```http
GET /api/users/me
Authorization: Bearer <token>
```

### Update Profile
```http
PUT /api/users/me
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "Jane",
  "lastName": "Doe",
  "phone": "9876543211",
  "address": "456 Oak Ave",
  "city": "Delhi",
  "pincode": "110001"
}
```

---

## 3. Restaurant Endpoints

### Get All Restaurants
```http
GET /api/restaurants?page=0&size=20
```

### Get Restaurant by ID
```http
GET /api/restaurants/1
```

### Search Restaurants
```http
GET /api/restaurants/search?city=Mumbai&minPrice=100&maxPrice=500&cuisineType=Italian&page=0&size=20
```

### Create Restaurant (Owner Only)
```http
POST /api/restaurants
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Pizza Palace",
  "description": "Best pizza in town",
  "address": "123 Food Street",
  "city": "Mumbai",
  "pincode": "400001",
  "phone": "9876543210",
  "email": "info@pizzapalace.com",
  "estimatedDeliveryTimeMinutes": 30,
  "minimumOrderAmount": 200.00,
  "deliveryFee": 30.00,
  "cuisineType": "Italian"
}
```

### Update Restaurant (Owner Only)
```http
PUT /api/restaurants/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Pizza Palace Updated",
  "status": "ACTIVE"
}
```

### Delete Restaurant (Owner Only)
```http
DELETE /api/restaurants/1
Authorization: Bearer <token>
```

---

## 4. Menu Item Endpoints

### Get Menu Items for Restaurant
```http
GET /api/restaurants/1/menu-items
```

### Create Menu Item (Owner Only)
```http
POST /api/restaurants/1/menu-items
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Margherita Pizza",
  "description": "Classic Italian pizza",
  "price": 299.00,
  "category": "Pizza",
  "available": true,
  "preparationTimeMinutes": 15,
  "isVegetarian": true,
  "isSpicy": false
}
```

### Update Menu Item (Owner Only)
```http
PUT /api/restaurants/1/menu-items/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "price": 349.00,
  "available": true
}
```

### Delete Menu Item (Owner Only)
```http
DELETE /api/restaurants/1/menu-items/1
Authorization: Bearer <token>
```

---

## 5. Order Endpoints

### Place Order (Customer Only)
```http
POST /api/orders
Authorization: Bearer <token>
Content-Type: application/json

{
  "restaurantId": 1,
  "items": [
    {
      "menuItemId": 1,
      "quantity": 2
    },
    {
      "menuItemId": 2,
      "quantity": 1
    }
  ],
  "deliveryAddress": "123 Main St",
  "deliveryCity": "Mumbai",
  "deliveryPincode": "400001",
  "deliveryPhone": "9876543210",
  "specialInstructions": "Please ring the doorbell",
  "paymentMethod": "UPI"
}
```

**Response:**
```json
{
  "id": 1,
  "orderNumber": "ORD1234567890",
  "customerId": 1,
  "restaurantId": 1,
  "status": "PLACED",
  "subtotal": 598.00,
  "deliveryFee": 30.00,
  "tax": 107.64,
  "totalAmount": 735.64,
  "deliveryAddress": "123 Main St",
  "orderItems": [
    {
      "menuItemId": 1,
      "menuItemName": "Margherita Pizza",
      "quantity": 2,
      "unitPrice": 299.00,
      "totalPrice": 598.00
    }
  ],
  "createdAt": "2024-01-15T10:30:00"
}
```

### Get Order by ID
```http
GET /api/orders/1
Authorization: Bearer <token>
```

### Get My Orders (Customer Only)
```http
GET /api/orders/my-orders?page=0&size=20
Authorization: Bearer <token>
```

### Update Order Status
```http
PUT /api/orders/1/status?status=ACCEPTED
Authorization: Bearer <token>
```

**Status values:** PLACED, ACCEPTED, PREPARING, PICKED, DELIVERED, CANCELLED

### Cancel Order (Customer Only)
```http
PUT /api/orders/1/cancel
Authorization: Bearer <token>
```

---

## 6. Payment Endpoints

### Process Payment (Customer Only)
```http
POST /api/payments
Authorization: Bearer <token>
Content-Type: application/json

{
  "orderId": 1,
  "paymentMethod": "UPI"
}
```

**Response:**
```json
{
  "id": 1,
  "orderId": 1,
  "transactionId": "TXN1234567890",
  "status": "COMPLETED",
  "paymentMethod": "UPI",
  "amount": 735.64,
  "gatewayResponse": "Payment successful. Transaction ID: TXN1234567890"
}
```

### Get Payment by Order ID
```http
GET /api/payments/order/1
Authorization: Bearer <token>
```

### Refund Payment (Admin Only)
```http
POST /api/payments/refund/1
Authorization: Bearer <token>
```

---

## 7. Delivery Endpoints

### Create Delivery (Admin Only)
```http
POST /api/deliveries/order/1
Authorization: Bearer <token>
```

### Assign Delivery Partner (Admin Only)
```http
PUT /api/deliveries/order/1/assign?deliveryPartnerId=4
Authorization: Bearer <token>
```

### Update Delivery Status (Partner Only)
```http
PUT /api/deliveries/order/1/status?status=PICKED_UP
Authorization: Bearer <token>
```

**Status values:** PENDING, ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED, FAILED

### Get Delivery by Order ID
```http
GET /api/deliveries/order/1
Authorization: Bearer <token>
```

---

## 8. Admin Endpoints

All admin endpoints require ADMIN role.

### Get All Users
```http
GET /api/admin/users?page=0&size=20
Authorization: Bearer <token>
```

### Get All Restaurants
```http
GET /api/admin/restaurants?page=0&size=20
Authorization: Bearer <token>
```

### Get All Orders
```http
GET /api/admin/orders?status=PLACED&page=0&size=20
Authorization: Bearer <token>
```

### Get Dashboard Summary
```http
GET /api/admin/reports/summary
Authorization: Bearer <token>
```

---

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "email": "Email should be valid",
    "password": "Password must be at least 8 characters"
  }
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

---

## Sample Workflow

1. **Register/Login** → Get JWT token
2. **Browse Restaurants** → Get list of restaurants
3. **View Menu** → Get menu items for a restaurant
4. **Place Order** → Create order with items
5. **Process Payment** → Pay for the order
6. **Track Order** → Check order status
7. **Track Delivery** → Check delivery status

---

## Notes

- All timestamps are in ISO 8601 format
- All monetary values are in the base currency (e.g., INR)
- Pagination uses 0-based page numbers
- Default page size is 20 items
- JWT tokens expire after 24 hours (configurable)
- Refresh tokens expire after 7 days (configurable)
