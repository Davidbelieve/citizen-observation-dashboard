import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import Login from './components/Login';
import Register from './components/Register';
import ProtectedRoute from './components/ProtectedRoute';
import EastMidlandsDashboard from './components/EastMidlandsDashboard';
import AuthService from './services/auth.service';
import './App.css';

// Existing Components imports (assuming they are in the same file or imported)
// For simplicity in this edit, I'm assuming existing ObservationForm and RewardsView are handled or we wrap them
import ObservationForm from './components/ObservationForm';
import RewardsView from './components/RewardsView';

const LandingPage = () => {
    const user = AuthService.getCurrentUser();
    const handleLogout = () => {
        AuthService.logout();
        window.location.reload();
    };

    return (
        <div className="landing-page">
            <nav className="main-nav">
                <div className="nav-brand">Citizen Science Hub</div>
                <div className="nav-links">
                    {user ? (
                        <>
                            <span>Welcome, {user.username}</span>
                            <button onClick={handleLogout} className="btn-logout">Logout</button>
                        </>
                    ) : (
                        <>
                            <Link to="/login" className="nav-link">Login</Link>
                            <Link to="/register" className="nav-link">Register</Link>
                        </>
                    )}
                </div>
            </nav>

            <header className="hero-section">
                <h1>Water Quality Monitoring System</h1>
                <p>Collaborative Citizen Science Platform</p>
            </header>

            <div className="dashboard-grid">
                <Link to="/dashboard/eastmidlands" className="dashboard-card-link">
                    <div className="dashboard-card">
                        <h2>East Midlands Dashboard</h2>
                        <p>View stats for the East Midlands Region</p>
                    </div>
                </Link>
                {/* Other regions would go here */}
                <Link to="/submit" className="dashboard-card-link">
                    <div className="dashboard-card action-card">
                        <h2>Submit Observation</h2>
                        <p>Contribute new data</p>
                    </div>
                </Link>
            </div>
        </div>
    );
};

// Wrapper for existing functionality to fit in route
const SubmitPage = () => {
    return (
        <div className="app-container">
            <ObservationForm />
            <RewardsView />
        </div>
    );
};

function App() {
    return (
        <Router>
            <div className="App">
                <Routes>
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />

                    <Route path="/" element={<LandingPage />} />

                    <Route element={<ProtectedRoute />}>
                        <Route path="/dashboard/eastmidlands" element={<EastMidlandsDashboard />} />
                        <Route path="/submit" element={<SubmitPage />} />
                    </Route>
                </Routes>
            </div>
        </Router>
    );
}

export default App;
