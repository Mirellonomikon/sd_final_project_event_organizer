import React, { useState, useEffect } from 'react';
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

const BuyTickets = ({ open, handleClose, eventId }) => {
    const [eventData, setEventData] = useState({ price: 0 });
    const [quantity, setQuantity] = useState(1);
    const [error, setError] = useState('');
    const token = localStorage.getItem('token');
    const userId = JSON.parse(localStorage.getItem('user')).id;

    useEffect(() => {
        if (open && eventId) {
            const fetchEventDetails = async () => {
                try {
                    const response = await axios.get(`http://localhost:8081/api/event/${eventId}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    });
                    const event = response.data;
                    setEventData({ price: event.price });
                    setError('');
                } catch (err) {
                    setError(err.response?.data || 'Failed to fetch event details.');
                }
            };

            fetchEventDetails();
        }
    }, [open, eventId, token]);

    const handleInputChange = (e) => {
        setQuantity(e.target.value);
    };

    const handlePurchase = async () => {
        try {
            const ticketDTO = {
                userId: userId,
                eventId: eventId,
                purchasePrice: eventData.price,
            };

            await axios.post(`http://localhost:8081/api/ticket/create?quantity=${quantity}`, ticketDTO, {
                headers: { Authorization: `Bearer ${token}` }
            });
            handleClose(true);
        } catch (err) {
            setError(err.response?.data || 'Failed to purchase tickets.');
        }
    };

    return (
        <Dialog open={open} onClose={() => handleClose(false)}
            sx={{ '& .MuiPaper-root': { backgroundColor: '#f1f8e9' } }}>
            <DialogTitle>Buy Tickets</DialogTitle>
            <DialogContent>
                {error && <Alert severity="error" style={{ backgroundColor: '#FFF6EA', marginBottom: '5px' }}>{error}</Alert>}
                <TextField
                    margin="dense"
                    name="quantity"
                    label="Quantity"
                    type="number"
                    fullWidth
                    variant="outlined"
                    value={quantity}
                    onChange={handleInputChange}
                    inputProps={{ min: 1 }}
                />
                <TextField
                    margin="dense"
                    name="price"
                    label="Price"
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={`${eventData.price} €`}
                    disabled
                />
                <TextField
                    margin="dense"
                    name="total"
                    label="Total"
                    type="text"
                    fullWidth
                    variant="outlined"
                    value={`${(eventData.price * quantity).toFixed(2)} €`}
                    disabled
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={() => handleClose(false)} color="secondary">
                    Cancel
                </Button>
                <Button onClick={handlePurchase} color="primary">
                    Purchase
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default BuyTickets;
