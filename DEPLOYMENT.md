# 🚀 Deployment Guide for BetterMe

Choose one of the following paths to deploy your application.

*   **Option 1: Render (Easiest)** - *May require a credit card.*
*   **Option 2: Neon + Koyeb (No Card Required)** - *Best free tier combo if Render asks for a card.*

---

## 📦 Prerequisites (For Both Options)

1.  **GitHub Account**: Ensure your code is pushed to a GitHub repository.
2.  **Vercel Account**: Sign up at [vercel.com](https://vercel.com) (for Frontend).

---

## 🔵 Option 1: Render (Database + Backend)

### 1. Database (PostgreSQL)
1.  Log in to **Render** and click **New +** -> **PostgreSQL**.
2.  **Name**: `betterme-db`
3.  **Region**: Choose closest to you.
4.  **Instance Type**: **Free**.
5.  Click **Create Database**.
6.  Copy the **Internal DB URL** (starts with `postgres://...`).

### 2. Backend (Docker)
1.  Click **New +** -> **Web Service**.
2.  Connect your **GitHub repository**.
3.  **Name**: `betterme-api`
4.  **Runtime**: **Docker**.
5.  **Instance Type**: **Free**.
6.  **Environment Variables**:
    *   `SPRING_PROFILES_ACTIVE`: `docker`
    *   `SPRING_DATASOURCE_URL`: Your **Internal DB URL**.
    *   `JWT_SECRET`: Generate a long random string.
    *   `GEMINI_API_KEY`: Your Google Gemini API Key.
    *   `GMAIL_USERNAME` / `GMAIL_APP_PASSWORD`: Your Gmail credentials.
    *   `FRONTEND_URL`: `http://localhost` (You will update this later).
7.  Click **Create Web Service**. Changes will be live in 5-10 mins.
8.  Copy your backend URL (e.g., `https://betterme-api.onrender.com`).

---

## � Option 2: Neon + Koyeb (No Credit Card)

### 1. Database: Neon.tech
1.  Go to [neon.tech](https://neon.tech) and sign up/login.
2.  Create a **New Project**.
3.  Copy the **Connection String**. It looks like `postgres://user:pass@ep-xyz.aws.neon.tech/neondb...`.
    *   *Tip*: Ensure "Pooled connection" is checked if available.

### 2. Backend: Koyeb
1.  Go to [koyeb.com](https://koyeb.com).
2.  **Create App** -> **GitHub**.
3.  Select your repository.
4.  **Builder**: Dockerfile.
5.  **Environment Variables**:
    *   `SPRING_DATASOURCE_URL`: Paste your **Neon Connection String**.
        *   **Important**: Append `?sslmode=require` to the end if not present.
    *   `SPRING_PROFILES_ACTIVE`: `docker`
    *   `JWT_SECRET`: Generate a long random string.
    *   `GEMINI_API_KEY`: Your Google Gemini API Key.
    *   `GMAIL_USERNAME` / `GMAIL_APP_PASSWORD`: Your Gmail credentials.
    *   `FRONTEND_URL`: `http://localhost` (You will update this later).
6.  Deploy!
7.  Copy your backend URL (e.g., `https://betterme-api.koyeb.app`).

---

## 🎨 3. Frontend Deployment (Vercel) - For BOTH Options

1.  Log in to **Vercel** and click **Add New** -> **Project**.
2.  Import your **GitHub repository**.
3.  **Framework Preset**: **Vite**.
4.  **Root Directory**: Click "Edit" and select `frontend`.
5.  **Environment Variables**:
    *   `VITE_API_URL`: Paste your **Backend URL** (from Render or Koyeb).
        *   *Example*: `https://betterme-api.koyeb.app` (No trailing slash `/`)
6.  Click **Deploy**.
7.  Once done, copy your new **Frontend Domain** (e.g., `https://betterme-app.vercel.app`).

---

## 🔗 4. Final Connection (Update CORS)

Now that you have the Frontend URL, go back and update the Backend so it allows requests from your new site.

### If using Option 1 (Render):
1.  Go to **Render Dashboard** -> **betterme-api** -> **Environment**.
2.  Edit `FRONTEND_URL` value.
3.  Paste your Vercel URL (e.g., `https://betterme-app.vercel.app`).
4.  Save Changes (Render will auto-deploy).

### If using Option 2 (Koyeb):
1.  Go to **Koyeb Dashboard** -> **betterme-api** -> **Settings**.
2.  Go to **Environment Variables**.
3.  Edit `FRONTEND_URL`.
4.  Paste your Vercel URL (e.g., `https://betterme-app.vercel.app`).
5.  Save and Redploy.

**🎉 Congratulations! Your full-stack app is live!**
