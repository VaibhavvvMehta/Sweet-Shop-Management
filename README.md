
# Sweet Shop Management System

A full-stack web application for managing a traditional Indian sweet shop, built with **Spring Boot** backend and **React TypeScript** frontend.

## Overview

The Sweet Shop Management System is designed to streamline operations for traditional sweet shops, providing comprehensive management tools for inventory, orders, and customer interactions. The system supports role-based access with dedicated interfaces for administrators and customers.

## Features

### Admin Features
- **Dashboard Management**: Simple and intuitive admin homepage
- **Sweet Management**: Add, edit, delete, and manage sweet inventory
- **Pricing Types**: Support for both per-item and per-kg pricing
- **Order Management**: View and manage all customer orders
- **Order Status Tracking**: Update order status through the complete workflow
- **User Management**: View registered customers

### Customer Features
- **Browse Products**: View available sweets with detailed information
- **Shopping Cart**: Add items to cart with quantity management
- **Order Placement**: Place orders with real-time availability checking
- **Order History**: Track personal order history and status
- **User Profile**: Manage personal information

### Authentication & Authorization
- **JWT-based Authentication**: Secure login system
- **Role-based Access Control**: Separate admin and customer interfaces
- **User Registration**: New customer account creation
- **Session Management**: Persistent login state

## Technology Stack

### Backend
- **Java 21**
- **Spring Boot 3.5.5**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Database Operations)
- **MySQL 8.0** (Primary Database)
- **Maven** (Dependency Management)
- **Hibernate** (ORM)

### Frontend
- **React 19.1.1**
- **TypeScript 4.9.5**
- **React Router 7.9.1** (Navigation)
- **Axios 1.12.2** (HTTP Client)
- **Material-UI 7.3.2** (UI Components)
- **React Hook Form 7.62.0** (Form Management)
- **Yup 1.7.0** (Form Validation)

## Prerequisites

Before running this application, make sure you have the following installed:

- **Java 21** or higher
- **Node.js 16** or higher
- **npm** or **yarn**
- **MySQL 8.0** or higher
- **Maven** (or use the included Maven wrapper)

## Project Structure

```
Sweet Shop Management/
├── backend/                    # Spring Boot Backend Application
│   ├── src/main/java/          # Java source code
│   │   └── com/sweetshop/      # Main application package
│   │       ├── config/         # Configuration classes
│   │       ├── controller/     # REST API endpoints
│   │       ├── dto/            # Data Transfer Objects
│   │       ├── model/          # JPA Entity classes
│   │       ├── repository/     # Data repositories
│   │       ├── service/        # Business logic services
│   │       └── util/           # Utility classes
│   ├── src/main/resources/     # Application resources
│   ├── src/test/               # Test classes
│   └── pom.xml                 # Maven configuration
├── frontend/                   # React TypeScript Frontend
│   ├── public/                 # Static assets
│   ├── src/                    # Source code
│   │   ├── components/         # Reusable UI components
│   │   ├── context/            # React Context providers
│   │   ├── hooks/              # Custom React hooks
│   │   ├── pages/              # Page components
│   │   ├── services/           # API service layer
│   │   ├── types/              # TypeScript definitions
│   │   └── utils/              # Utility functions
│   ├── package.json            # NPM dependencies
│   └── tsconfig.json           # TypeScript configuration
├── .gitignore                  # Git ignore rules
└── README.md                   # Project documentation
```

### Architecture Overview

#### Backend Architecture (Spring Boot)
- **Controller Layer**: REST API endpoints handling HTTP requests/responses
- **Service Layer**: Business logic implementation and data processing
- **Repository Layer**: JPA data access and database operations
- **Model Layer**: JPA entities representing database schema
- **DTO Layer**: Data transfer objects for API communication
- **Security Layer**: JWT authentication and role-based authorization
- **Configuration Layer**: Application settings and Spring bean configurations
- **Utility Layer**: Common utilities for JWT, password handling, etc.

#### Frontend Architecture (React + TypeScript)
- **Component-Based Architecture**: Modular, reusable UI components
- **Context API**: Global state management for authentication and cart
- **Service Layer**: Centralized API communication with backend
- **Type Safety**: Full TypeScript implementation for compile-time type checking
- **Hook-Based Logic**: Custom hooks for reusable stateful logic
- **CSS Modules**: Component-scoped styling approach
- **Page-Based Routing**: React Router for navigation between pages

