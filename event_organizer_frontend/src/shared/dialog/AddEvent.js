import React, { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    TextField,
    Button,
    DialogActions,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    Alert
} from '@mui/material';
import axios from 'axios';

const AddEvent = ({ open, handleClose }) => {
    const [name, setName] = useState('');
    const [date, setDate] = useState('');
    const [time, setTime] = useState('');
    const [location, setLocation] = useState('');
    const [organizer, setOrganizer] = useState('');
    const [locations, setLocations] = useState([]);
    const [organizers, setOrganizers] = useState([]);
    const [error, setError] = useState('');
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));

    useEffect(() => {
        const fetchLocations = async () => {
            try {
                const response = await axios.get('http://localhost:8081/api/location/all', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setLocations(response.data);
            } catch (err) {
                setError(err.response?.data || 'Failed to fetch locations.');
            }
        };

        const fetchOrganizers = async () => {
            try {
                const response = await axios.get('http://localhost:8081/api/user/role/organizer', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setOrganizers(response.data);
            } catch (err) {
                setError(err.response?.data || 'Failed to fetch organizers.');
            }
        };

        fetchLocations();
        fetchOrganizers();
    }, [token]);

    const resetForm = () => {
        setName('');
        setDate('');
        setTime('');
        setLocation('');
        setOrganizer('');
        setError('');
    };

    const handleSubmit = async () => {
        try {
            const eventDTO = {
                name,
                date,
                time,
                location,
                organizer: user.userType === 'organizer' ? user.id : organizer
            };

            await axios.post('http://localhost:8081/api/event/create', eventDTO, {
                headers: { Authorization: `Bearer ${token}` }
            });
            handleClose(true);
            resetForm();
        } catch (err) {
            setError(err.response?.data || 'Failed to create event.');
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
            <DialogTitle>Add Event</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: '5px' }}>{error}</Alert>}
                <TextField
                    autoFocus
                    margin="dense"
                    label="Event Name"
                    required
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="Date"
                    type="date"
                    required
                    fullWidth
                    variant="outlined"
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    InputLabelProps={{
                        shrink: true,
                    }}
                />
                <TextField
                    margin="dense"
                    label="Time"
                    type="time"
                    required
                    fullWidth
                    variant="outlined"
                    value={time}
                    onChange={(e) => setTime(e.target.value)}
                    InputLabelProps={{
                        shrink: true,
                    }}
                />
                <FormControl fullWidth margin="dense">
                    <InputLabel>Location</InputLabel>
                    <Select
                        value={location}
                        label="Location"
                        onChange={(e) => setLocation(e.target.value)}
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {locations.map((loc) => (
                            <MenuItem key={loc.id} value={loc.id}>
                                {loc.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
                {user.userType !== 'organizer' && (
                    <FormControl fullWidth margin="dense">
                        <InputLabel>Organizer</InputLabel>
                        <Select
                            value={organizer}
                            label="Organizer"
                            onChange={(e) => setOrganizer(e.target.value)}
                        >
                            <MenuItem value="">
                                <em>None</em>
                            </MenuItem>
                            {organizers.map((org) => (
                                <MenuItem key={org.id} value={org.id}>
                                    {org.name}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                )}
                {user.userType === 'organizer' && (
                    <TextField
                        margin="dense"
                        label="Organizer"
                        type="text"
                        fullWidth
                        variant="outlined"
                        value={user.name}
                        disabled
                    />
                )}
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

export default AddEvent;