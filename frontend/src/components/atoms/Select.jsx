import { forwardRef } from 'react'

/**
 * Reusable Select component
 */
export const Select = forwardRef(({ 
  name,
  placeholder = 'Select an option',
  error,
  disabled = false,
  options = [],
  className = '',
  style = {},
  ...props 
}, ref) => {
  return (
    <select
      ref={ref}
      name={name}
      id={name}
      disabled={disabled}
      className={className}
      style={{
        width: '100%',
        padding: '10px',
        border: error ? '2px solid #ef4444' : '1px solid #d1d5db',
        borderRadius: '6px',
        fontSize: '14px',
        fontFamily: 'inherit',
        backgroundColor: 'white',
        cursor: disabled ? 'not-allowed' : 'pointer',
        transition: 'border-color 0.2s, box-shadow 0.2s',
        outline: 'none',
        ...(error && {
          boxShadow: '0 0 0 3px rgba(239, 68, 68, 0.1)'
        }),
        ...(disabled && {
          backgroundColor: '#f3f4f6',
          opacity: 0.6
        }),
        ...style
      }}
      onFocus={(e) => {
        if (!error && !disabled) {
          e.target.style.borderColor = '#2563eb'
          e.target.style.boxShadow = '0 0 0 3px rgba(37, 99, 235, 0.1)'
        }
      }}
      onBlur={(e) => {
        if (!error) {
          e.target.style.borderColor = '#d1d5db'
          e.target.style.boxShadow = 'none'
        }
      }}
      {...props}
    >
      {placeholder && (
        <option value="" disabled>
          {placeholder}
        </option>
      )}
      {options.map((option) => {
        if (typeof option === 'string') {
          return (
            <option key={option} value={option}>
              {option}
            </option>
          )
        }
        return (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        )
      })}
    </select>
  )
})

Select.displayName = 'Select'

