export function Leaderboard({ contributors }) {
  if (!contributors || contributors.length === 0) {
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
          No contributors yet
        </p>
      </div>
    );
  }

  const medals = ['🥇', '🥈', '🥉'];
  const gradients = [
    'linear-gradient(135deg, #fef3c7 0%, #fde68a 100%)',
    'linear-gradient(135deg, #f1f5f9 0%, #cbd5e1 100%)',
    'linear-gradient(135deg, #fed7aa 0%, #fdba74 100%)',
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      {contributors.slice(0, 3).map((contributor, index) => (
        <div
          key={contributor.id || index}
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            padding: '20px',
            background:
              index === 0
                ? 'linear-gradient(135deg, #e0f2fe 0%, #bae6fd 100%)'
                : '#f8fafc',
            borderRadius: '12px',
            border: index === 0 ? '2px solid #0ea5e9' : '1px solid #e2e8f0',
            transition: 'all 0.3s',
            cursor: 'pointer',
          }}
          onMouseEnter={(e) => {
            e.currentTarget.style.transform = 'translateX(6px) scale(1.02)';
            e.currentTarget.style.boxShadow =
              '0 8px 20px rgba(14, 165, 233, 0.15)';
          }}
          onMouseLeave={(e) => {
            e.currentTarget.style.transform = 'translateX(0) scale(1)';
            e.currentTarget.style.boxShadow = 'none';
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <div
              style={{
                width: '48px',
                height: '48px',
                borderRadius: '12px',
                background: gradients[index],
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '24px',
                boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
              }}
            >
              {medals[index]}
            </div>
            <span
              style={{
                fontWeight: index === 0 ? '700' : '600',
                color: '#0f172a',
                fontSize: index === 0 ? '18px' : '16px',
                letterSpacing: '-0.01em',
              }}
            >
              {contributor.username || contributor.name}
            </span>
          </div>
          <div
            style={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'flex-end',
            }}
          >
            <span
              style={{
                fontWeight: '800',
                color: '#0ea5e9',
                fontSize: '24px',
                letterSpacing: '-0.02em',
              }}
            >
              {contributor.points}
            </span>
            <span
              style={{
                fontSize: '11px',
                color: '#64748b',
                textTransform: 'uppercase',
                fontWeight: '600',
                letterSpacing: '0.5px',
              }}
            >
              POINTS
            </span>
          </div>
        </div>
      ))}
    </div>
  );
}
