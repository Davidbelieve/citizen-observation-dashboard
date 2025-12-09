export function Card({ children, title }) {
  return (
    <div
      style={{
        backgroundColor: 'white',
        borderRadius: '8px',
        padding: '24px',
        boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
        marginBottom: '16px',
      }}
    >
      {title && <h3 style={{ marginTop: 0, marginBottom: '16px' }}>{title}</h3>}
      {children}
    </div>
  );
}
