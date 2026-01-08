export function ObservationList({ observations }) {
  if (!observations || observations.length === 0) {
    return (
      <div
        style={{
          textAlign: 'center',
          padding: '40px 20px',
          backgroundColor: '#f8fafc',
          borderRadius: '12px',
          border: '2px dashed #cbd5e1',
        }}
      >
        <p
          style={{
            color: '#64748b',
            fontSize: '15px',
            margin: 0,
          }}
        >
          No recent observations
        </p>
      </div>
    );
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      {observations.map((obs, index) => (
        <div
          key={obs.id || index}
          style={{
            padding: '20px',
            backgroundColor: '#f8fafc',
            borderRadius: '12px',
            border: '1px solid #e2e8f0',
            transition: 'all 0.2s',
            cursor: 'pointer',
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.backgroundColor = '#ffffff';
            e.currentTarget.style.borderColor = '#0ea5e9';
            e.currentTarget.style.transform = 'translateX(4px)';
            e.currentTarget.style.boxShadow =
              '0 4px 12px rgba(14, 165, 233, 0.1)';
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.backgroundColor = '#f8fafc';
            e.currentTarget.style.borderColor = '#e2e8f0';
            e.currentTarget.style.transform = 'translateX(0)';
            e.currentTarget.style.boxShadow = 'none';
          }}
        >
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              marginBottom: '12px',
              alignItems: 'center',
            }}
          >
            <strong
              style={{
                color: '#0ea5e9',
                fontSize: '17px',
                fontWeight: '700',
              }}
            >
              📍 {obs.postcode}
            </strong>
            <span
              style={{
                color: '#64748b',
                fontSize: '13px',
                backgroundColor: '#ffffff',
                padding: '6px 12px',
                borderRadius: '8px',
                border: '1px solid #e2e8f0',
                fontWeight: '500',
              }}
            >
              {new Date(obs.timestamp).toLocaleDateString()}
            </span>
          </div>
          <div
            style={{
              fontSize: '15px',
              color: '#334155',
              marginBottom: '12px',
              lineHeight: '1.6',
              fontWeight: '500',
            }}
          >
            {obs.observation}
          </div>
          {obs.measurements && (
            <div
              style={{
                fontSize: '13px',
                color: '#64748b',
                display: 'flex',
                gap: '12px',
                marginTop: '12px',
              }}
            >
              <span
                style={{
                  backgroundColor: '#ffffff',
                  padding: '8px 14px',
                  borderRadius: '8px',
                  border: '1px solid #e2e8f0',
                  fontWeight: '600',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                }}
              >
                <span style={{ fontSize: '16px' }}>🌡️</span>
                {obs.measurements.temperature}°C
              </span>
              <span
                style={{
                  backgroundColor: '#ffffff',
                  padding: '8px 14px',
                  borderRadius: '8px',
                  border: '1px solid #e2e8f0',
                  fontWeight: '600',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                }}
              >
                <span style={{ fontSize: '16px' }}>💧</span>
                {obs.measurements.humidity}%
              </span>
            </div>
          )}
        </div>
      ))}
    </div>
  );
}
