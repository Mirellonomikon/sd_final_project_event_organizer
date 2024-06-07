import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Typography,
    Box,
} from '@mui/material';
import axios from 'axios';

const OnSale = ({ eventId, open, onClose }) => {
    const [salePercent, setSalePercent] = useState('');
    const [error, setError] = useState(null);

    const handleSave = async () => {
        try {
            const response = await axios.put(`http://localhost:8081/api/event/sale/${eventId}`, null, {
                params: { salePercent: parseInt(salePercent) },
                headers: { Authorization: `Bearer ${localStorage.getItem('token')}` }
            });

            if (response.status === 200) {
                onClose(true);
            }
        } catch (err) {
            setError(`Failed to apply sale: ${err.response?.data || err.message}`);
        }
    };

    return (
        <Dialog open={open} onClose={() => onClose(false)} maxWidth="sm" fullWidth>
            <DialogTitle>Set Event on Sale</DialogTitle>
            <DialogContent>
                {error && (
                    <Typography color="error" gutterBottom>
                        {error}
                    </Typography>
                )}
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                    <TextField
                        label="Discount Percentage"
                        value={salePercent}
                        onChange={(e) => setSalePercent(e.target.value)}
                        type="number"
                        fullWidth
                    />
                </Box>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => onClose(false)}>Cancel</Button>
                <Button onClick={handleSave} color="primary">
                    Save
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default OnSale;
