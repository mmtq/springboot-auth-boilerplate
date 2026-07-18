# Spring Boot Boilerplate with Authentication

A production-oriented authentication boilerplate built with Spring Boot, Spring Security, JPA, and PostgreSQL.

This project implements a session-based authentication system using secure HttpOnly cookies instead of JWT-based authentication.

---

## Features Implemented

<img width="463" height="887" alt="image" src="https://github.com/user-attachments/assets/fc383d57-7716-4d46-83ad-12fb3dc09cb5" />


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
