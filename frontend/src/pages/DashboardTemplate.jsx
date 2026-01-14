
import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { Card, ObservationList, Leaderboard, ObservationForm } from '../components'
import { dashboardAPI } from '../services/api'

// Regional color scheme
const REGION_COLORS = {
  'north-east-england': '#3B82F6',
  'north-west-england': '#EF4444',
  'east-midlands': '#10B981',
  'west-midlands': '#F59E0B',
  'south-east-england': '#8B5CF6',
  'yorkshire': '#EC4899'
}

const REGION_NAMES = {
  'north-east-england': 'North East England',
  'north-west-england': 'North West England',
  'east-midlands': 'East Midlands',
  'west-midlands': 'West Midlands',
  'south-east-england': 'South East England',
  'yorkshire': 'Yorkshire and the Humber'
}

export function DashboardTemplate() {
  const { region } = useParams()
  const navigate = useNavigate()
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [data, setData] = useState({
    totalCount: 0,
    recentObservations: [],
    leaderboard: []
  })
  
  const PRIMARY_COLOR = REGION_COLORS[region] || '#6b7280'
  const regionName = REGION_NAMES[region] || region?.replace(/-/g, ' ')
  
  useEffect(() => {
    fetchDashboardData()
  }, [region])
  
  const fetchDashboardData = async () => {
    setLoading(true)
    setError(null)
    
    try {
      const [countData, observationsData, leaderboardData] = await Promise.all([
        dashboardAPI.getTotalCount(region),
        dashboardAPI.getRecentObservations(region, 5),
        dashboardAPI.getLeaderboard(region, 3)
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

  const handleSubmitObservation = async (observationData) => {
    setSubmitting(true)
    try {
      await dashboardAPI.createObservation(observationData)
      alert('Observation submitted successfully! 🎉')
      setIsFormOpen(false)
      fetchDashboardData()
    } catch (err) {
      alert('Error submitting observation: ' + err.message)
      throw err
    } finally {
      setSubmitting(false)
    }
  }
  
  if (loading) {
    return (
      <div style={{ 
        minHeight: '100vh',
        backgroundColor: '#f9fafb',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center'
      }}>
        <div style={{ textAlign: 'center' }}>
          <div style={{
            width: '48px',
            height: '48px',
            border: `4px solid ${PRIMARY_COLOR}30`,
            borderTop: `4px solid ${PRIMARY_COLOR}`,
            borderRadius: '50%',
            margin: '0 auto 16px',
            animation: 'spin 1s linear infinite'
          }} />
          <p style={{ fontSize: '18px', color: '#6b7280', margin: 0 }}>
            Loading {regionName} dashboard...
          </p>
          <style>
            {`@keyframes spin { to { transform: rotate(360deg); } }`}
          </style>
        </div>
      </div>
    )
  }
  
  if (error) {
    return (
      <div style={{ 
        minHeight: '100vh',
        backgroundColor: '#f9fafb',
        padding: '40px 20px'
      }}>
        <div style={{ maxWidth: '600px', margin: '0 auto' }}>
          <Card>
            <div style={{ textAlign: 'center', padding: '32px' }}>
              <div style={{
                width: '64px',
                height: '64px',
                backgroundColor: '#FEE2E2',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 16px',
                fontSize: '32px'
              }}>
                ⚠️
              </div>
              <h3 style={{ 
                color: '#DC2626', 
                fontSize: '20px', 
                marginBottom: '8px',
                fontWeight: '600'
              }}>
                Error Loading Dashboard
              </h3>
              <p style={{ color: '#6b7280', marginBottom: '24px' }}>{error}</p>
              <button
                onClick={fetchDashboardData}
                style={{
                  padding: '12px 24px',
                  backgroundColor: PRIMARY_COLOR,
                  color: 'white',
                  border: 'none',
                  borderRadius: '8px',
                  fontSize: '15px',
                  fontWeight: '600',
                  cursor: 'pointer',
                  transition: 'all 0.3s ease'
                }}
                onMouseEnter={(e) => {
                  e.currentTarget.style.transform = 'translateY(-2px)'
                  e.currentTarget.style.boxShadow = `0 6px 12px ${PRIMARY_COLOR}50`
                }}
                onMouseLeave={(e) => {
                  e.currentTarget.style.transform = 'translateY(0)'
                  e.currentTarget.style.boxShadow = 'none'
                }}
              >
                🔄 Try Again
              </button>
            </div>
          </Card>
        </div>
      </div>
    )
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f9fafb' }}>
      {/* Header with Breadcrumb */}
      <div style={{ 
        backgroundColor: 'white',
        borderBottom: '1px solid #e5e7eb',
        padding: '16px 0'
      }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '0 20px' }}>
          <div style={{ 
            display: 'flex', 
            alignItems: 'center', 
            gap: '8px',
            marginBottom: '16px',
            fontSize: '14px',
            color: '#6b7280'
          }}>
            <button
              onClick={() => navigate('/hub')}
              style={{
                background: 'none',
                border: 'none',
                color: PRIMARY_COLOR,
                cursor: 'pointer',
                padding: '4px 8px',
                borderRadius: '4px',
                transition: 'background-color 0.2s',
                fontWeight: '500'
              }}
              onMouseEnter={(e) => e.currentTarget.style.backgroundColor = `${PRIMARY_COLOR}10`}
              onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
            >
              Home
            </button>
            <span>/</span>
            <span style={{ color: '#111827', fontWeight: '500' }}>{regionName} Dashboard</span>
          </div>
          
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <h1 style={{ 
                fontSize: '32px',
                fontWeight: 'bold',
                color: '#111827',
                margin: '0 0 8px 0',
                display: 'flex',
                alignItems: 'center',
                gap: '12px'
              }}>
                <span style={{
                  fontSize: '36px',
                  filter: `drop-shadow(0 2px 4px ${PRIMARY_COLOR}40)`
                }}>
                  💧
                </span>
                {regionName}
              </h1>
              <p style={{ 
                color: '#6b7280',
                margin: 0,
                fontSize: '16px'
              }}>
                Real-time citizen water quality observation data
              </p>
            </div>
            <button
              onClick={() => setIsFormOpen(true)}
              disabled={submitting}
              style={{
                padding: '12px 24px',
                backgroundColor: PRIMARY_COLOR,
                color: 'white',
                border: 'none',
                borderRadius: '8px',
                fontSize: '15px',
                fontWeight: '600',
                cursor: submitting ? 'not-allowed' : 'pointer',
                opacity: submitting ? 0.6 : 1,
                transition: 'all 0.3s ease',
                boxShadow: `0 4px 6px ${PRIMARY_COLOR}40`
              }}
              onMouseEnter={(e) => {
                if (!submitting) {
                  e.currentTarget.style.transform = 'translateY(-2px)'
                  e.currentTarget.style.boxShadow = `0 6px 12px ${PRIMARY_COLOR}50`
                }
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.transform = 'translateY(0)'
                e.currentTarget.style.boxShadow = `0 4px 6px ${PRIMARY_COLOR}40`
              }}
            >
              📝 Add Observation
            </button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '32px 20px' }}>
        
        {/* Statistics Cards */}
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
          gap: '20px',
          marginBottom: '32px'
        }}>
          <div style={{
            backgroundColor: 'white',
            padding: '24px',
            borderRadius: '12px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
            border: `2px solid ${PRIMARY_COLOR}20`,
            transition: 'all 0.3s ease'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.transform = 'translateY(-4px)'
            e.currentTarget.style.boxShadow = `0 8px 16px ${PRIMARY_COLOR}30`
            e.currentTarget.style.borderColor = PRIMARY_COLOR
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.transform = 'translateY(0)'
            e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)'
            e.currentTarget.style.borderColor = `${PRIMARY_COLOR}20`
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{
                width: '56px',
                height: '56px',
                borderRadius: '12px',
                backgroundColor: `${PRIMARY_COLOR}15`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '28px'
              }}>
                📊
              </div>
              <div>
                <p style={{ 
                  margin: '0 0 4px 0',
                  fontSize: '13px',
                  fontWeight: '500',
                  color: '#6b7280',
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px'
                }}>
                  Total Observations
                </p>
                <p style={{ 
                  margin: 0,
                  fontSize: '32px',
                  fontWeight: 'bold',
                  color: PRIMARY_COLOR
                }}>
                  {data.totalCount}
                </p>
              </div>
            </div>
          </div>

          <div style={{
            backgroundColor: 'white',
            padding: '24px',
            borderRadius: '12px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
            border: `2px solid ${PRIMARY_COLOR}20`,
            transition: 'all 0.3s ease'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.transform = 'translateY(-4px)'
            e.currentTarget.style.boxShadow = `0 8px 16px ${PRIMARY_COLOR}30`
            e.currentTarget.style.borderColor = PRIMARY_COLOR
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.transform = 'translateY(0)'
            e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)'
            e.currentTarget.style.borderColor = `${PRIMARY_COLOR}20`
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{
                width: '56px',
                height: '56px',
                borderRadius: '12px',
                backgroundColor: `${PRIMARY_COLOR}15`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '28px'
              }}>
                📝
              </div>
              <div>
                <p style={{ 
                  margin: '0 0 4px 0',
                  fontSize: '13px',
                  fontWeight: '500',
                  color: '#6b7280',
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px'
                }}>
                  Recent Submissions
                </p>
                <p style={{ 
                  margin: 0,
                  fontSize: '32px',
                  fontWeight: 'bold',
                  color: PRIMARY_COLOR
                }}>
                  {data.recentObservations.length}
                </p>
              </div>
            </div>
          </div>

          <div style={{
            backgroundColor: 'white',
            padding: '24px',
            borderRadius: '12px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
            border: `2px solid ${PRIMARY_COLOR}20`,
            transition: 'all 0.3s ease'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.transform = 'translateY(-4px)'
            e.currentTarget.style.boxShadow = `0 8px 16px ${PRIMARY_COLOR}30`
            e.currentTarget.style.borderColor = PRIMARY_COLOR
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.transform = 'translateY(0)'
            e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)'
            e.currentTarget.style.borderColor = `${PRIMARY_COLOR}20`
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{
                width: '56px',
                height: '56px',
                borderRadius: '12px',
                backgroundColor: `${PRIMARY_COLOR}15`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '28px'
              }}>
                👥
              </div>
              <div>
                <p style={{ 
                  margin: '0 0 4px 0',
                  fontSize: '13px',
                  fontWeight: '500',
                  color: '#6b7280',
                  textTransform: 'uppercase',
                  letterSpacing: '0.5px'
                }}>
                  Active Contributors
                </p>
                <p style={{ 
                  margin: 0,
                  fontSize: '32px',
                  fontWeight: 'bold',
                  color: PRIMARY_COLOR
                }}>
                  {data.leaderboard.length}
                </p>
              </div>
            </div>
          </div>
        </div>
        
        {/* Main Content Grid */}
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: '2fr 1fr',
          gap: '24px',
          marginBottom: '24px'
        }}>
          <div style={{
            backgroundColor: 'white',
            borderRadius: '12px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
            overflow: 'hidden',
            border: '1px solid #e5e7eb',
            transition: 'box-shadow 0.3s ease'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.boxShadow = '0 8px 16px rgba(0,0,0,0.1)'
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)'
          }}>
            <div style={{
              padding: '20px 24px',
              borderBottom: '1px solid #e5e7eb',
              background: `linear-gradient(135deg, ${PRIMARY_COLOR}08 0%, transparent 100%)`
            }}>
              <h2 style={{ 
                margin: 0,
                fontSize: '20px',
                fontWeight: '600',
                color: '#111827'
              }}>
                📋 Recent Observations
              </h2>
            </div>
            <div style={{ padding: '24px' }}>
              <ObservationList observations={data.recentObservations} />
            </div>
          </div>
          
          <div style={{
            backgroundColor: 'white',
            borderRadius: '12px',
            boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
            overflow: 'hidden',
            border: '1px solid #e5e7eb',
            transition: 'box-shadow 0.3s ease'
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.boxShadow = '0 8px 16px rgba(0,0,0,0.1)'
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)'
          }}>
            <div style={{
              padding: '20px 24px',
              borderBottom: '1px solid #e5e7eb',
              background: `linear-gradient(135deg, ${PRIMARY_COLOR}08 0%, transparent 100%)`
            }}>
              <h2 style={{ 
                margin: 0,
                fontSize: '20px',
                fontWeight: '600',
                color: '#111827'
              }}>
                🏆 Top Contributors
              </h2>
            </div>
            <div style={{ padding: '24px' }}>
              <Leaderboard contributors={data.leaderboard} />
            </div>
          </div>
        </div>
        
        {/* Action Button */}
        <div>
          <button
            onClick={fetchDashboardData}
            disabled={loading}
            style={{
              padding: '12px 24px',
              backgroundColor: 'white',
              color: '#374151',
              border: '2px solid #e5e7eb',
              borderRadius: '8px',
              fontSize: '15px',
              fontWeight: '600',
              cursor: loading ? 'not-allowed' : 'pointer',
              opacity: loading ? 0.6 : 1,
              transition: 'all 0.3s ease',
              boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
            }}
            onMouseEnter={(e) => {
              if (!loading) {
                e.currentTarget.style.transform = 'translateY(-2px)'
                e.currentTarget.style.boxShadow = '0 4px 6px rgba(0,0,0,0.15)'
                e.currentTarget.style.borderColor = '#9ca3af'
              }
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'translateY(0)'
              e.currentTarget.style.boxShadow = '0 1px 3px rgba(0,0,0,0.1)'
              e.currentTarget.style.borderColor = '#e5e7eb'
            }}
          >
            🔄 Refresh Data
          </button>
        </div>
      </div>

      {/* Observation Form Modal */}
      <ObservationForm
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={handleSubmitObservation}
        loading={submitting}
      />
    </div>
  )
}






/*import { useState, useEffect } from 'react'
import { useParams } from 'react-router-dom'
import { Card, StatCard, ObservationList, Leaderboard, Breadcrumb, ObservationForm } from '../components'
import { dashboardAPI } from '../services/api'

export function DashboardTemplate() {
  const { region } = useParams()
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
  }, [region])
  
  const fetchDashboardData = async () => {
    setLoading(true)
    setError(null)
    
    try {
      const [countData, observationsData, leaderboardData] = await Promise.all([
        dashboardAPI.getTotalCount(region),
        dashboardAPI.getRecentObservations(region, 5),
        dashboardAPI.getLeaderboard(region, 3)
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
  
  const regionName =  region.replace(/-/g, ' ')

  const handleSubmitObservation = async (observationData) => {
    setSubmitting(true)
    try {
      await dashboardAPI.createObservation(region, observationData)
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
      <Breadcrumb currentPage={`${regionName} Dashboard`} />
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px' }}>
        <div style={{ flex: 1 }}>
          <h1 style={{ 
            textTransform: 'capitalize', 
            marginBottom: '8px' 
          }}>
            {regionName} Dashboard
          </h1>
          <p style={{ color: '#6b7280', marginBottom: 0 }}>
            Real-time citizen observation data
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
}*/