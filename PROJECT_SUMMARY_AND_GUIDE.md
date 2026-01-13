# Project Summary & Implementation Guide

## 📋 Summary of All Changes Made

### 1. Backend Structure Added
- **Authentication Service** (Port 8080)
  - JWT-based authentication
  - User registration and login
  - Password encryption with BCrypt
  - H2 in-memory database
  - CORS configuration for frontend

- **Busola Microservices** (South East England - Oluwabusola)
  - **Gateway Service** (Port 8090)
    - Spring Cloud Gateway
    - Routes `/api/v1/observations/**` → Port 8091
    - Routes `/api/v1/rewards/**` → Port 8092
    - CORS configuration added
    - Circuit breaker for fault tolerance
  
  - **Crowdsourced Service** (Port 8091)
    - Observation CRUD operations
    - SQLite database
    - REST API at `/api/v1/observations`
    - Pagination support
  
  - **Reward Service** (Port 8092)
    - Leaderboard functionality
    - Reward calculation
    - REST API at `/api/v1/rewards`
    - SQLite database

### 2. Frontend Improvements

#### Component Architecture (Atomic Design)
- **Atoms**: Button, Input, Textarea, Select, NumberInput
- **Molecules**: FormController, Card, StatCard
- **Organisms**: Modal, ObservationForm, ObservationList, Leaderboard, Breadcrumb
- **Templates**: RidwanDashboard, ProtectedRoute

#### React Hook Form Integration
- Replaced manual form handling with React Hook Form
- Form validation with custom rules
- Reusable form components
- Better error handling

#### API Services
- `oluwabusolaApi.js` - South East England API (uses Gateway 8090)
- `jasmineApi.js` - Yorkshire API
- `lolaApi.js` - North West England API
- `api.js` - Main authentication API (uses Port 8080)

### 3. Key Features Implemented
-  JWT authentication system
-    Protected routes
-  Modular component architecture
-   Reusable form components with validation
-   Modal dialogs
-   Observation creation form
-   Dashboard with statistics
-   Leaderboard display
-   Error handling and loading states
-   CORS configuration

---

## 🚀 Step-by-Step Guide: Adding Your Dashboard

### Step 1: Create Your API Service File

Create `frontend/src/services/yourNameApi.js`:

```javascript
import { authAPI } from './api'

/* 
 * Your Individual Component
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

export const yourNameAPI = {
  /**
   * Get total count of observations
   */
  async getTotalCount() {
    try {
      const response = await gatewayRequest('/observations?page=0&size=1')
      const data = await response.json()
      return { count: data.totalElements || 0 }
    } catch (error) {
      console.error('Error fetching observation count:', error)
      throw error
    }
  },

  /**
   * Get recent observations
   * @param {number} limit - Number of observations to return (default 5)
   */
  async getRecentObservations(limit = 5) {
    try {
      const response = await gatewayRequest(`/observations?page=0&size=${limit}`)
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
      
      return { observations }
    } catch (error) {
      console.error('Error fetching recent observations:', error)
      throw error
    }
  },

  /**
   * Get top contributors leaderboard
   * @param {number} limit - Number of contributors to return (default 3)
   */
  async getLeaderboard(limit = 3) {
    try {
      const response = await gatewayRequest('/rewards')
      
      if (response.status === 204) {
        return { contributors: [] }
      }
      
      const data = await response.json()
      
      const sortedContributors = (data || [])
        .sort((a, b) => b.totalPoints - a.totalPoints)
        .slice(0, limit)
      
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
   * Create a new observation
   * @param {Object} observationData - Observation data
   */
  async createObservation(observationData) {
    try {
      const response = await gatewayRequest('/observations', {
        method: 'POST',
        body: JSON.stringify(observationData)
      })
      const data = await response.json()
      return data.data || data
    } catch (error) {
      console.error('Error creating observation:', error)
      throw error
    }
  },

  /**
   * Trigger reward calculation
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
```

### Step 2: Create Your Dashboard Page

Create `frontend/src/pages/YourNameDashboard.jsx`:

```javascript
import { useState, useEffect } from 'react'
import { Card, StatCard, ObservationList, Leaderboard, Breadcrumb, ObservationForm } from '../components'
import { yourNameAPI } from '../services/yourNameApi'
import { Button } from '../components'

export function YourNameDashboard() {
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [data, setData] = useState({
    totalCount: 0,
    recentObservations: [],
    leaderboard: []
  })
  
  useEffect(() => {
    fetchDashboardData()
  }, [])
  
  const fetchDashboardData = async () => {
    setLoading(true)
    setError(null)
    
    try {
      const [countData, observationsData, leaderboardData] = await Promise.all([
        yourNameAPI.getTotalCount(),
        yourNameAPI.getRecentObservations(5),
        yourNameAPI.getLeaderboard(3)
      ])
      
      setData({
        totalCount: countData.count || 0,
        recentObservations: observationsData.observations || [],
        leaderboard: leaderboardData.contributors || []
      })
    } catch (err) {
      setError(err.message)
      console.error('Dashboard fetch error:', err)
    } finally {
      setLoading(false)
    }
  }
  
  if (loading) {
    return (
      <div style={{ 
        maxWidth: '1200px', 
        margin: '0 auto', 
        padding: '40px 20px',
        textAlign: 'center'
      }}>
        <p style={{ fontSize: '18px', color: '#6b7280' }}>
          Loading dashboard data...
        </p>
      </div>
    )
  }
  
  if (error) {
    return (
      <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px' }}>
        <Card>
          <div style={{ textAlign: 'center', padding: '20px' }}>
            <p style={{ color: '#ef4444', fontSize: '18px', marginBottom: '8px' }}>
              <strong>Error Loading Dashboard</strong>
            </p>
            <p style={{ color: '#6b7280' }}>{error}</p>
            <Button 
              onClick={fetchDashboardData}
              variant="primary"
            >
              Try Again
            </Button>
          </div>
        </Card>
      </div>
    )
  }
  
  const handleSubmitObservation = async (observationData) => {
    setSubmitting(true)
    try {
      await yourNameAPI.createObservation(observationData)
      alert('Observation submitted successfully!')
      fetchDashboardData() // Refresh the dashboard
    } catch (err) {
      alert('Error submitting observation: ' + err.message)
      throw err
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div style={{ 
      maxWidth: '1200px', 
      margin: '0 auto', 
      padding: '20px',
      backgroundColor: '#f3f4f6',
      minHeight: '100vh'
    }}>
      <Breadcrumb currentPage="Your Name Dashboard" />
      
      {/* Header Section */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '24px' }}>
        <div style={{ flex: 1 }}>
          <h1 style={{ marginBottom: '8px' }}>
            Your Name Dashboard 💧📊
          </h1>
          <p style={{ color: '#6b7280', marginBottom: 0 }}>
            Water quality monitoring dashboard with crowdsourced observations and rewards
          </p>
        </div>
        <Button
          onClick={() => setIsFormOpen(true)}
          variant="success"
        >
          ➕ Add Observation
        </Button>
      </div>
      
      {/* Statistics Cards */}
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '16px',
        marginBottom: '24px'
      }}>
        <StatCard 
          label="Total Observations" 
          value={data.totalCount}
          icon="📊"
        />
        <StatCard 
          label="Recent Submissions" 
          value={data.recentObservations.length}
          icon="📝"
        />
        <StatCard 
          label="Active Contributors" 
          value={data.leaderboard.length}
          icon="👥"
        />
      </div>
      
      {/* Main Content Grid */}
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: '2fr 1fr',
        gap: '24px',
        marginBottom: '24px'
      }}>
        <Card title="Recent Observations">
          <ObservationList observations={data.recentObservations} />
        </Card>
        
        <Card title="Top Contributors">
          <Leaderboard contributors={data.leaderboard} />
        </Card>
      </div>
      
      {/* Action Buttons */}
      <div style={{ display: 'flex', gap: '12px' }}>
        <Button 
          onClick={fetchDashboardData}
          variant="primary"
        >
          🔄 Refresh Data
        </Button>
        
        <Button 
          onClick={async () => {
            try {
              setLoading(true)
              await yourNameAPI.calculateRewards()
              alert('Rewards calculated successfully!')
              fetchDashboardData()
            } catch (err) {
              alert('Error calculating rewards: ' + err.message)
            } finally {
              setLoading(false)
            }
          }}
          disabled={loading}
          variant="success"
        >
          🏆 Calculate Rewards
        </Button>
      </div>

      {/* Observation Form Modal */}
      <ObservationForm
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        onSubmit={handleSubmitObservation}
        loading={submitting}
      />
    </div>
  )
}
```

### Step 3: Add Route to App.jsx

Update `frontend/src/App.jsx`:

```javascript
import { YourNameDashboard } from './pages/YourNameDashboard'

// Add this route inside <Routes>
<Route path="/dashboard/your-region-name" element={
  <ProtectedRoute>
    <YourNameDashboard />
  </ProtectedRoute>
} />
```

### Step 4: Update Vite Proxy Configuration

**IMPORTANT**: Since authentication is on port 8080 and gateway is on 8090, you need to configure the proxy properly.

Update `frontend/vite.config.js`:

```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
  
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Authentication endpoints go to port 8080
      '/api/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      // All other /api requests go to gateway (port 8090)
      '/api': {
        target: 'http://localhost:8090',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
```

**Note**: The order matters! More specific routes (`/api/auth`) must come before general routes (`/api`).

---

