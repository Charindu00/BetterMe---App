# 🚀 BetterMe Deployment Guide (Railway + Vercel)

**Railway** = Backend + Database (no credit card needed, just GitHub login)
**Vercel** = Frontend (no credit card needed)

---

## Step 1: Sign Up for Railway

1. Go to [railway.app](https://railway.app).
2. Click **"Login"** → **"Login with GitHub"**.
3. Authorize Railway with your GitHub account.
4. You get **$5 free credit** — more than enough for a portfolio app.

---

## Step 2: Create the Database

1. Click **"New Project"** → **"Provision PostgreSQL"**.
2. Railway creates a PostgreSQL database instantly.
3. Click on the **PostgreSQL card** that appeared.
4. Go to the **"Variables"** tab.
5. You will see variables like `PGHOST`, `PGUSER`, `PGPASSWORD`, `PGDATABASE`, `PGPORT`.
6. **Write down these values** — you need them in Step 3:
   - `PGHOST` (e.g., `monorail.proxy.rlwy.net`)
   - `PGUSER` (e.g., `postgres`)
   - `PGPASSWORD` (e.g., `aBcDeFg123`)
   - `PGDATABASE` (e.g., `railway`)
   - `PGPORT` (e.g., `12345`)

---

## Step 3: Deploy the Backend

1. In the **same project**, click **"+ New"** → **"GitHub Repo"**.
2. Select your **BetterMe** repository.
3. Railway will detect the repo. Click on the new service card.
4. Go to **"Settings"** tab:
   - **Root Directory**: Change to `backend`
   - **Builder**: Should auto-detect **Dockerfile**
5. Go to **"Variables"** tab and click **"New Variable"** for each:

| Variable Name | Value |
|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://PGHOST:PGPORT/PGDATABASE` (Replace PGHOST, PGPORT, PGDATABASE with values from Step 2) |
| `SPRING_DATASOURCE_USERNAME` | *(PGUSER from Step 2)* |
| `SPRING_DATASOURCE_PASSWORD` | *(PGPASSWORD from Step 2)* |
| `SPRING_PROFILES_ACTIVE` | `docker` |
| `JWT_SECRET` | `betterme-super-secret-jwt-key-2026` |
| `GEMINI_API_KEY` | `AIzaSyAZOATWm086MhN8daL9Ur9RxQDHKXvZKu4` |
| `GMAIL_USERNAME` | `charindudilminda5@gmail.com` |
| `GMAIL_APP_PASSWORD` | `ixaihuuvshkitece` |
| `FRONTEND_URL` | `http://localhost` |
| `PORT` | `8080` |

6. Go to **"Settings"** → **"Networking"** → Click **"Generate Domain"**.
7. Railway gives you a public URL (e.g., `https://betterme-api-production.up.railway.app`).
8. Wait for the build to finish (5-10 mins). ☕

---

## Step 4: Deploy the Frontend on Vercel

1. Go to [vercel.com](https://vercel.com) and log in with GitHub.
2. Click **"Add New"** → **"Project"**.
3. Import your **BetterMe** repository.
4. Configure:
   - **Framework Preset**: Vite
   - **Root Directory**: Click **"Edit"** → type `frontend`
5. Add **Environment Variable**:

| Key | Value |
|---|---|
| `VITE_API_URL` | Your Railway backend URL from Step 3 (e.g., `https://betterme-api-production.up.railway.app`) — **NO trailing slash** |

6. Click **"Deploy"**.
7. Copy your frontend URL (e.g., `https://betterme.vercel.app`).

---

## Step 5: Final Connection

1. Go back to **Railway** → Click on your **backend service**.
2. Go to **"Variables"** tab.
3. Find `FRONTEND_URL` → Click on it → Change `http://localhost` to your **Vercel URL** (e.g., `https://betterme.vercel.app`).
4. Railway auto-redeploys. Wait 2 minutes.

## 🎉 Your app is now live!

**Your URLs:**
- Frontend: `https://betterme.vercel.app` (share this!)
- Backend API: `https://betterme-api-production.up.railway.app`
