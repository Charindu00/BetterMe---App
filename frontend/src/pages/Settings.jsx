import { useTheme } from '../context/ThemeContext';
import { useAuth } from '../context/AuthContext';
import { Sun, Moon, User, LogOut } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import './Settings.css';

const Settings = () => {
    const { theme, toggleTheme, isDark } = useTheme();
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    return (
        <div className="settings page animate-fade-in">
            <div className="container">
                <div className="settings-content">
                    <h1 className="page-title">Settings</h1>
                    <p className="page-subtitle">Manage your preferences</p>

                    {/* Profile Section */}
                    <div className="settings-section card">
                        <h2>Profile</h2>
                        <div className="profile-info">
                            <div className="avatar">
                                <User size={32} />
                            </div>
                            <div className="profile-details">
                                <p className="profile-email">{user?.email || 'user@example.com'}</p>
                                <p className="profile-joined">Member since 2026</p>
                            </div>
                        </div>
                    </div>

                    {/* Appearance */}
                    <div className="settings-section card">
                        <h2>Appearance</h2>
                        <div className="setting-row">
                            <div className="setting-info">
                                <span className="setting-label">Theme</span>
                                <span className="setting-desc">Choose your preferred theme</span>
                            </div>
                            <button
                                className="theme-switch"
                                onClick={toggleTheme}
                            >
                                <div className={`switch-track ${isDark ? 'dark' : 'light'}`}>
                                    <div className="switch-thumb">
                                        {isDark ? <Moon size={14} /> : <Sun size={14} />}
                                    </div>
                                </div>
                                <span className="switch-label">
                                    {isDark ? 'Dark' : 'Light'}
                                </span>
                            </button>
                        </div>
                    </div>

                    {/* Theme Preview */}
                    <div className="theme-preview">
                        <div className="preview-card light">
                            <Sun size={20} />
                            <span>Light Mode</span>
                        </div>
                        <div className="preview-card dark">
                            <Moon size={20} />
                            <span>Dark Mode</span>
                        </div>
                    </div>

                    {/* Account Actions */}
                    <div className="settings-section card">
                        <h2>Account</h2>
                        <button className="logout-btn" onClick={handleLogout}>
                            <LogOut size={18} />
                            <span>Log Out</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Settings;
