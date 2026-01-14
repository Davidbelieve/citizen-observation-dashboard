import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { Hub } from './pages/Hub'
import { DashboardTemplate } from './pages/DashboardTemplate'
import { LolaDashboard } from './pages/LolaDashboard'
import { JasmineDashboard } from './pages/JasmineDashboard'
import { OluwabusolaDashboard } from './pages/OluwabusolaDashboard'
import { ProtectedRoute } from './components'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/hub" element={
          <ProtectedRoute>
            <Hub />
          </ProtectedRoute>
        } />
        <Route path="/dashboard/north-west-england" element={
          <ProtectedRoute>
            <LolaDashboard />
          </ProtectedRoute>
        } />
        <Route path="/dashboard/yorkshire" element={
          <ProtectedRoute>
            <JasmineDashboard />
          </ProtectedRoute>
        } />
        <Route path="/dashboard/south-east-england" element={
          <ProtectedRoute>
            <OluwabusolaDashboard />
          </ProtectedRoute>
        } />
        <Route path="/dashboard/:region" element={
          <ProtectedRoute>
            <DashboardTemplate />
          </ProtectedRoute>
        } />
        {/* Individual dashboard routes will be added here */}
      </Routes>
    </BrowserRouter>
  )
}
export default App