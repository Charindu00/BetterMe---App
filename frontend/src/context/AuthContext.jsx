import { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

/**
 * Auth Context - Manages user authentication state
 */
const AuthContext = createContext();

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // If token exists, validate and get user info
        if (token) {
            api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            // For now, we'll parse token payload (in production, call /api/auth/me)
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                setUser({ email: payload.sub });
            } catch {
                logout();
            }
        }
        setLoading(false);
    }, [token]);

    const login = async (email, password) => {
        const response = await api.post('/api/auth/login', { email, password });
        const { token: newToken, ...userData } = response.data;

        localStorage.setItem('token', newToken);
        api.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
        setToken(newToken);
        setUser(userData);

        return response.data;
    };

    const register = async (name, email, password) => {
        const response = await api.post('/api/auth/register', { name, email, password });
        const { token: newToken, ...userData } = response.data;

        localStorage.setItem('token', newToken);
        api.defaults.headers.common['Authorization'] = `Bearer ${newToken}`;
        setToken(newToken);
        setUser(userData);

        return response.data;
    };

    const logout = () => {
        localStorage.removeItem('token');
        delete api.defaults.headers.common['Authorization'];
        setToken(null);
        setUser(null);
    };

    const value = {
        user,
        token,
        loading,
        isAuthenticated: !!token,
        login,
        register,
        logout
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

export default AuthContext;
