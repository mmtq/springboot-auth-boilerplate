# Spring Boot Authentication Boilerplate

A production-oriented authentication boilerplate built with Spring Boot, Spring Security, JPA, and PostgreSQL.

This project implements a session-based authentication system using secure HttpOnly cookies instead of JWT-based authentication.

---

## Features Implemented

### Authentication

- User registration
- User login
- User logout
- Current user endpoint (`/me`)
- Session-based authentication
- Secure HttpOnly cookie authentication
- Database-backed session management

---

## Authentication Approach

This project uses **database-backed sessions** instead of JWT.

### Why session-based authentication?

Instead of storing authentication data inside a JWT, the server maintains sessions in the database.

The browser stores only a session token:
