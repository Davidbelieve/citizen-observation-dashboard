import React, { useState } from 'react';
import axios from 'axios';
import './ObservationForm.css';

const ObservationForm = () => {
    const [formData, setFormData] = useState({
        citizenId: '',
        postcode: '',
        temperature: '',
        ph: '',
        alkalinity: '',
        turbidity: '',
        observationNotes: ''
    });
    const [selectedFile, setSelectedFile] = useState(null);
    const [message, setMessage] = useState('');

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleFileChange = (e) => {
        if (e.target.files && e.target.files[0]) {
            setSelectedFile(e.target.files[0]);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const dataPayload = {
                ...formData,
                observations: formData.observationNotes ? formData.observationNotes.split(',').map(s => s.trim()) : [],
                imagePaths: [] // Backend handles the new file addition
            };

            const submitData = new FormData();
            submitData.append('data', new Blob([JSON.stringify(dataPayload)], { type: 'application/json' }));

            if (selectedFile) {
                submitData.append('image', selectedFile);
            }

            // Updated to point to Gateway port 8080 with multipart
            await axios.post('http://localhost:8080/api/observations', submitData, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            });
            setMessage('Observation submitted successfully!');
            setFormData({
                citizenId: '',
                postcode: '',
                temperature: '',
                ph: '',
                alkalinity: '',
                turbidity: '',
                observationNotes: ''
            });
            setSelectedFile(null);
        } catch (error) {
            setMessage('Error submitting observation. Please try again.');
            console.error('Submission error:', error);
        }
    };

    return (
        <div className="observation-form-container">
            <h2>Submit Observation</h2>
            {message && <div className="message">{message}</div>}
            <form onSubmit={handleSubmit}>
                {/* Column 1: Core Info */}
                <div className="form-section">
                    <div className="form-group">
                        <label>Citizen ID*</label>
                        <input name="citizenId" value={formData.citizenId} onChange={handleChange} required placeholder="C200" />
                    </div>
                    <div className="form-group">
                        <label>Postcode*</label>
                        <input name="postcode" value={formData.postcode} onChange={handleChange} required placeholder="NE1 7ST" />
                    </div>
                    <div className="form-group">
                        <label>Upload Image</label>
                        <input type="file" onChange={handleFileChange} accept="image/*" />
                    </div>
                </div>

                {/* Column 2: Measurements */}
                <div className="form-section">
                    <label style={{ marginBottom: '0' }}>Quality Measurements (Optional)</label>
                    <div className="measurements-container">
                        <div className="form-group">
                            <label>Temp (°C)</label>
                            <input name="temperature" type="number" step="0.1" value={formData.temperature} onChange={handleChange} />
                        </div>
                        <div className="form-group">
                            <label>pH Level</label>
                            <input name="ph" type="number" step="0.1" value={formData.ph} onChange={handleChange} />
                        </div>
                        <div className="form-group">
                            <label>Alkalinity</label>
                            <input name="alkalinity" type="number" value={formData.alkalinity} onChange={handleChange} />
                        </div>
                        <div className="form-group">
                            <label>Turbidity</label>
                            <input name="turbidity" type="number" value={formData.turbidity} onChange={handleChange} />
                        </div>
                    </div>
                </div>

                {/* Column 3: Notes */}
                <div className="form-section">
                    <div className="form-group" style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                        <label>Additional Notes</label>
                        <textarea
                            name="observationNotes"
                            value={formData.observationNotes}
                            onChange={handleChange}
                            placeholder="Describe water clarity, smell, wildlife, etc..."
                            style={{ flex: 1 }}
                        />
                    </div>
                </div>

                <button type="submit" className="submit-btn" disabled={!formData.citizenId || !formData.postcode}>
                    Submit Water Observation
                </button>
            </form>
        </div>
    );
};

export default ObservationForm;
