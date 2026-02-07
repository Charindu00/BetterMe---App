import { useState } from 'react';
import { useSearchParams, Link, useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';
import { Lock, Eye, EyeOff, Loader, CheckCircle, XCircle } from 'lucide-react';
import './Auth.css';

const ResetPassword = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const token = searchParams.get('token');

    const [formData, setFormData] = useState({
        newPassword: '',
        confirmPassword: ''
    });
    const [showPassword, setShowPassword] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (formData.newPassword.length < 6) {
            setError('Password must be at least 6 characters');
            return;
        }

        if (formData.newPassword !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        if (!token) {
            setError('Invalid reset link. No token provided.');
            return;
        }

        setLoading(true);

        try {
            const res = await authAPI.resetPassword(token, formData.newPassword);
            if (res.data.success) {
                setSuccess(true);
                setTimeout(() => navigate('/login'), 3000);
            } else {
                setError(res.data.message);
            }
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to reset password. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    if (!token) {
        return (
            <div className="auth-page">
                <div className="auth-container">
                    <div className="auth-header">
                        <Link to="/" className="auth-logo">
                            ✨ BetterMe
                        </Link>
                    </div>
                    <div className="auth-card verify-card">
                        <div className="verify-status error">
                            <XCircle className="verify-icon error" size={64} />
                            <h1>Invalid Reset Link</h1>
                            <p>This password reset link is invalid or expired.</p>
                            <Link to="/forgot-password" className="btn btn-primary">
                                Request New Link
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    if (success) {
        return (
            <div className="auth-page">
                <div className="auth-container">
                    <div className="auth-header">
                        <Link to="/" className="auth-logo">
                            ✨ BetterMe
                        </Link>
                    </div>
                    <div className="auth-card verify-card">
                        <div className="verify-status success">
                            <CheckCircle className="verify-icon success" size={64} />
                            <h1>Password Reset! 🎉</h1>
                            <p>Your password has been successfully reset.</p>
                            <p className="text-muted">Redirecting to login...</p>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="auth-page">
            <div className="auth-container">
                <div className="auth-header">
                    <Link to="/" className="auth-logo">
                        ✨ BetterMe
                    </Link>
                </div>

                <div className="auth-card">
                    <div className="auth-title">
                        <h1>Reset Password</h1>
                        <p>Enter your new password below</p>
                    </div>

                    <form onSubmit={handleSubmit} className="auth-form">
                        {error && <div className="auth-error">{error}</div>}

                        <div className="form-group">
                            <label htmlFor="newPassword">New Password</label>
                            <div className="input-with-icon">
                                <Lock className="input-icon" size={18} />
                                <input
                                    type={showPassword ? 'text' : 'password'}
                                    id="newPassword"
                                    name="newPassword"
                                    className="input"
                                    placeholder="Enter new password"
                                    value={formData.newPassword}
                                    onChange={handleChange}
                                    required
                                    minLength={6}
                                />
                                <button
                                    type="button"
                                    className="password-toggle"
                                    onClick={() => setShowPassword(!showPassword)}
                                >
                                    {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                                </button>
                            </div>
                        </div>

                        <div className="form-group">
                            <label htmlFor="confirmPassword">Confirm Password</label>
                            <div className="input-with-icon">
                                <Lock className="input-icon" size={18} />
                                <input
                                    type={showPassword ? 'text' : 'password'}
                                    id="confirmPassword"
                                    name="confirmPassword"
                                    className="input"
                                    placeholder="Confirm new password"
                                    value={formData.confirmPassword}
                                    onChange={handleChange}
                                    required
                                    minLength={6}
                                />
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary btn-lg w-full"
                            disabled={loading}
                        >
                            {loading ? (
                                <>
                                    <Loader className="spin" size={18} />
                                    Resetting...
                                </>
                            ) : (
                                'Reset Password'
                            )}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ResetPassword;
