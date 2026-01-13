# Project Summary & Implementation Guide

## Summary of All Changes Made

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

## Project Folder Structure

### Root Directory
```
citizen-observation-dashboard/
├── backend/                    # Backend services (Spring Boot)
│   ├── authentication/         # Authentication service (Port 8080)
│   └── busola/                # Oluwabusola's microservices
│       ├── crowdsourced/      # Crowdsourced service (Port 8091)
│       ├── gateway/           # Spring Cloud Gateway (Port 8090)
│       └── reward/            # Reward service (Port 8092)
├── frontend/                   # Frontend application (React + Vite)
├── PROJECT_SUMMARY_AND_GUIDE.md
├── README.md
└── COMPONENTS_USAGE.md
```

### Backend Structure

#### Authentication Service (`backend/authentication/`)
```
authentication/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/carbon/dashboard/
│       │       ├── CarbonDashboardApplication.java
│       │       ├── config/
│       │       │   └── CorsConfig.java          # CORS & RestTemplate config
│       │       ├── controller/
│       │       │   ├── AuthController.java     # /api/auth/** endpoints
│       │       │   ├── GatewayController.java  # Central routing hub
│       │       │   └── CarbonIntensityController.java
│       │       ├── dto/                        # Data Transfer Objects
│       │       │   ├── AuthResponse.java
│       │       │   ├── LoginRequest.java
│       │       │   ├── RegisterRequest.java
│       │       │   └── UserResponse.java
│       │       ├── exception/
│       │       │   └── GlobalExceptionHandler.java
│       │       ├── model/
│       │       │   └── User.java
│       │       ├── repository/
│       │       │   └── UserRepository.java
│       │       ├── security/
│       │       │   ├── JwtAuthenticationFilter.java
│       │       │   ├── JwtUtil.java
│       │       │   └── SecurityConfig.java     # Security & route permissions
│       │       └── service/
│       │           ├── CarbonIntensityService.java
│       │           └── UserService.java
│       └── resources/
│           └── application.properties           # Port 8080, DB config
└── target/                                      # Compiled classes (ignore)
```

**Key Files:**
- `GatewayController.java` - **Central routing hub** - routes all API requests
- `SecurityConfig.java` - Defines which routes require authentication
- `CorsConfig.java` - CORS configuration and RestTemplate bean

#### Busola Microservices (`backend/busola/`)

**Gateway Service** (`backend/busola/gateway/gateway/`)
```
gateway/gateway/
├── pom.xml
└── src/main/
    ├── java/com/waterQualityMonitoring/gateway/
    │   ├── GatewayApplication.java            # Main app + route configuration
    │   ├── UriConfiguration.java             # Service URLs
    │   └── FallbackController.java            # Circuit breaker fallbacks
    └── resources/
        └── application.properties             # Port 8090 config
```

**Crowdsourced Service** (`backend/busola/crowdsourced/`)
```
crowdsourced/
├── pom.xml
├── data/
│   └── crowdsourced.db                        # SQLite database
└── src/main/
    ├── java/com/waterQualityMonitoring/crowdsourced/
    │   ├── CrowdsourcedApplication.java
    │   ├── controller/                        # REST controllers
    │   ├── model/                             # Entity models
    │   ├── repository/                        # Data repositories
    │   └── service/                           # Business logic
    └── resources/
        └── application.properties              # Port 8091 config
```

**Reward Service** (`backend/busola/reward/reward/`)
```
reward/reward/
├── pom.xml
├── data/
│   └── reward.db                              # SQLite database
└── src/main/
    ├── java/com/waterQualityMonitoring/reward/
    │   ├── RewardApplication.java
    │   ├── controller/                        # REST controllers
    │   ├── model/                             # Entity models
    │   ├── repository/                        # Data repositories
    │   └── service/                           # Business logic
    └── resources/
        └── application.properties              # Port 8092 config
```

### Frontend Structure (`frontend/`)

