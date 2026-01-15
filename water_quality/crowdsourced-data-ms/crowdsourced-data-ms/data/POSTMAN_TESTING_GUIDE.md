# Postman Testing Guide - Crowdsourced Data Microservice

## Prerequisites
- ✅ Application is running on port 8081
- ✅ MySQL database `citizendb` is created
- ✅ Tables are auto-created by JPA
- ✅ Postman is installed

## Base URL
```
http://localhost:8081/api/observations
```

---

## 🧪 TEST 1: Create Observation (POST)

### Endpoint
```
POST http://localhost:8081/api/observations
```

### Steps:
1. Open Postman
2. Create a new request
3. Set method to **POST**
4. Enter URL: `http://localhost:8081/api/observations`
5. Click on **Headers** tab
6. Add header:
   - Key: `Content-Type`
   - Value: `application/json`
7. Click on **Body** tab
8. Select **raw** and **JSON** from dropdown
9. Paste the following JSON:

```json
{
  "citizenId": "C200",
  "postcode": "NE1 7ST",
  "temperature": 26.3,
  "ph": 7.2,
  "alkalinity": 8.5,
  "turbidity": 2.1,
  "observations": ["Clear water", "Good quality"],
  "imagePaths": ["image1.jpg", "image2.jpg"]
}
```

10. Click **Send**

### Expected Response:
- **Status**: `201 Created`
- **Response Body**:
```json
{
  "id": 1,
  "citizenId": "C200",
  "postcode": "NE1 7ST",
  "temperature": 26.3,
  "ph": 7.2,
  "alkalinity": 8.5,
  "turbidity": 2.1,
  "observations": ["Clear water", "Good quality"],
  "imagePaths": ["image1.jpg", "image2.jpg"],
  "submittedAt": "2024-01-15T10:30:00",
  "valid": true
}
```

### ✅ Success Indicators:
- Status code is 201
- Response contains `id` (auto-generated)
- `valid` is `true` (because postcode + measurements exist)
- `submittedAt` timestamp is present

---

## 🧪 TEST 2: Create Another Observation (POST)

### Use this JSON to create a second observation:

```json
{
  "citizenId": "C201",
  "postcode": "NE2 4AB",
  "temperature": 24.5,
  "ph": 6.8,
  "observations": ["Slightly cloudy"],
  "imagePaths": ["water_sample_1.jpg"]
}
```

### Expected Response:
- **Status**: `201 Created`
- **Response Body**: Contains new observation with `id: 2`

---

## 🧪 TEST 3: Create Observation with Only Observations (POST)

### Test validation - observation without measurements but with notes:

```json
{
  "citizenId": "C202",
  "postcode": "NE3 5CD",
  "observations": ["Water looks clean", "No unusual smell"]
}
```

### Expected Response:
- **Status**: `201 Created`
- **Response Body**: `valid: true` (because postcode + observation notes exist)

---

## 🧪 TEST 4: Get All Observations (GET)

### Endpoint
```
GET http://localhost:8081/api/observations
```

### Steps:
1. Create new request in Postman
2. Set method to **GET**
3. Enter URL: `http://localhost:8081/api/observations`
4. Click **Send**

### Expected Response:
- **Status**: `200 OK`
- **Response Body**: Array of all observations
```json
[
  {
    "id": 1,
    "citizenId": "C200",
    "postcode": "NE1 7ST",
    "temperature": 26.3,
    "ph": 7.2,
    "alkalinity": 8.5,
    "turbidity": 2.1,
    "observations": ["Clear water", "Good quality"],
    "imagePaths": ["image1.jpg", "image2.jpg"],
    "submittedAt": "2024-01-15T10:30:00",
    "valid": true
  },
  {
    "id": 2,
    "citizenId": "C201",
    "postcode": "NE2 4AB",
    "temperature": 24.5,
    "ph": 6.8,
    "alkalinity": null,
    "turbidity": null,
    "observations": ["Slightly cloudy"],
    "imagePaths": ["water_sample_1.jpg"],
    "submittedAt": "2024-01-15T10:35:00",
    "valid": true
  }
]
```

---

## 🧪 TEST 5: Get Observation by ID (GET)

### Endpoint
```
GET http://localhost:8081/api/observations/1
```

### Steps:
1. Create new request in Postman
2. Set method to **GET**
3. Enter URL: `http://localhost:8081/api/observations/1`
   - Replace `1` with the actual ID from previous POST response
4. Click **Send**

### Expected Response:
- **Status**: `200 OK`
- **Response Body**: Single observation object with id=1

