# Asset Management System

A Spring Boot application to track company assets, their categories, and employee assignments.

## Tech Stack

- Spring Boot 2.4
- Spring Data JPA
- H2 In-Memory Database

## Run Locally

```bash
git clone https://github.com/yourusername/asset-management.git
cd asset-management
mvn spring-boot:run
```

Access H2 console at:  
[http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
JDBC URL: `jdbc:h2:mem:testdb`

## API Endpoints

- POST /categories — Add category
- PUT /categories/{id} — Update category
- GET /categories — List categories
- POST /assets/{categoryId} — Add asset
- GET /assets — List assets
- GET /assets/search?name=... — Search assets
- PUT /assets/{id} — Update asset
- POST /assets/{assetId}/assign/{employeeId} — Assign asset
- POST /assets/{assetId}/recover — Recover asset
- DELETE /assets/{id} — Delete asset
