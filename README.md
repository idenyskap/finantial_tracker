# Financial Tracker

A comprehensive personal finance management application built with Spring Boot and React. Track your expenses, manage budgets, set financial goals, and gain insights into your spending patterns with powerful analytics and visualization tools.

## Features

### 🔐 Authentication & Security
- **User Registration & Login** with JWT authentication
- **Two-Factor Authentication (2FA)** with QR code setup
- **Email Verification** and password reset functionality
- **Secure Session Management** with token-based authentication

### 💰 Transaction Management
- **Add, edit, and delete transactions** with detailed categorization
- **Import transactions from CSV files** for bulk data entry
- **Advanced search and filtering** with saved search capabilities
- **Transaction tagging** for better organization
- **Recurring transactions** with automated scheduling

### 📊 Analytics & Reporting
- **Dashboard overview** with key financial metrics
- **Interactive charts** showing expense/income trends
- **Category-wise spending analysis** with pie charts
- **Monthly and weekly reports** via email
- **Comparative analytics** across different time periods
- **Balance tracking** with historical data

### 🎯 Budget & Goals
- **Budget creation and tracking** with spending limits
- **Goal setting** with progress monitoring
- **Budget alerts and notifications** when limits are exceeded
- **Priority-based goal management**

### 🌍 Multi-Currency Support
- **Currency conversion** with real-time exchange rates
- **Multi-currency transaction support**
- **Currency preference settings**
- **Exchange rate history tracking**

### 🔔 Notifications
- **Email notifications** for budget alerts and reminders
- **Customizable notification settings**
- **Daily and weekly financial summaries**
- **Payment reminders** for recurring expenses

### 🌐 Internationalization
- **Multi-language support** (English, Ukrainian)
- **Localized currency formats**
- **Theme customization** (Light/Dark mode)

## Tech Stack

### Backend
- **Java 17** with **Spring Boot 3.5**
- **Spring Security** for authentication and authorization
- **Spring Data JPA** with Hibernate for database operations
- **PostgreSQL** as primary database
- **Flyway** for database migrations
- **JWT** for stateless authentication
- **MapStruct** for object mapping
- **Lombok** for reducing boilerplate code
- **Apache POI** for Excel file processing
- **OpenCSV** for CSV file handling
- **Spring Mail** for email functionality
- **Spring Cache** for performance optimization

### Frontend
- **React 19** with modern hooks and context API
- **Vite** for fast development and building
- **React Router v7** for client-side routing
- **TanStack Query** for server state management
- **Chart.js** with React Chart.js 2 for data visualization
- **Axios** for API communication
- **i18next** for internationalization
- **Heroicons & Lucide React** for icons
- **Sonner** for toast notifications

### Database
- **PostgreSQL 15** for production
- **H2** for testing
- **Flyway migrations** for version control

### Development Tools
- **Maven** for dependency management and building
- **Docker Compose** for local development environment
- **ESLint** for code quality
- **Spring Boot DevTools** for hot reloading

## Getting Started

### Prerequisites
- **Java 17** or higher
- **Node.js 18** or higher
- **PostgreSQL 15** (or use Docker Compose)
- **Maven 3.6** or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/financial-tracker.git
   cd financial-tracker
   ```

2. **Start PostgreSQL database**
   ```bash
   docker-compose up -d
   ```

3. **Configure application properties**
   
   Create `src/main/resources/application-dev.yml` with your database and email settings:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5433/postgres
       username: postgres
       password: postgres
     mail:
       host: your-smtp-host
       port: 587
       username: your-email@example.com
       password: your-app-password
   ```

4. **Build and run the backend**
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

5. **Install and run the frontend**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

6. **Access the application**
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

## API Documentation

The application includes comprehensive API documentation using OpenAPI 3. Access the interactive documentation at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Database Schema

The application uses Flyway for database migrations. Key entities include:
- **Users** - User accounts with authentication details
- **Transactions** - Financial transactions with categories and tags
- **Categories** - Transaction categories (Income/Expense)
- **Budgets** - Spending limits with period tracking
- **Goals** - Financial goals with progress tracking
- **Recurring Transactions** - Automated transaction scheduling
- **Saved Searches** - User-defined search filters
- **Currencies** - Multi-currency support with exchange rates

## Testing

### Backend Tests
```bash
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm run lint
```

## Deployment

### Production Build

1. **Build the frontend**
   ```bash
   cd frontend
   npm run build
   ```

2. **Build the backend**
   ```bash
   ./mvnw clean package -DskipTests
   ```

3. **Run the application**
   ```bash
   java -jar target/financial_tracker-0.0.1-SNAPSHOT.jar
   ```

### Environment Variables

Configure these environment variables for production:
- `SPRING_PROFILES_ACTIVE=prod`
- `DATABASE_URL=jdbc:postgresql://localhost:5432/financialtracker`
- `DATABASE_USERNAME=your_db_user`
- `DATABASE_PASSWORD=your_db_password`
- `JWT_SECRET=your_jwt_secret_key`
- `MAIL_HOST=your_smtp_host`
- `MAIL_USERNAME=your_email`
- `MAIL_PASSWORD=your_app_password`

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Security Features

- **JWT-based authentication** with secure token handling
- **Password encryption** using BCrypt
- **CORS protection** with configurable origins
- **Input validation** and sanitization
- **SQL injection prevention** through JPA
- **Two-factor authentication** for enhanced security
- **Rate limiting** on sensitive endpoints
- **Secure email verification** workflow

## Performance Optimizations

- **Database indexing** on frequently queried columns
- **Spring Cache** for expensive operations
- **Lazy loading** for JPA relationships
- **Query optimization** with custom repository methods
- **Frontend code splitting** and lazy loading
- **Image and asset optimization**

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, please open an issue in the GitHub repository or contact the development team.

---

**Version**: 0.0.1-SNAPSHOT  
**Last Updated**: August 2025