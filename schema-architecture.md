SECTION 1. Architecture summary

This Spring Boot application follows a hybrid architecture, combining both MVC and RESTful design patterns. The application provides web-based interfaces for Admin and Doctor users using Thymeleaf templates rendered through MVC controllers, while other modules (such as patient registration, appointment scheduling, and prescription management) are exposed as RESTful APIs to support front-end clients or mobile apps.

The system integrates with two different databases: a relational MySQL database and a NoSQL MongoDB database. MySQL is used for managing structured data related to patients, doctors, appointments, and admin users. These entities are mapped using Spring Data JPA. MongoDB, on the other hand, stores prescription data in a flexible document format, suitable for the semi-structured nature of medical prescriptions.

A centralized service layer acts as a bridge between the controllers and the data access layer, encapsulating business logic and ensuring consistency. This layered approach helps keep the architecture modular, maintainable, and scalable. Repositories are used to abstract the database operations, with JPA repositories for MySQL and Mongo repositories for MongoDB.

SECTION 2. Numbered flow of data and control

User initiates a request
Request reaches the appropriate controller
Controller delegates to the service layer
Service layer interacts with the repository layer
Repositories access the appropriate database
Data is mapped to Java model classes
Response is prepared and returned to the user