```
frontend/
├── package.json
├── vite.config.js                             # Vite config + proxy setup
├── index.html
└── src/
    ├── main.jsx                               # React entry point
    ├── App.jsx                                # Main app component + routes
    ├── App.css
    ├── index.css
    ├── assets/
    │   └── react.svg
    ├── components/                            # Reusable components (Atomic Design)
    │   ├── index.js                           # Central export file
    │   ├── atoms/                             # Basic building blocks
    │   │   ├── index.js
    │   │   ├── Button.jsx
    │   │   ├── Input.jsx
    │   │   ├── Textarea.jsx
    │   │   ├── Select.jsx
    │   │   └── NumberInput.jsx
    │   ├── molecules/                         # Simple component groups
    │   │   ├── index.js
    │   │   ├── Card.jsx
    │   │   ├── FormController.jsx             # Form field wrapper
    │   │   └── StatCard.jsx
    │   ├── organisms/                         # Complex components
    │   │   ├── index.js
    │   │   ├── Modal.jsx
    │   │   ├── ObservationForm.jsx            # Full observation form
    │   │   ├── ObservationList.jsx
    │   │   ├── Leaderboard.jsx
    │   │   └── Breadcrumb.jsx
    │   └── templates/                         # Page-level components
    │       ├── index.js
    │       ├── ProtectedRoute.jsx             # Auth wrapper
    │       └── RidwanDashboard.jsx
    ├── pages/                                 # Page components
    │   ├── Login.jsx
    │   ├── Register.jsx
    │   ├── Hub.jsx                            # Main hub/dashboard selector
    │   ├── OluwabusolaDashboard.jsx           # South East England
    │   ├── LolaDashboard.jsx                  # North West England
    │   ├── JasmineDashboard.jsx               # Yorkshire
    │   └── DashboardTemplate.jsx              # Generic template
    └── services/                              # API service files
        ├── api.js                             # Authentication API (port 8080)
        ├── oluwabusolaApi.js                  # South East API (via gateway 8090)
        ├── lolaApi.js                         # North West API (port 8082)
        └── jasmineApi.js                      # Yorkshire API (port 8086)
```

### Component Organization (Atomic Design)

The frontend follows **Atomic Design** principles:

1. **Atoms** (`components/atoms/`) - Basic, indivisible components
   - `Button`, `Input`, `Textarea`, `Select`, `NumberInput`
   - Cannot be broken down further
   - Highly reusable

2. **Molecules** (`components/molecules/`) - Simple combinations of atoms
   - `Card`, `FormController`, `StatCard`
   - Groups of atoms working together
   - Still reusable but more specific

3. **Organisms** (`components/organisms/`) - Complex UI components
   - `Modal`, `ObservationForm`, `ObservationList`, `Leaderboard`
   - Complete functional sections
   - May be page-specific but still reusable

4. **Templates** (`components/templates/`) - Page-level layouts
   - `ProtectedRoute`, `RidwanDashboard`
   - Define page structure
   - Less reusable, more context-specific

### Key File Locations

| What You Need | File Location |
|---------------|---------------|
| **Add new API route** | `backend/authentication/.../GatewayController.java` |
| **Update security rules** | `backend/authentication/.../SecurityConfig.java` |
| **Configure CORS** | `backend/authentication/.../CorsConfig.java` |
| **Add frontend API service** | `frontend/src/services/yourNameApi.js` |
| **Add new page** | `frontend/src/pages/YourNameDashboard.jsx` |
| **Add route to app** | `frontend/src/App.jsx` |
| **Create reusable component** | `frontend/src/components/[atoms|molecules|organisms]/` |
| **Vite proxy config** | `frontend/vite.config.js` |
| **Gateway routes (8090)** | `backend/busola/gateway/gateway/.../GatewayApplication.java` |

### Important Notes

- **Backend**: Each service is a separate Spring Boot application with its own `pom.xml`
- **Frontend**: Single React application with all dashboards in one codebase
- **Database**: Each service may have its own database (SQLite for crowdsourced/reward, H2 for auth)
- **Target folders**: Generated build artifacts - can be ignored/cleaned with `mvn clean`
- **Node modules**: Frontend dependencies - can be regenerated with `npm install`

---

##  Step-by-Step Guide: Adding Your Dashboard

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
            Your Name Dashboard
          </h1>
          <p style={{ color: '#6b7280', marginBottom: 0 }}>
            Water quality monitoring dashboard with crowdsourced observations and rewards
          </p>
        </div>
        <Button
          onClick={() => setIsFormOpen(true)}
          variant="success"
        >
          Add Observation
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
          icon=""
        />
        <StatCard 
          label="Recent Submissions" 
          value={data.recentObservations.length}
          icon=""
        />
        <StatCard 
          label="Active Contributors" 
          value={data.leaderboard.length}
          icon=""
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
          Refresh Data
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
          Calculate Rewards
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

### Step 4: Vite Proxy Configuration (Already Configured!)

**IMPORTANT**: The Vite proxy is already configured correctly! All `/api/**` requests go to port 8080 (authentication service), which then routes to the appropriate microservices.

**Current Configuration** (`frontend/vite.config.js`):
```javascript
'/api': {
  target: 'http://localhost:8080',  // All API requests go here
  changeOrigin: true,
  secure: false,
  ws: false
}
```

