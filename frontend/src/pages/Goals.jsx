import { useState, useEffect } from 'react';
import { Plus, Target, Check, Trash2, TrendingUp, Calendar, X } from 'lucide-react';
import { goalsAPI } from '../services/api';
import './Goals.css';

const Goals = () => {
    const [goals, setGoals] = useState([]);
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [formData, setFormData] = useState({
        title: '',
        description: '',
        type: 'COUNT',
        targetValue: 10,
        unit: '',
        icon: '🎯',
        deadline: ''
    });

    const icons = ['🎯', '📚', '💪', '🏃', '💰', '✨', '🎨', '🌟', '🏆', '🚀'];
    const types = [
        { value: 'COUNT', label: 'Count (e.g., Read 12 books)' },
        { value: 'STREAK', label: 'Streak (e.g., 30-day streak)' },
        { value: 'DURATION', label: 'Duration (e.g., 100 hours)' }
    ];

    useEffect(() => {
        loadGoals();
    }, []);

    const loadGoals = async () => {
        try {
            const [goalsRes, statsRes] = await Promise.all([
                goalsAPI.getAll(),
                goalsAPI.getStats()
            ]);
            setGoals(goalsRes.data);
            setStats(statsRes.data);
        } catch (error) {
            console.error('Failed to load goals:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await goalsAPI.create({
                ...formData,
                deadline: formData.deadline || null
            });
            setGoals([response.data, ...goals]);
            setShowModal(false);
            setFormData({
                title: '', description: '', type: 'COUNT',
                targetValue: 10, unit: '', icon: '🎯', deadline: ''
            });
        } catch (error) {
            console.error('Failed to create goal:', error);
        }
    };

    const handleProgress = async (goalId, increment) => {
        try {
            const response = await goalsAPI.incrementProgress(goalId, increment);
            setGoals(goals.map(g => g.id === goalId ? response.data : g));
        } catch (error) {
            console.error('Failed to update progress:', error);
        }
    };

    const handleDelete = async (goalId) => {
        if (!confirm('Delete this goal?')) return;
        try {
            await goalsAPI.delete(goalId);
            setGoals(goals.filter(g => g.id !== goalId));
        } catch (error) {
            console.error('Failed to delete goal:', error);
        }
    };

    if (loading) {
        return (
            <div className="goals page">
                <div className="container">
                    <div className="goals-grid">
                        {[1, 2].map(i => <div key={i} className="skeleton goal-skeleton" />)}
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="goals page animate-fade-in">
            <div className="container">
                {/* Header */}
                <div className="goals-header">
                    <div>
                        <h1 className="page-title">Goals</h1>
                        <p className="page-subtitle">Set targets and track your progress</p>
                    </div>
                    <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                        <Plus size={18} />
                        New Goal
                    </button>
                </div>

                {/* Stats */}
                {stats && (
                    <div className="goals-stats">
                        <div className="goal-stat">
                            <Target size={20} />
                            <span>{stats.totalGoals}</span>
                            <label>Active</label>
                        </div>
                        <div className="goal-stat">
                            <Check size={20} />
                            <span>{stats.completedGoals}</span>
                            <label>Completed</label>
                        </div>
                        <div className="goal-stat">
                            <TrendingUp size={20} />
                            <span>{stats.averageProgress}%</span>
                            <label>Avg Progress</label>
                        </div>
                    </div>
                )}

                {/* Goals Grid */}
                {goals.length === 0 ? (
                    <div className="empty-goals card">
                        <div className="empty-icon">🎯</div>
                        <h3>No goals yet</h3>
                        <p>Create your first goal to start tracking!</p>
                        <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                            <Plus size={18} />
                            Create Goal
                        </button>
                    </div>
                ) : (
                    <div className="goals-grid">
                        {goals.map(goal => (
                            <div
                                key={goal.id}
                                className={`goal-card card ${goal.completed ? 'completed' : ''}`}
                            >
                                <div className="goal-header">
                                    <span className="goal-icon">{goal.icon}</span>
                                    <button
                                        className="delete-btn"
                                        onClick={() => handleDelete(goal.id)}
                                    >
                                        <Trash2 size={16} />
                                    </button>
                                </div>

                                <h3 className="goal-title">{goal.title}</h3>
                                {goal.description && (
                                    <p className="goal-desc">{goal.description}</p>
                                )}

                                {/* Progress */}
                                <div className="progress-section">
                                    <div className="progress-header">
                                        <span className="progress-text">
                                            {goal.currentValue} / {goal.targetValue} {goal.unit}
                                        </span>
                                        <span className="progress-percent">
                                            {Math.round(goal.progressPercentage)}%
                                        </span>
                                    </div>
                                    <div className="progress-bar">
                                        <div
                                            className="progress-fill"
                                            style={{ width: `${Math.min(goal.progressPercentage, 100)}%` }}
                                        />
                                    </div>
                                </div>

                                {/* Deadline */}
                                {goal.deadline && (
                                    <div className={`goal-deadline ${goal.overdue ? 'overdue' : ''}`}>
                                        <Calendar size={14} />
                                        <span>
                                            {goal.daysRemaining > 0
                                                ? `${goal.daysRemaining} days left`
                                                : goal.daysRemaining === 0
                                                    ? 'Due today'
                                                    : 'Overdue'}
                                        </span>
                                    </div>
                                )}

                                {/* Actions */}
                                {!goal.completed && (
                                    <div className="goal-actions">
                                        <button
                                            className="btn btn-secondary btn-sm"
                                            onClick={() => handleProgress(goal.id, 1)}
                                        >
                                            +1
                                        </button>
                                        <button
                                            className="btn btn-primary btn-sm"
                                            onClick={() => handleProgress(goal.id, 5)}
                                        >
                                            +5
                                        </button>
                                    </div>
                                )}

                                {goal.completed && (
                                    <div className="goal-completed">
                                        <Check size={18} />
                                        <span>Completed!</span>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}

                {/* Modal */}
                {showModal && (
                    <div className="modal-overlay" onClick={() => setShowModal(false)}>
                        <div className="modal" onClick={e => e.stopPropagation()}>
                            <div className="modal-header">
                                <h2>New Goal</h2>
                                <button className="close-btn" onClick={() => setShowModal(false)}>
                                    <X size={20} />
                                </button>
                            </div>

                            <form onSubmit={handleSubmit}>
                                <div className="input-group">
                                    <label>Icon</label>
                                    <div className="icon-picker">
                                        {icons.map(icon => (
                                            <button
                                                key={icon}
                                                type="button"
                                                className={`icon-option ${formData.icon === icon ? 'selected' : ''}`}
                                                onClick={() => setFormData({ ...formData, icon })}
                                            >
                                                {icon}
                                            </button>
                                        ))}
                                    </div>
                                </div>

                                <div className="input-group">
                                    <label>Goal Title</label>
                                    <input
                                        type="text"
                                        className="input"
                                        placeholder="e.g., Read 12 books"
                                        value={formData.title}
                                        onChange={e => setFormData({ ...formData, title: e.target.value })}
                                        required
                                    />
                                </div>

                                <div className="form-row">
                                    <div className="input-group">
                                        <label>Target Value</label>
                                        <input
                                            type="number"
                                            className="input"
                                            min="1"
                                            value={formData.targetValue}
                                            onChange={e => setFormData({ ...formData, targetValue: parseInt(e.target.value) })}
                                            required
                                        />
                                    </div>
                                    <div className="input-group">
                                        <label>Unit</label>
                                        <input
                                            type="text"
                                            className="input"
                                            placeholder="e.g., books, hours"
                                            value={formData.unit}
                                            onChange={e => setFormData({ ...formData, unit: e.target.value })}
                                        />
                                    </div>
                                </div>

                                <div className="input-group">
                                    <label>Deadline (optional)</label>
                                    <input
                                        type="date"
                                        className="input"
                                        value={formData.deadline}
                                        onChange={e => setFormData({ ...formData, deadline: e.target.value })}
                                    />
                                </div>

                                <div className="modal-actions">
                                    <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-primary">
                                        Create Goal
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Goals;
