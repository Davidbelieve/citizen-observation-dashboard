import { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { Card, StatCard, ObservationList, Leaderboard, Breadcrumb, ObservationForm } from '../components'
import { jasmineAPI } from '../services/jasmineApi'

export function JasmineDashboard() {
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
          jasmineAPI.getTotalObservations(),
          jasmineAPI.getRecentObservations(5),
          jasmineAPI.getLeaderboard(3)
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
          Loading Yorkshire and the Humber dashboard data...
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
      await jasmineAPI.createObservation(observationData)
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
      backgroundColor: '#CCD6E6',
      minHeight: '100vh'
    }}>
      <Breadcrumb currentPage="Yorkshire and the Humber Dashboard" />
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px' }}>
        <div style={{ flex: 1 }}>
          <h1 style={{ marginBottom: '8px' }}>
            Yorkshire and the Humber Dashboard 🏵🐑
          </h1>
          <p style={{ color: '#41414D', marginBottom: 0 }}>
            Water quality monitoring for North Yorkshire, East Yorkshire, Northern Lincolnshire, West Yorkshire, and South Yorkshire
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
          fontSize: '14px'
        }}
      >
        🔄 Refresh Data
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