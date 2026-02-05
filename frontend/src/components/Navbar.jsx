import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useTheme } from '../context/ThemeContext';
import { useAuth } from '../context/AuthContext';
import {
    Sun, Moon, Menu, X, Home, Target, BarChart3,
    Sparkles, Settings, LogOut, Flame
} from 'lucide-react';
import './Navbar.css';

const Navbar = () => {
    const { theme, toggleTheme } = useTheme();
    const { isAuthenticated, logout, user } = useAuth();
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    const navLinks = [
        { path: '/dashboard', label: 'Dashboard', icon: Home },
        { path: '/habits', label: 'Habits', icon: Flame },
        { path: '/goals', label: 'Goals', icon: Target },
        { path: '/analytics', label: 'Analytics', icon: BarChart3 },
    ];

    const isActive = (path) => location.pathname === path;

    return (
        <nav className="navbar glass">
            <div className="navbar-container">
                {/* Logo */}
                <Link to="/" className="navbar-logo">
                    <Sparkles className="logo-icon" />
                    <span className="logo-text">
                        Better<span className="text-gradient">Me</span>
                    </span>
                </Link>

                {/* Desktop Navigation */}
                {isAuthenticated && (
                    <div className="navbar-links">
                        {navLinks.map(({ path, label, icon: Icon }) => (
                            <Link
                                key={path}
                                to={path}
                                className={`nav-link ${isActive(path) ? 'active' : ''}`}
                            >
                                <Icon size={18} />
                                <span>{label}</span>
                            </Link>
                        ))}
                    </div>
                )}

                {/* Right Side */}
                <div className="navbar-actions">
                    {/* Theme Toggle */}
                    <button
                        className="theme-toggle"
                        onClick={toggleTheme}
                        aria-label="Toggle theme"
                    >
                        {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
                    </button>

                    {isAuthenticated ? (
                        <>
                            <Link to="/settings" className="nav-icon-btn">
                                <Settings size={20} />
                            </Link>
                            <button className="nav-icon-btn" onClick={handleLogout}>
                                <LogOut size={20} />
                            </button>
                        </>
                    ) : (
                        <div className="auth-buttons">
                            <Link to="/login" className="btn btn-ghost">Log In</Link>
                            <Link to="/register" className="btn btn-primary">Sign Up</Link>
                        </div>
                    )}

                    {/* Mobile Menu Toggle */}
                    <button
                        className="mobile-menu-btn"
                        onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                    >
                        {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
                    </button>
                </div>
            </div>

            {/* Mobile Menu */}
            {mobileMenuOpen && (
                <div className="mobile-menu">
                    {isAuthenticated ? (
                        <>
                            {navLinks.map(({ path, label, icon: Icon }) => (
                                <Link
                                    key={path}
                                    to={path}
                                    className={`mobile-link ${isActive(path) ? 'active' : ''}`}
                                    onClick={() => setMobileMenuOpen(false)}
                                >
                                    <Icon size={20} />
                                    <span>{label}</span>
                                </Link>
                            ))}
                            <button className="mobile-link" onClick={handleLogout}>
                                <LogOut size={20} />
                                <span>Log Out</span>
                            </button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="mobile-link" onClick={() => setMobileMenuOpen(false)}>
                                Log In
                            </Link>
                            <Link to="/register" className="mobile-link" onClick={() => setMobileMenuOpen(false)}>
                                Sign Up
                            </Link>
                        </>
                    )}
                </div>
            )}
        </nav>
    );
};

export default Navbar;
