// Mock mode - set to false when real backend is ready
const USE_MOCK_DATA = true

const API_BASE_URL = 'http://localhost:8080/api'

const REGION_API_URLS = {
  'north-east-england': 'http://localhost:8081',
  'north-west-england': 'http://localhost:8082',
  'east-midlands': 'http://localhost:8083',
  'west-midlands': 'http://localhost:8084',
  'south-east-england': 'http://localhost:8085'
}

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

// Real API helper
async function apiRequest(url, options = {}) {
  const token = localStorage.getItem('authToken')
  
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

// Real Authentication API
const realAuthAPI = {
  register: async (username, password) => {
    return apiRequest(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      body: JSON.stringify({ username, password })
    })
  },
  
  login: async (username, password) => {
    const response = await apiRequest(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      body: JSON.stringify({ username, password })
    })
    
    if (response.token) {
      localStorage.setItem('authToken', response.token)
    }
    
    return response
  },
  
  logout: () => {
    localStorage.removeItem('authToken')
  },
  
  isAuthenticated: () => {
    return !!localStorage.getItem('authToken')
  }
}

// Real Dashboard API
const realDashboardAPI = {
  getTotalCount: async (region) => {
    const baseUrl = REGION_API_URLS[region]
    
    if (!baseUrl) {
      throw new Error(`Unknown region: ${region}`)
    }
    
    try {
      const response = await fetch(`${baseUrl}/observations/count?region=${region}`)
      if (!response.ok) throw new Error('Failed to fetch count')
      return await response.json()
    } catch (error) {
      console.error('Error fetching count:', error)
      return { count: 0 }
    }
  },
  
  getRecentObservations: async (region, limit = 5) => {
    const baseUrl = REGION_API_URLS[region]
    
    if (!baseUrl) {
      throw new Error(`Unknown region: ${region}`)
    }
    
    try {
      const response = await fetch(`${baseUrl}/observations/recent?region=${region}&limit=${limit}`)
      if (!response.ok) throw new Error('Failed to fetch observations')
      return await response.json()
    } catch (error) {
      console.error('Error fetching observations:', error)
      return { observations: [] }
    }
  },
  
  getLeaderboard: async (region, limit = 3) => {
    const baseUrl = REGION_API_URLS[region]
    
    if (!baseUrl) {
      throw new Error(`Unknown region: ${region}`)
    }
    
    try {
      const response = await fetch(`${baseUrl}/contributors/leaderboard?region=${region}&limit=${limit}`)
      if (!response.ok) throw new Error('Failed to fetch leaderboard')
      return await response.json()
    } catch (error) {
      console.error('Error fetching leaderboard:', error)
      return { contributors: [] }
    }
  }
}

// Export the appropriate API based on mode
export const authAPI = USE_MOCK_DATA ? mockAuthAPI : realAuthAPI
export const dashboardAPI = USE_MOCK_DATA ? mockDashboardAPI : realDashboardAPI

export { REGION_API_URLS }
export default { authAPI, dashboardAPI }