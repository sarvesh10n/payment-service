

# Setup

### Please refer project-documentation repository for entire setup.

## Base URL

```
http://localhost:4001/payments
```


## Database and Migrations
- This service utilizes **Spring Hibernate** for database queries, ensuring efficient ORM-based interactions.
- **Flyway** manages database schema migrations, enabling version-controlled updates and rollback support.
- Database changes are handled through Flyway migration scripts stored in the `db/migration` folder, ensuring smooth and structured updates without data loss.
- Schema updates are automatically applied at application startup, maintaining consistency across environments (Just need to create database separately as mentioned in start of doc).


## Endpoints

### Please refer `API Documentation` and `Postman Collection` Folder in `project-documentation` repository