**No changes needed!** The authentication service (8080) handles all routing through `GatewayController`.

---

## Current Architecture Overview

### Centralized Routing Through Authentication Service

**All API requests flow through port 8080 (Authentication Service):**

```
Frontend → /api/anything
    ↓
Vite Proxy → http://localhost:8080/api/anything
    ↓
Authentication Service (8080)
    ├─ /api/auth/** → Handled by AuthController (local)
    ├─ /api/v1/** → Routes to Gateway (8090) → Routes to 8091/8092
    ├─ /api/regions/** → Routes to region-specific services
    └─ /api/yourname/** → Routes to your service (if configured)
```

### Key Points

1. **Single Entry Point**: Frontend only needs to know about port 8080
2. **Centralized Routing**: `GatewayController` handles all routing logic
3. **Authentication**: All routes (except `/api/auth/**`) require JWT token
4. **No Frontend Changes Needed**: Vite proxy is already configured correctly

### Verify Authentication Endpoints

Check `frontend/src/services/api.js` - it should use:
- `POST /api/auth/register` → `http://localhost:8080/api/auth/register`
- `POST /api/auth/login` → `http://localhost:8080/api/auth/login`

These are handled directly by `AuthController` in the authentication service.

---

## Customizing the Observation Form

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

## Port Configuration Summary

| Service | Port | Endpoint Base | Purpose |
|---------|------|---------------|---------|
| **Authentication Service** | 8080 | `/api/**` | Central routing hub - routes all API requests |
| **Authentication** | 8080 | `/api/auth/**` | Login, Register, JWT tokens (handled locally) |
| **Gateway (Oluwabusola)** | 8090 | `/api/v1/**` | Spring Cloud Gateway - routes to 8091/8092 |
| **North East (Ridwan)** | 8081 | `/api/regions/north-east-england/**` | Direct routing |
| **North West (Lola)** | 8082 | `/api/regions/north-west-england/**` | Direct routing |
| **Yorkshire (Jasmine)** | 8086 | `/api/regions/yorkshire/**` | Direct routing |

### Routing Flow

```
Frontend → /api/v1/observations
    ↓
Vite Proxy → http://localhost:8080/api/v1/observations
    ↓
Authentication Service (8080) → GatewayController
    ↓
Gateway Service (8090) → /api/v1/observations
    ↓
Crowdsourced Service (8091) → /api/v1/observations
```

---

## Adding Your API to GatewayController

The `GatewayController` in the authentication service (port 8080) acts as the central routing hub. All frontend requests go through port 8080, which then routes to the appropriate microservices.

### Quick Decision Guide

| Your Situation | Recommended Option | Example Path | Effort |
|----------------|-------------------|--------------|--------|
| Want a dedicated API path | **Option 1: Custom Path** | `/api/yourname/**` | Medium - Add method + SecurityConfig |
| Implementing a region dashboard | **Option 2: Region-Based** | `/api/regions/your-region/**` | Low - Just add to map |
| Need circuit breakers/advanced routing | **Option 3: Gateway 8090** | `/api/v1/yourservice/**` | High - Configure Spring Cloud Gateway |

### Current Routing Architecture

```
Frontend Request: /api/your-path
    ↓
Vite Proxy: → http://localhost:8080/api/your-path
    ↓
Authentication Service (8080): GatewayController routes based on path
    ↓
Your Microservice: Receives the request
```

### Option 1: Add a Custom API Path (e.g., `/api/yourname/**`)

If you want a dedicated path like `/api/yourname/**` that routes directly to your service:

**Step 1: Add the routing method to GatewayController.java**

```java
/**
 * YOUR NAME'S ENDPOINT ROUTING LOGIC
 * Routes /api/yourname/** requests directly to your service.
 * 
 * Pattern: /api/yourname/**
 * 
 * Examples:
 * - GET /api/yourname/data → http://localhost:YOUR_PORT/api/data
 * - POST /api/yourname/submit → http://localhost:YOUR_PORT/api/submit
 */
@RequestMapping(value = "/api/yourname/**", method = {RequestMethod.GET, RequestMethod.POST, 
                                                      RequestMethod.PUT, RequestMethod.DELETE, 
                                                      RequestMethod.PATCH, RequestMethod.OPTIONS})
public ResponseEntity<?> routeYourNameRequest(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @RequestBody(required = false) Object requestBody,
        HttpServletRequest request) {
    
    HttpMethod method = HttpMethod.valueOf(request.getMethod());
    
    if (method == HttpMethod.OPTIONS) {
        return ResponseEntity.ok().build();
    }
    
    String requestPath = request.getRequestURI();
    // Extract path after /api/yourname
    int yournameIndex = requestPath.indexOf("/yourname");
    String pathAfterYourname = requestPath.substring(yournameIndex + "/yourname".length());
    
    // Your service URL
    String YOUR_SERVICE_URL = "http://localhost:YOUR_PORT"; // Replace with your port
    String targetUrl = YOUR_SERVICE_URL + pathAfterYourname;
    
    // Add query parameters if any
    String queryString = request.getQueryString();
    if (queryString != null && !queryString.isEmpty()) {
        targetUrl += "?" + queryString;
    }
    
    return forwardRequest(targetUrl, authHeader, requestBody, method, request);
}
```

