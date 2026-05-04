# Backend Technology Stack

## Core Technologies

- Programming Language: Java 21
- Framework: Spring Boot
- Database: PostgreSQL
- Database Migration Tool: Liquibase
- Message Broker: Apache Kafka

## Technology Responsibilities

### Backend Application (Spring Boot)

The backend service is implemented using Spring Boot and is responsible for:
- handling HTTP requests (REST API)
- implementing business logic
- interacting with the database
- producing and/or consuming messages via Kafka

### PostgreSQL

PostgreSQL is used as the primary data storage:
- relational data model
- transactional support
- data integrity and consistency

### Liquibase

Liquibase is used for database schema management:
- version control for database structure
- automated execution of migrations
- support for rollback operations

### Apache Kafka

Kafka is used for asynchronous communication between system components:
- event-driven data exchange
- real-time message processing
- reduced coupling between services

## Internal Interaction

- The backend receives and processes HTTP requests
- Depending on the operation, it:
  - interacts with PostgreSQL
  - publishes events to Kafka
  - consumes messages from Kafka
