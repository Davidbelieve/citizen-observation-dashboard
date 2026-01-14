import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Card } from '../components/molecules/Card'
import { StatCard } from '../components/molecules/StatCard'
import { ObservationList } from '../components/organisms/ObservationList'
import { Leaderboard } from '../components/organisms/Leaderboard'

export function DashboardTemplate() {
  const { region } = useParams()
  const navigate = useNavigate()
  
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [data, setData] = useState({
    totalCount: 0,
    recentObservations: [],
    leaderboard: []
  })
  
  const getPortsForRegion = (regionName) => {
    const portConfig = {
      'north-east-england': { data: 8091, rewards: 8092 },
      'north-west-england': { data: 8093, rewards: 8094 },
      'east-midlands': { data: 8095, rewards: 8096 },
      'west-midlands': { data: 8097, rewards: 8098 },
      'south-east-england': { data: 8099, rewards: 8100 }
    }
    return portConfig[regionName] || { data: 8091, rewards: 8092 }
  }
  
  useEffect(() => {
    fetchDashboardData()
  }, [region])
  
  const fetchDashboardData = async () => {
    setLoading(true)
    setError(null)
    
    try {
      const ports = getPortsForRegion(region)
      const DATA_API = `http://localhost:${ports.data}`
      const REWARDS_API = `http://localhost:${ports.rewards}`
      
      console.log(`Fetching ${region} from ports ${ports.data}, ${ports.rewards}`)
      
      // Fetch observations
      const observationsResponse = await fetch(`${DATA_API}/api/observations`)
      if (!observationsResponse.ok) throw new Error(`Data service unavailable`)
      const observationsData = await observationsResponse.json()
      
      // Map observations with proper date conversion
      const mappedObservations = observationsData.map(obs => {
        let timestamp
        if (Array.isArray(obs.submissionTimestamp)) {
          // Convert Java LocalDateTime array [year, month, day, hour, min, sec, nano]
          timestamp = new Date(
            obs.submissionTimestamp[0],      // year
            obs.submissionTimestamp[1] - 1,  // month (0-indexed in JS)
            obs.submissionTimestamp[2],      // day
            obs.submissionTimestamp[3] || 0, // hour
            obs.submissionTimestamp[4] || 0, // minute
            obs.submissionTimestamp[5] || 0  // second
          )
        } else {
          timestamp = new Date(obs.submissionTimestamp)
        }
        
        return {
          id: obs.id,
          postcode: obs.postcode,
          citizenId: obs.citizenId,
          timestamp: timestamp.toISOString(),
          formattedTimestamp: timestamp.toLocaleDateString('en-GB', {
            day: '2-digit',
            month: 'short',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
          }),
          observation: obs.observations ? obs.observations.join(', ') : 'N/A',
          measurements: {
            temperature: obs.temperature,
            ph: obs.ph,
            turbidity: obs.turbidity,
            alkalinity: obs.alkalinity
          }
        }
      })
      
      const sortedObservations = mappedObservations.sort((a, b) => {
        return new Date(b.timestamp) - new Date(a.timestamp)
      })
      
      // Fetch leaderboard
      const leaderboardResponse = await fetch(`${REWARDS_API}/api/rewards/leaderboard?limit=3`)
      if (!leaderboardResponse.ok) throw new Error(`Rewards service unavailable`)
      const leaderboardData = await leaderboardResponse.json()
      
      // Map leaderboard to match component expectations
      const mappedLeaderboard = leaderboardData.map(contributor => ({
        id: contributor.citizenId,
        username: contributor.citizenId,
        points: contributor.totalPoints,
        totalPoints: contributor.totalPoints,
        count: contributor.totalObservations,
        totalObservations: contributor.totalObservations,
        rank: contributor.rank,
        badge: contributor.currentBadge
      }))
      
      setData({
        totalCount: observationsData.length,
        recentObservations: sortedObservations.slice(0, 5),
        leaderboard: mappedLeaderboard
      })
      
      console.log('✅ Data loaded:', {
        total: observationsData.length,
        observations: sortedObservations.length,
        leaderboard: mappedLeaderboard
      })
      
    } catch (err) {
      setError(err.message)
      console.error('Dashboard fetch error:', err)
    } finally {
      setLoading(false)
    }
  }
  
  if (loading) {
    return (
      <div style={{ 
        maxWidth: '1200px', 
        margin: '0 auto', 
        padding: '40px 20px',
        textAlign: 'center'
      }}>
        <p style={{ fontSize: '18px', color: '#6b7280' }}>
          Loading dashboard data...
        </p>
      </div>
    )
  }
  
  if (error) {
    return (
      <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px' }}>
        <Card>
          <div style={{ textAlign: 'center', padding: '20px' }}>
            <p style={{ color: '#ef4444', fontSize: '18px', marginBottom: '8px' }}>
              <strong>Error Loading Dashboard</strong>
            </p>
            <p style={{ color: '#6b7280' }}>{error}</p>
            <button 
              onClick={fetchDashboardData}
              style={{
                marginTop: '16px',
                padding: '10px 20px',
                backgroundColor: '#2563eb',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
            >
              Try Again
            </button>
          </div>
        </Card>
      </div>
    )
  }
  
  const regionName = region.replace(/-/g, ' ')
  
  return (
    <div style={{ 
      maxWidth: '1200px', 
      margin: '0 auto', 
      padding: '20px',
      backgroundColor: '#f3f4f6',
      minHeight: '100vh'
    }}>
      <div style={{ marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ 
            textTransform: 'capitalize', 
            marginBottom: '8px',
            fontSize: '32px',
            fontWeight: 'bold'
          }}>
            {regionName} Dashboard
          </h1>
          <p style={{ color: '#6b7280', marginBottom: '0' }}>
            Real-time citizen observation data
          </p>
        </div>
        <button
          onClick={() => navigate('/hub')}
          style={{
            padding: '10px 20px',
            backgroundColor: '#2563eb',
            color: 'white',
            border: 'none',
            borderRadius: '6px',
            cursor: 'pointer',
            fontWeight: '500'
          }}
        >
          ← Back to Hub
        </button>
      </div>
      
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '16px',
        marginBottom: '24px'
      }}>
        <StatCard 
          label="Total Observations" 
          value={data.totalCount}
          icon="📊"
        />
        <StatCard 
          label="Recent Submissions" 
          value={data.recentObservations.length}
          icon="📝"
        />
        <StatCard 
          label="Active Contributors" 
          value={data.leaderboard.length}
          icon="👥"
        />
      </div>
      
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: '2fr 1fr',
        gap: '24px',
        marginBottom: '24px'
      }}>
        <Card title="Recent Observations">
          <ObservationList observations={data.recentObservations} />
        </Card>
        
        <Card title="Top Contributors">
          <Leaderboard contributors={data.leaderboard} />
        </Card>
      </div>
      
      <button 
        onClick={fetchDashboardData}
        style={{
          padding: '10px 20px',
          backgroundColor: '#2563eb',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
          fontSize: '14px'
        }}
      >
        🔄 Refresh Data
      </button>
    </div>
  )
}