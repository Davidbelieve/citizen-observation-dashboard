import { Link, useNavigate } from 'react-router-dom'
import { authAPI } from '../services/api'
import { Card } from '../components/Card'

export function Hub() {
  const navigate = useNavigate()

  const regions = [
    { id: 'north-east-england', name: 'North East England', icon: '📍' },
    { id: 'north-west-england', name: 'North West England', icon: '📍' },
    { id: 'east-midlands', name: 'East Midlands', icon: '📍' },
    { id: 'west-midlands', name: 'West Midlands', icon: '📍' },
    { id: 'south-east-england', name: 'South East England', icon: '📍' }
  ]

  const handleLogout = () => {
    authAPI.logout()
    navigate('/login')
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f3f4f6', padding: '20px' }}>
      <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '32px' }}>
        <div>
          <h1 style={{ fontSize: '32px', fontWeight: 'bold', color: '#111827', marginBottom: '4px' }}>Dashboard Hub</h1>
          <p style={{ color: '#6b7280' }}>Select a region to view citizen observations</p>
        </div>
        <button onClick={handleLogout} style={{ padding: '10px 20px', backgroundColor: 'white', color: '#374151', border: '1px solid #d1d5db', borderRadius: '6px', cursor: 'pointer', fontSize: '14px', fontWeight: '500' }}>Logout</button>
      </div>
      <div style={{ maxWidth: '1200px', margin: '0 auto', display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '24px' }}>
        {regions.map(region => (
          <Link key={region.id} to={`/dashboard/${region.id}`} style={{ textDecoration: 'none' }}>
            <Card>
              <div style={{ display: 'flex', alignItems: 'center', gap: '16px', padding: '16px' }}>
                <div style={{ fontSize: '48px' }}>{region.icon}</div>
                <div style={{ flex: 1 }}>
                  <h3 style={{ fontSize: '20px', fontWeight: '600', color: '#111827', margin: '0 0 4px 0' }}>{region.name}</h3>
                  <p style={{ color: '#6b7280', fontSize: '14px', margin: 0 }}>View regional data →</p>
                </div>
              </div>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  )
}