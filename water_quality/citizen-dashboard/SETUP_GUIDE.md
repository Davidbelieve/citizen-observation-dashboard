# Quick Setup Guide - Citizen Dashboard

## 🚀 Quick Start (3 Steps)

### Step 1: Install Dependencies
```bash
cd citizen-dashboard
npm install
```

### Step 2: Start Backend Services
Make sure both microservices are running:

**Terminal 1 - Crowdsourced Data MS:**
```bash
cd crowdsourced-data-ms
mvn spring-boot:run
```
✅ Should start on port **8081**

**Terminal 2 - Rewards MS:**
```bash
cd rewards-ms
mvn spring-boot:run
```
✅ Should start on port **8082**

### Step 3: Start React App
```bash
cd citizen-dashboard
npm start
```
✅ Should start on port **3000** and open automatically in browser

## ✅ Verify Everything Works

1. **Check Backend Services:**
   - Open `http://localhost:8081/api/observations` in browser → Should return `[]` or observation data
   - Open `http://localhost:8082/api/rewards/C200` in browser → Should return reward data or error

2. **Test Frontend:**
   - Open `http://localhost:3000` in browser
   - Submit an observation with Citizen ID: `C200`
   - View rewards for `C200`

## 🎯 Test Scenario

### Submit Observation:
```json
Citizen ID: C200
Postcode: NE1 7ST
Temperature: 26.3
pH: 7.2
Alkalinity: 8.5
Turbidity: 2.1
Observations: Clear water, Good quality
```

### View Rewards:
- Enter Citizen ID: `C200`
- Click "Get Rewards"
- Should see points and badge

## 🐛 Common Issues

### Issue: CORS Error
**Solution**: Make sure both backend controllers have `@CrossOrigin(origins = "http://localhost:3000")`

### Issue: Connection Refused
**Solution**: Verify both backend services are running on ports 8081 and 8082

### Issue: No Rewards Showing
**Solution**: 
1. Make sure you've submitted at least one observation
2. Check that the observation is valid (has postcode + measurements/observations)
3. Verify Citizen ID matches exactly

## 📝 File Structure Checklist

```
citizen-dashboard/
├── src/
│   ├── components/
│   │   ├── ObservationForm.jsx ✅
│   │   ├── ObservationForm.css ✅
│   │   ├── RewardsView.jsx ✅
│   │   └── RewardsView.css ✅
│   ├── services/
│   │   └── api.js ✅
│   ├── App.js ✅
│   ├── App.css ✅
│   └── index.js ✅
└── package.json ✅
```

## 🎉 Success!

If everything is working, you should see:
- ✅ Dashboard loads at `http://localhost:3000`
- ✅ Can submit observations
- ✅ Can view rewards
- ✅ No console errors
- ✅ Backend services responding

---

**Next Steps**: Test the full workflow and prepare for group phase enhancements!

