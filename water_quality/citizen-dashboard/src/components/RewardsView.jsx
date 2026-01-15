import React, { useState } from 'react';
import axios from 'axios';
import './RewardsView.css';

const RewardsView = () => {
    const [citizenId, setCitizenId] = useState('');
    const [rewards, setRewards] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const fetchRewards = async () => {
        if (!citizenId) return;
        setLoading(true);
        setError('');
        try {
            // Using Gateway Port 8080
            const response = await axios.get(`http://localhost:8080/api/rewards/${citizenId}`);
            setRewards(response.data);
        } catch (err) {
            setError('Could not fetch rewards. Check Citizen ID or try later.');
            setRewards(null);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="rewards-view-container">
            <h2>Check Rewards</h2>
            <div className="search-box">
                <input
                    type="text"
                    value={citizenId}
                    onChange={(e) => setCitizenId(e.target.value)}
                    placeholder="Enter Citizen ID (e.g. C200)"
                />
                <button onClick={fetchRewards} disabled={loading}>
                    {loading ? '...' : 'Get Rewards'}
                </button>
            </div>

            {error && <div className="error-msg">{error}</div>}

            {rewards && (
                <div className="rewards-card">
                    <div className="badge-header">
                        <div className={`badge-icon ${rewards.badge?.toLowerCase()}`}>
                            {rewards.badge || 'None'}
                        </div>
                        <div className="points-display">
                            <span className="points-val">{rewards.totalPoints || 0}</span>
                            <span className="points-label">Total Points</span>
                        </div>
                    </div>
                    {rewards.observationCount !== undefined && (
                        <div className="debug-info" style={{ marginTop: '1rem', fontSize: '0.8rem', opacity: 0.6 }}>
                            Total Observations Scanned: {rewards.observationCount}
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default RewardsView;
