# Citizen Water Quality Dashboard

A React frontend application for the Citizen Science Water Quality Monitoring System. This dashboard allows citizens to submit water quality observations and view their rewards.

## 🚀 Features

- **Observation Submission Form**: Submit water quality data including:
  - Citizen ID and Postcode (required)
  - Temperature, pH, Alkalinity, Turbidity (optional measurements)
  - Observation notes and image paths
- **Rewards View**: View your total points, badge status, and observation history
- **Real-time Updates**: See your rewards and observations instantly
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Modern UI**: Clean, intuitive interface with gradient styling

## 📋 Prerequisites

- Node.js (v14 or higher)
- npm (v6 or higher)
- Both backend microservices running:
  - Crowdsourced Data MS on port 8081
  - Rewards MS on port 8082

## 🛠️ Installation

1. Navigate to the citizen-dashboard directory:
   ```bash
   cd citizen-dashboard
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Verify axios is installed:
   ```bash
   npm list axios
   ```

## 🏃 Running the Application

1. **Start the backend services first:**
   - Start Crowdsourced Data MS (port 8081)
   - Start Rewards MS (port 8082)

2. **Start the React application:**
   ```bash
   npm start
   ```

3. **Open your browser:**
   - The app will automatically open at `http://localhost:3000`
   - If it doesn't, manually navigate to the URL

## 📁 Project Structure

```
citizen-dashboard/
├── public/
│   └── index.html
├── src/
│   ├── components/
│   │   ├── ObservationForm.jsx      # Form for submitting observations
│   │   ├── ObservationForm.css      # Styles for observation form
│   │   ├── RewardsView.jsx          # Component for viewing rewards
│   │   └── RewardsView.css          # Styles for rewards view
│   ├── services/
│   │   └── api.js                   # API service for backend communication
│   ├── App.js                       # Main application component
│   ├── App.css                      # Main application styles
│   ├── index.js                     # Application entry point
│   └── index.css                    # Global styles
├── package.json
└── README.md
```

## 🎯 Usage

### Submitting an Observation

1. Fill in the **Observation Form** on the left side:
   - Enter your **Citizen ID** (e.g., C200) - *Required*
   - Enter your **Postcode** (e.g., NE1 7ST) - *Required*
   - Optionally add measurements:
     - Temperature (°C)
     - pH
     - Alkalinity
     - Turbidity
   - Add observation notes (comma-separated)
   - Add image paths (comma-separated URLs)

2. Click **Submit Observation**

3. You'll see a success message if the submission was successful

### Viewing Rewards

1. Enter a **Citizen ID** in the **Rewards View** on the right side
2. Click **Get Rewards**
3. View your:
   - Total Points
   - Badge (Gold, Silver, Bronze, or None)
   - Observation statistics
   - Recent observations

## 🎨 Badge System

- **Gold**: ≥500 points
- **Silver**: ≥200 points
- **Bronze**: ≥100 points
- **None**: <100 points

## 📊 Points Calculation

- **Base Points**: 10 points per valid observation
- **Bonus Points**: +10 points for complete submissions
  - Complete submission requires:
    - Postcode present
    - All 4 measurements (temperature, pH, alkalinity, turbidity)
    - At least 1 observation note

## 🔧 Configuration

### API Endpoints

The application connects to:
- **Crowdsourced Data MS**: `http://localhost:8081/api`
- **Rewards MS**: `http://localhost:8082/api`

To change these endpoints, edit `src/services/api.js`:

```javascript
const CROWD_BASE = "http://localhost:8081/api";
const REWARD_BASE = "http://localhost:8082/api";
```

### CORS Configuration

Both backend services have CORS enabled for `http://localhost:3000`. If you change the React app port, update the CORS configuration in:
- `crowdsourced-data-ms/src/main/java/.../controller/ObservationController.java`
- `rewards-ms/src/main/java/.../controller/RewardsController.java`

## 🐛 Troubleshooting

### Connection Errors

**Problem**: "Failed to fetch" or "Network Error"

**Solutions**:
1. Verify both backend services are running:
   - Check `http://localhost:8081/api/observations` in browser
   - Check `http://localhost:8082/api/rewards/C200` in browser
2. Check CORS configuration in backend controllers
3. Verify no firewall is blocking the connection

### Invalid Citizen ID

**Problem**: "Citizen ID not found" error

**Solutions**:
1. Make sure you've submitted at least one observation for that Citizen ID
2. Check the Citizen ID spelling (case-sensitive)
3. Verify the observation was successfully submitted

### Observation Not Appearing

**Problem**: Observation submitted but not showing in rewards

**Solutions**:
1. Check if the observation is valid (requires postcode + at least one measurement or observation)
2. Verify the Citizen ID matches exactly
3. Check backend logs for any errors

## 📝 Development

### Building for Production

```bash
npm run build
```

This creates an optimized production build in the `build` folder.

### Running Tests

```bash
npm test
```

## 🎯 Next Steps (For Group Phase)

- User authentication (login/signup)
- Community dashboards (regional views)
- Leaderboard (top contributors)
- Advanced filtering and search
- Data visualization charts
- Image upload functionality

## 📚 Dependencies

- **react**: ^19.2.0 - React library
- **react-dom**: ^19.2.0 - React DOM renderer
- **axios**: ^1.13.2 - HTTP client for API calls
- **react-scripts**: 5.0.1 - Create React App scripts

## 👥 Contributing

This is part of the Citizen Science Water Quality Monitoring System project. For group contributions, coordinate with team members.

## 📄 License

Part of the KF7014 assessment project.

---

**Note**: Make sure both backend microservices are running before starting the React application!