#### Key Design Patterns
- **Repository Pattern**: Data access abstraction in backend
- **Service Pattern**: Business logic separation and organization
- **DTO Pattern**: Data transfer optimization between layers
- **Context Pattern**: React global state management
- **Hook Pattern**: Reusable stateful logic in React
- **Observer Pattern**: React state updates and re-rendering
- **Factory Pattern**: JWT token creation and validation

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Sweet-Shop-Management
```

### 2. Prerequisites Installation

**Backend Requirements:**
- Install Java 21 or higher
- Install MySQL 8.0 or higher
- Ensure Maven is installed (or use included wrapper)

**Frontend Requirements:**
- Install Node.js 16 or higher
- Install npm or yarn package manager

### 3. Database Setup

1. **Start MySQL Service**:
```bash
# Windows
net start mysql

# macOS (with Homebrew)
brew services start mysql

# Linux
sudo systemctl start mysql
```

2. **Create MySQL Database**:
```sql
CREATE DATABASE sweet_shop_db;
CREATE USER 'sweetshop_user'@'localhost' IDENTIFIED BY 'sweetshop_password';
GRANT ALL PRIVILEGES ON sweet_shop_db.* TO 'sweetshop_user'@'localhost';
FLUSH PRIVILEGES;
```

3. **Configure Database Connection**:
Edit `backend/src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/sweet_shop_db
spring.datasource.username=sweetshop_user
spring.datasource.password=sweetshop_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
app.jwt.secret=mySecretKey
app.jwt.expiration=86400000
```

### 4. Backend Setup

```bash
# Navigate to backend directory
cd backend

# Clean and compile (first time setup)
./mvnw clean compile

# Run tests to verify setup
./mvnw test

# Start the application
./mvnw spring-boot:run
```

**Verification Steps:**
- Backend will start on `http://localhost:8080`
- Check health: `http://localhost:8080/actuator/health` (should return UP)
- API documentation: `http://localhost:8080/swagger-ui.html`

### 5. Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Verify installation
npm audit

# Start development server
npm start
```

**Verification Steps:**
- Frontend will start on `http://localhost:3000`
- Application should automatically open in your default browser
- Check console for any compilation errors

### 6. Application Testing

**Access the Application:**
- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080/api/v1`

**Test Login:**
- Admin: `admin@sweetshop.com` / `admin123`
- Customer: `user@sweetshop.com` / `user123`

## Default User Accounts

The application comes with pre-configured demo accounts:

### Admin Account
- **Email**: `admin@sweetshop.com`
- **Password**: `admin123`
- **Role**: Administrator

### Customer Account
- **Email**: `user@sweetshop.com`
- **Password**: `user123`
- **Role**: Customer

## Key Features in Detail

### Sweet Management
- **Comprehensive Inventory**: Manage sweet details including name, description, price, and quantity
- **Flexible Pricing**: Support for both per-item and per-kilogram pricing models
- **Stock Management**: Track inventory levels and availability
- **Category Organization**: Organize sweets by traditional Indian categories

### Order Workflow
1. **Pending** → New orders awaiting admin review
2. **Confirmed** → Orders accepted by admin
3. **Preparing** → Orders being prepared
4. **Ready** → Orders ready for pickup/delivery
5. **Out for Delivery** → Orders dispatched
6. **Delivered** → Orders completed

### Security Features
- **JWT Token Authentication**: Secure API access
- **Password Encryption**: BCrypt password hashing
- **Role-based Authorization**: Protected admin endpoints
- **CORS Configuration**: Secure cross-origin requests

## API Endpoints

### Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration

### Sweet Management
- `GET /api/v1/sweets` - Get all sweets
- `POST /api/v1/sweets` - Create new sweet (Admin)
- `PUT /api/v1/sweets/{id}` - Update sweet (Admin)
- `DELETE /api/v1/sweets/{id}` - Delete sweet (Admin)

### Order Management
- `GET /api/v1/orders` - Get user orders
- `POST /api/v1/orders` - Create new order
- `PUT /api/v1/orders/{id}/status` - Update order status (Admin)

## Troubleshooting

### Common Issues

1. **Port Already in Use**:
   - Backend (8080): `netstat -ano | findstr :8080` then `taskkill /PID <PID> /F`
   - Frontend (3000): `netstat -ano | findstr :3000` then `taskkill /PID <PID> /F`

2. **Database Connection Issues**:
   - Verify MySQL is running
   - Check database credentials in `application.properties`
   - Ensure database `sweet_shop_db` exists

3. **CORS Errors**:
   - Verify frontend URL in backend CORS configuration
   - Check that both servers are running on correct ports

## Development

### Adding New Features
1. **Backend**: Add controllers, services, and entities as needed
2. **Frontend**: Create components and integrate with backend APIs
3. **Database**: Update entity models and run migrations

### Running Tests
```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd frontend
npm test
```

## License

This project is developed as a learning exercise and demonstration of full-stack development capabilities.

## Contributing

This is a personal project, but suggestions and improvements are welcome!

## Support

For any issues or questions, please check the troubleshooting section or review the code documentation.

## Test Report

### Backend Tests Status
```bash
# Run backend tests
cd backend
./mvnw test
```

**Current Test Configuration:**
- **Test Framework**: JUnit 5 with Spring Boot Test
- **Mock Framework**: Mockito
- **Test Database**: H2 in-memory database for testing
- **Coverage Tool**: JaCoCo Maven Plugin

**Available Test Suites** (Currently in test_disabled directory):
- **Controller Tests**: Authentication, Sweet Management, Order Management
- **Service Tests**: Business Logic validation
- **Integration Tests**: End-to-end API testing
- **Security Tests**: JWT authentication and authorization

**Note**: Tests are currently disabled during development phase. To enable tests:
1. Rename `src/test_disabled` to `src/test`
2. Configure test database properties
3. Run `./mvnw test` to execute test suite

**Expected Test Coverage:**
- Controller Layer: 95%
- Service Layer: 90%
- Repository Layer: 85%
- Overall Coverage Target: 85%

### Frontend Tests Status
```bash
# Run frontend tests
cd frontend
npm test
```

**Current Test Configuration:**
- **Test Framework**: Jest with React Testing Library
- **Type Checking**: TypeScript compiler
- **Component Testing**: React Testing Library
- **Coverage Tool**: Jest Coverage Reports

**Available Tests:**
- **App.test.tsx**: Basic application rendering test
- **Component Tests**: Individual component functionality (to be expanded)
- **Service Tests**: API service layer testing (to be implemented)
- **Hook Tests**: Custom React hooks testing (to be implemented)

**Test Execution:**
```bash
# Current basic test
npm test -- --coverage --watchAll=false
```

**Expected Test Coverage Goals:**
- Component Tests: 80%
- Service Layer: 85%
- Utility Functions: 90%
- Overall Coverage Target: 80%

### Test Development Status
This project is in active development with comprehensive test suites planned. The testing strategy includes:

1. **Unit Tests**: Individual component and function testing
2. **Integration Tests**: API endpoint and database integration testing
3. **End-to-End Tests**: Complete user workflow testing
4. **Security Tests**: Authentication and authorization validation
5. **Performance Tests**: Load and response time testing

*Test implementation is prioritized for production deployment.*

## My AI Usage

This project was developed with the assistance of AI tools to enhance code quality and structure:

### Backend Development
- **ChatGPT**: Used for backend code structure, Spring Boot architecture design, and Java implementation guidance
- **Code Structure**: AI assistance helped in organizing the layered architecture (Controller, Service, Repository, Model)
- **Best Practices**: Implementation of proper Spring Boot patterns and security configurations

### Frontend Development  
- **Claude & ChatGPT**: Collaborative AI assistance for React TypeScript frontend development
- **Component Architecture**: AI guidance for React component structure and TypeScript implementation
- **UI/UX Design**: Assistance with Material-UI integration and responsive design patterns

### Key AI Contributions
- **Project Architecture**: Overall full-stack application structure and design patterns
- **Code Organization**: Proper separation of concerns and modular development approach
- **Documentation**: Comprehensive README and code documentation
- **Best Practices**: Implementation of modern development standards and security practices

*This project demonstrates the effective collaboration between human creativity and AI assistance in modern software development.*

---

**Mithu Sweet Bhandar** - A modern solution for traditional sweet shop management!
