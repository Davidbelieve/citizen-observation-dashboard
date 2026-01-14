export function RegionCard({ region, color, onClick }) {
  return (
    <button
      onClick={onClick}
      style={{
        backgroundColor: 'white',
        border: `3px solid ${color}`,
        borderRadius: '12px',
        padding: '24px',
        cursor: 'pointer',
        transition: 'all 0.3s ease',
        textAlign: 'left',
        width: '100%',
        boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = 'translateY(-4px)'
        e.currentTarget.style.boxShadow = '0 8px 16px rgba(0,0,0,0.15)'
        e.currentTarget.style.backgroundColor = `${color}15`
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = 'translateY(0)'
        e.currentTarget.style.boxShadow = '0 2px 8px rgba(0,0,0,0.1)'
        e.currentTarget.style.backgroundColor = 'white'
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
        <div
          style={{
            width: '48px',
            height: '48px',
            borderRadius: '50%',
            backgroundColor: color,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '24px',
            flexShrink: 0,
          }}
        >
          📍
        </div>
        <div style={{ flex: 1 }}>
          <h3 style={{ 
            margin: '0 0 4px 0', 
            fontSize: '20px',
            color: '#1f2937',
            fontWeight: '600'
          }}>
            {region}
          </h3>
          <p style={{ 
            margin: 0, 
            color: '#6b7280',
            fontSize: '14px'
          }}>
            View regional data →
          </p>
        </div>
      </div>
    </button>
  )
}