//crowd and rewards runs on ports 8095 and 8096 while the gate is on 8086
const URL =  '/jasmineAPI'

export const jasmineAPI = {
    //get total count of obs in yorkshire

    async getTotalObservations() {
        //error handling
        try {
            //url for GET method
            const response = await fetch(`${URL}/crowd/count`)
            //if response is not ok, throw error
            if (!response.ok) {
                throw new Error(`HTTP Error: ${response.status}`)
            }
            //else return the fetched count
            return { count: response.json()}
        } catch (error) {
            console.error('Cannot fetch total number of observations', error)
            throw error
        }
    },

    //get the top five recent obs
    async getRecentObservations(limit = 5){
        //error handling
        try{
            //url for GET method
            const response = await fetch(`${URL}/crowd/top5`)
            console.log((JSON.stringify(response)))
            if (!response.ok) {
                throw new Error(`HTTP Error: ${response.status}`)
            }
            //save response as a json object 
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
            }))}
        } catch (error) {
            console.error('Error fetching recent observations:', error)
            throw error
        }
    },
    //Get top contributors leaderboard for North West England
    // // @param {number} limit - Number of contributors to return (default 3)

    async getLeaderboard(limit = 3) {
         try {
            const response = await fetch(`${URL}/rewards/top3`)
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`)
            }
            const data = await response.json()
            
            // Transform to match dashboard component expectations
            return {
                contributors: data.map(contributor => ({
                    id: contributor.id,
                    username: 'citizen' + contributor.customerID, // Using citizenId as username
                    points: contributor.points
                }))
            }
        } catch (error) {
            console.error('Error fetching leaderboard:', error)
            throw error
        }
    }  
} 