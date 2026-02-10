import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';
import { Sparkles, Mail, Lock, User, ArrowRight, Loader, Check, CheckCircle } from 'lucide-react';
import './Auth.css';

const Register = () => {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const [registrationSuccess, setRegistrationSuccess] = useState(false);

    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        if (password.length < 6) {
            setError('Password must be at least 6 characters');
            return;
        }

        setLoading(true);

        try {
            // Call register API directly (don't use AuthContext which sets token)
            await authAPI.register(name, email, password);
            // Show success message instead of auto-login
            setRegistrationSuccess(true);
        } catch (err) {
            setError(err.response?.data?.message || 'Registration failed');
        } finally {
            setLoading(false);
        }
    };

    const benefits = [
        'Track unlimited habits',
        'Set goals with deadlines',
        'AI-powered motivation',
        'Beautiful analytics',
    ];

    // Show success message after registration
    if (registrationSuccess) {
        return (
            <div className="auth-page">
                <div className="auth-container">
                    <div className="auth-form-section" style={{ textAlign: 'center', padding: '60px 40px' }}>
                        <div className="auth-header">
                            <Link to="/" className="auth-logo">
                                <Sparkles size={28} />
                                <span>BetterMe</span>
                            </Link>
                        </div>

                        <div style={{ marginTop: '40px' }}>
                            <CheckCircle size={80} style={{ color: '#22c55e', marginBottom: '24px' }} />
                            <h1 style={{ marginBottom: '16px' }}>Check Your Email! 📧</h1>
                            <p style={{ color: 'var(--text-secondary)', fontSize: '18px', marginBottom: '24px' }}>
                                We've sent a verification link to:
                            </p>
                            <p style={{
                                color: 'var(--text-primary)',
                                fontWeight: '600',
                                fontSize: '20px',
                                padding: '12px 24px',
                                background: 'var(--surface-secondary)',
                                borderRadius: '8px',
                                display: 'inline-block',
                                marginBottom: '32px'
                            }}>
                                {email}
                            </p>
                            <p style={{ color: 'var(--text-secondary)', marginBottom: '32px' }}>
                                Please click the link in your email to verify your account.<br />
                                <span style={{ fontSize: '14px' }}>Check your spam folder if you don't see it.</span>
                            </p>

                            <Link to="/login" className="btn btn-primary btn-lg">
                                Go to Login
                                <ArrowRight size={18} />
                            </Link>
                        </div>
                    </div>

                    {/* Right Side - Visual */}
                    <div className="auth-visual register-visual">
                        <div className="auth-visual-content">
                            <div className="visual-icon">✉️</div>
                            <h2>Almost There!</h2>
                            <p style={{ color: 'rgba(255,255,255,0.8)', marginTop: '16px' }}>
                                Just one more step to start your journey to becoming your best self.
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="auth-page">
            <div className="auth-container">
                {/* Left Side - Form */}
                <div className="auth-form-section">
                    <div className="auth-header">
                        <Link to="/" className="auth-logo">
                            <Sparkles size={28} />
                            <span>BetterMe</span>
                        </Link>
                        <h1>Create Account</h1>
                        <p>Start your self-improvement journey</p>
                    </div>

                    <form onSubmit={handleSubmit} className="auth-form">
                        {error && (
                            <div className="auth-error">
                                {error}
                            </div>
                        )}

                        <div className="input-group">
                            <label htmlFor="name">Full Name</label>
                            <div className="input-wrapper">
                                <User size={18} className="input-icon" />
                                <input
                                    id="name"
                                    type="text"
                                    className="input with-icon"
                                    placeholder="John Doe"
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    required
                                />
                            </div>
                        </div>

                        <div className="input-group">
                            <label htmlFor="email">Email</label>
                            <div className="input-wrapper">
                                <Mail size={18} className="input-icon" />
                                <input
                                    id="email"
                                    type="email"
                                    className="input with-icon"
                                    placeholder="your@email.com"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    required
                                />
                            </div>
                        </div>

                        <div className="input-group">
                            <label htmlFor="password">Password</label>
                            <div className="input-wrapper">
                                <Lock size={18} className="input-icon" />
                                <input
                                    id="password"
                                    type="password"
                                    className="input with-icon"
                                    placeholder="••••••••"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>
                        </div>

                        <button
                            type="submit"
                            className="btn btn-primary btn-lg auth-submit"
                            disabled={loading}
                        >
                            {loading ? (
                                <Loader size={20} className="spinner-icon" />
                            ) : (
                                <>
                                    Create Account
                                    <ArrowRight size={18} />
                                </>
                            )}
                        </button>
                    </form>

                    <div className="auth-footer">
                        <p>
                            Already have an account?{' '}
                            <Link to="/login">Sign in</Link>
                        </p>
                    </div>
                </div>

                {/* Right Side - Visual */}
                <div className="auth-visual register-visual">
                    <div className="auth-visual-content">
                        <div className="visual-icon">✨</div>
                        <h2>What You'll Get</h2>

                        <ul className="benefits-list">
                            {benefits.map((benefit, index) => (
                                <li key={index}>
                                    <Check size={18} className="benefit-check" />
                                    <span>{benefit}</span>
                                </li>
                            ))}
                        </ul>

                        <div className="visual-quote">
                            <p>"The secret of getting ahead is getting started."</p>
                            <span>— Mark Twain</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Register;

