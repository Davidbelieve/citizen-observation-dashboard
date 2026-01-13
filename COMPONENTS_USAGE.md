# Reusable Components Usage Guide

This document provides examples of how to use the reusable form components with React Hook Form.

## Components Overview

- **Modal**: Reusable modal/dialog component
- **FormController**: Wrapper for form fields with label and error handling
- **Input**: Text input component
- **Textarea**: Textarea component
- **Select**: Dropdown select component
- **NumberInput**: Number input component
- **Button**: Reusable button component

## Basic Form Example

```jsx
import { useForm, Controller } from 'react-hook-form'
import { Modal, FormController, Input, Button } from '../components'

function MyForm({ isOpen, onClose }) {
  const { control, handleSubmit, formState: { errors }, reset } = useForm({
    defaultValues: {
      name: '',
      email: ''
    }
  })

  const onSubmit = (data) => {
    console.log(data)
    reset()
    onClose()
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="My Form">
      <form onSubmit={handleSubmit(onSubmit)}>
        <FormController
          name="name"
          label="Name"
          required
          error={errors.name}
        >
          <Controller
            name="name"
            control={control}
            rules={{ required: 'Name is required' }}
            render={({ field }) => (
              <Input
                {...field}
                placeholder="Enter your name"
                error={errors.name}
              />
            )}
          />
        </FormController>

        <FormController
          name="email"
          label="Email"
          required
          error={errors.email}
        >
          <Controller
            name="email"
            control={control}
            rules={{ 
              required: 'Email is required',
              pattern: {
                value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                message: 'Invalid email address'
              }
            }}
            render={({ field }) => (
              <Input
                {...field}
                type="email"
                placeholder="Enter your email"
                error={errors.email}
              />
            )}
          />
        </FormController>

        <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end', marginTop: '24px' }}>
          <Button type="button" variant="secondary" onClick={onClose}>
            Cancel
          </Button>
          <Button type="submit" variant="primary">
            Submit
          </Button>
        </div>
      </form>
    </Modal>
  )
}
```

## Using Select Component

```jsx
<FormController
  name="country"
  label="Country"
  required
  error={errors.country}
>
  <Controller
    name="country"
    control={control}
    rules={{ required: 'Country is required' }}
    render={({ field }) => (
      <Select
        {...field}
        placeholder="Select a country"
        options={[
          { value: 'uk', label: 'United Kingdom' },
          { value: 'us', label: 'United States' },
          { value: 'ca', label: 'Canada' }
        ]}
        error={errors.country}
      />
    )}
  />
</FormController>
```

## Using NumberInput Component

```jsx
<FormController
  name="age"
  label="Age"
  required
  error={errors.age}
>
  <Controller
    name="age"
    control={control}
    rules={{ 
      required: 'Age is required',
      min: { value: 18, message: 'Must be 18 or older' },
      max: { value: 100, message: 'Must be 100 or younger' }
    }}
    render={({ field }) => (
      <NumberInput
        {...field}
        placeholder="Enter your age"
        min={18}
        max={100}
        error={errors.age}
      />
    )}
  />
</FormController>
```

## Using Textarea Component

```jsx
<FormController
  name="message"
  label="Message"
  error={errors.message}
  helpText="Enter your message here"
>
  <Controller
    name="message"
    control={control}
    rules={{ maxLength: { value: 500, message: 'Message must be less than 500 characters' } }}
    render={({ field }) => (
      <Textarea
        {...field}
        placeholder="Enter your message"
        rows={5}
        error={errors.message}
      />
    )}
  />
</FormController>
```

## Button Variants

```jsx
<Button variant="primary">Primary Button</Button>
<Button variant="secondary">Secondary Button</Button>
<Button variant="success">Success Button</Button>
<Button variant="danger">Danger Button</Button>
<Button variant="ghost">Ghost Button</Button>
```

## Button Sizes

```jsx
<Button size="small">Small</Button>
<Button size="medium">Medium</Button>
<Button size="large">Large</Button>
```

## Modal Sizes

```jsx
<Modal size="small" title="Small Modal">...</Modal>
<Modal size="medium" title="Medium Modal">...</Modal>
<Modal size="large" title="Large Modal">...</Modal>
<Modal size="xlarge" title="Extra Large Modal">...</Modal>
```

## Custom Validation

```jsx
const validateCustom = (value) => {
  if (!value) return 'This field is required'
  if (value.length < 5) return 'Must be at least 5 characters'
  return true
}

<Controller
  name="customField"
  control={control}
  rules={{ validate: validateCustom }}
  render={({ field }) => (
    <Input {...field} error={errors.customField} />
  )}
/>
```

