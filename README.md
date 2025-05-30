# Asset Management System

A Spring Boot application to track company assets, their categories, and employee assignments.

## Tech Stack

- Spring Boot 2.4
- Spring Data JPA
- H2 In-Memory Database

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
