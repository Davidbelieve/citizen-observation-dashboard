import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { Hub } from './pages/Hub'
import { DashboardTemplate } from './pages/DashboardTemplate'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/hub" element={<Hub />} />
        
        {/* All regional dashboards use the template */}
        <Route path="/dashboard/:region" element={<DashboardTemplate />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App