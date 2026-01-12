/* I am working on the North West England Region, my gateway runs on port 8080 and it connects 
to Crowdsourced Data microservice on port 8081 and Rewards microservice on port 8082*/

const GATEWAY_URL = '/api'

//north west england postcodes prefixes
const North_WEST_POSTCODES = ['M', 'L', 'WA', 'CH', 'PR', 'FY', 'LA', 'BB', 'OL', 'BL', 'WN', 'SK']
//Check if postcode is in the north west region
function isNorthWestPostcode(postcode) {
    if (!postcode) return false
    const prefix =postcode.trim().toUpperCase().split(/\d/)[0]
    return North_WEST_POSTCODES.some(nw => prefix.startsWith(nw))
}
export const lolaAPI ={
   // Get total count of observations in North West

  async getTotalCount() {
    try {
      const response = await fetch(`${GATEWAY_URL}/observations`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
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

  
   //Get recent observations from North West England
   //@param {number} limit - Number of observations to return the default is 5

  async getRecentObservations(limit = 5) {
    try {
      const response = await fetch(`${GATEWAY_URL}/observations`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
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

  
   //Get top contributors leaderboard for North West England
// @param {number} limit - Number of contributors to return (default 3)
   
  async getLeaderboard(limit = 3) {
    try {
      const response = await fetch(`${GATEWAY_URL}/rewards/leaderboard?limit=${limit}`)
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
      const data = await response.json()
      
      // Transform to match dashboard component expectations
      return {
        contributors: data.map(contributor => ({
          id: contributor.id,
          username: contributor.citizenId, // Using citizenId as username
          points: contributor.totalPoints
        }))
      }
    } catch (error) {
      console.error('Error fetching leaderboard:', error)
      throw error
    }
  }  
}

