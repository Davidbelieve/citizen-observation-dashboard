import { Link } from 'react-router-dom'

export function Breadcrumb({ currentPage }) {
  return (
    <nav style={{ 
      marginBottom: '20px',
      fontSize: '14px',
      color: '#6b7280'
    }}>
      <Link 
        to="/hub" 
        style={{ 
          color: '#2563eb',
          textDecoration: 'none',
          transition: 'color 0.2s'
        }}
        onMouseOver={(e) => e.target.style.color = '#1d4ed8'}
        onMouseOut={(e) => e.target.style.color = '#2563eb'}
      >
        Home
      </Link>
      <span style={{ margin: '0 8px', color: '#9ca3af' }}>/</span>
      <span style={{ color: '#374151', fontWeight: '500' }}>{currentPage}</span>
    </nav>
  )
}

