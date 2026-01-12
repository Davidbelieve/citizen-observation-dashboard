import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { Hub }from './pages/Hub'
import { DashboardTemplate}  from './pages/DashboardTemplate'
import { LolaDashboard } from './pages/LolaDashboard'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element ={<Navigate to="/login" />} />
        <Route path="/login" element ={<Login />} />
        <Route path="/register" element ={<Register />} />
        <Route path="/hub" element ={<Hub />} />
        <Route path="/dashboard/north-west-england" element={<LolaDashboard />} />
        <Route path="/dashboard/:region" element ={<DashboardTemplate />} />
        {/* Individual dashboard routes will be added here */}
      </Routes>
   </BrowserRouter>
  )
}

export default App

/*import { Card } from './components/Card'
import { StatCard } from './components/StatCard'
import { ObservationList } from './components/ObservationList'
import { Leaderboard } from './components/Leaderboard'

// Mock data for testing
const mockObservations = [
  {
    id: 1,
    postcode: 'NE1 4ST',
    timestamp: '2024-12-09T10:30:00Z',
    observation: 'Sunny with light clouds',
    measurements: { temperature: 15.5, humidity: 67 }
  },
  {
    id: 2,
    postcode: 'NE2 1AB',
    timestamp: '2024-12-09T09:15:00Z',
    observation: 'Overcast and breezy',
    measurements: { temperature: 12.3, humidity: 72 }
  },
  {
    id: 3,
    postcode: 'NE3 2CD',
    timestamp: '2024-12-08T14:20:00Z',
    observation: 'Light rain',
    measurements: { temperature: 10.8, humidity: 85 }
  }
]

const mockContributors = [
  { id: 1, username: 'observer123', points: 150 },
  { id: 2, username: 'weatherwatcher', points: 120 },
  { id: 3, username: 'climatetracker', points: 95 }
]

function App() {
  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px', backgroundColor: '#f3f4f6', minHeight: '100vh' }}>
      <h1 style={{ marginBottom: '8px' }}>Citizen Observation Dashboard</h1>
      <p style={{ color: '#6b7280', marginBottom: '32px' }}>UI Components Test</p>
      
      <h2 style={{ marginBottom: '16px' }}>Stats Overview</h2>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px', marginBottom: '32px' }}>
        <StatCard label="Total Observations" value={42} icon="📊" />
        <StatCard label="Recent Submissions" value={5} icon="📝" />
        <StatCard label="Active Contributors" value={12} icon="👥" />
      </div>
      
      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px' }}>
        <Card title="Recent Observations">
          <ObservationList observations={mockObservations} />
        </Card>
        
        <Card title="Top Contributors">
          <Leaderboard contributors={mockContributors} />
        </Card>
      </div>
    </div>
  )
}

export default App*/
