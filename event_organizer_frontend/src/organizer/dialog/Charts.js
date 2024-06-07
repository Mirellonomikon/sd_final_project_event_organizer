import React from 'react';
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Typography } from '@mui/material';
import { Chart } from 'react-google-charts';

const Charts = ({ open, onClose, locationCapacity, ticketsAvailable }) => {
    const data = [
        ['Status', 'Count'],
        ['Tickets Available', ticketsAvailable],
        ['Tickets Sold', locationCapacity - ticketsAvailable]
    ];

    const options = {
        title: 'Event Capacity',
        pieHole: 0.4,
        is3D: false,
        slices: [
            { color: "#FF6384" },
            { color: "#36A2EB" },
        ],
    };

    const handleDialogClick = (event) => {
        event.stopPropagation();
    };

    return (
        <Dialog open={open} onClose={() => onClose(false)} maxWidth="lg" fullWidth onClick={handleDialogClick}>
            <DialogTitle>Event Capacity Chart</DialogTitle>
            <DialogContent>
                {locationCapacity != null && ticketsAvailable != null ? (
                    <Chart
                        chartType="PieChart"
                        data={data}
                        options={options}
                        width="100%"
                        height="400px"
                    />
                ) : (
                    <Typography>Loading chart data...</Typography>
                )}
            </DialogContent>
            <DialogActions>
                <Button onClick={() => onClose(false)}>Close</Button>
            </DialogActions>
        </Dialog>
    );
};

export default Charts;
