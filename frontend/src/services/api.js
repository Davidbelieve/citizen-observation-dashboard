// Mock mode - set to false when real backend is ready
const USE_MOCK_DATA = true

// Unified API Base URL - All requests go through authentication gateway
const API_BASE_URL = '/api'
// Mock data storage (simulates a database)
const mockUsers = [
  { username: 'demo', password: 'demo123' }
]

const mockDashboardData = {
  observations: [
    {
      id: 1,
      postcode: 'NE1 4ST',
      timestamp: '2024-12-09T10:30:00Z',
      observation: 'Clear skies with moderate temperature',
      measurements: { temperature: 15.5, humidity: 67 }
    },
    {
      id: 2,
      postcode: 'NE2 1AB',
      timestamp: '2024-12-09T09:15:00Z',
      observation: 'Overcast with light breeze',
      measurements: { temperature: 12.3, humidity: 72 }
    },
    {
      id: 3,
      postcode: 'NE3 2CD',
      timestamp: '2024-12-08T14:20:00Z',
      observation: 'Light rain with high humidity',
      measurements: { temperature: 10.8, humidity: 85 }
    },
    {
      id: 4,
      postcode: 'NE4 5EF',
      timestamp: '2024-12-08T11:45:00Z',
      observation: 'Sunny with warm conditions',
      measurements: { temperature: 18.2, humidity: 55 }
    },
    {
      id: 5,
      postcode: 'NE5 6GH',
      timestamp: '2024-12-07T16:30:00Z',
      observation: 'Foggy morning conditions',
      measurements: { temperature: 8.5, humidity: 92 }
    }
  ],
  contributors: [
    { id: 1, username: 'observer123', points: 150 },
    { id: 2, username: 'weatherwatcher', points: 120 },
    { id: 3, username: 'climatetracker', points: 95 },
    { id: 4, username: 'enviro_monitor', points: 78 },
    { id: 5, username: 'data_collector', points: 65 }
  ]
}

// Simulate API delay
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms))

// Mock Authentication API
const mockAuthAPI = {
  register: async (username, password) => {
    await delay(500)
    
    if (mockUsers.find(u => u.username === username)) {
      throw new Error('Username already exists')
    }
    
    mockUsers.push({ username, password })
    return { success: true, message: 'Registration successful' }
  },
  
  login: async (username, password) => {
    await delay(500)
    
    const user = mockUsers.find(u => u.username === username && u.password === password)
    
    if (!user) {
      throw new Error('Invalid username or password')
    }
    
    const token = 'mock_token_' + Math.random().toString(36).substr(2, 9)
    localStorage.setItem('authToken', token)
    localStorage.setItem('username', username)
    
    return { token, username }
  },
  
  logout: () => {
    localStorage.removeItem('authToken')
    localStorage.removeItem('username')
  },
  
  isAuthenticated: () => {
    return !!localStorage.getItem('authToken')
  },
  
  getToken: () => {
    return localStorage.getItem('authToken')
  }
}

// Mock Dashboard API
const mockDashboardAPI = {
  getTotalCount: async (region) => {
    await delay(300)
    return { count: mockDashboardData.observations.length }
  },
  
  getRecentObservations: async (region, limit = 5) => {
    await delay(400)
    return { 
      observations: mockDashboardData.observations.slice(0, limit) 
    }
  },
  
  getLeaderboard: async (region, limit = 3) => {
    await delay(350)
    return { 
      contributors: mockDashboardData.contributors.slice(0, limit) 
    }
  }
}

// Real API helper - Enhanced to always include auth token
async function apiRequest(url, options = {}) {
  const token = localStorage.getItem('authToken')
  
  if (!token && !options.skipAuth) {
    // Redirect to login if no token (unless explicitly skipping auth)
    throw new Error('Not authenticated. Please login.')
  }
  
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` }),
      ...options.headers
    },
    ...options
  }
  
  try {
    const response = await fetch(url, config)
    
    // Handle 401 Unauthorized - token expired or invalid
    if (response.status === 401) {
      localStorage.removeItem('authToken')
      localStorage.removeItem('username')
      window.location.href = '/login'
      throw new Error('Session expired. Please login again.')
    }
    
    if (!response.ok) {
      const error = await response.json().catch(() => ({ 
        message: 'Request failed' 
      }))
      throw new Error(error.message || `HTTP Error: ${response.status}`)
    }
    
    return await response.json()
  } catch (error) {
    console.error('API Request failed:', error)
    throw error
  }
}

// Real Authentication API - Uses unified endpoint
const realAuthAPI = {
  register: async (username, password) => {
    return apiRequest(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      body: JSON.stringify({ username, password }),
      skipAuth: true // Registration doesn't need auth
    })
  },
  
  login: async (username, password) => {
    const response = await apiRequest(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      body: JSON.stringify({ username, password }),
      skipAuth: true // Login doesn't need auth
    })
    
    if (response.token) {
      localStorage.setItem('authToken', response.token)
      localStorage.setItem('username', username)
    }
    
    return response
  },
  
  logout: () => {
    localStorage.removeItem('authToken')
    localStorage.removeItem('username')
  },
  
  isAuthenticated: () => {
    return !!localStorage.getItem('authToken')
  },
  
  getToken: () => {
    return localStorage.getItem('authToken')
  }
}

// Real Dashboard API - Routes through gateway
const realDashboardAPI = {
  getTotalCount: async (region) => {
    try {
      // Route through gateway: /api/regions/{region}/observations/count
      const response = await apiRequest(
        `${API_BASE_URL}/regions/${region}/observations/count?region=${region}`
      )
      return response
    } catch (error) {
      console.error('Error fetching count:', error)
      return { count: 0 }
    }
  },
  
  getRecentObservations: async (region, limit = 5) => {
    try {
      // Route through gateway: /api/regions/{region}/observations/recent
      const response = await apiRequest(
        `${API_BASE_URL}/regions/${region}/observations/recent?region=${region}&limit=${limit}`
      )
      return response
    } catch (error) {
      console.error('Error fetching observations:', error)
      return { observations: [] }
    }
  },
  
  getLeaderboard: async (region, limit = 3) => {
    try {
      // Route through gateway: /api/regions/{region}/contributors/leaderboard
      const response = await apiRequest(
        `${API_BASE_URL}/regions/${region}/contributors/leaderboard?region=${region}&limit=${limit}`
      )
      return response
    } catch (error) {
      console.error('Error fetching leaderboard:', error)
      return { contributors: [] }
    }
  },

  createObservation: async (region, observationData) => {
    try {
      // Route through gateway: /api/regions/{region}/observations
      const response = await apiRequest(
        `${API_BASE_URL}/regions/${region}/observations`,
        {
          method: 'POST',
          body: JSON.stringify(observationData)
        }
      )
      return response
    } catch (error) {
      console.error('Error creating observation:', error)
      throw error
    }
  }
}

// Export the appropriate API based on mode
export const authAPI = USE_MOCK_DATA ? mockAuthAPI : realAuthAPI
export const dashboardAPI = USE_MOCK_DATA ? mockDashboardAPI : realDashboardAPI

export default { authAPI, dashboardAPI }