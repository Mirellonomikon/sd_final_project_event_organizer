import React, { useState, useEffect } from 'react';
import {
    Button,
    TextField,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Select,
    MenuItem,
    FormControl,
    InputLabel,
    Alert,
} from '@mui/material';
import axios from 'axios';

const AddEvent = ({ open, handleClose }) => {
    const [name, setName] = useState('');
    const [eventType, setEventType] = useState('');
    const [eventDate, setEventDate] = useState('');
    const [eventTime, setEventTime] = useState('');
    const [location, setLocation] = useState('');
    const [organizer, setOrganizer] = useState('');
    const [ticketsAvailable, setTicketsAvailable] = useState('');
    const [price, setPrice] = useState('');
    const [onSale, setOnSale] = useState('');
    const [locations, setLocations] = useState([]);
    const [organizers, setOrganizers] = useState([]);
    const [error, setError] = useState('');

    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));

    useEffect(() => {
        if (open) {
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
        } else {
            resetForm();
        }
    }, [open, token]);

    const resetForm = () => {
        setName('');
        setEventType('');
        setEventDate('');
        setEventTime('');
        setLocation('');
        setOrganizer('');
        setTicketsAvailable('');
        setPrice('');
        setOnSale('');
        setError('');
    };

    const handleLocationChange = async (locationId) => {
        setLocation(locationId);
        if (locationId) {
            try {
                const response = await axios.get(`http://localhost:8081/api/location/${locationId}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setTicketsAvailable(response.data.capacity);
            } catch (err) {
                setError(err.response?.data || 'Failed to fetch location capacity.');
            }
        } else {
            setTicketsAvailable('');
        }
    };

    const handleSave = async () => {
        try {
            const eventDTO = {
                name,
                eventType,
                eventDate,
                eventTime,
                location,
                organizer: user.userType === 'organizer' ? user.id : organizer,
                ticketsAvailable,
                price,
                onSale
            };

            await axios.post('http://localhost:8081/api/event/create', eventDTO, {
                headers: { Authorization: `Bearer ${token}` }
            });
            handleClose(true);
        } catch (err) {
            setError(err.response?.data || 'Failed to save event.');
        }
    };

    return (
        <Dialog open={open} onClose={() => handleClose(false)} sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Add Event</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: "5px" }}>{error}</Alert>}
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
                    label="Event Type"
                    required
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={eventType}
                    onChange={(e) => setEventType(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="Event Date"
                    type="date"
                    required
                    fullWidth
                    variant="outlined"
                    value={eventDate}
                    onChange={(e) => setEventDate(e.target.value)}
                    InputLabelProps={{
                        shrink: true,
                    }}
                />
                <TextField
                    margin="dense"
                    label="Event Time"
                    type="time"
                    required
                    fullWidth
                    variant="outlined"
                    value={eventTime}
                    onChange={(e) => setEventTime(e.target.value)}
                    InputLabelProps={{
                        shrink: true,
                    }}
                />
                <FormControl fullWidth margin="dense">
                    <InputLabel>Location</InputLabel>
                    <Select
                        value={location}
                        label="Location"
                        onChange={(e) => handleLocationChange(e.target.value)}
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
                <TextField
                    margin="dense"
                    label="Price"
                    required
                    type="number"
                    fullWidth
                    variant="outlined"
                    value={price}
                    onChange={(e) => setPrice(e.target.value)}
                />
                <TextField
                    margin="dense"
                    label="On Sale Percentage"
                    required
                    type="number"
                    fullWidth
                    variant="outlined"
                    value={onSale}
                    onChange={(e) => setOnSale(e.target.value)}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={() => handleClose(false)} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleSave} color="primary">
                    Save
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default AddEvent;
