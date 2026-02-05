import axios from 'axios';

/**
 * API Service - Axios instance configured for our backend
 */
const api = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json'
    }
});

// Request interceptor - add token to all requests
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response interceptor - handle errors globally
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Token expired or invalid
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;

// ═══════════════════════════════════════════════════════════════════════════
// API ENDPOINTS
// ═══════════════════════════════════════════════════════════════════════════

// Auth
export const authAPI = {
    login: (email, password) => api.post('/api/auth/login', { email, password }),
    register: (name, email, password) => api.post('/api/auth/register', { name, email, password })
};

// Habits
export const habitsAPI = {
    getAll: () => api.get('/api/habits'),
    get: (id) => api.get(`/api/habits/${id}`),
    create: (data) => api.post('/api/habits', data),
    update: (id, data) => api.put(`/api/habits/${id}`, data),
    delete: (id) => api.delete(`/api/habits/${id}`),
    checkIn: (id) => api.post(`/api/habits/${id}/checkin`),
    getHistory: (id, days = 30) => api.get(`/api/habits/${id}/history?days=${days}`),
    getStats: () => api.get('/api/habits/stats')
};

// Goals
export const goalsAPI = {
    getAll: () => api.get('/api/goals'),
    get: (id) => api.get(`/api/goals/${id}`),
    create: (data) => api.post('/api/goals', data),
    update: (id, data) => api.put(`/api/goals/${id}`, data),
    delete: (id) => api.delete(`/api/goals/${id}`),
    updateProgress: (id, value) => api.post(`/api/goals/${id}/progress`, { value }),
    incrementProgress: (id, increment) => api.post(`/api/goals/${id}/progress`, { increment }),
    getStats: () => api.get('/api/goals/stats')
};

// Dashboard
export const dashboardAPI = {
    getSummary: () => api.get('/api/dashboard/summary'),
    getWeekly: () => api.get('/api/dashboard/weekly'),
    getMonthly: (year, month) => api.get(`/api/dashboard/monthly?year=${year}&month=${month}`),
    getStreaks: () => api.get('/api/dashboard/streaks'),
    getAchievements: () => api.get('/api/dashboard/achievements')
};

// Analytics
export const analyticsAPI = {
    getTrends: (period = 'daily', days = 30) =>
        api.get(`/api/analytics/trends?period=${period}&days=${days}`),
    getHeatmap: (year) => api.get(`/api/analytics/heatmap?year=${year}`),
    getHabitAnalytics: (days = 30) => api.get(`/api/analytics/habits?days=${days}`)
};

// Motivation (AI)
export const motivationAPI = {
    getDaily: () => api.get('/api/motivation/daily'),
    getHabitTips: (habitId) => api.post(`/api/motivation/habit/${habitId}`),
    getCelebration: () => api.get('/api/motivation/celebration'),
    chat: (message) => api.post('/api/motivation/chat', { message })
};

// Notifications
export const notificationsAPI = {
    getAll: () => api.get('/api/notifications'),
    getUnread: () => api.get('/api/notifications/unread'),
    getUnreadCount: () => api.get('/api/notifications/unread/count'),
    markAllRead: () => api.post('/api/notifications/read-all'),
    markRead: (id) => api.post(`/api/notifications/${id}/read`)
};
