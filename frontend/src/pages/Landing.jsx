import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
    Sparkles, Target, Flame, BarChart3, Brain,
    CheckCircle, ArrowRight, Star
} from 'lucide-react';
import './Landing.css';

const Landing = () => {
    const { isAuthenticated } = useAuth();

    const features = [
        {
            icon: Flame,
            title: 'Habit Tracking',
            description: 'Build streaks and stay consistent with daily check-ins',
            gradient: 'gradient-warm'
        },
        {
            icon: Target,
            title: 'Goal Setting',
            description: 'Set measurable goals and track your progress',
            gradient: 'gradient-primary'
        },
        {
            icon: BarChart3,
            title: 'Analytics',
            description: 'Visualize your growth with charts and heatmaps',
            gradient: 'gradient-cool'
        },
        {
            icon: Brain,
            title: 'AI Coach',
            description: 'Get personalized motivation from AI',
            gradient: 'gradient-accent'
        }
    ];

    const stats = [
        { value: '10K+', label: 'Active Users' },
        { value: '50K+', label: 'Habits Tracked' },
        { value: '1M+', label: 'Check-ins' },
        { value: '4.9', label: 'App Rating', icon: Star }
    ];

    return (
        <div className="landing">
            {/* Hero Section */}
            <section className="hero">
                <div className="hero-bg" />
                <div className="container hero-content">
                    <div className="hero-badge">
                        <Sparkles size={14} />
                        <span>AI-Powered Self Improvement</span>
                    </div>

                    <h1 className="hero-title">
                        Build Better Habits,<br />
                        <span className="text-gradient">Become Your Best Self</span>
                    </h1>

                    <p className="hero-description">
                        Track habits, achieve goals, and get personalized AI motivation
                        to transform your daily routine into lasting positive change.
                    </p>

                    <div className="hero-cta">
                        {isAuthenticated ? (
                            <Link to="/dashboard" className="btn btn-primary btn-lg">
                                Go to Dashboard
                                <ArrowRight size={20} />
                            </Link>
                        ) : (
                            <>
                                <Link to="/register" className="btn btn-primary btn-lg">
                                    Start Free Today
                                    <ArrowRight size={20} />
                                </Link>
                                <Link to="/login" className="btn btn-secondary btn-lg">
                                    Sign In
                                </Link>
                            </>
                        )}
                    </div>

                    {/* Stats */}
                    <div className="hero-stats">
                        {stats.map((stat, index) => (
                            <div key={index} className="stat-item">
                                <span className="stat-value">
                                    {stat.value}
                                    {stat.icon && <Star className="stat-icon" size={16} fill="currentColor" />}
                                </span>
                                <span className="stat-label">{stat.label}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* Features Section */}
            <section className="features">
                <div className="container">
                    <div className="section-header">
                        <h2>Everything You Need to <span className="text-gradient">Succeed</span></h2>
                        <p>Powerful features designed to help you build lasting habits</p>
                    </div>

                    <div className="features-grid">
                        {features.map((feature, index) => (
                            <div key={index} className="feature-card card">
                                <div className={`feature-icon ${feature.gradient}`}>
                                    <feature.icon size={24} />
                                </div>
                                <h3>{feature.title}</h3>
                                <p>{feature.description}</p>
                            </div>
                        ))}
                    </div>
                </div>
            </section>

            {/* How It Works */}
            <section className="how-it-works">
                <div className="container">
                    <div className="section-header">
                        <h2>How It Works</h2>
                        <p>Get started in just 3 simple steps</p>
                    </div>

                    <div className="steps">
                        <div className="step">
                            <div className="step-number">1</div>
                            <h3>Create Habits</h3>
                            <p>Define the habits you want to build with custom schedules</p>
                        </div>
                        <div className="step-connector" />
                        <div className="step">
                            <div className="step-number">2</div>
                            <h3>Track Daily</h3>
                            <p>Check in each day and build your streak 🔥</p>
                        </div>
                        <div className="step-connector" />
                        <div className="step">
                            <div className="step-number">3</div>
                            <h3>See Results</h3>
                            <p>Watch your progress grow with analytics and achievements</p>
                        </div>
                    </div>
                </div>
            </section>

            {/* CTA Section */}
            <section className="cta-section">
                <div className="container">
                    <div className="cta-card card-gradient">
                        <h2>Ready to Transform Your Life?</h2>
                        <p>Join thousands who are building better habits every day</p>
                        <Link to="/register" className="btn btn-lg cta-btn">
                            Get Started Free
                            <ArrowRight size={20} />
                        </Link>
                    </div>
                </div>
            </section>

            {/* Footer */}
            <footer className="footer">
                <div className="container">
                    <div className="footer-content">
                        <div className="footer-brand">
                            <Sparkles size={20} />
                            <span>BetterMe</span>
                        </div>
                        <p>© 2026 BetterMe. Built with ❤️ for self-improvement</p>
                    </div>
                </div>
            </footer>
        </div>
    );
};

export default Landing;
