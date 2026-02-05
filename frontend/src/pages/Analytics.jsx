import { useState, useEffect } from 'react';
import { TrendingUp, Calendar } from 'lucide-react';
import { analyticsAPI } from '../services/api';
import './Analytics.css';

const Analytics = () => {
    const [trends, setTrends] = useState(null);
    const [heatmap, setHeatmap] = useState(null);
    const [habitStats, setHabitStats] = useState([]);
    const [loading, setLoading] = useState(true);
    const [period, setPeriod] = useState('daily');

    useEffect(() => {
        loadAnalytics();
    }, [period]);

    const loadAnalytics = async () => {
        try {
            const [trendsRes, heatmapRes, habitsRes] = await Promise.all([
                analyticsAPI.getTrends(period, period === 'daily' ? 30 : 12),
                analyticsAPI.getHeatmap(new Date().getFullYear()),
                analyticsAPI.getHabitAnalytics(30)
            ]);
            setTrends(trendsRes.data);
            setHeatmap(heatmapRes.data);
            setHabitStats(habitsRes.data);
        } catch (error) {
            console.error('Failed to load analytics:', error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <div className="analytics page">
                <div className="container">
                    <div className="skeleton chart-skeleton" />
                </div>
            </div>
        );
    }

    return (
        <div className="analytics page animate-fade-in">
            <div className="container">
                {/* Header */}
                <div className="analytics-header">
                    <div>
                        <h1 className="page-title">Analytics</h1>
                        <p className="page-subtitle">Track your progress over time</p>
                    </div>
                    <div className="period-toggle">
                        <button
                            className={`toggle-btn ${period === 'daily' ? 'active' : ''}`}
                            onClick={() => setPeriod('daily')}
                        >
                            Daily
                        </button>
                        <button
                            className={`toggle-btn ${period === 'weekly' ? 'active' : ''}`}
                            onClick={() => setPeriod('weekly')}
                        >
                            Weekly
                        </button>
                    </div>
                </div>

                {/* Summary Stats */}
                {trends && (
                    <div className="summary-stats">
                        <div className="summary-stat">
                            <span className="summary-value">{trends.totalCheckIns}</span>
                            <span className="summary-label">Total Check-ins</span>
                        </div>
                        <div className="summary-stat">
                            <span className="summary-value">{trends.averageCompletionRate}%</span>
                            <span className="summary-label">Avg Completion</span>
                        </div>
                        <div className="summary-stat">
                            <span className="summary-value">{heatmap?.longestStreak || 0}</span>
                            <span className="summary-label">Longest Streak</span>
                        </div>
                        <div className="summary-stat">
                            <span className="summary-value">{heatmap?.daysWithActivity || 0}</span>
                            <span className="summary-label">Active Days</span>
                        </div>
                    </div>
                )}

                {/* Trend Chart */}
                <div className="card chart-card">
                    <h3>
                        <TrendingUp size={20} />
                        Completion Rate
                    </h3>
                    {trends?.dataPoints && trends.dataPoints.length > 0 ? (
                        <div className="bar-chart">
                            {trends.dataPoints.slice(-14).map((point, index) => (
                                <div key={index} className="chart-bar">
                                    <div
                                        className="bar-value"
                                        style={{ height: `${point.completionRate}%` }}
                                        title={`${point.completionRate}%`}
                                    />
                                    <span className="bar-label">
                                        {formatLabel(point.date, period)}
                                    </span>
                                </div>
                            ))}
                        </div>
                    ) : (
                        <div className="empty-chart">
                            <p>Start tracking habits to see your trends!</p>
                        </div>
                    )}
                </div>

                {/* Heatmap */}
                <div className="card heatmap-card">
                    <h3>
                        <Calendar size={20} />
                        Activity Heatmap
                    </h3>
                    {heatmap?.cells && heatmap.cells.length > 0 ? (
                        <div className="heatmap-container">
                            <div className="heatmap">
                                {generateHeatmapWeeks(heatmap.cells).map((week, weekIndex) => (
                                    <div key={weekIndex} className="heatmap-week">
                                        {week.map((day, dayIndex) => (
                                            <div
                                                key={dayIndex}
                                                className={`heatmap-cell level-${day?.level || 0}`}
                                                title={day ? `${day.date}: ${day.count} check-ins` : ''}
                                            />
                                        ))}
                                    </div>
                                ))}
                            </div>
                            <div className="heatmap-legend">
                                <span>Less</span>
                                {[0, 1, 2, 3, 4].map(level => (
                                    <div key={level} className={`legend-cell level-${level}`} />
                                ))}
                                <span>More</span>
                            </div>
                        </div>
                    ) : (
                        <div className="empty-chart">
                            <p>No activity data yet</p>
                        </div>
                    )}
                </div>

                {/* Per-Habit Stats */}
                {habitStats.length > 0 && (
                    <div className="card">
                        <h3>Habit Performance</h3>
                        <div className="habit-stats-list">
                            {habitStats.map((habit, index) => (
                                <div key={habit.habitId} className="habit-stat-row">
                                    <div className="habit-stat-info">
                                        <span className="habit-stat-icon">{habit.icon}</span>
                                        <span className="habit-stat-name">{habit.habitName}</span>
                                    </div>
                                    <div className="habit-stat-bar">
                                        <div
                                            className="habit-stat-fill"
                                            style={{ width: `${Math.min(habit.completionRate, 100)}%` }}
                                        />
                                    </div>
                                    <span className="habit-stat-value">{habit.completionRate}%</span>
                                </div>
                            ))}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

// Helper functions
const formatLabel = (dateStr, period) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    if (period === 'weekly') {
        return `W${getWeekNumber(date)}`;
    }
    return date.getDate().toString();
};

const getWeekNumber = (date) => {
    const firstDayOfYear = new Date(date.getFullYear(), 0, 1);
    const pastDaysOfYear = (date - firstDayOfYear) / 86400000;
    return Math.ceil((pastDaysOfYear + firstDayOfYear.getDay() + 1) / 7);
};

const generateHeatmapWeeks = (cells) => {
    const weeks = [];
    let currentWeek = [];

    cells.forEach((cell, index) => {
        const date = new Date(cell.date);
        const dayOfWeek = date.getDay();

        if (dayOfWeek === 0 && currentWeek.length > 0) {
            weeks.push(currentWeek);
            currentWeek = [];
        }

        currentWeek.push(cell);
    });

    if (currentWeek.length > 0) {
        weeks.push(currentWeek);
    }

    return weeks.slice(-52); // Last 52 weeks
};

export default Analytics;
