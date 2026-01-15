import { useState, useEffect } from 'react'
import { Card, StatCard, ObservationList, ObservationTable, Leaderboard, Breadcrumb, ObservationForm } from '../components'
import { oluwabusolaAPI } from '../services/oluwabusolaApi'

export function OluwabusolaDashboard() {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [allObservations, setAllObservations] = useState({
    items: [],
    page: 0,
    size: 10,
    totalPages: 0,
    totalElements: 0
  })
  const [rewardSummary, setRewardSummary] = useState({
    totalContributors: 0,
    totalPoints: 0,
    warnings: [],
    summaries: []
  })
  const [data, setData] = useState({
    totalCount: 0,
    recentObservations: [],
    leaderboard: []
  })
  
  useEffect(() => {
    fetchDashboardData()
  }, [])

  useEffect(() => {
    if (allObservations.page !== 0) {
      fetchDashboardData()
    }
  }, [allObservations.page])
  
  const fetchDashboardData = async () => {
    setLoading(true)
    setError(null)
    
    try {
      const [countData, observationsData, rewardResult, allObservationsData] = await Promise.all([
        oluwabusolaAPI.getTotalCount(),
        oluwabusolaAPI.getRecentObservations(5),
        oluwabusolaAPI.calculateRewards(),
        oluwabusolaAPI.getAllObservations(allObservations.page, allObservations.size)
      ])
      
      const summaries = rewardResult?.summaries || []
      const sortedSummaries = [...summaries].sort((a, b) => b.totalPoints - a.totalPoints)
      const contributors = sortedSummaries.map(contributor => ({
        id: contributor.citizenId,
        username: contributor.citizenId,
        points: contributor.totalPoints,
        badge: contributor.badge
      }))
      const sortedRecentObservations = [...(observationsData.observations || [])].sort((a, b) => {
        const aTime = a.timestamp ? new Date(a.timestamp).getTime() : 0
        const bTime = b.timestamp ? new Date(b.timestamp).getTime() : 0
        return bTime - aTime
      })

      setData({
        totalCount: countData.count || 0,
        recentObservations: sortedRecentObservations,
        leaderboard: contributors
      })

      const totalPoints = summaries.reduce((sum, contributor) => sum + (contributor.totalPoints || 0), 0)
      setRewardSummary({
        totalContributors: summaries.length,
        totalPoints,
        warnings: rewardResult?.warnings || [],
        summaries: sortedSummaries
      })

      const sortedAllObservations = [...(allObservationsData.observations || [])].sort((a, b) => {
        const aTime = a.timestamp ? new Date(a.timestamp).getTime() : 0
        const bTime = b.timestamp ? new Date(b.timestamp).getTime() : 0
        return bTime - aTime
      })

      setAllObservations({
        items: sortedAllObservations,
        page: allObservationsData.page || 0,
        size: allObservationsData.size || allObservations.size,
        totalPages: allObservationsData.totalPages || 0,
        totalElements: allObservationsData.totalElements || 0
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
      <div className="oluwabusola-dashboard">
        <div className="dashboard-container loading-state">
          <p style={{ fontSize: '18px', color: '#6b7280' }}>
            Loading dashboard data...
          </p>
        </div>
      </div>
    )
  }
  
  if (error) {
    return (
      <div className="oluwabusola-dashboard">
        <div className="dashboard-container">
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

  const handleObservationPageChange = (nextPage) => {
    setAllObservations((prev) => ({
      ...prev,
      page: nextPage
    }))
  }

  const buildMeasurementChips = (measurements) => {
    if (!measurements) {
      return []
    }
    const chips = []
    if (measurements.temperature !== undefined && measurements.temperature !== null) {
      chips.push(`Temp ${measurements.temperature}°C`)
    }
    if (measurements.pH !== undefined && measurements.pH !== null) {
      chips.push(`pH ${measurements.pH}`)
    }
    if (measurements.alkalinity !== undefined && measurements.alkalinity !== null) {
      chips.push(`Alk ${measurements.alkalinity} mg/L`)
    }
    if (measurements.turbidity !== undefined && measurements.turbidity !== null) {
      chips.push(`Turb ${measurements.turbidity} NTU`)
    }
    return chips
  }

  const observationColumns = [
    {
      key: 'submitted',
      label: 'Submitted',
      render: (row) => (row.timestamp ? new Date(row.timestamp).toLocaleString() : '—')
    },
    {
      key: 'postcode',
      label: 'Postcode',
      accessor: 'postcode'
    },
    {
      key: 'citizenId',
      label: 'Citizen ID',
      render: (row) => <span className="cell-pill">{row.citizenId || '—'}</span>
    },
    {
      key: 'measurement',
      label: 'Measurement',
      render: (row) => {
        const chips = buildMeasurementChips(row.measurements)
        if (chips.length === 0) {
          return '—'
        }
        return (
          <div className="chip-list">
            {chips.map((chip) => (
              <span key={`${row.id}-${chip}`} className="chip">
                {chip}
              </span>
            ))}
          </div>
        )
      }
    }
  ]

  const contributorColumns = [
    {
      key: 'rank',
      label: 'Rank',
      render: (_row, _value, index) => index + 1
    },
    {
      key: 'citizenId',
      label: 'Citizen ID',
      render: (row) => <span className="cell-pill">{row.citizenId || '—'}</span>
    },
    {
      key: 'points',
      label: 'Points',
      accessor: 'totalPoints'
    }
  ]


  return (
    <div className="oluwabusola-dashboard">
      <style>{`
        .oluwabusola-dashboard {
          background-color: #f3f4f6;
          min-height: 100vh;
          padding: 16px;
        }
        .dashboard-container {
          max-width: 1200px;
          margin: 0 auto;
          display: flex;
          flex-direction: column;
          gap: 24px;
        }
        .loading-state {
          align-items: center;
          justify-content: center;
          min-height: 60vh;
          text-align: center;
        }
        .dashboard-header {
          display: flex;
          justify-content: space-between;
          align-items: flex-start;
          gap: 16px;
          flex-wrap: wrap;
        }
        .header-title {
          flex: 1 1 320px;
        }
        .stats-grid {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 16px;
        }
        .content-grid {
          display: grid;
          grid-template-columns: minmax(0, 2fr) minmax(0, 1fr);
          gap: 24px;
        }
        .actions-row {
          display: flex;
          flex-wrap: wrap;
          gap: 12px;
        }
        .actions-row button {
          width: fit-content;
        }
        .contributors-table {
          width: 100%;
          border-collapse: collapse;
        }
        .contributors-table th,
        .contributors-table td {
          text-align: left;
          padding: 10px 8px;
          border-bottom: 1px solid #e5e7eb;
          font-size: 14px;
        }
        .contributors-table th {
          color: #6b7280;
          font-weight: 600;
          text-transform: uppercase;
          letter-spacing: 0.04em;
          font-size: 12px;
        }
        .contributors-scroll {
          overflow-x: auto;
        }
        @media (max-width: 900px) {
          .content-grid {
            grid-template-columns: 1fr;
          }
        }
        @media (max-width: 640px) {
          .oluwabusola-dashboard {
            padding: 12px;
          }
          .stats-grid {
            grid-template-columns: 1fr;
          }
          .actions-row button {
            width: 100%;
          }
        }
      `}</style>
      <div className="dashboard-container">
      <Breadcrumb currentPage="Oluwabusola Dashboard" />
      <div className="dashboard-header">
        <div className="header-title">
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
      
      <div className="stats-grid">
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
        <StatCard 
          label="Reward Points Issued" 
          value={rewardSummary.totalPoints}
          icon="🏆"
        />
      </div>
      
      <div className="content-grid">
        <Card title="Recent Observations">
          <ObservationList observations={data.recentObservations} />
        </Card>
        
        <Card title="Top Contributors">
          <Leaderboard contributors={data.leaderboard} />
        </Card>
      </div>
      
      <div className="actions-row">
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
        
        <button 
          onClick={async () => {
            try {
              setLoading(true)
              await fetchDashboardData()
              alert('Rewards calculated successfully!')
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
      </div>

      <Card title="All Contributors">
        <ObservationTable
          observations={rewardSummary.summaries}
          columns={contributorColumns}
          emptyMessage="No contributor rewards yet. Calculate rewards to populate this list."
          rowKey={(row, index) => row.citizenId || index}
        />
      </Card>

      <Card title="All Observations">
        <ObservationTable
          observations={allObservations.items}
          columns={observationColumns}
          page={allObservations.page}
          totalPages={allObservations.totalPages}
          onPageChange={handleObservationPageChange}
        />
      </Card>

      <ObservationForm
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={handleSubmitObservation}
        loading={submitting}
      />
      </div>
    </div>
  )
}