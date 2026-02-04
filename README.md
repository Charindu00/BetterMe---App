# 🚀 BetterMe - AI-Powered Self-Improvement App

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot)
![React](https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)
![Gemini AI](https://img.shields.io/badge/Gemini-AI-8E75B2?style=for-the-badge&logo=google)

**A full-stack self-improvement application with AI-powered motivation coaching**

[Features](#-features) • [Tech Stack](#-tech-stack) • [Quick Start](#-quick-start) • [API Endpoints](#-api-endpoints)

</div>

---

## 📖 About

BetterMe is a comprehensive self-improvement platform that helps users build better habits, track their progress, and stay motivated through AI-powered coaching. Features include streak tracking, visual progress dashboards, personalized motivational messages, and a full admin panel.

## ✨ Features

- 🔐 **Secure Authentication** - JWT-based auth with role-based access control
- 🎯 **Habit Tracking** - Create habits, check in daily, track streaks 🔥
- 📈 **Progress Dashboard** - Weekly, monthly views + achievements 🏆
- 🤖 **AI Motivation Coach** - Powered by Google Gemini API
- 🎯 **Goal Setting** - Set targets with deadlines and track progress
- 📊 **Analytics Charts** - Trends, heatmaps, per-habit stats
- 👨‍💼 **Admin Dashboard** - User management, activity logs, and system stats
- 📢 **Announcements** - Admin-to-user communication system
- 🐳 **Docker Ready** - One command to start the full stack
- 📱 **Responsive Design** - Works on desktop and mobile

## 🛠 Tech Stack

### Backend
- **Framework**: Spring Boot 3.2
- **Security**: Spring Security with JWT + Role-based access
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA / Hibernate
- **AI Integration**: Google Gemini API

### DevOps
- **Containerization**: Docker & Docker Compose
- **Version Control**: Git & GitHub

---

## 🚀 Quick Start

### Option 1: Docker (Recommended)

```bash
# Clone the repository
git clone https://github.com/Charindu00/BetterMe---App.git
cd BetterMe---App

# Start everything with Docker
docker-compose up --build
```

The API will be available at `http://localhost:8080`

### Option 2: Local Development

```bash
# 1. Start PostgreSQL via Docker
docker-compose -f docker-compose.dev.yml up -d

# 2. Run Spring Boot
cd backend
mvn spring-boot:run
```

---

## 🔑 Default Admin Credentials

| Field | Value |
|-------|-------|
| Email | `admin@betterme.com` |
| Password | `admin123` |

> ⚠️ **Change these in production!** Set `ADMIN_EMAIL` and `ADMIN_PASSWORD` environment variables.

---

## 📋 API Endpoints

### Authentication (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login and get JWT |
| GET | `/api/auth/health` | Health check |

### Habits (Authenticated)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/habits` | List user's habits |
| POST | `/api/habits` | Create new habit |
| GET | `/api/habits/{id}` | Get habit details |
| PUT | `/api/habits/{id}` | Update habit |
| DELETE | `/api/habits/{id}` | Delete (archive) habit |
| POST | `/api/habits/{id}/checkin` | Check in today 🔥 |
| GET | `/api/habits/{id}/history` | Get check-in history |
| GET | `/api/habits/stats` | Get habit statistics |

### Dashboard & Visualizations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard/summary` | Quick stats overview |
| GET | `/api/dashboard/weekly` | Last 7 days progress |
| GET | `/api/dashboard/monthly` | Calendar view data |
| GET | `/api/dashboard/streaks` | Top streaks leaderboard |
| GET | `/api/dashboard/achievements` | Earned badges 🏆 |

### AI Motivation Coach (Gemini API)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/motivation/daily` | Personalized daily motivation |
| POST | `/api/motivation/habit/{id}` | AI tips for specific habit |
| GET | `/api/motivation/celebration` | Achievement celebration 🎉 |
| POST | `/api/motivation/chat` | Chat with Coach AI 🤖 |

### Announcements

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/announcements/active` | Public | Get active announcements |
| GET | `/api/admin/announcements` | Admin | Get all announcements |
| POST | `/api/admin/announcements` | Admin | Create announcement |
| PUT | `/api/admin/announcements/{id}/toggle` | Admin | Toggle active status |
| DELETE | `/api/admin/announcements/{id}` | Admin | Delete announcement |

### Goal Setting

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/goals` | List all goals |
| POST | `/api/goals` | Create goal |
| PUT | `/api/goals/{id}` | Update goal |
| DELETE | `/api/goals/{id}` | Delete goal |
| POST | `/api/goals/{id}/progress` | Update progress |
| GET | `/api/goals/stats` | Goal statistics |

### Analytics Charts

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/analytics/trends` | Daily/weekly trends 📈 |
| GET | `/api/analytics/heatmap` | GitHub-style heatmap 🗓️ |
| GET | `/api/analytics/habits` | Per-habit completion rates |

### Admin Dashboard

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/stats` | Dashboard statistics |
| GET | `/api/admin/users` | List all users |
| DELETE | `/api/admin/users/{id}` | Delete user |
| PUT | `/api/admin/users/{id}/role` | Change user role |
| GET | `/api/admin/activity-logs` | View activity logs |

---

## 📁 Project Structure

```
betterme/
├── backend/                  # Spring Boot API
│   ├── src/main/java/com/betterme/
│   │   ├── config/           # Security, DataSeeder
│   │   ├── controller/       # REST endpoints
│   │   ├── dto/              # Request/Response objects
│   │   ├── model/            # Entities (User, Role, etc.)
│   │   ├── repository/       # Data access
│   │   ├── security/         # JWT handling
│   │   └── service/          # Business logic
│   ├── Dockerfile
│   └── pom.xml
│
├── docker-compose.yml        # Full stack (API + DB)
├── docker-compose.dev.yml    # DB only for local dev
└── README.md
```

---

## 🔧 Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_PASSWORD` | `betterme123` | PostgreSQL password |
| `JWT_SECRET` | (base64 string) | JWT signing key |
| `ADMIN_EMAIL` | `admin@betterme.com` | Default admin email |
| `ADMIN_PASSWORD` | `admin123` | Default admin password |
| `GEMINI_API_KEY` | - | Google Gemini API key |

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License.

---

<div align="center">

**Built with ❤️ for self-improvement**

</div>
