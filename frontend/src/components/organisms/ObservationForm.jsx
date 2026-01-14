import { useForm, Controller } from 'react-hook-form'
import { Modal } from './Modal'
import { FormController } from '../molecules/FormController'
import { Input } from '../atoms/Input'
import { NumberInput } from '../atoms/NumberInput'
import { Button } from '../atoms/Button'

export function ObservationForm({ isOpen, onClose, onSubmit, loading }) {
  const {
    control,
    handleSubmit,
    formState: { errors },
    reset,
    watch
  } = useForm({
    defaultValues: {
      postcode: '',
      citizenUniqueId: '',
      observations: [],  // Array for multiple selections
      temperatureC: '',
      pH: '',
      alkalinityMgPerL: '',
      turbidityNtu: '',
      images: null
    }
  })

  const validatePostcode = (value) => {
    if (!value || !value.trim()) {
      return 'Postcode is required'
    }
    const postcodeRegex = /^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}$/i
    if (!postcodeRegex.test(value.trim())) {
      return 'Please enter a valid UK postcode'
    }
    return true
  }

  const validateCitizenId = (value) => {
    if (!value || !value.trim()) {
      return 'Citizen ID is required'
    }
    return true
  }

  const validateTemperature = (value) => {
    if (!value) return true // Optional field
    const num = parseFloat(value)
    if (isNaN(num) || num < -50 || num > 50) {
      return 'Temperature must be between -50 and 50°C'
    }
    return true
  }

  const validatePH = (value) => {
    if (!value) return true // Optional field
    const num = parseFloat(value)
    if (isNaN(num) || num < 0 || num > 14) {
      return 'pH must be between 0 and 14'
    }
    return true
  }

  const validatePositiveNumber = (value, fieldName) => {
    if (!value) return true // Optional field
    const num = parseFloat(value)
    if (isNaN(num) || num < 0) {
      return `${fieldName} must be a positive number`
    }
    return true
  }

  const onSubmitForm = async (data) => {
    // Validate: must have postcode + (at least one measurement OR at least one observation)
    const hasMeasurement = data.temperatureC || data.pH || data.alkalinityMgPerL || data.turbidityNtu
    const hasObservation = data.observations && data.observations.length > 0
    
    if (!hasMeasurement && !hasObservation) {
      alert('Please provide at least one measurement OR select at least one observation')
      return
    }

    // Build the observation payload
    const observationData = {
      postcode: data.postcode.trim().toUpperCase(),
      citizenUniqueId: data.citizenUniqueId.trim(),
      observations: data.observations && data.observations.length > 0 
        ? data.observations.join(',') 
        : null,
      images: data.images || null
    }

    // Add measurement if any measurement fields are provided
    if (hasMeasurement) {
      observationData.measurement = {}
      if (data.temperatureC) observationData.measurement.temperatureC = parseFloat(data.temperatureC)
      if (data.pH) observationData.measurement.pH = parseFloat(data.pH)
      if (data.alkalinityMgPerL) observationData.measurement.alkalinityMgPerL = parseFloat(data.alkalinityMgPerL)
      if (data.turbidityNtu) observationData.measurement.turbidityNtu = parseFloat(data.turbidityNtu)
    }

    try {
      await onSubmit(observationData)
      reset()
      onClose()
    } catch (error) {
      console.error('Form submission error:', error)
    }
  }

  const handleClose = () => {
    reset()
    onClose()
  }

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Add New Observation">
      <form onSubmit={handleSubmit(onSubmitForm)}>
        <FormController
          name="postcode"
          label="Postcode"
          required
          error={errors.postcode}
        >
          <Controller
            name="postcode"
            control={control}
            rules={{ validate: validatePostcode }}
            render={({ field }) => (
              <Input
                {...field}
                placeholder="e.g., SW1A 1AA"
                error={errors.postcode}
              />
            )}
          />
        </FormController>

        <FormController
          name="citizenUniqueId"
          label="Citizen ID"
          required
          error={errors.citizenUniqueId}
        >
          <Controller
            name="citizenUniqueId"
            control={control}
            rules={{ validate: validateCitizenId }}
            render={({ field }) => (
              <Input
                {...field}
                placeholder="Your unique citizen identifier"
                error={errors.citizenUniqueId}
              />
            )}
          />
        </FormController>

        <FormController
          name="observations"
          label="Observations (Select all that apply)"
          error={errors.observations}
          helpText="Select at least one if no measurements provided"
        >
          <Controller
            name="observations"
            control={control}
            render={({ field: { onChange, value } }) => {
              const selectedObservations = value || []
              
              const toggleObservation = (obs) => {
                if (selectedObservations.includes(obs)) {
                  onChange(selectedObservations.filter(o => o !== obs))
                } else {
                  onChange([...selectedObservations, obs])
                }
              }
              
              const observationOptions = [
                'Clear',
                'Cloudy',
                'Murky',
                'Foamy',
                'Oily',
                'Discoloured',
                'Presence of Odour'
              ]
              
              return (
                <div style={{ 
                  display: 'grid', 
                  gridTemplateColumns: '1fr 1fr',
                  gap: '8px',
                  padding: '12px',
                  border: `1px solid ${errors.observations ? '#ef4444' : '#e5e7eb'}`,
                  borderRadius: '6px',
                  backgroundColor: '#f9fafb'
                }}>
                  {observationOptions.map(obs => (
                    <label 
                      key={obs}
                      style={{ 
                        display: 'flex', 
                        alignItems: 'center', 
                        gap: '8px',
                        cursor: 'pointer',
                        padding: '6px',
                        borderRadius: '4px',
                        transition: 'background-color 0.2s'
                      }}
                      onMouseEnter={(e) => e.currentTarget.style.backgroundColor = '#ffffff'}
                      onMouseLeave={(e) => e.currentTarget.style.backgroundColor = 'transparent'}
                    >
                      <input
                        type="checkbox"
                        checked={selectedObservations.includes(obs.toUpperCase())}
                        onChange={() => toggleObservation(obs.toUpperCase())}
                        style={{ 
                          width: '16px', 
                          height: '16px',
                          cursor: 'pointer'
                        }}
                      />
                      <span style={{ fontSize: '14px', color: '#374151' }}>
                        {obs}
                      </span>
                    </label>
                  ))}
                </div>
              )
            }}
          />
          {errors.observations && (
            <p style={{ color: '#ef4444', fontSize: '12px', marginTop: '4px' }}>
              {errors.observations.message}
            </p>
          )}
        </FormController>

        <div style={{ marginBottom: '20px', padding: '16px', backgroundColor: '#f9fafb', borderRadius: '8px' }}>
          <h4 style={{ marginTop: 0, marginBottom: '12px', fontSize: '16px', fontWeight: '600', color: '#111827' }}>
            Measurements (Optional)
          </h4>
          <p style={{ fontSize: '12px', color: '#6b7280', marginTop: 0, marginBottom: '12px' }}>
            Provide at least one measurement if no observation is selected
          </p>
          
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', marginBottom: '12px' }}>
            <FormController
              name="temperatureC"
              label="Temperature (°C)"
              error={errors.temperatureC}
            >
              <Controller
                name="temperatureC"
                control={control}
                rules={{ validate: validateTemperature }}
                render={({ field }) => (
                  <NumberInput
                    {...field}
                    placeholder="e.g., 12.5"
                    step="0.1"
                    min="-50"
                    max="50"
                    error={errors.temperatureC}
                  />
                )}
              />
            </FormController>

            <FormController
              name="pH"
              label="pH"
              error={errors.pH}
            >
              <Controller
                name="pH"
                control={control}
                rules={{ validate: validatePH }}
                render={({ field }) => (
                  <NumberInput
                    {...field}
                    placeholder="e.g., 7.2"
                    step="0.1"
                    min="0"
                    max="14"
                    error={errors.pH}
                  />
                )}
              />
            </FormController>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <FormController
              name="alkalinityMgPerL"
              label="Alkalinity (mg/L)"
              error={errors.alkalinityMgPerL}
            >
              <Controller
                name="alkalinityMgPerL"
                control={control}
                rules={{ validate: (value) => validatePositiveNumber(value, 'Alkalinity') }}
                render={({ field }) => (
                  <NumberInput
                    {...field}
                    placeholder="e.g., 25.0"
                    step="0.1"
                    min="0"
                    error={errors.alkalinityMgPerL}
                  />
                )}
              />
            </FormController>

            <FormController
              name="turbidityNtu"
              label="Turbidity (NTU)"
              error={errors.turbidityNtu}
            >
              <Controller
                name="turbidityNtu"
                control={control}
                rules={{ validate: (value) => validatePositiveNumber(value, 'Turbidity') }}
                render={({ field }) => (
                  <NumberInput
                    {...field}
                    placeholder="e.g., 0.9"
                    step="0.1"
                    min="0"
                    error={errors.turbidityNtu}
                  />
                )}
              />
            </FormController>
          </div>
        </div>

        <FormController
          name="images"
          label="Images (Optional)"
          error={errors.images}
          helpText="Upload up to 3 photos of the water sample"
        >
          <Controller
            name="images"
            control={control}
            rules={{
              validate: (files) => {
                if (files && files.length > 3) {
                  return 'Maximum 3 images allowed'
                }
                return true
              }
            }}
            render={({ field: { onChange, value, ...field } }) => (
              <input
                {...field}
                type="file"
                accept="image/*"
                multiple
                onChange={(e) => {
                  const files = Array.from(e.target.files).slice(0, 3)
                  onChange(files)
                }}
                style={{
                  padding: '8px',
                  border: `1px solid ${errors.images ? '#ef4444' : '#d1d5db'}`,
                  borderRadius: '6px',
                  width: '100%',
                  fontSize: '14px'
                }}
              />
            )}
          />
          {errors.images && (
            <p style={{ color: '#ef4444', fontSize: '12px', marginTop: '4px' }}>
              {errors.images.message}
            </p>
          )}
        </FormController>

        <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '24px' }}>
          <Button
            type="button"
            variant="secondary"
            onClick={handleClose}
            disabled={loading}
          >
            Cancel
          </Button>
          <Button
            type="submit"
            variant="primary"
            disabled={loading}
            loading={loading}
          >
            Submit Observation
          </Button>
        </div>
      </form>
    </Modal>
  )
}