export function Leaderboard({ contributors }) {
  if (!contributors || contributors.length === 0) {
    return (
      <p style={{ color: '#6b7280', fontStyle: 'italic' }}>
        No contributors yet
      </p>
    );
  }

  const medals = ['🥇', '🥈', '🥉'];

  return (
    <div>
      {contributors.slice(0, 3).map((contributor, index) => (
        <div
          key={contributor.id || index}
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            padding: '12px',
            borderBottom: index < 2 ? '1px solid #e5e7eb' : 'none',
            backgroundColor: index === 0 ? '#fef3c7' : 'white',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <span style={{ fontSize: '24px' }}>{medals[index]}</span>
            <span style={{ fontWeight: index === 0 ? 'bold' : 'normal' }}>
              {contributor.username || contributor.name}
            </span>
          </div>
          <span
            style={{
              fontWeight: 'bold',
              color: '#2563eb',
              fontSize: '18px',
            }}
          >
            {contributor.points} pts
          </span>
        </div>
      ))}
    </div>
  );
}
