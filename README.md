# Sweet Shop Management System

A full-stack web application for managing a traditional Indian sweet shop, built with **Spring Boot** backend and **React TypeScript** frontend.

## � Overview

The Sweet Shop Management System is designed to streamline operations for traditional sweet shops, providing comprehensive management tools for inventory, orders, and customer interactions. The system supports role-based access with dedicated interfaces for administrators and customers.

## ✨ Features

### 👨‍💼 Admin Features
- **Dashboard Management**: Simple and intuitive admin homepage
- **Sweet Management**: Add, edit, delete, and manage sweet inventory
- **Pricing Types**: Support for both per-item and per-kg pricing
- **Order Management**: View and manage all customer orders
- **Order Status Tracking**: Update order status through the complete workflow
- **User Management**: View registered customers

### 👤 Customer Features
- **Browse Products**: View available sweets with detailed information
- **Shopping Cart**: Add items to cart with quantity management
- **Order Placement**: Place orders with real-time availability checking
- **Order History**: Track personal order history and status
- **User Profile**: Manage personal information

### 🔒 Authentication & Authorization
- **JWT-based Authentication**: Secure login system
- **Role-based Access Control**: Separate admin and customer interfaces
- **User Registration**: New customer account creation
- **Session Management**: Persistent login state

## 🛠️ Technology Stack

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

## 📋 Prerequisites

Before running this application, make sure you have the following installed:

- **Java 21** or higher
- **Node.js 16** or higher
- **npm** or **yarn**
- **MySQL 8.0** or higher
- **Maven** (or use the included Maven wrapper)

## 📁 Detailed Project Structure

