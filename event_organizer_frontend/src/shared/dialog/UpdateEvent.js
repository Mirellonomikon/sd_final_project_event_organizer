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
    Alert,
} from '@mui/material';
import axios from 'axios';

const UpdateEvent = ({ open, onClose, eventId }) => {
    const [defaultEvent, setDefaultEvent] = useState(null);
    const [name, setName] = useState('');
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
            const fetchEventDetails = async () => {
                try {
                    const response = await axios.get(`http://localhost:8081/api/event/${eventId}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    });
                    const event = response.data;
                    setDefaultEvent(event);

                    setName(event.name);
                    setEventDate(event.eventDate);
                    setEventTime(event.eventTime);
                    setLocation(event.location.id);
                    setOrganizer(event.organizer.id);
                    setTicketsAvailable(event.ticketsAvailable);
                    setPrice(event.price);
                    setOnSale(event.onSale);

                    const fetchLocations = async () => {
                        const response = await axios.get('http://localhost:8081/api/location/all', {
                            headers: { Authorization: `Bearer ${token}` }
                        });
                        setLocations(response.data);
                    };

                    const fetchOrganizers = async () => {
                        const response = await axios.get('http://localhost:8081/api/user/role/organizer', {
                            headers: { Authorization: `Bearer ${token}` }
                        });
                        setOrganizers(response.data);
                    };

                    fetchLocations();
                    fetchOrganizers();
                } catch (err) {
                    setError(err.response?.data || 'Failed to fetch event details.');
                }
            };

            fetchEventDetails();
        } else {
            setError('');
        }
    }, [open, eventId, token]);

    const handleUpdate = async () => {
        try {
            const eventDTO = {
                name,
                eventDate,
                eventTime,
                location,
                organizer: user.userType === 'organizer' ? user.id : organizer,
                ticketsAvailable,
                price,
                onSale
            };

            await axios.put(`http://localhost:8081/api/event/${eventId}`, eventDTO, {
                headers: { Authorization: `Bearer ${token}` }
            });
            onClose(true);
        } catch (err) {
            setError(err.response?.data || 'Failed to update event.');
        }
    };

    const handleReset = () => {
        if (defaultEvent) {
            setName(defaultEvent.name);
            setEventDate(defaultEvent.eventDate);
            setEventTime(defaultEvent.eventTime);
            setLocation(defaultEvent.location.id);
            setOrganizer(defaultEvent.organizer.id);
            setTicketsAvailable(defaultEvent.ticketsAvailable);
            setPrice(defaultEvent.price);
            setOnSale(defaultEvent.onSale);
        }
    };

    return (
        <Dialog open={open} onClose={() => { onClose(false); handleReset(); }} sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Update Event</DialogTitle>
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
                <TextField
                    margin="dense"
                    label="Tickets Available"
                    required
                    type="number"
                    fullWidth
                    variant="outlined"
                    value={ticketsAvailable}
                    onChange={(e) => setTicketsAvailable(e.target.value)}
                />
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
                <Button onClick={() => onClose(false)} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handleUpdate} color="primary">
                    Update
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default UpdateEvent;
