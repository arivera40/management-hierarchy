# Company Hierarchy Management System

This project is a Spring Boot application designed to manage employee data within a company hierarchy. It allows users to read employee data from a CSV file, store it in a local database, and provides APIs for querying and managing the company hierarchy.

## Objective

The main objective of this project is to develop a Spring Boot application that demonstrates proficiency in various skills including Spring Boot, Java 8, JUnit tests, API development, working with CSV files, and data persistence with a relational database.

## Skills Tested

- Spring Boot
- Java 8
- JUnit tests
- API development
- Working with CSV files
- Data persistence with a relational database

## Getting Started

To run the project locally, follow these steps:

1. Clone the repository: `git clone https://github.com/yourusername/company-hierarchy-management.git`
2. Navigate to the project directory: `cd company-hierarchy-management`
3. Install dependencies: `mvn install`
4. Start the Spring Boot application: `mvn spring-boot:run`
5. Access the application at: `http://localhost:8080`

## Usage

### API Endpoints

- **POST /employees/startup**: Add formatted CSV to load database
- **GET /employees**: Retrieve all employees
- **GET /employees/{id}**: Retrieve an employee by ID
- **POST /employees**: Add a new employee
- **PUT /employees/{id}**: Update an existing employee
- **DELETE /employees/{id}**: Delete an employee by ID

