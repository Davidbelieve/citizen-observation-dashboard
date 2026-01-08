export function Card({ children, title }) {
  return (
    <div
      style={{
        backgroundColor: '#ffffff',
        borderRadius: '16px',
        padding: '28px',
        boxShadow:
          '0 1px 3px rgba(0, 0, 0, 0.08), 0 8px 24px rgba(0, 0, 0, 0.06)',
        marginBottom: '20px',
        border: '1px solid #e2e8f0',
        transition: 'transform 0.2s, box-shadow 0.2s',
      }}
      onMouseEnter={(e) => {
        e.currentTarget.style.transform = 'translateY(-2px)';
        e.currentTarget.style.boxShadow =
          '0 4px 6px rgba(0, 0, 0, 0.08), 0 12px 32px rgba(0, 0, 0, 0.08)';
      }}
      onMouseLeave={(e) => {
        e.currentTarget.style.transform = 'translateY(0)';
        e.currentTarget.style.boxShadow =
          '0 1px 3px rgba(0, 0, 0, 0.08), 0 8px 24px rgba(0, 0, 0, 0.06)';
      }}
    >
      {title && (
        <h3
          style={{
            marginTop: 0,
            marginBottom: '20px',
            color: '#0f172a',
            fontSize: '20px',
            fontWeight: '700',
            letterSpacing: '-0.02em',
          }}
        >
          {title}
        </h3>
      )}
      {children}
    </div>
  );
}
