import React, { useState, useEffect } from 'react';
import StatCard from './StatCard';
import Card from './Card';
import ObservationList from './ObservationList';
import Leaderboard from './Leaderboard';

/**
 * North East England Dashboard
 * Developer: Ridwan
 * Region: Newcastle & Northumberland
 * 
 */
function RidwanDashboard({ onBack }) {
  // State management
  const [observations, setObservations] = useState([]);
  const [leaderboard, setLeaderboard] = useState([]);
  const [stats, setStats] = useState({ total: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const DATA_SERVICE_URL = 'http://localhost:8091';
  const REWARDS_SERVICE_URL = 'http://localhost:8092';

  // Fetch data on component mount
  useEffect(() => {
    fetchDashboardData();
  }, []);

  /**
   * Fetch all dashboard data from microservices
   */
  const fetchDashboardData = async () => {
    setLoading(true);
    setError('');

    try {
      const observationsResponse = await fetch(`${DATA_SERVICE_URL}/api/observations`);
      
      const leaderboardResponse = await fetch(`${REWARDS_SERVICE_URL}/api/rewards/leaderboard?limit=3`);

      if (!observationsResponse.ok || !leaderboardResponse.ok) {
        throw new Error('Failed to fetch data from microservices');
      }

      const observationsData = await observationsResponse.json();
      const leaderboardData = await leaderboardResponse.json();

      // Sort observations by timestamp (newest first)
      const sortedObservations = observationsData.sort((a, b) => {
        const dateA = new Date(a.submissionTimestamp || a.timestamp);
        const dateB = new Date(b.submissionTimestamp || b.timestamp);
        return dateB - dateA;
      });

      // Update state with fetched data
      setObservations(sortedObservations.slice(0, 5)); // Top 5 most recent
      setStats({ total: observationsData.length });
      setLeaderboard(leaderboardData);

    } catch (err) {
      // Handle errors
      setError('Unable to connect to North East services. Please ensure microservices are running on ports 8091 and 8092.');
      console.error('Dashboard fetch error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-500 to-purple-600 p-6">
      
      {/* Dashboard Header */}
      <div className="max-w-7xl mx-auto mb-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-4xl font-bold text-white mb-2">
              💧 North East England
            </h1>
            <p className="text-blue-100">Newcastle & Northumberland Water Quality Monitoring</p>
          </div>
          {onBack && (
            <button
              onClick={onBack}
              className="bg-white/20 hover:bg-white/30 text-white px-6 py-3 rounded-lg transition flex items-center gap-2"
            >
              <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 19l-7-7m0 0l7-7m-7 7h18" />
              </svg>
              Back to Hub
            </button>
          )}
        </div>
      </div>

      {/* Main Dashboard Content */}
      <div className="max-w-7xl mx-auto">
        
        {/* Error Display */}
        {error && (
          <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded-lg shadow">
            <div className="flex items-start">
              <svg className="h-6 w-6 mr-3 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
              <div>
                <p className="font-bold">Connection Error</p>
                <p className="text-sm mt-1">{error}</p>
                <button 
                  onClick={fetchDashboardData}
                  className="mt-3 bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded text-sm transition"
                >
                  Retry Connection
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Loading State */}
        {loading ? (
          <div className="text-center py-20">
            <div className="inline-block">
              <div className="text-6xl mb-4 animate-pulse">⏳</div>
              <p className="text-white text-xl">Loading dashboard data...</p>
              <p className="text-blue-200 text-sm mt-2">Connecting to microservices on ports 8091 and 8092</p>
            </div>
          </div>
        ) : (
          <>
            {/* Statistics Cards Row */}
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
                icon="✅" 
              />
            </div>

            {/* Main Content Grid: Observations & Leaderboard */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              
              {/* Recent Observations (Takes 2/3 width) */}
              <div className="lg:col-span-2">
                <Card title="🕐 5 Most Recent Observations">
                  <ObservationList observations={observations} />
                </Card>
              </div>

              {/* Top Contributors Leaderboard (Takes 1/3 width) */}
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

export default RidwanDashboard;