```
Sweet Shop Management/
├── backend/                                    # Spring Boot Backend Application
│   ├── .gitattributes                         # Git line ending configuration
│   ├── .gitignore                             # Backend-specific Git ignore rules
│   ├── .mvn/                                  # Maven wrapper configuration
│   ├── HELP.md                                # Maven project help documentation
│   ├── mvnw                                   # Maven wrapper script (Unix/Linux)
│   ├── mvnw.cmd                               # Maven wrapper script (Windows)
│   ├── pom.xml                                # Maven project configuration & dependencies
│   ├── setup_database.sql                    # Database initialization script
│   ├── src/main/java/com/sweetshop/sweet_shop_management/
│   │   ├── SweetShopManagementApplication.java # Main Spring Boot application class
│   │   ├── config/                            # Configuration classes
│   │   │   ├── ApplicationLifecycleConfig.java # Application startup/shutdown events
│   │   │   ├── CorsConfig.java               # Cross-Origin Resource Sharing configuration
│   │   │   ├── JwtConfig.java                # JWT token configuration
│   │   │   └── SecurityConfig.java           # Spring Security configuration
│   │   ├── controller/                       # REST API endpoints
│   │   │   ├── AuthController.java           # Authentication & authorization endpoints
│   │   │   ├── OrderController.java          # Order management endpoints
│   │   │   ├── SweetController.java          # Sweet/product management endpoints
│   │   │   └── UserController.java           # User management endpoints
│   │   ├── dto/                              # Data Transfer Objects
│   │   │   ├── request/                      # Request DTOs
│   │   │   │   ├── LoginRequest.java         # User login request
│   │   │   │   ├── OrderCreateRequest.java   # Order creation request
│   │   │   │   ├── RegisterRequest.java      # User registration request
│   │   │   │   ├── SweetCreateRequest.java   # Sweet creation request
│   │   │   │   └── SweetUpdateRequest.java   # Sweet update request
│   │   │   └── response/                     # Response DTOs
│   │   │       ├── AuthResponse.java         # Authentication response
│   │   │       ├── OrderResponse.java        # Order data response
│   │   │       ├── SweetResponse.java        # Sweet data response
│   │   │       └── UserResponse.java         # User data response
│   │   ├── model/                            # JPA Entity classes
│   │   │   ├── Order.java                    # Order entity (customer orders)
│   │   │   ├── OrderItem.java                # Order item entity (items within orders)
│   │   │   ├── Sweet.java                    # Sweet/product entity
│   │   │   └── User.java                     # User entity (admin/customer)
│   │   ├── repository/                       # JPA data repositories
│   │   │   ├── OrderItemRepository.java      # Order item data access
│   │   │   ├── OrderRepository.java          # Order data access
│   │   │   ├── SweetRepository.java          # Sweet data access
│   │   │   └── UserRepository.java           # User data access
│   │   ├── service/                          # Business logic services
│   │   │   ├── impl/                         # Service implementations
│   │   │   │   ├── AuthServiceImpl.java      # Authentication service implementation
│   │   │   │   ├── OrderServiceImpl.java     # Order management service implementation
│   │   │   │   ├── SweetServiceImpl.java     # Sweet management service implementation
│   │   │   │   └── UserServiceImpl.java      # User management service implementation
│   │   │   ├── AuthService.java              # Authentication service interface
│   │   │   ├── DataInitializationService.java # Demo data setup service
│   │   │   ├── OrderService.java             # Order management service interface
│   │   │   ├── SweetService.java             # Sweet management service interface
│   │   │   └── UserService.java              # User management service interface
│   │   └── util/                             # Utility classes
│   │       ├── JwtUtil.java                  # JWT token generation & validation utilities
│   │       └── PasswordUtil.java             # Password hashing & validation utilities
│   ├── src/main/resources/
│   │   ├── application.properties            # Application configuration (database, JWT, etc.)
│   │   ├── static/                           # Static web resources (empty for API-only backend)
│   │   └── templates/                        # Template files (empty for API-only backend)
│   ├── src/test/java/                        # Test classes
│   │   └── com/sweetshop/sweet_shop_management/
│   │       └── SweetShopManagementApplicationTests.java # Main application tests
│   └── target/                               # Compiled classes & build artifacts (generated)
│       ├── classes/                          # Compiled Java classes
│       ├── generated-sources/                # Generated source files
│       ├── generated-test-sources/           # Generated test source files
│       └── test-classes/                     # Compiled test classes
├── frontend/                                 # React TypeScript Frontend Application
│   ├── .gitignore                            # Frontend-specific Git ignore rules
│   ├── package.json                          # NPM dependencies & scripts
│   ├── package-lock.json                     # NPM lock file (auto-generated)
│   ├── tsconfig.json                         # TypeScript compiler configuration
│   ├── public/                               # Static public assets
│   │   ├── index.html                        # Main HTML template
│   │   ├── favicon.ico                       # Application favicon
│   │   ├── logo192.png                       # App logo (192x192)
│   │   ├── logo512.png                       # App logo (512x512)
│   │   ├── manifest.json                     # Progressive Web App manifest
│   │   └── robots.txt                        # Search engine crawling rules
│   ├── src/                                  # Source code
│   │   ├── components/                       # Reusable UI components
│   │   │   ├── common/                       # Common shared components
│   │   │   │   ├── ConfirmDialog.tsx         # Confirmation dialog component
│   │   │   │   ├── ErrorBoundary.tsx         # Error boundary for error handling
│   │   │   │   └── LoadingSpinner.tsx        # Loading spinner component
│   │   │   ├── forms/                        # Form components
│   │   │   │   ├── LoginForm.tsx             # User login form
│   │   │   │   ├── RegisterForm.tsx          # User registration form
│   │   │   │   └── SweetForm.tsx             # Sweet creation/editing form
│   │   │   └── layout/                       # Layout components
│   │   │       ├── Footer.tsx                # Application footer
│   │   │       ├── Layout.tsx                # Main layout wrapper
│   │   │       └── Navbar.tsx                # Navigation bar component
│   │   ├── context/                          # React Context providers
│   │   │   ├── AuthContext.tsx               # Authentication state management
│   │   │   └── CartContext.tsx               # Shopping cart state management
│   │   ├── hooks/                            # Custom React hooks
│   │   │   ├── useAuth.ts                    # Authentication hook
│   │   │   ├── useCart.ts                    # Cart management hook
│   │   │   └── useLocalStorage.ts            # Local storage persistence hook
│   │   ├── pages/                            # Page-level components
│   │   │   ├── AdminPage.css                 # Admin dashboard styles
│   │   │   ├── AdminPage.tsx                 # Admin dashboard page
│   │   │   ├── CartPage.css                  # Shopping cart page styles
│   │   │   ├── CartPage.tsx                  # Shopping cart page
│   │   │   ├── HomePage.css                  # Home page styles
│   │   │   ├── HomePage.tsx                  # Landing/home page
│   │   │   ├── LoginPage.tsx                 # User login page
│   │   │   ├── OrdersPage.css                # Orders page styles
│   │   │   ├── OrdersPage.tsx                # Order history/management page
│   │   │   ├── ProductsPage.css              # Products catalog page styles
│   │   │   ├── ProductsPage.tsx              # Products catalog page
│   │   │   ├── RegisterPage.tsx              # User registration page
│   │   │   └── auth.css                      # Authentication pages styles
│   │   ├── services/                         # API service layer
│   │   │   ├── api.ts                        # Base Axios API configuration
│   │   │   ├── authService.ts                # Authentication API calls
│   │   │   ├── cartService.ts                # Cart management service
│   │   │   ├── orderService.ts               # Order management API calls
│   │   │   ├── sweetService.ts               # Sweet/product API calls
│   │   │   └── userService.ts                # User management API calls
│   │   ├── types/                            # TypeScript type definitions
│   │   │   └── index.ts                      # All interface & type definitions
│   │   ├── utils/                            # Utility functions
│   │   │   ├── constants.ts                  # Application constants
│   │   │   ├── formatters.ts                 # Data formatting utilities
│   │   │   └── validators.ts                 # Form validation utilities
│   │   ├── App.css                           # Global application styles
│   │   ├── App.tsx                           # Main application component
│   │   ├── index.css                         # Global CSS styles
│   │   ├── index.tsx                         # React application entry point
│   │   └── react-app-env.d.ts                # React TypeScript environment types
│   └── node_modules/                         # NPM dependencies (auto-generated, ignored)
├── .gitignore                                # Root Git ignore rules
├── API_DOCUMENTATION_SUMMARY.md              # API documentation summary
├── package.json                              # Root package.json (if any workspace scripts)
└── README.md                                 # This documentation file
```

### 🏗️ Architecture Overview

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

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Sweet-Shop-Management
```

### 2. Database Setup

1. **Create MySQL Database**:
```sql
CREATE DATABASE sweet_shop_db;
```

2. **Configure Database Connection**:
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sweet_shop_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Backend Setup

```bash
# Navigate to backend directory
cd backend

# Clean and compile
./mvnw clean compile

# Run the application
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

### 4. Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

The frontend will start on `http://localhost:3000`

## 🔑 Default User Accounts

The application comes with pre-configured demo accounts:

### Admin Account
- **Email**: `admin@sweetshop.com`
- **Password**: `admin123`
- **Role**: Administrator

### Customer Account
- **Email**: `user@sweetshop.com`
- **Password**: `user123`
- **Role**: Customer

## 🎯 Key Features in Detail

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

## 🌐 API Endpoints

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

## 🐛 Troubleshooting

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

## 🔧 Development

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

## � License

This project is developed as a learning exercise and demonstration of full-stack development capabilities.

## 🤝 Contributing

This is a personal project, but suggestions and improvements are welcome!

## 📞 Support

For any issues or questions, please check the troubleshooting section or review the code documentation.

---

**Mithu Sweet Bhandar** - A modern solution for traditional sweet shop management! 🍬✨