import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/auth.service';
import './Auth.css';

const Register = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await AuthService.register(username, password);
            navigate('/login');
        } catch (err) {
            setError(err.response?.data || 'Registration failed');
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-card">
                <div className="auth-header">
                    <h2>Join Us</h2>
                    <p>Create an account to start contributing</p>
                </div>

                {error && <div className="error-message">{error}</div>}

                <form onSubmit={handleRegister} className="auth-form">
                    <div className="form-group">
                        <label>Username</label>
                        <input
                            type="text"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            className="auth-input"
                            placeholder="Choose a username"
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label>Password</label>
                        <input
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="auth-input"
                            placeholder="Create a password"
                            required
                        />
                    </div>
                    <button type="submit" className="auth-button">
                        Create Account
                    </button>
                </form>

                <div className="auth-footer">
                    Already have an account? <a href="/login" className="auth-link">Login</a>
                </div>
            </div>
        </div>
    );
};

export default Register;
