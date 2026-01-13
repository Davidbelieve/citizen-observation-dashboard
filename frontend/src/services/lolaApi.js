import { authAPI } from './api'

/* 
 * North West England Region
 * Routes through authentication gateway: /api/regions/north-west-england/**
 * Gateway forwards to: http://localhost:8080/api
 */

const GATEWAY_BASE = '/api/regions/north-west-england'

// North west england postcodes prefixes
const North_WEST_POSTCODES = ['M', 'L', 'WA', 'CH', 'PR', 'FY', 'LA', 'BB', 'OL', 'BL', 'WN', 'SK']

// Check if postcode is in the north west region
function isNorthWestPostcode(postcode) {
    if (!postcode) return false
    const prefix = postcode.trim().toUpperCase().split(/\d/)[0]
    return North_WEST_POSTCODES.some(nw => prefix.startsWith(nw))
}

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

export const lolaAPI = {
  // Get total count of observations in North West
  async getTotalCount() {
    try {
      const response = await gatewayRequest('/observations')
      const data = await response.json()
      
      // Filter for only North West postcodes 
      const northWestObservations = data.filter(obs => 
        isNorthWestPostcode(obs.postcode)
      )
      
      return { count: northWestObservations.length }
    } catch (error) {
      console.error('Error fetching observation count:', error)
      throw error
    }
  },

  // Get recent observations from North West England
  // @param {number} limit - Number of observations to return the default is 5
  async getRecentObservations(limit = 5) {
    try {
      const response = await gatewayRequest('/observations')
      const data = await response.json()
      
      // Filter for North West postcodes
      const northWestObservations = data.filter(obs => 
        isNorthWestPostcode(obs.postcode)
      )
      
      // Sort by timestamp (most recent first)
      const sorted = northWestObservations.sort((a, b) => 
        new Date(b.submissionTimestamp) - new Date(a.submissionTimestamp)
      )
      
      // Take the most recent {limit} observations
      const recent = sorted.slice(0, limit)
      
      // Transform to match dashboard component expectations
      return {
        observations: recent.map(obs => ({
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

  // Get top contributors leaderboard for North West England
  // @param {number} limit - Number of contributors to return (default 3)
  async getLeaderboard(limit = 3) {
    try {
      const response = await gatewayRequest(`/rewards/leaderboard?limit=${limit}`)
      const data = await response.json()
      
      // Transform to match dashboard component expectations
      return {
        contributors: data.map(contributor => ({
          id: contributor.id,
          username: contributor.citizenId,
          points: contributor.totalPoints
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
      const response = await gatewayRequest('/observations', {
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
