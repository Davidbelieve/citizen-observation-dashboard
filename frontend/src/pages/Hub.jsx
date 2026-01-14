import { Link, useNavigate } from 'react-router-dom'
import { authAPI } from '../services/api'
import { Card } from '../components'

export function Hub() {
  const navigate = useNavigate()

  const regions = [
    { id: 'north-east-england', name: 'North East England', icon: '📍', color: '#3B82F6' },
    { id: 'north-west-england', name: 'North West England', icon: '📍', color: '#EF4444' },
    { id: 'yorkshire', name: 'Yorkshire and the Humber', icon: '📍', color: '#EC4899' },
    { id: 'east-midlands', name: 'East Midlands', icon: '📍', color: '#10B981' },
    { id: 'west-midlands', name: 'West Midlands', icon: '📍', color: '#F59E0B' },
    { id: 'south-east-england', name: 'South East England', icon: '📍', color: '#8B5CF6' }
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
            Help protect public health and preserve aquatic ecosystems by contributing water quality observations. 
            Your data helps identify pollution events and supports sustainable water management across England.
          </p>
          
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
          gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', 
          gap: '24px' 
        }}>
          {regions.map(region => (
            <Link 
              key={region.id} 
              to={`/dashboard/${region.id}`} 
              style={{ textDecoration: 'none' }}
            >
              <Card>
                <div style={{ 
                  display: 'flex', 
                  alignItems: 'center', 
                  gap: '16px', 
                  padding: '16px' 
                }}>
                  <div style={{ 
                    fontSize: '48px',
                    width: '60px',
                    height: '60px',
                    borderRadius: '50%',
                    backgroundColor: `${region.color}20`,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    {region.icon}
                  </div>
                  <div style={{ flex: 1 }}>
                    <h3 style={{ 
                      fontSize: '20px', 
                      fontWeight: '600', 
                      color: '#111827', 
                      margin: '0 0 4px 0' 
                    }}>
                      {region.name}
                    </h3>
                    <p style={{ 
                      color: region.color, 
                      fontSize: '14px', 
                      margin: 0,
                      fontWeight: '500'
                    }}>
                      View regional data →
                    </p>
                  </div>
                </div>
              </Card>
            </Link>
          ))}
        </div>
      </div>
    </div>
  )
}