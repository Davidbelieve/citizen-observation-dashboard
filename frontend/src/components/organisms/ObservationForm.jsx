import { useForm, Controller } from 'react-hook-form'
import { Modal } from './Modal'
import { FormController } from '../molecules/FormController'
import { Input } from '../atoms/Input'
import { Textarea } from '../atoms/Textarea'
import { NumberInput } from '../atoms/NumberInput'
import { Button } from '../atoms/Button'

export function ObservationForm({ isOpen, onClose, onSubmit, loading }) {
  const {
    control,
    handleSubmit,
    formState: { errors },
    reset
  } = useForm({
    defaultValues: {
      postcode: '',
      citizenUniqueId: '',
      notes: '',
      temperatureC: '',
      pH: '',
      alkalinityMgPerL: '',
      turbidityNtu: '',
      tags: ''
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
    // Build the observation payload
    const observationData = {
      postcode: data.postcode.trim().toUpperCase(),
      citizenUniqueId: data.citizenUniqueId.trim(),
      notes: data.notes?.trim() || null
    }

    // Add measurement if any measurement fields are provided
    const hasMeasurement = data.temperatureC || data.pH || data.alkalinityMgPerL || data.turbidityNtu
    if (hasMeasurement) {
      observationData.measurement = {}
      if (data.temperatureC) observationData.measurement.temperatureC = parseFloat(data.temperatureC)
      if (data.pH) observationData.measurement.pH = parseFloat(data.pH)
      if (data.alkalinityMgPerL) observationData.measurement.alkalinityMgPerL = parseFloat(data.alkalinityMgPerL)
      if (data.turbidityNtu) observationData.measurement.turbidityNtu = parseFloat(data.turbidityNtu)
    }

    // Add tags if provided
    if (data.tags?.trim()) {
      observationData.tags = data.tags.split(',').map(tag => tag.trim()).filter(tag => tag.length > 0)
    }

    try {
      await onSubmit(observationData)
      // Reset form on success
      reset()
      onClose()
    } catch (error) {
      // Error handling is done by parent component
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
          name="notes"
          label="Notes"
          error={errors.notes}
        >
          <Controller
            name="notes"
            control={control}
            render={({ field }) => (
              <Textarea
                {...field}
                placeholder="Additional observations or comments..."
                rows={3}
                error={errors.notes}
              />
            )}
          />
        </FormController>

        <div style={{ marginBottom: '20px', padding: '16px', backgroundColor: '#f9fafb', borderRadius: '8px' }}>
          <h4 style={{ marginTop: 0, marginBottom: '12px', fontSize: '16px', fontWeight: '600', color: '#111827' }}>
            Measurements (Optional)
          </h4>
          
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
          name="tags"
          label="Tags (comma-separated)"
          error={errors.tags}
          helpText="Separate multiple tags with commas"
        >
          <Controller
            name="tags"
            control={control}
            render={({ field }) => (
              <Input
                {...field}
                placeholder="e.g., pollution, algae, clear"
                error={errors.tags}
              />
            )}
          />
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

