# 🚀 Deployment Guide for BetterMe

This guide will help you deploy the **BetterMe** application to the cloud for free using **Render** (Backend & Database) and **Vercel** (Frontend).

## 📦 Prerequisites

1.  **GitHub Account**: Ensure your code is pushed to a GitHub repository.
2.  **Render Account**: Sign up at [render.com](https://render.com).
3.  **Vercel Account**: Sign up at [vercel.com](https://vercel.com).

---

## 1️⃣ Database Deployment (PostgreSQL on Render)

1.  Log in to **Render** and click **New +** -> **PostgreSQL**.
2.  **Name**: `betterme-db`
3.  **Region**: Choose the one closest to you (e.g., Singapore, Frankfurt).
4.  **Version**: 16 (Standard).
5.  **Instance Type**: **Free**.
6.  Click **Create Database**.
7.  Once created, copy the **Internal DB URL** (starts with `postgres://...`) and keep it safe. You'll need it for the Backend.

---

## 2️⃣ Backend Deployment (Docker on Render)

1.  Click **New +** -> **Web Service**.
2.  Connect your **GitHub repository**.
3.  **Name**: `betterme-api`
4.  **Region**: Same as your database.
5.  **Runtime**: **Docker** (Render will automatically detect the `Dockerfile` in `backend/`).
6.  **Instance Type**: **Free**.
7.  **Environment Variables** (Add these under "Advanced"):
    *   `SPRING_PROFILES_ACTIVE`: `docker`
    *   `SPRING_DATASOURCE_URL`: The **Internal DB URL** you copied from the Database step.
    *   `SPRING_DATASOURCE_USERNAME`: `betterme` (or whatever username Render assigned, usually in the connection string)
    *   `SPRING_DATASOURCE_PASSWORD`: The password from Render Dashboard.
    *   `JWT_SECRET`: Generate a long random string (e.g., `mySup3rS3cr3tKeyThatIsVeryLong123!`).
    *   `GEMINI_API_KEY`: Your Google Gemini API Key.
    *   `GMAIL_USERNAME`: Your Gmail address (for emails).
    *   `GMAIL_APP_PASSWORD`: Your Gmail App Password.
    *   `FRONTEND_URL`: The URL of your frontend (we will get this in Step 3, you can come back and update it).
8.  Click **Create Web Service**.
    *   Render will build your Docker image (takes 5-10 mins).
    *   Once live, copy your backend URL (e.g., `https://betterme-api.onrender.com`).

---

## 3️⃣ Frontend Deployment (Vercel)

1.  Log in to **Vercel** and clicking **Add New** -> **Project**.
2.  Import your **GitHub repository**.
3.  **Framework Preset**: **Vite**.
4.  **Root Directory**: Click "Edit" and select `frontend`.
5.  **Environment Variables**:
    *   `VITE_API_URL`: Paste your **Render Backend URL** (e.g., `https://betterme-api.onrender.com`). **IMPORTANT**: No trailing slash `/` at the end.
6.  Click **Deploy**.
7.  Vercel will build and deploy your site in ~1 minute.
8.  Once done, you'll get a domain (e.g., `https://betterme-app.vercel.app`).

---

## 4️⃣ Final Connection

1.  Go back to **Render** -> **betterme-api** -> **Environment**.
2.  Add/Update `FRONTEND_URL` with your **Vercel Frontend URL** (e.g., `https://betterme-app.vercel.app`).
3.  Render will restart the backend.

**🎉 Done! Your app is now live!**
