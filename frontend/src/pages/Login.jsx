import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authAPI } from '../services/api'
import { Card } from '../components/Card'

export function Login() {
  const navigate = useNavigate()
  const [formData, setFormData] = useState({ username: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
    setError('')
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    if (!formData.username || !formData.password) {
      setError('Please enter both username and password')
      setLoading(false)
      return
    }
    try {
      await authAPI.login(formData.username, formData.password)
      navigate('/hub')
    } catch (err) {
      setError(err.message || 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f3f4f6', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px' }}>
      <div style={{ maxWidth: '400px', width: '100%' }}>
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <h1 style={{ fontSize: '32px', fontWeight: 'bold', color: '#111827', marginBottom: '8px' }}>Citizen Observations</h1>
          <p style={{ color: '#6b7280' }}>Sign in to access your dashboard</p>
        </div>
        <Card>
          <form onSubmit={handleSubmit}>
            <div style={{ marginBottom: '20px' }}>
              <label htmlFor="username" style={{ display: 'block', fontSize: '14px', fontWeight: '500', color: '#374151', marginBottom: '6px' }}>Username</label>
              <input id="username" name="username" type="text" value={formData.username} onChange={handleChange} placeholder="Enter your username" disabled={loading} style={{ width: '100%', padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: '6px', fontSize: '14px', outline: 'none', boxSizing: 'border-box' }} />
            </div>
            <div style={{ marginBottom: '20px' }}>
              <label htmlFor="password" style={{ display: 'block', fontSize: '14px', fontWeight: '500', color: '#374151', marginBottom: '6px' }}>Password</label>
              <input id="password" name="password" type="password" value={formData.password} onChange={handleChange} placeholder="Enter your password" disabled={loading} style={{ width: '100%', padding: '10px 12px', border: '1px solid #d1d5db', borderRadius: '6px', fontSize: '14px', outline: 'none', boxSizing: 'border-box' }} />
            </div>
            {error && <div style={{ padding: '12px', backgroundColor: '#fef2f2', border: '1px solid #fecaca', borderRadius: '6px', marginBottom: '20px' }}><p style={{ color: '#dc2626', fontSize: '14px', margin: 0 }}>{error}</p></div>}
            <button type="submit" disabled={loading} style={{ width: '100%', padding: '12px', backgroundColor: loading ? '#9ca3af' : '#2563eb', color: 'white', border: 'none', borderRadius: '6px', fontSize: '16px', fontWeight: '500', cursor: loading ? 'not-allowed' : 'pointer', marginBottom: '16px' }}>{loading ? 'Signing in...' : 'Sign In'}</button>
            <div style={{ textAlign: 'center' }}>
              <p style={{ color: '#6b7280', fontSize: '14px', margin: 0 }}>Don't have an account? <Link to="/register" style={{ color: '#2563eb', textDecoration: 'none', fontWeight: '500' }}>Register here</Link></p>
            </div>
          </form>
        </Card>
      </div>
    </div>
  )
}