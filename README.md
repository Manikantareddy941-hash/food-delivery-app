# Food Delivery Application

A production-ready food delivery application similar to Swiggy/Zomato built with Java Spring Boot.

## Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.0
- **Security**: Spring Security with JWT authentication
- **Database**: PostgreSQL with Flyway migrations
- **Caching**: Redis
- **Messaging**: Apache Kafka
- **API Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose

## Features

### Core Modules

1. **User Service**
   - User registration and authentication (JWT)
   - Role-based access control (CUSTOMER, RESTAURANT_OWNER, DELIVERY_PARTNER, ADMIN)
   - User profile management

2. **Restaurant Service**
   - Restaurant CRUD operations
   - Menu item management
   - Restaurant availability and status
   - Search and filtering (by city, price, cuisine type)

3. **Order Service**
   - Cart system
   - Order placement
   - Order lifecycle management (PLACED → ACCEPTED → PREPARING → PICKED → DELIVERED → CANCELLED)
   - Event-driven updates using Kafka

4. **Delivery Service**
   - Delivery partner assignment
   - Delivery status tracking
   - Real-time order tracking

5. **Payment Service**
   - Mock payment gateway integration
   - Payment processing and refunds
   - Transaction records

6. **Admin Dashboard**
   - User management
   - Restaurant management
   - Order management
   - Reports and analytics

## Architecture

- **Layered Architecture**: Controller → Service → Repository
- **DTO Pattern**: No entity exposure to API layer
- **Global Exception Handling**: Centralized error handling
- **Validation**: Hibernate Validator for request validation
- **Pagination**: Spring Data pagination support
- **Caching**: Redis for frequently accessed data

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for running dependencies)
- PostgreSQL 15+ (if running locally without Docker)

## Quick Start

### Using Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd food-delivery-app
   ```

2. **Start all services**
   ```bash
   docker-compose up -d
   ```
   This will start:
   - PostgreSQL database
   - Redis cache
   - Zookeeper
   - Kafka
   - Application (after build)

3. **Access the application**
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - API Docs: http://localhost:8080/api-docs

### Manual Setup

1. **Start dependencies**
   ```bash
   docker-compose up -d postgres redis zookeeper kafka
   ```

2. **Configure database**
   - Create database: `food_delivery_db`
   - Update `application.yml` with your database credentials

3. **Build and run**
   ```bash
   mvn clean package
   java -jar target/food-delivery-app-1.0.0.jar
   ```

## Configuration

### Environment Variables

Create a `.env` file or set environment variables:

```env
DB_USERNAME=postgres
DB_PASSWORD=postgres
REDIS_HOST=localhost
REDIS_PORT=6379
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
JWT_SECRET=your-256-bit-secret-key-change-this-in-production-minimum-32-characters
```

### Application Properties

Key configurations in `application.yml`:

- Database connection settings
- JWT secret and expiration
- Redis connection
- Kafka bootstrap servers
- Payment gateway mock settings

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/refresh` - Refresh JWT token

### Users

- `GET /api/users/me` - Get current user profile
- `PUT /api/users/me` - Update profile
- `GET /api/users/{id}` - Get user by ID (Admin only)

### Restaurants

- `GET /api/restaurants` - Get all restaurants (paginated)
- `GET /api/restaurants/{id}` - Get restaurant by ID
- `GET /api/restaurants/city/{city}` - Get restaurants by city
- `GET /api/restaurants/search` - Search with filters
- `POST /api/restaurants` - Create restaurant (Owner only)
- `PUT /api/restaurants/{id}` - Update restaurant (Owner only)
- `DELETE /api/restaurants/{id}` - Delete restaurant (Owner only)

### Menu Items

- `GET /api/restaurants/{restaurantId}/menu-items` - Get menu items
- `POST /api/restaurants/{restaurantId}/menu-items` - Create menu item (Owner only)
- `PUT /api/restaurants/{restaurantId}/menu-items/{id}` - Update menu item (Owner only)
- `DELETE /api/restaurants/{restaurantId}/menu-items/{id}` - Delete menu item (Owner only)

### Orders