### Test with Invalid ID:
- URL: `http://localhost:8081/api/observations/999`
- **Expected Status**: `404 Not Found`
- **Response Body**:
```json
{
  "timestamp": "2024-01-15T10:40:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Observation not found with id: 999"
}
```

---

## 🧪 TEST 6: Get Observations by Citizen ID (GET)

### Endpoint
```
GET http://localhost:8081/api/observations/citizen/C200
```

### Steps:
1. Create new request in Postman
2. Set method to **GET**
3. Enter URL: `http://localhost:8081/api/observations/citizen/C200`
   - Replace `C200` with actual citizen ID
4. Click **Send**

### Expected Response:
- **Status**: `200 OK`
- **Response Body**: Array of all observations for citizen C200
```json
[
  {
    "id": 1,
    "citizenId": "C200",
    "postcode": "NE1 7ST",
    ...
  }
]
```

---

## 🧪 TEST 7: Get Observations by Postcode (GET)

### Endpoint
```
GET http://localhost:8081/api/observations/postcode/NE1%207ST
```

### Steps:
1. Create new request in Postman
2. Set method to **GET**
3. Enter URL: `http://localhost:8081/api/observations/postcode/NE1 7ST`
   - Postman will automatically encode spaces as `%20`
   - Or use: `http://localhost:8081/api/observations/postcode/NE1%207ST`
4. Click **Send**

### Expected Response:
- **Status**: `200 OK`
- **Response Body**: Array of all observations for postcode NE1 7ST

---

## 🧪 TEST 8: Validation Error Test (POST - Invalid Data)

### Test missing required fields:

```json
{
  "postcode": "NE1 7ST",
  "temperature": 26.3
}
```

### Expected Response:
- **Status**: `400 Bad Request`
- **Response Body**:
```json
{
  "timestamp": "2024-01-15T10:45:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "errors": {
    "citizenId": "Citizen ID is required"
  }
}
```

---

## 🧪 TEST 9: Invalid Observation (POST - No Measurements or Observations)

### Test validation logic:

```json
{
  "citizenId": "C300",
  "postcode": "NE4 6EF"
}
```

### Expected Response:
- **Status**: `201 Created`
- **Response Body**: `valid: false` (because no measurements or observations provided)

---

## 📋 Quick Reference - All Endpoints

| Method | Endpoint | Description | Status Codes |
|--------|----------|-------------|--------------|
| POST | `/api/observations` | Create observation | 201, 400 |
| GET | `/api/observations` | Get all observations | 200 |
| GET | `/api/observations/{id}` | Get by ID | 200, 404 |
| GET | `/api/observations/citizen/{citizenId}` | Get by citizen | 200 |
| GET | `/api/observations/postcode/{postcode}` | Get by postcode | 200 |

---

## 🎯 Testing Checklist

- [ ] POST - Create observation with all fields
- [ ] POST - Create observation with partial data
- [ ] POST - Test validation (missing required fields)
- [ ] GET - Get all observations
- [ ] GET - Get observation by ID (valid ID)
- [ ] GET - Get observation by ID (invalid ID - should return 404)
- [ ] GET - Get observations by citizen ID
- [ ] GET - Get observations by postcode
- [ ] Verify `valid` field is set correctly
- [ ] Verify `submittedAt` timestamp is present

---

## 💡 Tips

1. **Save Requests**: Save each request in a Postman collection for easy reuse
2. **Variables**: Use Postman variables for base URL (e.g., `{{baseUrl}}/api/observations`)
3. **Environment**: Create a Postman environment with variable `baseUrl = http://localhost:8081`
4. **Check Database**: Verify data in MySQL Workbench after POST requests
5. **Check Logs**: Monitor Spring Boot console for SQL queries and validation messages

---

## 🐛 Troubleshooting

### Issue: Connection refused
- **Solution**: Make sure Spring Boot application is running on port 8081

### Issue: Database connection error
- **Solution**: Check MySQL is running and database `citizendb` exists

### Issue: 404 Not Found
- **Solution**: Verify the endpoint URL is correct and application is running

### Issue: 400 Bad Request
- **Solution**: Check JSON format and required fields (citizenId, postcode)

### Issue: 500 Internal Server Error
- **Solution**: Check Spring Boot console logs for detailed error messages

---

## 📸 Screenshots to Capture

For your submission, capture screenshots of:
1. POST request creating an observation (request + response)
2. GET all observations response
3. GET by ID response
4. GET by citizen ID response
5. GET by postcode response
6. Validation error response (400 Bad Request)
7. Not found error response (404)

Save these screenshots in the `data/` folder for your submission.