**Step 2: Update SecurityConfig.java**

Add your new path to the security configuration:

```java
.authorizeHttpRequests(auth -> auth
    // Public endpoints
    .requestMatchers("/api/auth/**").permitAll()
    .requestMatchers("/h2-console/**").permitAll()
    // Gateway routes - require authentication
    .requestMatchers("/api/regions/**").authenticated()
    .requestMatchers("/api/v1/**").authenticated()
    .requestMatchers("/api/yourname/**").authenticated()  // Add this line
    // Protected endpoints
    .requestMatchers("/api/carbon/**").authenticated()
    .anyRequest().authenticated()
)
```

**Step 3: Update your frontend API service**

In your `frontend/src/services/yourNameApi.js`:

```javascript
const GATEWAY_BASE = '/api/yourname'  // Use your custom path

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
  // ... rest of your code
}
```

### Option 2: Add a Region-Based Route (e.g., `/api/regions/your-region/**`)

If you want to use the region-based routing pattern:

**Step 1: Add your region to REGION_MICROSERVICE_MAP**

In `GatewayController.java`, add your region to the static map:

```java
static {
    REGION_MICROSERVICE_MAP.put("north-east-england", "http://localhost:8081");
    REGION_MICROSERVICE_MAP.put("north-west-england", "http://localhost:8082/api");
    REGION_MICROSERVICE_MAP.put("east-midlands", "http://localhost:8083");
    REGION_MICROSERVICE_MAP.put("west-midlands", "http://localhost:8084");
    REGION_MICROSERVICE_MAP.put("south-east-england", SOUTH_EAST_GATEWAY);
    REGION_MICROSERVICE_MAP.put("yorkshire", "http://localhost:8086/citizenscience");
    REGION_MICROSERVICE_MAP.put("your-region-name", "http://localhost:YOUR_PORT/api");  // Add this line
}
```

**Note**: The existing `routeRequest` method already handles all regions, so no additional routing method is needed!

**Step 2: Update your frontend API service**

In your `frontend/src/services/yourNameApi.js`:

```javascript
const GATEWAY_BASE = '/api/regions/your-region-name'  // Use region-based path

async function gatewayRequest(path, options = {}) {
  // ... same as above
}
```

### Option 3: Route Through Existing Gateway (Port 8090)

If you want to use the existing Spring Cloud Gateway (port 8090) like Oluwabusola does:

**Step 1: Configure your service in the Gateway**

Update `backend/busola/gateway/gateway/src/main/java/com/waterQualityMonitoring/gateway/GatewayApplication.java`:

```java
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder, UriConfiguration uriConfiguration) {
    String yourServiceApi = uriConfiguration.getYourServiceApi(); // Add this to UriConfiguration
    
    return builder.routes()
        // Existing routes...
        .route("your-service", r -> r
            .path("/api/v1/yourservice/**")
            .filters(f -> f
                .addRequestHeader("Gateway", "Spring Cloud Gateway")
                .circuitBreaker(c -> c
                    .setName("your-service-circuit-breaker")
                    .setFallbackUri("forward:/fallback/yourservice")))
            .uri(yourServiceApi))
        .build();
}
```

**Step 2: Add your service URL to UriConfiguration**

In `backend/busola/gateway/gateway/src/main/java/com/waterQualityMonitoring/gateway/UriConfiguration.java`:

```java
private String yourServiceApi = "http://localhost:YOUR_PORT";

public String getYourServiceApi() {
    return yourServiceApi;
}

public void setYourServiceApi(String yourServiceApi) {
    this.yourServiceApi = yourServiceApi;
}
```

**Step 3: Use `/api/v1/yourservice/**` in your frontend**

```javascript
const GATEWAY_BASE = '/api/v1/yourservice'
```

### Complete Example: Adding a Custom API Path

Here's a complete example for adding `/api/weather/**` that routes to a weather service on port 8085:

**1. GatewayController.java - Add this method:**