- `POST /api/orders` - Place order (Customer only)
- `GET /api/orders/{id}` - Get order by ID
- `GET /api/orders/my-orders` - Get customer orders
- `GET /api/orders/restaurant/{restaurantId}` - Get restaurant orders
- `PUT /api/orders/{id}/status` - Update order status
- `PUT /api/orders/{id}/cancel` - Cancel order (Customer only)

### Payments

- `POST /api/payments` - Process payment (Customer only)
- `GET /api/payments/order/{orderId}` - Get payment by order ID
- `GET /api/payments/transaction/{transactionId}` - Get payment by transaction ID
- `POST /api/payments/refund/{orderId}` - Refund payment (Admin only)

### Deliveries

- `POST /api/deliveries/order/{orderId}` - Create delivery (Admin only)
- `PUT /api/deliveries/order/{orderId}/assign` - Assign delivery partner (Admin only)
- `PUT /api/deliveries/order/{orderId}/status` - Update delivery status (Partner only)
- `GET /api/deliveries/order/{orderId}` - Get delivery by order ID

### Admin

- `GET /api/admin/users` - Get all users (Admin only)
- `GET /api/admin/restaurants` - Get all restaurants (Admin only)
- `GET /api/admin/orders` - Get all orders (Admin only)
- `GET /api/admin/reports/summary` - Get dashboard summary (Admin only)

## Sample Users

The application creates sample users on startup:

- **Admin**: `admin@fooddelivery.com` / `admin123`
- **Customer**: `customer@fooddelivery.com` / `customer123`
- **Restaurant Owner**: `owner@fooddelivery.com` / `owner123`
- **Delivery Partner**: `delivery@fooddelivery.com` / `delivery123`

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

## API Documentation

Once the application is running, access Swagger UI at:
- http://localhost:8080/swagger-ui.html

The API documentation includes:
- All available endpoints
- Request/response schemas
- Authentication requirements
- Example requests

## Database Migrations

Flyway automatically runs migrations on startup. Migration files are located in:
- `src/main/resources/db/migration/`

## Kafka Topics

- `order-events`: Order status change events

## Project Structure

```
src/
├── main/
│   ├── java/com/fooddelivery/
│   │   ├── common/          # Common utilities, configs, exceptions
│   │   ├── user/            # User service
│   │   ├── restaurant/      # Restaurant service
│   │   ├── order/           # Order service
│   │   ├── payment/         # Payment service
│   │   ├── delivery/        # Delivery service
│   │   └── admin/           # Admin APIs
│   └── resources/
│       ├── application.yml  # Application configuration
│       └── db/migration/    # Flyway migrations
└── test/                    # Unit and integration tests
```

## Development

### Adding a New Feature

1. Create entity in appropriate package
2. Create repository interface
3. Create DTOs (Request/Response)
4. Implement service layer
5. Create controller with proper security annotations
6. Add Flyway migration if needed
7. Write unit tests

### Code Style

- Follow Java naming conventions
- Use Lombok for boilerplate code reduction
- Keep methods focused and single-purpose
- Add JavaDoc for public APIs

## Production Considerations

Before deploying to production:

1. **Change JWT Secret**: Update `JWT_SECRET` in environment variables
2. **Database Security**: Use strong passwords and SSL connections
3. **Redis Security**: Enable authentication
4. **Kafka Security**: Configure SASL/SSL
5. **Logging**: Configure proper log aggregation
6. **Monitoring**: Set up application monitoring (Prometheus, Grafana)
7. **Rate Limiting**: Implement API rate limiting
8. **HTTPS**: Use HTTPS in production
9. **CORS**: Configure CORS properly
10. **Error Handling**: Review and customize error messages

## Troubleshooting

### Database Connection Issues
- Check PostgreSQL is running: `docker ps`
- Verify connection string in `application.yml`
- Check database credentials

### Kafka Connection Issues
- Ensure Zookeeper and Kafka are running
- Check `KAFKA_BOOTSTRAP_SERVERS` configuration
- Verify Kafka health: `docker logs food-delivery-kafka`

### Redis Connection Issues
- Verify Redis is running: `docker ps`
- Check Redis connection in `application.yml`
- Test connection: `redis-cli ping`

## License

This project is licensed under the Apache License 2.0.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## Support

For issues and questions, please open an issue on the repository.

## Acknowledgments

Built with Spring Boot and following industry best practices for enterprise applications.
