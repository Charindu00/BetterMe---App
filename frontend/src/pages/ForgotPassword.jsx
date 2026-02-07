import { useState } from 'react';
import { Link } from 'react-router-dom';
import { authAPI } from '../services/api';
import { Mail, ArrowLeft, Loader, CheckCircle } from 'lucide-react';
import './Auth.css';

const ForgotPassword = () => {
    const [email, setEmail] = useState('');
    const [loading, setLoading] = useState(false);
    const [sent, setSent] = useState(false);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await authAPI.forgotPassword(email);
            setSent(true);
        } catch (err) {
            setError(err.response?.data?.message || 'Something went wrong. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    if (sent) {
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
                            <h1>Check Your Email</h1>
                            <p>
                                If an account exists for <strong>{email}</strong>, you'll receive a password reset link shortly.
                            </p>
                            <p className="text-muted">
                                Don't see it? Check your spam folder.
                            </p>
                            <Link to="/login" className="btn btn-outline">
                                Back to Login
                            </Link>
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
                        <h1>Forgot Password?</h1>
                        <p>Enter your email and we'll send you a reset link</p>
                    </div>

                    <form onSubmit={handleSubmit} className="auth-form">
                        {error && <div className="auth-error">{error}</div>}

                        <div className="form-group">
                            <label htmlFor="email">Email Address</label>
                            <div className="input-with-icon">
                                <Mail className="input-icon" size={18} />
                                <input
                                    type="email"
                                    id="email"
                                    className="input"
                                    placeholder="you@example.com"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
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
                                    Sending...
                                </>
                            ) : (
                                'Send Reset Link'
                            )}
                        </button>
                    </form>

                    <div className="auth-footer">
                        <Link to="/login" className="auth-link">
                            <ArrowLeft size={16} />
                            Back to Login
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ForgotPassword;