## 🔧 Updating Authentication Endpoint

### Current Setup
- **Main Authentication**: Port 8080 (`/api/auth/*`)
- **Gateway**: Port 8090 (`/api/v1/*`)
- **Crowdsourced Service**: Port 8091
- **Reward Service**: Port 8092

### If Authentication is on Port 8080

Update your `frontend/vite.config.js` to handle both authentication (8080) and gateway (8090):

```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
  
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      // Authentication endpoints go to port 8080
      '/api/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      // All other /api requests (including /api/v1) go to gateway (port 8090)
      '/api': {
        target: 'http://localhost:8090',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
```

**Important**: The `/api/auth` route must be defined BEFORE the general `/api` route for proper routing.

### Verify Authentication Endpoints

Check `frontend/src/services/api.js` - it should use:
- `POST /api/auth/register`
- `POST /api/auth/login`

These will proxy to `http://localhost:8080/api/auth/*`

---

## 🎨 Customizing the Observation Form

The `ObservationForm` component is already created and reusable. It includes:

- Postcode (required, UK format validation)
- Citizen ID (required)
- Notes (optional textarea)
- Measurements section (optional):
  - Temperature (°C)
  - pH
  - Alkalinity (mg/L)
  - Turbidity (NTU)
- Tags (comma-separated)

### If You Need Different Fields

You can create a custom form component using the reusable atoms and molecules:

```javascript
import { useForm, Controller } from 'react-hook-form'
import { Modal, FormController, Input, Textarea, NumberInput, Button } from '../components'

export function CustomObservationForm({ isOpen, onClose, onSubmit, loading }) {
  const { control, handleSubmit, formState: { errors }, reset } = useForm({
    defaultValues: {
      // Your custom fields here
      field1: '',
      field2: '',
      // ...
    }
  })

  const onSubmitForm = async (data) => {
    try {
      await onSubmit(data)
      reset()
      onClose()
    } catch (error) {
      console.error('Form submission error:', error)
    }
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Add New Observation">
      <form onSubmit={handleSubmit(onSubmitForm)}>
        {/* Add your custom form fields using FormController, Input, etc. */}
        
        <FormController
          name="field1"
          label="Field 1"
          required
          error={errors.field1}
        >
          <Controller
            name="field1"
            control={control}
            rules={{ required: 'Field 1 is required' }}
            render={({ field }) => (
              <Input
                {...field}
                placeholder="Enter field 1"
                error={errors.field1}
              />
            )}
          />
        </FormController>

        {/* Add more fields as needed */}

        <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '24px' }}>
          <Button type="button" variant="secondary" onClick={onClose} disabled={loading}>
            Cancel
          </Button>
          <Button type="submit" variant="primary" disabled={loading} loading={loading}>
            Submit
          </Button>
        </div>
      </form>
    </Modal>
  )
}
```

---

## 📝 Port Configuration Summary

| Service | Port | Endpoint Base | Purpose |
|---------|------|---------------|---------|
| Authentication | 8080 | `/api/auth` | Login, Register, JWT tokens |
| Gateway | 8090 | `/api/v1` | Routes to microservices |
| Crowdsourced | 8091 | `/api/v1/observations` | Observation CRUD |
| Reward | 8092 | `/api/v1/rewards` | Leaderboard, rewards |

---

##   Checklist for Adding Your Dashboard

- [ ] Create API service file (`yourNameApi.js`)
- [ ] Create dashboard page component (`YourNameDashboard.jsx`)
- [ ] Add route to `App.jsx`
- [ ] Verify Vite proxy configuration
- [ ] Test authentication flow
- [ ] Test observation creation
- [ ] Test data fetching
- [ ] Customize form fields if needed
- [ ] Test error handling
- [ ] Verify CORS is working

---

## 🐛 Common Issues & Solutions

### 1. "Failed to fetch" Error
- **Solution**: Ensure gateway (8090), crowdsourced (8091), and reward (8092) services are running
- Check CORS configuration in gateway
- Verify Vite proxy settings

### 2. Authentication Not Working
- **Solution**: Ensure authentication service (8080) is running
- Check `/api/auth` proxy configuration in `vite.config.js`
- Verify JWT token is being stored in localStorage

### 3. Form Not Submitting
- **Solution**: Check browser console for errors
- Verify API endpoint URLs
- Check network tab for request/response

### 4. CORS Errors
- **Solution**: Gateway has CORS configured, but ensure services are running
- Check browser console for specific CORS error messages

---

## 📚 Additional Resources

- Component usage guide: See `COMPONENTS_USAGE.md`
- React Hook Form docs: https://react-hook-form.com/
- Spring Cloud Gateway docs: https://spring.io/projects/spring-cloud-gateway

---

**Last Updated**: Based on current project state
**Maintained By**: Development Team

