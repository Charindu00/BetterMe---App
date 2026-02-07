import { useState, useEffect } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { authAPI } from '../services/api';
import { CheckCircle, XCircle, Loader, Mail } from 'lucide-react';
import './Auth.css';

const VerifyEmail = () => {
    const [searchParams] = useSearchParams();
    const [status, setStatus] = useState('loading'); // loading, success, error
    const [message, setMessage] = useState('');
    const token = searchParams.get('token');

    useEffect(() => {
        const verifyEmail = async () => {
            if (!token) {
                setStatus('error');
                setMessage('Invalid verification link. No token provided.');
                return;
            }

            try {
                const res = await authAPI.verifyEmail(token);
                if (res.data.success) {
                    setStatus('success');
                    setMessage(res.data.message);
                } else {
                    setStatus('error');
                    setMessage(res.data.message);
                }
            } catch (err) {
                setStatus('error');
                setMessage(err.response?.data?.message || 'Email verification failed. Please try again.');
            }
        };

        verifyEmail();
    }, [token]);

    return (
        <div className="auth-page">
            <div className="auth-container">
                <div className="auth-header">
                    <Link to="/" className="auth-logo">
                        ✨ BetterMe
                    </Link>
                </div>

                <div className="auth-card verify-card">
                    {status === 'loading' && (
                        <div className="verify-status">
                            <Loader className="verify-icon loading spin" size={64} />
                            <h1>Verifying your email...</h1>
                            <p>Please wait while we confirm your email address.</p>
                        </div>
                    )}

                    {status === 'success' && (
                        <div className="verify-status success">
                            <CheckCircle className="verify-icon success" size={64} />
                            <h1>Email Verified! 🎉</h1>
                            <p>{message}</p>
                            <Link to="/login" className="btn btn-primary btn-lg">
                                Continue to Login
                            </Link>
                        </div>
                    )}

                    {status === 'error' && (
                        <div className="verify-status error">
                            <XCircle className="verify-icon error" size={64} />
                            <h1>Verification Failed</h1>
                            <p>{message}</p>
                            <div className="verify-actions">
                                <Link to="/login" className="btn btn-outline">
                                    Back to Login
                                </Link>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default VerifyEmail;
