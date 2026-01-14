import { authAPI } from './api'

/* 
 * North West England Component
 * Routes through Authentication Service (port 8080)
 * Pattern: /api/regions/north-west-england/**
 */

const GATEWAY_BASE = '/api/regions/north-west-england'

// Helper function to make authenticated requests
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
  
  try {
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
  } catch (error) {
    if (error instanceof TypeError && error.message.includes('fetch')) {
      throw new Error(`Network error: Unable to connect to gateway (port 8080). Original error: ${error.message}`)
    }
    throw error
  }
}

export const lolaAPI = {
 //Get total count of observations

  async getTotalCount() {
    try {
      const response = await gatewayRequest('/observations')
      const data = await response.json()
      
      // Filter for North West postcodes
      const northWestObs = data.filter(obs => {
        const prefix = obs.postcode?.trim().toUpperCase().split(/\d/)[0]
        return ['M', 'L', 'WA', 'CH', 'PR', 'FY', 'LA', 'BB', 'OL', 'BL', 'WN', 'SK'].includes(prefix)
      })
      
      return { count: northWestObs.length }
    } catch (error) {
      console.error('Error fetching observation count:', error)
      throw error
    }
  },

  /**
   * Get recent observations
   */
  async getRecentObservations(limit = 5) {
    try {
      const response = await gatewayRequest('/observations')
      const data = await response.json()
      
      // Filter for North West postcodes
      const northWestObs = data.filter(obs => {
        const prefix = obs.postcode?.trim().toUpperCase().split(/\d/)[0]
        return ['M', 'L', 'WA', 'CH', 'PR', 'FY', 'LA', 'BB', 'OL', 'BL', 'WN', 'SK'].includes(prefix)
      })
      
      // Sort by timestamp
      const sorted = northWestObs.sort((a, b) => 
        new Date(b.submissionTimestamp) - new Date(a.submissionTimestamp)
      )
      
      return {
        observations: sorted.slice(0, limit).map(obs => ({
          id: obs.id,
          postcode: obs.postcode,
          timestamp: obs.submissionTimestamp,
          observation: obs.observations || 'No observation recorded',
          measurements: {
            temperature: obs.temperature,
            pH: obs.pH,
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

  /**
   * Get leaderboard
   */
  async getLeaderboard(limit = 3) {
    try {
      const response = await gatewayRequest('/rewards/leaderboard?limit=' + limit)
      const data = await response.json()
      
      return {
        contributors: data.map(contributor => ({
          id: contributor.citizenId,
          username: contributor.citizenId,
          points: contributor.totalPoints
        }))
      }
    } catch (error) {
      console.error('Error fetching leaderboard:', error)
      throw error
    }
  },
  /**
 * Submit a new observation
 */
  async createObservation(observationData) {
  try {
    // Transform from form format to YOUR backend format
    const transformedData = {
      citizenId: observationData.citizenId,
      postcode: observationData.postcode,
      temperature: observationData.measurement?.temperatureC || null,
      pH: observationData.measurement?.pH || null,
      alkalinity: observationData.measurement?.alkalinityMgPerL || null,
      turbidity: observationData.measurement?.turbidityNtu || null,
      observations: observationData.notes || observationData.observation || "CLEAR",
      images: observationData.images || null
    }
    
    const response = await gatewayRequest('/observations', {
      method: 'POST',
      body: JSON.stringify(transformedData)
    })
    const data = await response.json()
    return data
  } catch (error) {
    console.error('Error creating observation:', error)
    throw error
  }
}

}