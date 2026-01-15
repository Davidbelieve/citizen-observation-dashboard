import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './EastMidlandsDashboard.css';

const EastMidlandsDashboard = () => {
    const [stats, setStats] = useState({
        totalObservations: 0,
        recentObservations: [],
        leaderboard: []
    });

    useEffect(() => {
        fetchDashboardData();
    }, []);

    const fetchDashboardData = async () => {
        // In a real scenario, you'd fetch this from your aggregated API
        // For now, I'll mock the fetch or simulate it
        // NOTE: This assumes the backend has valid endpoints for these stats
        // Changing to use localhost:8083/api/observations
        try {
            // Fetch all observations to calculate total
            const obsResponse = await axios.get('http://localhost:8080/api/observations');
            const observations = obsResponse.data || [];

            // Fetch leaderboard - this endpoint might need to assume it exists or calculate client side if not
            // Assuming rewards-ms handles points, we might calculate ranking here if no direct endpoint
            // For demonstration, let's mock leaderboard if API fails, but try to fetch

            // 1. Total Observations
            const total = observations.length;

            // 2. Recent 5
            const recent = [...observations].reverse().slice(0, 5);

            // 3. Leaderboard - Fetching real rankings from Rewards Service
            let leaderboard = [];
            try {
                const lbResponse = await axios.get('http://localhost:8080/api/rewards/leaderboard');
                leaderboard = lbResponse.data.map((entry, index) => ({
                    rank: index + 1,
                    citizenId: entry.citizenId,
                    points: entry.points
                }));
            } catch (lbError) {
                console.error("Could not fetch leaderboard", lbError);
                leaderboard = []; // Fallback to empty if not ready
            }

            setStats({
                totalObservations: total,
                recentObservations: recent,
                leaderboard: leaderboard
            });

        } catch (error) {
            console.error("Error fetching dashboard data", error);
        }
    };

    return (
        <div className="em-dashboard">
            <header className="em-header">
                <h1>East Midlands Water Quality Dashboard</h1>
                <p>Local Authority Overview</p>
            </header>

            <div className="em-grid">
                {/* Total Stats Card */}
                <div className="em-card total-card">
                    <h3>Total Observations</h3>
                    <div className="big-number">{stats.totalObservations}</div>
                    <p>Submitted in East Midlands Region</p>
                </div>

                {/* Leaderboard Card */}
                <div className="em-card leaderboard-card">
                    <h3>Top Contributors</h3>
                    <ul className="leaderboard-list">
                        {stats.leaderboard.map((user) => (
                            <li key={user.rank} className={`rank-${user.rank}`}>
                                <span className="rank-badge">{user.rank}</span>
                                <span className="user-id">{user.citizenId}</span>
                                <span className="points">{user.points} pts</span>
                            </li>
                        ))}
                    </ul>
                </div>

                {/* Recent Activity Card */}
                <div className="em-card recent-card">
                    <h3>Recent Observations</h3>
                    <div className="table-responsive">
                        <table>
                            <thead>
                                <tr>
                                    <th>Citizen ID</th>
                                    <th>Postcode</th>
                                    <th>Measurements</th>
                                    <th>Notes</th>
                                </tr>
                            </thead>
                            <tbody>
                                {stats.recentObservations.map((obs, index) => (
                                    <tr key={index}>
                                        <td>{obs.citizenId}</td>
                                        <td>{obs.postcode}</td>
                                        <td>
                                            {obs.temperature && <div>Temp: {obs.temperature}°C</div>}
                                            {obs.ph && <div>pH: {obs.ph}</div>}
                                        </td>
                                        <td className="notes-col">
                                            {obs.observations && obs.observations.length > 0 ? obs.observations.join(', ') : 'No notes'}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default EastMidlandsDashboard;
