import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    TextField,
    Button,
    DialogActions,
    Alert
} from '@mui/material';
import axios from 'axios';

const AddLocation = ({ open, handleClose }) => {
    const [name, setName] = useState('');
    const [address, setAddress] = useState('');
    const [capacity, setCapacity] = useState('');
    const [error, setError] = useState('');
    const token = localStorage.getItem('token');

    const resetForm = () => {
        setName('');
        setAddress('');
        setCapacity('');
        setError('');
    };

    const handleSubmit = async () => {
        try {
            const locationDTO = {
                name,
                address,
                capacity
            };

            await axios.post('http://localhost:8081/api/location/create', locationDTO, {
                headers: { Authorization: `Bearer ${token}` }
            });
            handleClose(true);
            resetForm();
        } catch (err) {
            setError(err.response?.data || 'Failed to create location.');
        }
    };

    const handleDialogClose = (submitSuccessful) => {
        if (!submitSuccessful) {
            resetForm();
        }
        handleClose(submitSuccessful);
    };

    return (
        <Dialog open={open} onClose={() => handleDialogClose(false)} sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Add Location</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: '5px' }}>{error}</Alert>}
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
                <Button onClick={() => handleDialogClose(false)} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleSubmit} color="primary">
                    Submit
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default AddLocation;
