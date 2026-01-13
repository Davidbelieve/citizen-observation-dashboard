import { Navigate } from 'react-router-dom'
import { authAPI } from '../../services/api'

export function ProtectedRoute({ children }) {
  if (!authAPI.isAuthenticated()) {
    return <Navigate to="/login" replace />
  }
  
  return children
}

