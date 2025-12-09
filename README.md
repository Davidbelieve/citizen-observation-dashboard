# UI Components Guide

## Components Available

### Card
Container component with optional title.
```jsx
<Card title="My Title">
  Content goes here
</Card>
```

### StatCard
Display metrics with icons.
```jsx
<StatCard label="Total Users" value={42} icon="👥" />
```

### ObservationList
Shows list of observations.
```jsx
<ObservationList observations={observationsArray} />
```

### Leaderboard
Top 3 contributors with medals.
```jsx
<Leaderboard contributors={contributorsArray} />
```

## Data Formats Required

### Observations Array
```json
[
  {
    "id": 1,
    "postcode": "NE1 4ST",
    "timestamp": "2024-12-09T10:30:00Z",
    "observation": "Weather description",
    "measurements": {
      "temperature": 15.5,
      "humidity": 67
    }
  }
]
```

### Contributors Array
```json
[
  {
    "id": 1,
    "username": "user123",
    "points": 150
  }
]
```

## Team Member API Endpoints

Each dashboard needs these endpoints:

- `GET /observations/count?region={region}` → Returns `{ "count": 42 }`
- `GET /observations/recent?region={region}&limit=5` → Returns observations array
- `GET /contributors/leaderboard?region={region}&limit=3` → Returns contributors array

## API Base URLs

| Region | Developer | Port |
|--------|-----------|------|
| North East | [Name] | 8081 |
| North West | [Name] | 8082 |
| East Midlands | [Name] | 8083 |
| West Midlands | [Name] | 8084 |
| South East | [Name] | 8085 |
