import { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import { useAuth } from '../context/AuthContext';
import { notificationsAPI, userAPI } from '../services/api';
import { Sun, Moon, Bell, Menu, X, User } from 'lucide-react';
import './TopBar.css';

const TopBar = ({ onMenuClick, mobileMenuOpen }) => {
    const { theme, toggleTheme, isDark } = useTheme();
    const { user } = useAuth();
    const [notifications, setNotifications] = useState([]);
    const [showNotifications, setShowNotifications] = useState(false);
    const [unreadCount, setUnreadCount] = useState(0);
    const [profilePicture, setProfilePicture] = useState(null);
    const dropdownRef = useRef(null);

    // Fetch user profile for avatar
    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const res = await userAPI.getProfile();
                setProfilePicture(res.data.profilePicture);
            } catch {
                // Ignore errors
            }
        };
        if (user) {
            fetchProfile();
        }
    }, [user]);

    // Listen for profile updates
    useEffect(() => {
        const handleProfileUpdate = (e) => {
            if (e.detail?.profilePicture !== undefined) {
                setProfilePicture(e.detail.profilePicture);
            }
        };
        window.addEventListener('profileUpdated', handleProfileUpdate);
        return () => window.removeEventListener('profileUpdated', handleProfileUpdate);
    }, []);

    // Mock notifications for now - will be replaced with API
    useEffect(() => {
        const mockNotifications = [
            { id: 1, message: '🔥 You completed a 7-day streak!', time: '2 hours ago', read: false },
            { id: 2, message: '🎯 Goal "Read 12 books" is 50% complete', time: '1 day ago', read: false },
            { id: 3, message: '✨ New achievement unlocked: Week Warrior', time: '2 days ago', read: true },
        ];
        setNotifications(mockNotifications);
        setUnreadCount(mockNotifications.filter(n => !n.read).length);
    }, []);

    // Close dropdown when clicking outside
    useEffect(() => {
        const handleClickOutside = (e) => {
            if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
                setShowNotifications(false);
            }
        };
        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const markAllRead = async () => {
        try {
            await notificationsAPI.markAllRead();
        } catch {
            // Ignore API errors, update UI anyway
        }
        setNotifications(notifications.map(n => ({ ...n, read: true })));
        setUnreadCount(0);
    };

    return (
        <header className="topbar">
            <div className="topbar-left">
                {/* Mobile menu button */}
                <button className="mobile-menu-btn" onClick={onMenuClick}>
                    {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
                </button>
            </div>

            <div className="topbar-right">
                {/* Theme Toggle */}
                <button
                    className="topbar-btn theme-btn"
                    onClick={toggleTheme}
                    aria-label="Toggle theme"
                >
                    {isDark ? <Sun size={20} /> : <Moon size={20} />}
                </button>

                {/* Notifications */}
                <div className="notification-wrapper" ref={dropdownRef}>
                    <button
                        className="topbar-btn notification-btn"
                        onClick={() => setShowNotifications(!showNotifications)}
                        aria-label="View notifications"
                    >
                        <Bell size={20} />
                        {unreadCount > 0 && (
                            <span className="notification-badge">{unreadCount}</span>
                        )}
                    </button>

                    {showNotifications && (
                        <div className="notification-dropdown">
                            <div className="notification-header">
                                <h3>Notifications</h3>
                                {unreadCount > 0 && (
                                    <button className="mark-read-btn" onClick={markAllRead}>
                                        Mark all read
                                    </button>
                                )}
                            </div>

                            <div className="notification-list">
                                {notifications.length === 0 ? (
                                    <div className="no-notifications">
                                        <Bell size={32} />
                                        <p>No notifications yet</p>
                                    </div>
                                ) : (
                                    notifications.map(notification => (
                                        <div
                                            key={notification.id}
                                            className={`notification-item ${!notification.read ? 'unread' : ''}`}
                                        >
                                            <p className="notification-message">{notification.message}</p>
                                            <span className="notification-time">{notification.time}</span>
                                        </div>
                                    ))
                                )}
                            </div>
                        </div>
                    )}
                </div>

                {/* User Avatar */}
                <Link to="/settings" className="user-avatar">
                    {profilePicture ? (
                        <img src={profilePicture} alt="Profile" className="avatar-img" />
                    ) : (
                        <User size={18} />
                    )}
                </Link>
            </div>
        </header>
    );
};

export default TopBar;

