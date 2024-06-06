import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    TextField,
    Button,
    DialogActions,
    Alert,
} from '@mui/material';
import axios from 'axios';

const UpdateLocation= ({ open, handleClose, locationId }) => {
    const [defaultLocation, setDefaultLocation] = useState(null);
    const [name, setName] = useState('');
    const [address, setAddress] = useState('');
    const [capacity, setCapacity] = useState('');
    const [error, setError] = useState('');
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (open) {
            const fetchLocationDetails = async () => {
                try {
                    const response = await axios.get(`http://localhost:8081/api/location/${locationId}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    });
                    const location = response.data;
                    setDefaultLocation(location);

                    setName(location.name);
                    setAddress(location.address);
                    setCapacity(location.capacity);
                } catch (err) {
                    setError(err.response?.data || 'Failed to fetch location details.');
                }
            };

            fetchLocationDetails();
        } else {
            setError('');
        }
    }, [open, locationId, token]);

    const handleUpdate = async () => {
        try {
            const locationDTO = {
                name,
                address,
                capacity
            };

            await axios.put(`http://localhost:8081/api/location/${locationId}`, locationDTO, {
                headers: { Authorization: `Bearer ${token}` }
            });
            handleClose(true);
        } catch (err) {
            setError(err.response?.data || 'Failed to update location.');
        }
    };

    const handleReset = () => {
        if (defaultLocation) {
            setName(defaultLocation.name);
            setAddress(defaultLocation.address);
            setCapacity(defaultLocation.capacity);
        }
    };

    return (
        <Dialog open={open} onClose={() => { handleClose(false); handleReset(); }} sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Update Location</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: "5px" }}>{error}</Alert>}
                <TextField
                    autoFocus
                    margin="dense"
                    label="Location Name"
                    required
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="Address"
                    required
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="Capacity"
                    required
                    type="number"
                    fullWidth
                    variant="outlined"
                    value={capacity}
                    onChange={(e) => setCapacity(e.target.value)}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={() => handleClose(false)} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleUpdate} color="primary">
                    Update
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default UpdateLocation;
