import React, { useState, useEffect } from 'react';
import { StatCard } from '../molecules/StatCard';
import { Card } from '../molecules/Card';
import { ObservationList } from '../organisms/ObservationList';
import { Leaderboard } from '../organisms/Leaderboard';

/**
 * North East England Dashboard
 * Displays water quality observations and leaderboard
 */
export function RidwanDashboard({ onBack }) {
  const [observations, setObservations] = useState([]);
  const [leaderboard, setLeaderboard] = useState([]);
  const [stats, setStats] = useState({ total: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');

    try {
      // Get auth token for authenticated requests
      const token = localStorage.getItem('authToken');
      const headers = {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` })
      };

      // Route through authentication service (port 8080) → gateway (port 8090) → services (8091, 8092)
      // All requests use /api/v1/** which routes through the gateway
      const observationsResponse = await fetch('/api/v1/observations', { headers });
      const leaderboardResponse = await fetch('/api/v1/rewards/leaderboard?limit=3', { headers });

      if (!observationsResponse.ok) {
        const errorData = await observationsResponse.json().catch(() => ({ message: 'Failed to fetch observations' }));
        throw new Error(errorData.message || errorData.error || 'Unable to fetch observations');
      }

      if (!leaderboardResponse.ok) {
        const errorData = await leaderboardResponse.json().catch(() => ({ message: 'Failed to fetch leaderboard' }));
        throw new Error(errorData.message || errorData.error || 'Unable to fetch leaderboard');
      }

      const observationsData = await observationsResponse.json();
      const leaderboardData = await leaderboardResponse.json();

      const sortedObservations = observationsData.sort((a, b) => 
        new Date(b.submissionTimestamp) - new Date(a.submissionTimestamp)
      );

      setObservations(sortedObservations.slice(0, 5));
      setStats({ total: observationsData.length });
      setLeaderboard(leaderboardData);

    } catch (err) {
      setError(err.message || 'Unable to connect to services. Please ensure backend services are running.');
      console.error('Dashboard error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 p-6">
      {/* Header */}
      <div className="max-w-7xl mx-auto mb-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-4xl font-bold text-white mb-2">
              💧 North East England
            </h1>
            <p className="text-blue-100">Newcastle & Northumberland Water Quality</p>
          </div>
          {onBack && (
            <button
              onClick={onBack}
              className="bg-white/20 hover:bg-white/30 text-white px-6 py-3 rounded-lg transition flex items-center gap-2"
            >
              <span>←</span> Back to Hub
            </button>
          )}
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto">
        {/* Error Message */}
        {error && (
          <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded">
            <div className="flex items-center">
              <svg className="h-5 w-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
              <div>
                <p className="font-bold">Connection Error</p>
                <p className="text-sm">{error}</p>
              </div>
            </div>
            <button 
              onClick={fetchDashboardData}
              className="mt-3 bg-red-600 text-white px-4 py-2 rounded text-sm hover:bg-red-700 transition"
            >
              Retry Connection
            </button>
          </div>
        )}

        {/* Loading State */}
        {loading ? (
          <div className="text-center py-20">
            <div className="text-6xl mb-4 animate-pulse">⏳</div>
            <p className="text-white text-xl">Loading dashboard data...</p>
          </div>
        ) : (
          <>
            {/* Stats Row */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
              <StatCard 
                label="Total Observations" 
                value={stats.total} 
                icon="📊" 
              />
              <StatCard 
                label="Active Contributors" 
                value={leaderboard.length} 
                icon="👥" 
              />
              <StatCard 
                label="Region Status" 
                value="Active" 
                icon=" " 
              />
            </div>

            {/* Main Dashboard Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              
              {/* Recent Observations */}
              <div className="lg:col-span-2">
                <Card title="🕐 5 Most Recent Observations">
                  <ObservationList observations={observations} />
                </Card>
              </div>

              {/* Leaderboard */}
              <div>
                <Card title="🏆 Top 3 Contributors">
                  <Leaderboard contributors={leaderboard} />
                </Card>
              </div>

            </div>
          </>
        )}
      </div>
    </div>
  );
}

