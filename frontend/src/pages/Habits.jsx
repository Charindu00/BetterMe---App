import { useState, useEffect } from 'react';
import {
    Plus, Flame, Check, Trash2, Edit2, X, Save
} from 'lucide-react';
import { habitsAPI } from '../services/api';
import './Habits.css';

const Habits = () => {
    const [habits, setHabits] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [editingHabit, setEditingHabit] = useState(null);
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        icon: '💪',
        frequency: 'DAILY'
    });

    const icons = ['💪', '📚', '🧘', '🏃', '💧', '🎯', '✍️', '🎨', '🌱', '⭐'];

    useEffect(() => {
        loadHabits();
    }, []);

    const loadHabits = async () => {
        try {
            const response = await habitsAPI.getAll();
            setHabits(response.data);
        } catch (error) {
            console.error('Failed to load habits:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleCheckIn = async (habitId) => {
        try {
            const response = await habitsAPI.checkIn(habitId);
            setHabits(habits.map(h =>
                h.id === habitId ? response.data : h
            ));
        } catch (error) {
            console.error('Check-in failed:', error);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (editingHabit) {
                const response = await habitsAPI.update(editingHabit.id, formData);
                setHabits(habits.map(h => h.id === editingHabit.id ? response.data : h));
            } else {
                const response = await habitsAPI.create(formData);
                setHabits([response.data, ...habits]);
            }
            closeModal();
        } catch (error) {
            console.error('Failed to save habit:', error);
        }
    };

    const handleDelete = async (habitId) => {
        if (!confirm('Delete this habit?')) return;
        try {
            await habitsAPI.delete(habitId);
            setHabits(habits.filter(h => h.id !== habitId));
        } catch (error) {
            console.error('Failed to delete habit:', error);
        }
    };

    const openModal = (habit = null) => {
        if (habit) {
            setEditingHabit(habit);
            setFormData({
                name: habit.name,
                description: habit.description || '',
                icon: habit.icon,
                frequency: habit.frequency
            });
        } else {
            setEditingHabit(null);
            setFormData({ name: '', description: '', icon: '💪', frequency: 'DAILY' });
        }
        setShowModal(true);
    };

    const closeModal = () => {
        setShowModal(false);
        setEditingHabit(null);
    };

    if (loading) {
        return (
            <div className="habits page">
                <div className="container">
                    <div className="habits-grid">
                        {[1, 2, 3].map(i => (
                            <div key={i} className="skeleton habit-skeleton" />
                        ))}
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="habits page animate-fade-in">
            <div className="container">
                {/* Header */}
                <div className="habits-header">
                    <div>
                        <h1 className="page-title">My Habits</h1>
                        <p className="page-subtitle">Track your daily habits and build streaks</p>
                    </div>
                    <button className="btn btn-primary" onClick={() => openModal()}>
                        <Plus size={18} />
                        Add Habit
                    </button>
                </div>

                {/* Habits Grid */}
                {habits.length === 0 ? (
                    <div className="empty-habits card">
                        <div className="empty-icon">🎯</div>
                        <h3>No habits yet</h3>
                        <p>Create your first habit to start tracking!</p>
                        <button className="btn btn-primary" onClick={() => openModal()}>
                            <Plus size={18} />
                            Create Habit
                        </button>
                    </div>
                ) : (
                    <div className="habits-grid">
                        {habits.map(habit => (
                            <div key={habit.id} className="habit-card card">
                                <div className="habit-header">
                                    <span className="habit-icon">{habit.icon}</span>
                                    <div className="habit-actions">
                                        <button
                                            className="action-icon"
                                            onClick={() => openModal(habit)}
                                        >
                                            <Edit2 size={16} />
                                        </button>
                                        <button
                                            className="action-icon delete"
                                            onClick={() => handleDelete(habit.id)}
                                        >
                                            <Trash2 size={16} />
                                        </button>
                                    </div>
                                </div>

                                <h3 className="habit-name">{habit.name}</h3>
                                {habit.description && (
                                    <p className="habit-desc">{habit.description}</p>
                                )}

                                <div className="habit-stats">
                                    <div className="streak-badge">
                                        <Flame size={16} />
                                        <span>{habit.currentStreak} day streak</span>
                                    </div>
                                    <span className="total-checkins">
                                        {habit.totalCheckIns || 0} check-ins
                                    </span>
                                </div>

                                <button
                                    className={`checkin-btn ${habit.checkedInToday ? 'checked' : ''}`}
                                    onClick={() => handleCheckIn(habit.id)}
                                    disabled={habit.checkedInToday}
                                >
                                    {habit.checkedInToday ? (
                                        <>
                                            <Check size={18} />
                                            Done Today!
                                        </>
                                    ) : (
                                        <>
                                            <Plus size={18} />
                                            Check In
                                        </>
                                    )}
                                </button>
                            </div>
                        ))}
                    </div>
                )}

                {/* Modal */}
                {showModal && (
                    <div className="modal-overlay" onClick={closeModal}>
                        <div className="modal" onClick={e => e.stopPropagation()}>
                            <div className="modal-header">
                                <h2>{editingHabit ? 'Edit Habit' : 'New Habit'}</h2>
                                <button className="close-btn" onClick={closeModal}>
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
                                    <label htmlFor="name">Habit Name</label>
                                    <input
                                        id="name"
                                        type="text"
                                        className="input"
                                        placeholder="e.g., Morning Exercise"
                                        value={formData.name}
                                        onChange={e => setFormData({ ...formData, name: e.target.value })}
                                        required
                                    />
                                </div>

                                <div className="input-group">
                                    <label htmlFor="description">Description (optional)</label>
                                    <input
                                        id="description"
                                        type="text"
                                        className="input"
                                        placeholder="e.g., 30 minutes of cardio"
                                        value={formData.description}
                                        onChange={e => setFormData({ ...formData, description: e.target.value })}
                                    />
                                </div>

                                <div className="modal-actions">
                                    <button type="button" className="btn btn-secondary" onClick={closeModal}>
                                        Cancel
                                    </button>
                                    <button type="submit" className="btn btn-primary">
                                        <Save size={18} />
                                        {editingHabit ? 'Update' : 'Create'}
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

export default Habits;
