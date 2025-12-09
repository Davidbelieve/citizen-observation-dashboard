export function ObservationList({ observations }) {
  if (!observations || observations.length === 0) {
    return (
      <p style={{ color: '#6b7280', fontStyle: 'italic' }}>
        No recent observations
      </p>
    );
  }

  return (
    <div>
      {observations.map((obs, index) => (
        <div
          key={obs.id || index}
          style={{
            padding: '12px',
            borderBottom: '1px solid #e5e7eb',
            backgroundColor: index % 2 === 0 ? '#f9fafb' : 'white',
          }}
        >
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              marginBottom: '4px',
            }}
          >
            <strong>{obs.postcode}</strong>
            <span style={{ color: '#6b7280', fontSize: '14px' }}>
              {new Date(obs.timestamp).toLocaleDateString()}
            </span>
          </div>
          <div style={{ fontSize: '14px', color: '#374151' }}>
            {obs.observation}
          </div>
          {obs.measurements && (
            <div
              style={{ fontSize: '12px', color: '#6b7280', marginTop: '4px' }}
            >
              Temp: {obs.measurements.temperature}°C | Humidity:{' '}
              {obs.measurements.humidity}%
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
