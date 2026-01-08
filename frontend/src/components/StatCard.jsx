export function StatCard({ label, value, icon }) {
  return (
    <div
      style={{
        backgroundColor: '#ffffff',
        border: '1px solid #e2e8f0',
        borderRadius: '16px',
        padding: '28px',
        textAlign: 'center',
        boxShadow: '0 1px 3px rgba(0, 0, 0, 0.05)',
        transition: 'all 0.3s',
        cursor: 'default',
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = 'translateY(-4px)';
        e.currentTarget.style.boxShadow =
          '0 12px 24px rgba(14, 165, 233, 0.15)';
        e.currentTarget.style.borderColor = '#0ea5e9';
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = 'translateY(0)';
        e.currentTarget.style.boxShadow = '0 1px 3px rgba(0, 0, 0, 0.05)';
        e.currentTarget.style.borderColor = '#e2e8f0';
      }}
    >
      {icon && (
        <div
          style={{
            fontSize: '48px',
            marginBottom: '16px',
            filter: 'drop-shadow(0 4px 8px rgba(14, 165, 233, 0.15))',
          }}
        >
          {icon}
        </div>
      )}
      <div
        style={{
          fontSize: '13px',
          color: '#64748b',
          marginBottom: '8px',
          textTransform: 'uppercase',
          letterSpacing: '1px',
          fontWeight: '600',
        }}
      >
        {label}
      </div>
      <div
        style={{
          fontSize: '36px',
          fontWeight: '800',
          color: '#0ea5e9',
          letterSpacing: '-0.02em',
        }}
      >
        {value}
      </div>
    </div>
  );
}
