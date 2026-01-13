import { authAPI } from './api'

/* 
 * Oluwabusola's Individual Component
 * Routes through Spring Cloud Gateway (port 8090)
 * Gateway routes:
 * - /api/v1/observations/** → http://localhost:8091/api/v1/observations/** (crowdsourced service)
 * - /api/v1/rewards/** → http://localhost:8092/api/v1/rewards/** (reward service)
 */

const GATEWAY_BASE = '/api/v1'

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
    // Handle network errors (failed to fetch)
    if (error instanceof TypeError && error.message.includes('fetch')) {
      throw new Error(`Network error: Unable to connect to gateway. Please ensure the gateway service is running on port 8090. Original error: ${error.message}`)
    }
    throw error
  }
}

export const oluwabusolaAPI = {
  /**
   * Get total count of observations
   * @returns {Promise<{count: number}>}
   */
  async getTotalCount() {
    try {
      // Get first page to get total count
      const response = await gatewayRequest('/observations?page=0&size=1')
      const data = await response.json()
      
      // PagedResponse structure: { content: [], page, size, totalElements, totalPages }
      return { count: data.totalElements || 0 }
    } catch (error) {
      console.error('Error fetching observation count:', error)
      throw error
    }
  },

  /**
   * Get recent observations
   * @param {number} limit - Number of observations to return (default 5)
   * @returns {Promise<{observations: Array}>}
   */
  async getRecentObservations(limit = 5) {
    try {
      const response = await gatewayRequest(`/observations?page=0&size=${limit}`)
      const data = await response.json()
      
      // Transform PagedResponse to match dashboard component expectations
      const observations = (data.content || []).map(obs => ({
        id: obs.id,
        postcode: obs.postcode,
        timestamp: obs.submittedAt,
        observation: obs.notes || 'No observation recorded',
        citizenId: obs.citizenUniqueId,
        validated: obs.validated,
        measurements: obs.measurement ? {
          temperature: obs.measurement.temperatureC,
          pH: obs.measurement.pH,
          alkalinity: obs.measurement.alkalinityMgPerL,
          turbidity: obs.measurement.turbidityNtu
        } : null,
        tags: obs.tags || [],
        images: obs.images || []
      }))
      
      return { observations }
    } catch (error) {
      console.error('Error fetching recent observations:', error)
      throw error
    }
  },

  /**
   * Get all observations with pagination
   * @param {number} page - Page number (0-indexed)
   * @param {number} size - Page size (1-100)
   * @returns {Promise<{observations: Array, page: number, size: number, totalElements: number, totalPages: number}>}
   */
  async getAllObservations(page = 0, size = 20) {
    try {
      const response = await gatewayRequest(`/observations?page=${page}&size=${size}`)
      const data = await response.json()
      
      const observations = (data.content || []).map(obs => ({
        id: obs.id,
        postcode: obs.postcode,
        timestamp: obs.submittedAt,
        observation: obs.notes || 'No observation recorded',
        citizenId: obs.citizenUniqueId,
        validated: obs.validated,
        measurements: obs.measurement ? {
          temperature: obs.measurement.temperatureC,
          pH: obs.measurement.pH,
          alkalinity: obs.measurement.alkalinityMgPerL,
          turbidity: obs.measurement.turbidityNtu
        } : null,
        tags: obs.tags || [],
        images: obs.images || []
      }))
      
      return {
        observations,
        page: data.page || page,
        size: data.size || size,
        totalElements: data.totalElements || 0,
        totalPages: data.totalPages || 0
      }
    } catch (error) {
      console.error('Error fetching observations:', error)
      throw error
    }
  },

  /**
   * Get observation by ID
   * @param {string} id - Observation UUID
   * @returns {Promise<Object>}
   */
  async getObservationById(id) {
    try {
      const response = await gatewayRequest(`/observations/${id}`)
      const data = await response.json()
      
      return {
        id: data.id,
        postcode: data.postcode,
        timestamp: data.submittedAt,
        observation: data.notes || 'No observation recorded',
        citizenId: data.citizenUniqueId,
        validated: data.validated,
        measurements: data.measurement ? {
          temperature: data.measurement.temperatureC,
          pH: data.measurement.pH,
          alkalinity: data.measurement.alkalinityMgPerL,
          turbidity: data.measurement.turbidityNtu
        } : null,
        tags: data.tags || [],
        images: data.images || []
      }
    } catch (error) {
      console.error('Error fetching observation:', error)
      throw error
    }
  },

  /**
   * Create a new observation
   * @param {Object} observationData - Observation data
   * @returns {Promise<Object>}
   */
  async createObservation(observationData) {
    try {
      const response = await gatewayRequest('/observations', {
        method: 'POST',
        body: JSON.stringify(observationData)
      })
      const data = await response.json()
      
      // ApiResponse structure: { message: string, data: ObservationResponse }
      return data.data || data
    } catch (error) {
      console.error('Error creating observation:', error)
      throw error
    }
  },

  /**
   * Update an observation
   * @param {string} id - Observation UUID
   * @param {Object} observationData - Updated observation data
   * @returns {Promise<Object>}
   */
  async updateObservation(id, observationData) {
    try {
      const response = await gatewayRequest(`/observations/${id}`, {
        method: 'PUT',
        body: JSON.stringify(observationData)
      })
      return await response.json()
    } catch (error) {
      console.error('Error updating observation:', error)
      throw error
    }
  },

  /**
   * Delete an observation
   * @param {string} id - Observation UUID
   * @returns {Promise<boolean>}
   */
  async deleteObservation(id) {
    try {
      const response = await gatewayRequest(`/observations/${id}`, {
        method: 'DELETE'
      })
      return response.status === 204
    } catch (error) {
      console.error('Error deleting observation:', error)
      throw error
    }
  },

  /**
   * Get top contributors leaderboard
   * @param {number} limit - Number of contributors to return (default 3)
   * @returns {Promise<{contributors: Array}>}
   */
  async getLeaderboard(limit = 3) {
    try {
      const response = await gatewayRequest('/rewards')
      
      // Handle 204 No Content
      if (response.status === 204) {
        return { contributors: [] }
      }
      
      const data = await response.json()
      
      // Sort by totalPoints descending and take top N
      const sortedContributors = (data || [])
        .sort((a, b) => b.totalPoints - a.totalPoints)
        .slice(0, limit)
      
      // Transform to match dashboard component expectations
      return {
        contributors: sortedContributors.map(contributor => ({
          id: contributor.citizenId,
          username: contributor.citizenId,
          points: contributor.totalPoints,
          badge: contributor.badge
        }))
      }
    } catch (error) {
      console.error('Error fetching leaderboard:', error)
      throw error
    }
  },

  /**
   * Get reward summary for a specific citizen
   * @param {string} citizenId - Citizen unique identifier
   * @returns {Promise<Object>}
   */
  async getRewardByCitizen(citizenId) {
    try {
      const response = await gatewayRequest(`/rewards/${citizenId}`)
      const data = await response.json()
      
      return {
        citizenId: data.citizenId,
        totalPoints: data.totalPoints,
        badge: data.badge
      }
    } catch (error) {
      console.error('Error fetching reward:', error)
      throw error
    }
  },

  /**
   * Trigger reward calculation
   * @returns {Promise<Object>}
   */
  async calculateRewards() {
    try {
      const response = await gatewayRequest('/rewards/calculate', {
        method: 'POST'
      })
      return await response.json()
    } catch (error) {
      console.error('Error calculating rewards:', error)
      throw error
    }
  }
}