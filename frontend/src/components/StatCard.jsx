export function StatCard({ label, value, icon }) {
  return (
    <div
      style={{
        backgroundColor: '#f9fafb',
        border: '1px solid #e5e7eb',
        borderRadius: '8px',
        padding: '20px',
        textAlign: 'center',
      }}
    >
      {icon && (
        <div style={{ fontSize: '32px', marginBottom: '8px' }}>{icon}</div>
      )}
      <div style={{ fontSize: '14px', color: '#6b7280', marginBottom: '4px' }}>
        {label}
      </div>
      <div style={{ fontSize: '28px', fontWeight: 'bold', color: '#111827' }}>
        {value}
      </div>
    </div>
  );
}
