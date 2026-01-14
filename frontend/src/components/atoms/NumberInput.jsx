import { forwardRef } from 'react'

/**
 * Reusable NumberInput component for numeric inputs
 */
export const NumberInput = forwardRef(({ 
  name,
  placeholder,
  error,
  disabled = false,
  min,
  max,
  step = 'any',
  className = '',
  style = {},
  ...props 
}, ref) => {
  return (
    <input
      ref={ref}
      type="number"
      name={name}
      id={name}
      placeholder={placeholder}
      disabled={disabled}
      min={min}
      max={max}
      step={step}
      className={className}
      style={{
        width: '100%',
        padding: '8px',
        border: error ? '2px solid #ef4444' : '1px solid #d1d5db',
        borderRadius: '6px',
        fontSize: '14px',
        fontFamily: 'inherit',
        transition: 'border-color 0.2s, box-shadow 0.2s',
        outline: 'none',
        ...(error && {
          boxShadow: '0 0 0 3px rgba(239, 68, 68, 0.1)'
        }),
        ...(disabled && {
          backgroundColor: '#f3f4f6',
          cursor: 'not-allowed',
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
    />
  )
})

NumberInput.displayName = 'NumberInput'

