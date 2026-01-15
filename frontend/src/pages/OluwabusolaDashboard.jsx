import { useState, useEffect } from 'react'
import { Card, StatCard, ObservationList, Leaderboard, Breadcrumb, ObservationForm } from '../components'
import { oluwabusolaAPI } from '../services/oluwabusolaApi'

export function OluwabusolaDashboard() {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [data, setData] = useState({
    totalCount: 0,
    recentObservations: [],
    leaderboard: []
  })
  
  useEffect(() => {
    fetchDashboardData()
  }, [])
  
  const fetchDashboardData = async () => {
    setLoading(true)
    setError(null)
    
    try {
      const [countData, observationsData, leaderboardData] = await Promise.all([
        oluwabusolaAPI.getTotalCount(),
        oluwabusolaAPI.getRecentObservations(5),
        oluwabusolaAPI.getLeaderboard(3)
      ])
      
      setData({
        totalCount: countData.count || 0,
        recentObservations: observationsData.observations || [],
        leaderboard: leaderboardData.contributors || []
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
  
  const handleSubmitObservation = async (observationData) => {
    setSubmitting(true)
    try {
      await oluwabusolaAPI.createObservation(observationData)
      alert('Observation submitted successfully!')
      fetchDashboardData() // Refresh the dashboard
    } catch (err) {
      alert('Error submitting observation: ' + err.message)
      throw err
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div style={{ 
      maxWidth: '1200px', 
      margin: '0 auto', 
      padding: '20px',
      backgroundColor: '#f3f4f6',
      minHeight: '100vh'
    }}>
      <Breadcrumb currentPage="Oluwabusola Dashboard" />
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px' }}>
        <div style={{ flex: 1 }}>
          <h1 style={{ marginBottom: '8px' }}>
            South East England Dashboard 💧📊
          </h1>
          <p style={{ color: '#6b7280', marginBottom: 0 }}>
            Water quality monitoring dashboard with crowdsourced observations and rewards
          </p>
        </div>
        <button
          onClick={() => setIsFormOpen(true)}
          style={{
            padding: '12px 24px',
            backgroundColor: '#10b981',
            color: 'white',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer',
            fontSize: '14px',
            fontWeight: '600',
            boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
            transition: 'background-color 0.2s',
            whiteSpace: 'nowrap'
          }}
          onMouseEnter={(e) => e.target.style.backgroundColor = '#059669'}
          onMouseLeave={(e) => e.target.style.backgroundColor = '#10b981'}
        >
          ➕ Add Observation
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
          fontSize: '14px',
          marginRight: '10px'
        }}
      >
        🔄 Refresh Data
      </button>
      
      <button 
        onClick={async () => {
          try {
            setLoading(true)
            await oluwabusolaAPI.calculateRewards()
            alert('Rewards calculated successfully!')
            fetchDashboardData()
          } catch (err) {
            alert('Error calculating rewards: ' + err.message)
          } finally {
            setLoading(false)
          }
        }}
        disabled={loading}
        style={{
          padding: '10px 20px',
          backgroundColor: '#10b981',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: loading ? 'not-allowed' : 'pointer',
          fontSize: '14px',
          opacity: loading ? 0.6 : 1
        }}
      >
        🏆 Calculate Rewards
      </button>

      <ObservationForm
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={handleSubmitObservation}
        loading={submitting}
      />
    </div>
  )
}