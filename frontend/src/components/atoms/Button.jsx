/**
 * Reusable Button component
 */
export function Button({ 
  children, 
  variant = 'primary',
  size = 'medium',
  disabled = false,
  loading = false,
  type = 'button',
  onClick,
  className = '',
  style = {},
  ...props 
}) {
  const variantStyles = {
    primary: {
      backgroundColor: disabled || loading ? '#9ca3af' : '#2563eb',
      color: 'white',
      border: 'none'
    },
    secondary: {
      backgroundColor: disabled || loading ? '#f3f4f6' : 'white',
      color: disabled || loading ? '#9ca3af' : '#374151',
      border: '1px solid #d1d5db'
    },
    success: {
      backgroundColor: disabled || loading ? '#9ca3af' : '#10b981',
      color: 'white',
      border: 'none'
    },
    danger: {
      backgroundColor: disabled || loading ? '#9ca3af' : '#ef4444',
      color: 'white',
      border: 'none'
    },
    ghost: {
      backgroundColor: 'transparent',
      color: disabled || loading ? '#9ca3af' : '#374151',
      border: 'none'
    }
  }

  const sizeStyles = {
    small: {
      padding: '6px 12px',
      fontSize: '12px'
    },
    medium: {
      padding: '10px 20px',
      fontSize: '14px'
    },
    large: {
      padding: '12px 24px',
      fontSize: '16px'
    }
  }

  return (
    <button
      type={type}
      disabled={disabled || loading}
      onClick={onClick}
      className={className}
      style={{
        ...variantStyles[variant],
        ...sizeStyles[size],
        fontWeight: '500',
        borderRadius: '6px',
        cursor: disabled || loading ? 'not-allowed' : 'pointer',
        transition: 'background-color 0.2s, opacity 0.2s',
        opacity: disabled || loading ? 0.6 : 1,
        ...style
      }}
      onMouseEnter={(e) => {
        if (!disabled && !loading && variant !== 'ghost') {
          const hoverColors = {
            primary: '#1d4ed8',
            secondary: '#f9fafb',
            success: '#059669',
            danger: '#dc2626'
          }
          if (hoverColors[variant]) {
            e.target.style.backgroundColor = hoverColors[variant]
          }
        }
      }}
      onMouseLeave={(e) => {
        if (!disabled && !loading) {
          e.target.style.backgroundColor = variantStyles[variant].backgroundColor
        }
      }}
      {...props}
    >
      {loading ? 'Loading...' : children}
    </button>
  )
}

