import { useState, useRef, useEffect } from 'react';
import { useTheme } from '../context/ThemeContext';
import { useAuth } from '../context/AuthContext';
import { userAPI } from '../services/api';
import {
    Sun, Moon, User, LogOut, Camera, Check, X,
    Eye, EyeOff, Loader2, Trash2
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import './Settings.css';

const Settings = () => {
    const { theme, toggleTheme, isDark } = useTheme();
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const fileInputRef = useRef(null);

    // Profile state
    const [profile, setProfile] = useState({
        name: '',
        email: '',
        profilePicture: null,
        createdAt: null
    });
    const [originalName, setOriginalName] = useState('');
    const [isEditingName, setIsEditingName] = useState(false);
    const [nameLoading, setNameLoading] = useState(false);
    const [nameSuccess, setNameSuccess] = useState(false);

    // Password state
    const [passwordForm, setPasswordForm] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });
    const [showPasswords, setShowPasswords] = useState({
        current: false,
        new: false,
        confirm: false
    });
    const [passwordLoading, setPasswordLoading] = useState(false);
    const [passwordError, setPasswordError] = useState('');
    const [passwordSuccess, setPasswordSuccess] = useState(false);

    // Avatar state
    const [avatarLoading, setAvatarLoading] = useState(false);

    // Fetch profile on mount
    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const res = await userAPI.getProfile();
                setProfile(res.data);
                setOriginalName(res.data.name);
            } catch {
                // Use auth context user as fallback
                if (user) {
                    setProfile({ name: user.name || '', email: user.email || '' });
                    setOriginalName(user.name || '');
                }
            }
        };
        fetchProfile();
    }, [user]);

    const handleLogout = () => {
        logout();
        navigate('/');
    };

    // Name update
    const handleNameSave = async () => {
        if (profile.name === originalName) {
            setIsEditingName(false);
            return;
        }
        setNameLoading(true);
        try {
            const res = await userAPI.updateProfile({ name: profile.name });
            setProfile(res.data);
            setOriginalName(res.data.name);
            setNameSuccess(true);
            setTimeout(() => setNameSuccess(false), 2000);
            setIsEditingName(false);
        } catch (err) {
            console.error('Failed to update name:', err);
        } finally {
            setNameLoading(false);
        }
    };

    const handleNameCancel = () => {
        setProfile({ ...profile, name: originalName });
        setIsEditingName(false);
    };

    // Password change
    const handlePasswordChange = async (e) => {
        e.preventDefault();
        setPasswordError('');
        setPasswordSuccess(false);

        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
            setPasswordError('New passwords do not match');
            return;
        }
        if (passwordForm.newPassword.length < 6) {
            setPasswordError('Password must be at least 6 characters');
            return;
        }

        setPasswordLoading(true);
        try {
            await userAPI.changePassword({
                currentPassword: passwordForm.currentPassword,
                newPassword: passwordForm.newPassword
            });
            setPasswordSuccess(true);
            setPasswordForm({ currentPassword: '', newPassword: '', confirmPassword: '' });
            setTimeout(() => setPasswordSuccess(false), 3000);
        } catch (err) {
            setPasswordError(err.response?.data?.error || 'Failed to change password');
        } finally {
            setPasswordLoading(false);
        }
    };

    // Avatar upload
    const handleAvatarClick = () => {
        fileInputRef.current?.click();
    };

    const handleFileChange = async (e) => {
        const file = e.target.files?.[0];
        if (!file) return;

        // Validate file type
        if (!file.type.startsWith('image/')) {
            alert('Please select an image file');
            return;
        }

        // Validate file size (max 2MB)
        if (file.size > 2 * 1024 * 1024) {
            alert('Image size must be less than 2MB');
            return;
        }

        setAvatarLoading(true);
        try {
            // Convert to Base64
            const reader = new FileReader();
            reader.onload = async () => {
                const base64 = reader.result;
                try {
                    const res = await userAPI.uploadAvatar(base64);
                    setProfile(res.data);
                    // Notify TopBar of profile update
                    window.dispatchEvent(new CustomEvent('profileUpdated', {
                        detail: { profilePicture: res.data.profilePicture }
                    }));
                } catch (err) {
                    console.error('Failed to upload avatar:', err);
                } finally {
                    setAvatarLoading(false);
                }
            };
            reader.readAsDataURL(file);
        } catch {
            setAvatarLoading(false);
        }
    };

    const handleRemoveAvatar = async () => {
        setAvatarLoading(true);
        try {
            const res = await userAPI.removeAvatar();
            setProfile(res.data);
            // Notify TopBar of profile update
            window.dispatchEvent(new CustomEvent('profileUpdated', {
                detail: { profilePicture: null }
            }));
        } catch (err) {
            console.error('Failed to remove avatar:', err);
        } finally {
            setAvatarLoading(false);
        }
    };

    const formatDate = (dateStr) => {
        if (!dateStr) return 'Unknown';
        return new Date(dateStr).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long'
        });
    };

    return (
        <div className="settings page animate-fade-in">
            <div className="container">
                <div className="settings-content">
                    <h1 className="page-title">Settings</h1>
                    <p className="page-subtitle">Manage your profile and preferences</p>

                    {/* Profile Picture Section */}
                    <div className="settings-section card">
                        <h2>Profile Picture</h2>
                        <div className="avatar-section">
                            <div className="avatar-wrapper">
                                <div className="avatar-large" onClick={handleAvatarClick}>
                                    {avatarLoading ? (
                                        <Loader2 size={32} className="spinner-icon" />
                                    ) : profile.profilePicture ? (
                                        <img src={profile.profilePicture} alt="Profile" />
                                    ) : (
                                        <User size={40} />
                                    )}
                                    <div className="avatar-overlay">
                                        <Camera size={20} />
                                    </div>
                                </div>
                                <input
                                    type="file"
                                    ref={fileInputRef}
                                    onChange={handleFileChange}
                                    accept="image/*"
                                    hidden
                                />
                            </div>
                            <div className="avatar-info">
                                <p className="avatar-hint">Click to upload a new photo</p>
                                <p className="avatar-size">Max size: 2MB (JPG, PNG, GIF)</p>
                                {profile.profilePicture && (
                                    <button
                                        className="remove-avatar-btn"
                                        onClick={handleRemoveAvatar}
                                        disabled={avatarLoading}
                                    >
                                        <Trash2 size={14} />
                                        Remove Photo
                                    </button>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Profile Info Section */}
                    <div className="settings-section card">
                        <h2>Profile Information</h2>

                        {/* Name */}
                        <div className="setting-row">
                            <div className="setting-info">
                                <span className="setting-label">Name</span>
                            </div>
                            <div className="setting-control">
                                {isEditingName ? (
                                    <div className="edit-name-group">
                                        <input
                                            type="text"
                                            className="input name-input"
                                            value={profile.name}
                                            onChange={(e) => setProfile({ ...profile, name: e.target.value })}
                                            autoFocus
                                        />
                                        <button
                                            className="icon-btn save-btn"
                                            onClick={handleNameSave}
                                            disabled={nameLoading}
                                        >
                                            {nameLoading ? <Loader2 size={16} className="spinner-icon" /> : <Check size={16} />}
                                        </button>
                                        <button className="icon-btn cancel-btn" onClick={handleNameCancel}>
                                            <X size={16} />
                                        </button>
                                    </div>
                                ) : (
                                    <div className="name-display">
                                        <span className="profile-value">{profile.name || 'Not set'}</span>
                                        <button className="edit-btn" onClick={() => setIsEditingName(true)}>
                                            Edit
                                        </button>
                                        {nameSuccess && <Check size={16} className="success-icon" />}
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Email (read-only) */}
                        <div className="setting-row">
                            <div className="setting-info">
                                <span className="setting-label">Email</span>
                            </div>
                            <div className="setting-control">
                                <span className="profile-value">{profile.email}</span>
                            </div>
                        </div>

                        {/* Member since */}
                        <div className="setting-row">
                            <div className="setting-info">
                                <span className="setting-label">Member since</span>
                            </div>
                            <div className="setting-control">
                                <span className="profile-value muted">{formatDate(profile.createdAt)}</span>
                            </div>
                        </div>
                    </div>

                    {/* Password Section */}
                    <div className="settings-section card">
                        <h2>Change Password</h2>
                        <form className="password-form" onSubmit={handlePasswordChange}>
                            <div className="input-group">
                                <label>Current Password</label>
                                <div className="password-input-wrapper">
                                    <input
                                        type={showPasswords.current ? 'text' : 'password'}
                                        className="input"
                                        value={passwordForm.currentPassword}
                                        onChange={(e) => setPasswordForm({ ...passwordForm, currentPassword: e.target.value })}
                                        placeholder="Enter current password"
                                    />
                                    <button
                                        type="button"
                                        className="password-toggle"
                                        onClick={() => setShowPasswords({ ...showPasswords, current: !showPasswords.current })}
                                    >
                                        {showPasswords.current ? <EyeOff size={18} /> : <Eye size={18} />}
                                    </button>
                                </div>
                            </div>

                            <div className="input-group">
                                <label>New Password</label>
                                <div className="password-input-wrapper">
                                    <input
                                        type={showPasswords.new ? 'text' : 'password'}
                                        className="input"
                                        value={passwordForm.newPassword}
                                        onChange={(e) => setPasswordForm({ ...passwordForm, newPassword: e.target.value })}
                                        placeholder="Enter new password"
                                    />
                                    <button
                                        type="button"
                                        className="password-toggle"
                                        onClick={() => setShowPasswords({ ...showPasswords, new: !showPasswords.new })}
                                    >
                                        {showPasswords.new ? <EyeOff size={18} /> : <Eye size={18} />}
                                    </button>
                                </div>
                            </div>

                            <div className="input-group">
                                <label>Confirm New Password</label>
                                <div className="password-input-wrapper">
                                    <input
                                        type={showPasswords.confirm ? 'text' : 'password'}
                                        className="input"
                                        value={passwordForm.confirmPassword}
                                        onChange={(e) => setPasswordForm({ ...passwordForm, confirmPassword: e.target.value })}
                                        placeholder="Confirm new password"
                                    />
                                    <button
                                        type="button"
                                        className="password-toggle"
                                        onClick={() => setShowPasswords({ ...showPasswords, confirm: !showPasswords.confirm })}
                                    >
                                        {showPasswords.confirm ? <EyeOff size={18} /> : <Eye size={18} />}
                                    </button>
                                </div>
                            </div>

                            {passwordError && <p className="error-message">{passwordError}</p>}
                            {passwordSuccess && <p className="success-message">Password changed successfully!</p>}

                            <button
                                type="submit"
                                className="btn btn-primary change-password-btn"
                                disabled={passwordLoading || !passwordForm.currentPassword || !passwordForm.newPassword}
                            >
                                {passwordLoading ? <Loader2 size={18} className="spinner-icon" /> : null}
                                Change Password
                            </button>
                        </form>
                    </div>

                    {/* Appearance */}
                    <div className="settings-section card">
                        <h2>Appearance</h2>
                        <div className="setting-row">
                            <div className="setting-info">
                                <span className="setting-label">Theme</span>
                                <span className="setting-desc">Choose your preferred theme</span>
                            </div>
                            <button className="theme-switch" onClick={toggleTheme}>
                                <div className={`switch-track ${isDark ? 'dark' : 'light'}`}>
                                    <div className="switch-thumb">
                                        {isDark ? <Moon size={14} /> : <Sun size={14} />}
                                    </div>
                                </div>
                                <span className="switch-label">{isDark ? 'Dark' : 'Light'}</span>
                            </button>
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
