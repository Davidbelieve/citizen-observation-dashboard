import { Link, useNavigate } from 'react-router-dom'
import { authAPI } from '../services/api'
import { RegionCard } from '../components/RegionCard'

export function Hub() {
  const navigate = useNavigate()

  const regions = [
    { id: 'north-east-england', name: 'North East England', color: '#3B82F6' },
    { id: 'north-west-england', name: 'North West England', color: '#EF4444' },
    { id: 'east-midlands', name: 'East Midlands', color: '#10B981' },
    { id: 'west-midlands', name: 'West Midlands', color: '#F59E0B' },
    { id: 'south-east-england', name: 'South East England', color: '#8B5CF6' },
    { id: 'yorkshire', name: 'Yorkshire and the Humber', color: '#EC4899' }
  ]

  const handleLogout = () => {
    authAPI.logout()
    navigate('/login')
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f9fafb' }}>
      {/* Header */}
      <div style={{ 
        backgroundColor: 'white', 
        borderBottom: '1px solid #e5e7eb',
        padding: '16px 20px'
      }}>
        <div style={{ 
          maxWidth: '1200px', 
          margin: '0 auto', 
          display: 'flex', 
          justifyContent: 'space-between', 
          alignItems: 'center' 
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <span style={{ fontSize: '32px' }}>💧</span>
            <h1 style={{ 
              fontSize: '24px', 
              fontWeight: 'bold', 
              color: '#111827', 
              margin: 0 
            }}>
              Citizen Observations
            </h1>
          </div>
          <button 
            onClick={handleLogout} 
            style={{ 
              padding: '10px 20px', 
              backgroundColor: 'white', 
              color: '#374151', 
              border: '1px solid #d1d5db', 
              borderRadius: '6px', 
              cursor: 'pointer', 
              fontSize: '14px', 
              fontWeight: '500',
              transition: 'all 0.2s'
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = '#f3f4f6'
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = 'white'
            }}
          >
            Logout
          </button>
        </div>
      </div>

      <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '40px 20px' }}>
        {/* Hero Section */}
        <div style={{ 
          textAlign: 'center', 
          marginBottom: '48px',
          padding: '40px 20px',
          backgroundColor: 'white',
          borderRadius: '16px',
          boxShadow: '0 1px 3px rgba(0,0,0,0.1)'
        }}>
          <h2 style={{ 
            fontSize: '36px', 
            fontWeight: 'bold', 
            color: '#111827', 
            margin: '0 0 16px 0' 
          }}>
            Community Water Quality Monitoring
          </h2>
          <p style={{ 
            fontSize: '18px', 
            color: '#6b7280', 
            maxWidth: '700px', 
            margin: '0 auto 24px auto',
            lineHeight: '1.6'
          }}>
             
            Your data helps identify pollution events and supports sustainable water management across England.
          </p>
          <button
            onClick={() => navigate('/submit')}
            style={{
              padding: '14px 32px',
              backgroundColor: '#2563eb',
              color: 'white',
              border: 'none',
              borderRadius: '8px',
              fontSize: '16px',
              fontWeight: '600',
              cursor: 'pointer',
              boxShadow: '0 4px 6px rgba(37, 99, 235, 0.3)',
              transition: 'all 0.3s'
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.backgroundColor = '#1d4ed8'
              e.currentTarget.style.transform = 'translateY(-2px)'
              e.currentTarget.style.boxShadow = '0 6px 12px rgba(37, 99, 235, 0.4)'
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.backgroundColor = '#2563eb'
              e.currentTarget.style.transform = 'translateY(0)'
              e.currentTarget.style.boxShadow = '0 4px 6px rgba(37, 99, 235, 0.3)'
            }}
          >
            📝 Submit Observation
          </button>
        </div>

        {/* Regional Dashboards Section */}
        <div style={{ marginBottom: '24px' }}>
          <h3 style={{ 
            fontSize: '24px', 
            fontWeight: '600', 
            color: '#111827', 
            marginBottom: '8px' 
          }}>
            Regional Dashboards
          </h3>
          <p style={{ color: '#6b7280', fontSize: '16px' }}>
            Select a region to view water quality data and community contributions
          </p>
        </div>

        {/* Region Cards Grid */}
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', 
          gap: '24px' 
        }}>
          {regions.map(region => (
            <Link 
              key={region.id} 
              to={`/dashboard/${region.id}`} 
              style={{ textDecoration: 'none' }}
            >
              <RegionCard
                region={region.name}
                color={region.color}
                onClick={() => {}}
              />
            </Link>
          ))}
        </div>
      </div>
    </div>
  )
}