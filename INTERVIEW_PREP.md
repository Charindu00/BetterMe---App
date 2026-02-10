# 🎓 BetterMe - Interview Preparation Guide

This guide is designed to help you explain the **BetterMe** project confidently in technical interviews. It covers the architecture, key technical decisions, and common questions interviewers might ask.

---

## 1. Project Overview (The "Elevator Pitch")

**"Tell me about this project."**

> "BetterMe is a full-stack self-improvement application designed to help users build habits and track goals. I built it to solve the problem of [fragmented tools/lack of motivation].
> 
> It features a **Spring Boot** backend with a **PostgreSQL** database for robust data management, and a **React** frontend for a responsive user experience.
> 
> Key features include:
> *   **AI Coaching**: Integrated Google Gemini to provide personalized daily motivation.
> *   **Gamification**: A custom achievements engine to drive user engagement.
> *   **Analytics**: Visualizing weekly progress and streaks.
> *   **Security**: Full JWT-based authentication with email verification."

---

## 2. Technical Architecture 🏗️

### Backend (Spring Boot 3 + Java 17)
*   **Layered Architecture**:
    *   `Controller`: Handles HTTP requests (REST API).
    *   `Service`: Business logic (e.g., calculating streaks, calling AI).
    *   `Repository`: Data access (Spring Data JPA interfaces).
    *   `Model`: JPA Entities mapping to database tables.
*   **Security**:
    *   **Stateless Auth**: Uses **JWT (JSON Web Tokens)**. No server-side sessions.
    *   **SecurityConfig**: Configures CORS, password hashing (`BCrypt`), and public/private endpoints.
    *   **Filters**: `JwtAuthenticationFilter` intercepts requests to validate the token before they reach the controller.

### Frontend (React 18 + Vite)
*   **Component-Based**: Modular UI (Dashboard, HabitCard, GoalCard).
*   **State Management**: Uses `useState` and `useEffect` hooks. Context API (`AuthContext`) manages global user state.
*   **Routing**: `react-router-dom` for client-side navigation.
*   **Styling**: Custom CSS with CSS variables for **Dark/Light mode** theming.

### Database (PostgreSQL)
*   **Relational Model**:
    *   `User` (1) ↔ (Many) `Habit`
    *   `User` (1) ↔ (Many) `Goal`
    *   `User` (1) ↔ (Many) `Notification`
    *   `Habit` (1) ↔ (Many) `HabitCheckIn`

---

## 3. Key Technical Challenges & Solutions 💡

**Q: "What was the most challenging part of this project?"**

### Challenge 1: The Gamification Logic (Achievements)
*   **Problem**: How to efficiently track and unlock achievements like "7-day streak" or "100 check-ins" without slowing down the app?
*   **Solution**:
    *   I created a dynamic `Achievement` DTO system in `DashboardService`.
    *   Instead of a complex event bus, I calculate achievements **on-demand** when the dashboard loads.
    *   I fetch raw stats (total check-ins, active habits) and compare them against an `Evidence` enum (`AchievementType`) to determine if they are unlocked.
    *   *Why this is good:* It avoids storing redundant "unlocked" states in the DB that could get out of sync.

### Challenge 2: AI Integration
*   **Problem**: Integrating an LLM (Large Language Model) securely.
*   **Solution**:
    *   Used **Google's Gemini API**.
    *   Created `GeminiService` to encapsulate all API logic.
    *   **Prompt Engineering**: I designed specific system prompts (`"You are a supportive life coach..."`) to ensure the AI creates helpful, non-robotic responses.
    *   **Security**: API keys are injected via environment variables (`application.yml`), never hardcoded.

### Challenge 3: Secure Email Verification
*   **Problem**: Preventing fake users from clogging the database.
*   **Solution**:
    *   Implemented a 2-step registration.
    *   1. User signs up → Account created but `enabled = false`.
    *   2. Backend generates a random token, saves it to `VerificationToken` table, and emails it (via Gmail SMTP).
    *   3. User clicks link → Backend validates token → Sets `enabled = true`.
    *   *Transaction Management*: Used `@Transactional` to ensure token deletion and user activation happen atomically.

---

## 4. Mock Interview Questions 🎤

### Generic / Behavioral

**Q1: Why did you choose React and Spring Boot?**
*   **A**: "I chose **Spring Boot** because it's the industry standard for enterprise backends, offering strong type safety, dependency injection, and excellent security features out of the box. I chose **React** for its component-based architecture and rich ecosystem, which allowed me to build a dynamic, interactive UI quickly. The separation of concerns (Frontend vs Backend) also makes the app scalable."

**Q2: How does the app handle data validation?**
*   **A**: "On the backend, I use Jakarta Validation (`@Valid`, `@NotNull`) in DTOs. On the frontend, I use simple form validation state. If the backend rejects data (e.g., invalid email), it returns a 400 Bad Request, which the global Axios interceptor catches and displays."

### Technical Deep Dives

**Q3: Explain how JWT authentication works in your app.**
*   **A**: "When a user logs in, the backend validates their credentials and generates a signed JWT containing their email and role. This token is sent to the frontend.
    *   The frontend stores it in `localStorage`.
    *   An **Axios Interceptor** attaches this token (`Bearer <token>`) to the `Authorization` header of every subsequent request.
    *   The backend's `JwtAuthenticationFilter` reads this header, verifies the signature, and sets the `SecurityContext` for that request."

**Q4: How did you handle CORS issues?**
*   **A**: "Since the frontend (port 5173) and backend (port 8080) run on different origins during development, browsers block requests by default. I configured a `CorsConfigurationSource` bean in Spring Security to explicitly allow requests from my frontend's origin, allowing methods like GET, POST, PUT, DELETE."

**Q5: How would you scale this app if it had 100,000 users?**
*   **A**:
    *   **Database**: Add an index on frequently queried columns (like `user_id` in `habits` table). Use connection pooling (already handled by HikariCP in Spring Boot).
    *   **Caching**: Introduce **Redis** to cache the Dashboard Summary, as calculating streaks every time is expensive.
    *   **Async**: Move email sending to a message queue (RabbitMQ/Kafka) so the user doesn't have to wait for the email to send before the request completes.

---

## 5. Code Walkthrough Scenarios 💻

*(Be ready to open these files if asked)*

*   **"Show me your database models"**: Open `backend/src/main/java/com/betterme/model/User.java` and `Habit.java`. Explain the `@OneToMany` relationship.
*   **"Show me your API logic"**: Open `DashboardService.java`. It's the "brain" that aggregates data.
*   **"Show me the frontend"**: Open `frontend/src/pages/Dashboard.jsx`. Show how `useEffect` fetches data and `useState` holds it.

---

Good luck! You built a solid, portfolio-worthy application. Be proud of the clean architecture and the "extra mile" features like AI and Gamification. 🚀
