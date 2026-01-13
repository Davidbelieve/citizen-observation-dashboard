/**
 * FormController - A wrapper component for form fields that integrates with React Hook Form
 * Provides consistent styling, error handling, and label management
 */
export function FormController({ 
  name, 
  label, 
  required = false, 
  error, 
  children, 
  helpText,
  className = ''
}) {
  return (
    <div style={{ marginBottom: '20px' }} className={className}>
      {label && (
        <label 
          htmlFor={name}
          style={{ 
            display: 'block', 
            marginBottom: '8px', 
            fontWeight: '500', 
            color: '#374151',
            fontSize: '14px'
          }}
        >
          {label}
          {required && <span style={{ color: '#ef4444', marginLeft: '4px' }}>*</span>}
        </label>
      )}
      {children}
      {error && (
        <p style={{ 
          color: '#ef4444', 
          fontSize: '12px', 
          marginTop: '4px',
          marginBottom: 0
        }}>
          {error.message || error}
        </p>
      )}
      {helpText && !error && (
        <p style={{ 
          color: '#6b7280', 
          fontSize: '12px', 
          marginTop: '4px',
          marginBottom: 0
        }}>
          {helpText}
        </p>
      )}
    </div>
  )
}

