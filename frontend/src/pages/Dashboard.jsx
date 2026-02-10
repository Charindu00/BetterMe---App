import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
    Flame, Target, CheckCircle, TrendingUp, Sparkles,
    ArrowRight, Calendar, Award
} from 'lucide-react';
import { dashboardAPI, motivationAPI, userAPI } from '../services/api';
import './Dashboard.css';

const Dashboard = () => {
    const [summary, setSummary] = useState(null);
    const [weeklyProgress, setWeeklyProgress] = useState(null);
    const [motivation, setMotivation] = useState(null);
    const [achievements, setAchievements] = useState([]);
    const [userName, setUserName] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadDashboard();
    }, []);

    const loadDashboard = async () => {
        try {
            const [summaryRes, weeklyRes, achievementsRes] = await Promise.allSettled([
                dashboardAPI.getSummary(),
                dashboardAPI.getWeekly(),
                dashboardAPI.getAchievements()
            ]);

            if (summaryRes.status === 'fulfilled') setSummary(summaryRes.value.data);
            if (weeklyRes.status === 'fulfilled') setWeeklyProgress(weeklyRes.value.data);
            if (achievementsRes.status === 'fulfilled') setAchievements(achievementsRes.value.data);

            // Get user name
            try {
                const userRes = await userAPI.getProfile();
                setUserName(userRes.data?.name || '');
            } catch {
                // fallback to empty
            }

            // Get AI motivation
            try {
                const motivationRes = await motivationAPI.getDaily();
                setMotivation(motivationRes.data);
            } catch {
                // AI might not be configured
            }
        } catch (error) {
            console.error('Failed to load dashboard:', error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="dashboard page">
                <div className="container">
                    <div className="loading-grid">
                        {[1, 2, 3, 4].map(i => (
                            <div key={i} className="skeleton stat-skeleton" />
                        ))}
                    </div>
                </div>
            </div>
        );
    }

    const stats = [
        {
            icon: CheckCircle,
            label: 'Today\'s Progress',
            value: summary?.todayProgress || 0,
            suffix: '%',
            gradient: 'gradient-primary'
        },
        {
            icon: Flame,
            label: 'Current Streak',
            value: summary?.longestStreak || 0,
            suffix: ' days',
            gradient: 'gradient-warm'
        },
        {
            icon: Target,
            label: 'Active Goals',
            value: summary?.activeGoals || 0,
            suffix: '',
            gradient: 'gradient-cool'
        },
        {
            icon: Award,
            label: 'Achievements',
            value: achievements?.filter(a => a.unlocked)?.length || 0,
            suffix: '',
            gradient: 'gradient-accent'
        }
    ];

    return (
        <div className="dashboard page animate-fade-in">
            <div className="container">
                {/* Header */}
                <div className="dashboard-header">
                    <div>
                        <h1 className="page-title">
                            Hello{userName ? `, ${userName}` : ''}! 👋
                        </h1>
                        <p className="page-subtitle">Here's your progress overview</p>
                    </div>
                    <div className="header-actions">
                        <Link to="/habits" className="btn btn-primary">
                            <Flame size={18} />
                            Check In
                        </Link>
                    </div>
                </div>

                {/* Stats Grid */}
                <div className="stats-grid">
                    {stats.map((stat, index) => (
                        <div key={index} className="stat-card card">
                            <div className={`stat-icon ${stat.gradient}`}>
                                <stat.icon size={22} />
                            </div>
                            <div className="stat-content">
                                <span className="stat-value">
                                    {stat.value}{stat.suffix}
                                </span>
                                <span className="stat-label">{stat.label}</span>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Main Content */}
                <div className="dashboard-grid">
                    {/* AI Motivation */}
                    <div className="motivation-card card-gradient">
                        <div className="motivation-header">
                            <Sparkles size={20} />
                            <span>AI Coach</span>
                        </div>
                        <p className="motivation-text">
                            {motivation?.message || "Keep pushing forward! Every small step counts towards your bigger goals. You've got this! 💪"}
                        </p>
                    </div>

                    {/* Weekly Progress */}
                    <div className="card weekly-card">
                        <div className="card-header">
                            <h3>
                                <TrendingUp size={20} />
                                This Week
                            </h3>
                            <Link to="/analytics" className="card-link">
                                View All <ArrowRight size={16} />
                            </Link>
                        </div>
                        {weeklyProgress?.days && (
                            <div className="weekly-bars">
                                {weeklyProgress.days.map((day, index) => (
                                    <div key={index} className="day-bar">
                                        <div
                                            className="bar-fill"
                                            style={{ height: `${day.completionRate}%` }}
                                        />
                                        <span className="day-label">{day.dayName?.substring(0, 3) || getDayName(index)}</span>
                                    </div>
                                ))}
                            </div>
                        )}
                        {!weeklyProgress?.days && (
                            <div className="empty-state">
                                <p>Start tracking habits to see your weekly progress!</p>
                            </div>
                        )}
                    </div>



                    {/* All Achievements */}
                    {achievements.length > 0 && (
                        <div className="card achievements-card achievements-full">
                            <div className="card-header">
                                <h3>
                                    <Award size={20} />
                                    Achievements ({achievements.filter(a => a.unlocked).length}/{achievements.length})
                                </h3>
                            </div>
                            <div className="achievements-grid">
                                {achievements.map((achievement, index) => (
                                    <div
                                        key={index}
                                        className={`achievement-item ${achievement.unlocked ? 'unlocked' : 'locked'}`}
                                    >
                                        <span className="achievement-icon">{achievement.icon}</span>
                                        <div className="achievement-info">
                                            <span className="achievement-name">{achievement.name}</span>
                                            <span className="achievement-desc">{achievement.description}</span>
                                            {!achievement.unlocked && (
                                                <div className="achievement-progress">
                                                    <div className="achievement-progress-bar">
                                                        <div
                                                            className="achievement-progress-fill"
                                                            style={{ width: `${achievement.progressPercentage || 0}%` }}
                                                        />
                                                    </div>
                                                    <span className="achievement-progress-text">
                                                        {achievement.currentProgress}/{achievement.requiredProgress}
                                                    </span>
                                                </div>
                                            )}
                                            {achievement.unlocked && (
                                                <span className="achievement-unlocked-badge">✅ Unlocked</span>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

// Helper functions
const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Morning';
    if (hour < 17) return 'Afternoon';
    return 'Evening';
};

const getDayName = (index) => {
    const days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    return days[index] || '';
};

export default Dashboard;