```java
/**
 * Weather Service Routing
 * Routes /api/weather/** requests to Weather Service (port 8085).
 * 
 * Pattern: /api/weather/**
 * 
 * Examples:
 * - GET /api/weather/forecast → http://localhost:8085/api/forecast
 * - GET /api/weather/current → http://localhost:8085/api/current
 */
@RequestMapping(value = "/api/weather/**", method = {RequestMethod.GET, RequestMethod.POST, 
                                                    RequestMethod.PUT, RequestMethod.DELETE, 
                                                    RequestMethod.PATCH, RequestMethod.OPTIONS})
public ResponseEntity<?> routeWeatherRequest(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @RequestBody(required = false) Object requestBody,
        HttpServletRequest request) {
    
    HttpMethod method = HttpMethod.valueOf(request.getMethod());
    
    if (method == HttpMethod.OPTIONS) {
        return ResponseEntity.ok().build();
    }
    
    String requestPath = request.getRequestURI();
    int weatherIndex = requestPath.indexOf("/weather");
    String pathAfterWeather = requestPath.substring(weatherIndex + "/weather".length());
    
    String WEATHER_SERVICE = "http://localhost:8085";
    String targetUrl = WEATHER_SERVICE + pathAfterWeather;
    
    String queryString = request.getQueryString();
    if (queryString != null && !queryString.isEmpty()) {
        targetUrl += "?" + queryString;
    }
    
    return forwardRequest(targetUrl, authHeader, requestBody, method, request);
}
```

**2. SecurityConfig.java - Add this line:**

```java
.requestMatchers("/api/weather/**").authenticated()  // Add after /api/v1/**
```

**3. Frontend service - Use this:**

```javascript
const GATEWAY_BASE = '/api/weather'

// Then use: gatewayRequest('/forecast') → /api/weather/forecast → http://localhost:8085/api/forecast
```

### Important Notes

1. **Path Order Matters**: More specific paths should be defined before general ones. Spring matches the first pattern that fits.

2. **Authentication Required**: All routes except `/api/auth/**` require a valid JWT token. Make sure your frontend includes the `Authorization: Bearer <token>` header.

3. **CORS**: CORS is already configured for `http://localhost:3000` and `http://localhost:5173`. If you need additional origins, update `CorsConfig.java`.

4. **Error Handling**: The `forwardRequest` method handles errors automatically. Connection errors will return a 502 Bad Gateway with a helpful message.

5. **Path Transformation**: 
   - `/api/yourname/data` → `http://localhost:YOUR_PORT/api/data` (removes `/yourname`)
   - `/api/regions/your-region/data` → `http://localhost:YOUR_PORT/api/data` (removes `/api/regions/your-region`)

### Quick Reference: Which Option to Choose?

- **Option 1 (Custom Path)**: Use if you want a dedicated path like `/api/yourname/**`
- **Option 2 (Region-Based)**: Use if you're implementing a region-specific dashboard
- **Option 3 (Gateway 8090)**: Use if you want to leverage the Spring Cloud Gateway with circuit breakers and advanced routing

---

##   Checklist for Adding Your Dashboard

### Backend Setup
- [ ] Decide on routing pattern (Custom Path / Region-Based / Gateway 8090)
- [ ] Add routing method to `GatewayController.java` (if custom path)
- [ ] Add region to `REGION_MICROSERVICE_MAP` (if region-based)
- [ ] Update `SecurityConfig.java` to allow your API path
- [ ] Configure your microservice to run on a specific port
- [ ] Test routing with `curl` or Postman

### Frontend Setup
- [ ] Create API service file (`yourNameApi.js`)
- [ ] Create dashboard page component (`YourNameDashboard.jsx`)
- [ ] Add route to `App.jsx`
- [ ] Verify Vite proxy configuration (should already be set to port 8080)
- [ ] Test authentication flow
- [ ] Test data fetching
- [ ] Test error handling
- [ ] Verify CORS is working

### Testing
- [ ] Test GET requests
- [ ] Test POST/PUT/DELETE requests
- [ ] Test with valid JWT token
- [ ] Test with invalid/expired token (should redirect to login)
- [ ] Test error scenarios (service down, network errors)

---

## Common Issues & Solutions

### 1. "Failed to fetch" Error
- **Solution**: Ensure gateway (your gatewayport), crowdsourced (your crowdsource port), and reward (rewardport) services are running
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

## Additional Resources

- Component usage guide: See `COMPONENTS_USAGE.md`
- React Hook Form docs: https://react-hook-form.com/
- Spring Cloud Gateway docs: https://spring.io/projects/spring-cloud-gateway

---

**Last Updated**: Based on current project state
**Maintained By**: Development Team

