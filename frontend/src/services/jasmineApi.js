import { authAPI } from './api'

/* 
 * Yorkshire Region
 * Routes through authentication gateway: /api/regions/yorkshire/**
 * Gateway forwards to: http://localhost:8086/citizenscience
 */

const GATEWAY_BASE = '/api/regions/yorkshire'

// Helper function to make authenticated requests through gateway
async function gatewayRequest(path, options = {}) {
  const token = authAPI.getToken()
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers
    },
    ...options
  }
  
  const response = await fetch(`${GATEWAY_BASE}${path}`, config)
  
  if (response.status === 401) {
    authAPI.logout()
    window.location.href = '/login'
    throw new Error('Session expired')
  }
  
  if (!response.ok) {
    const errorData = await response.json().catch(() => ({ 
      message: `HTTP error! status: ${response.status}` 
    }))
    throw new Error(errorData.message || errorData.error || `HTTP error! status: ${response.status}`)
  }
  
  return response
}

export const jasmineAPI = {
  // Get total count of observations in Yorkshire
  async getTotalObservations() {
    try {
      const response = await gatewayRequest('/crowd/count')
      const data = await response.json()
      return { count: data }
    } catch (error) {
      console.error('Cannot fetch total number of observations', error)
      throw error
    }
  },

  // Get the top five recent observations
  async getRecentObservations(limit = 5) {
    try {
      const response = await gatewayRequest('/crowd/top5')
      const data = await response.json()
      
      return {
        observations: data.map(obs => ({
          id: obs.dataID,
          postcode: obs.postcode,
          timestamp: obs.submissionTime,
          observation: obs.observations[0] || 'No observation recorded',
          measurements: {
            temperature: obs.temp,
            ph: obs.pH,
            alkalinity: obs.alkalinity,
            turbidity: obs.turbidity
          }
        }))
      }
    } catch (error) {
      console.error('Error fetching recent observations:', error)
      throw error
    }
  },

  // Get top contributors leaderboard
  // @param {number} limit - Number of contributors to return (default 3)
  async getLeaderboard(limit = 3) {
    try {
      const response = await gatewayRequest('/rewards/top3')
      const data = await response.json()
      
      return {
        contributors: data.map(contributor => ({
          id: contributor.id,
          username: 'citizen' + contributor.customerID,
          points: contributor.points
        }))
      }
    } catch (error) {
      console.error('Error fetching leaderboard:', error)
      throw error
    }
  },

  // Create a new observation
  async createObservation(observationData) {
    try {
      const response = await gatewayRequest('/crowd', {
        method: 'POST',
        body: JSON.stringify(observationData)
      })
      const data = await response.json()
      return data
    } catch (error) {
      console.error('Error creating observation:', error)
      throw error
    }
  }
} 