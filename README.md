# BetterMe - AI-Powered Self-Improvement App 🚀

**Every step forward is progress, no matter how small.**

BetterMe is a comprehensive habit tracking and goal setting application designed to help you build better routines and achieve your dreams. Built with a modern tech stack and powered by AI, it offers personalized motivation and insights to keep you on track.

![BetterMe Dashboard](screenshots/dashboard.png) *Add your screenshot here*

## ✨ Key Features

- **📊 Interactive Dashboard**: track your daily progress, weekly trends, and active goals at a glance.
- **🔥 Habit Tracking**: maintain streaks, view completion rates, and manage daily routines.
- **🎯 Goal Setting**: create and track long-term goals with deadlines and milestones.
- **🤖 AI Coach**: integrate with **Google Gemini API** for personalized daily motivation, habit tips, and encouragement.
- **🏆 Gamification**: earn achievements and badges for consistency and milestones (e.g., "Week Warrior", "Perfect Day").
- **📧 Secure Authentication**: User registration with email verification and JWT-based session management.
- **📱 Responsive Design**: optimized for both desktop and mobile devices.

## 🛠️ Tech Stack

### Backend
- **Java 17 & Spring Boot 3**: Robust and scalable backend API.
- **Spring Security & JWT**: Secure stateless authentication.
- **PostgreSQL**: Reliable relational database for user data.
- **Google Gemini API**: AI integration for smart coaching features.
- **Docker & Docker Compose**: Containerized environment for easy deployment.

### Frontend
- **React 18 & Vite**: Fast and responsive single-page application.
- **Tailwind CSS (or Custom CSS)**: Modern, clean UI styling with dark/light mode support.
- **Axios**: Efficient API communication.
- **Lucide React**: Beautiful scalable vector icons.

## 🚀 Getting Started

### Prerequisites
- Docker and Docker Compose installed
- Java 17 (optional, if running locally without Docker)
- Node.js 18+ (optional, if running frontend locally)

### Installation

1.  **Clone the repository**
    ```bash
    git clone https://github.com/yourusername/betterme.git
    cd betterme
    ```

2.  **Configure Environment Variables**
    Create a `.env` file in the `backend` directory (or update `docker-compose.yml` environment section) with your credentials:
    ```env
    DB_USERNAME=postgres
    DB_PASSWORD=password
    GEMINI_API_KEY=your_gemini_api_key_here
    MAIL_USERNAME=your_email
    MAIL_PASSWORD=your_email_app_password
    ```

3.  **Run with Docker Compose**
    Build and start the entire application (DB, Backend, Frontend server if containerized):
    ```bash
    docker-compose up -d --build
    ```

4.  **Access the App**
    -   **Frontend**: http://localhost:5173 (or port configured)
    -   **Backend API**: http://localhost:8080

## 📸 Screenshots

| Dashboard | Habits |
|---|---|
| ![Dashboard](screenshots/dashboard.png) | ![Habits](screenshots/habits.png) |

| Goals | Mobile View |
|---|---|
| ![Goals](screenshots/goals.png) | ![Mobile](screenshots/mobile.png) |

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
*Built with ❤️ by [Your Name]*
