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