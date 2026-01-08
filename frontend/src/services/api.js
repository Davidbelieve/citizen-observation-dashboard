// Base URLs for authentication and microservices
const AUTH_BASE_URL = 'http://localhost:8080/api'

// Each team member's microservice URL
const REGION_API_URLS = {
  'north-east-england': 'http://localhost:8081',
  'north-west-england': 'http://localhost:8082',
  'east-midlands': 'http://localhost:8083',
  'west-midlands': 'http://localhost:8084',
  'south-east-england': 'http://localhost:8085'
}

// Generic API request handler with error handling
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

// Authentication API
export const authAPI = {
  register: async (username, password) => {
    return apiRequest(`${AUTH_BASE_URL}/auth/register`, {
      method: