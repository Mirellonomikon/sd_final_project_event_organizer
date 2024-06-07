import React, { useEffect, useState, useCallback } from 'react';
import {
    Container,
    Button,
    Typography,
    Box,
    Alert,
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableRow,
    TableContainer,
    Paper,
    Toolbar,
    TablePagination,
    TableSortLabel,
    IconButton,
    ClickAwayListener,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Edit, Delete, LocalOffer, PieChart } from '@mui/icons-material';
import AddEvent from '../shared/dialog/AddEvent';
import UpdateEvent from '../shared/dialog/UpdateEvent';
import OnSale from './dialog/OnSale';
import Charts from './dialog/Charts';
import UpdateCredentials from '../shared/dialog/UpdateCredentials';

const OrganizerEvents = () => {
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [isAddFormOpen, setIsAddFormOpen] = useState(false);
    const [isUpdateFormOpen, setIsUpdateFormOpen] = useState(false);
    const [isOnSaleFormOpen, setIsOnSaleFormOpen] = useState(false);
    const [isChartsOpen, setIsChartsOpen] = useState(false);
    const [isUpdateCredentialsOpen, setIsUpdateCredentialsOpen] = useState(false);
    const [selectedEventId, setSelectedEventId] = useState(null);
    const [deleteConfirmationOpen, setDeleteConfirmationOpen] = useState(false);

    const navigate = useNavigate();
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));
    const userId = user.id;

    const fetchEvents = useCallback(async () => {
        try {
            const response = await axios.get(`http://localhost:8081/api/event/organizer/${userId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setEvents(response.data);
        } catch (err) {
            setError(`Failed to fetch events: ${err.response?.data || err.message}`);
        } finally {
            setLoading(false);
        }
    }, [token, userId]);

    useEffect(() => {
        fetchEvents();
    }, [fetchEvents]);

    const handleDelete = async () => {
        try {
            await axios.delete(`http://localhost:8081/api/event/${selectedEvent?.id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setDeleteConfirmationOpen(false);
            fetchEvents();
        } catch (err) {
            setError(`Failed to delete event: ${err.response?.data || err.message}`);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        navigate('/login');
    };

    const handleDeleteButtonClick = () => {
        setDeleteConfirmationOpen(true);
    };

    const handleCancelDelete = () => {
        setDeleteConfirmationOpen(false);
    };

    const handleSort = (field) => {
        const isAsc = sortField === field && sortDirection === 'asc';
        setSortField(field);
        setSortDirection(isAsc ? 'desc' : 'asc');
    };

    const handleRowClick = (event) => {
        setSelectedEvent(selectedEvent?.id === event.id ? null : event);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleOpenAddForm = () => {
        setIsAddFormOpen(true);
    };

    const handleCloseAddForm = (submitSuccessful) => {
        setIsAddFormOpen(false);
        if (submitSuccessful) {
            fetchEvents();
        }
    };

    const handleOpenUpdateForm = (eventId) => {
        setSelectedEventId(eventId);
        setIsUpdateFormOpen(true);
    };

    const handleCloseUpdateForm = (submitSuccessful) => {
        setIsUpdateFormOpen(false);
        if (submitSuccessful) {
            fetchEvents();
        }
    };

    const handleOpenOnSaleForm = (eventId) => {
        setSelectedEventId(eventId);
        setIsOnSaleFormOpen(true);
    };

    const handleCloseOnSaleForm = (submitSuccessful) => {
        setIsOnSaleFormOpen(false);
        if (submitSuccessful) {
            fetchEvents();
        }
    };

    const handleOpenCharts = () => {
        setIsChartsOpen(true);
    };

    const handleCloseCharts = () => {
        setIsChartsOpen(false);
    };

    const handleOpenUpdateCredentials = () => {
        setIsUpdateCredentialsOpen(true);
    };

    const handleCloseUpdateCredentials = (submitSuccessful) => {
        setIsUpdateCredentialsOpen(false);
        if (submitSuccessful) {
            fetchEvents();
        }
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleTableClickAway = () => {
        setSelectedEvent(null);
    };

    const sortedEvents = events.sort((a, b) => {
        const aField = a[sortField] || '';
        const bField = b[sortField] || '';

        return sortDirection === 'asc' ? (aField > bField ? 1 : -1) : (aField < bField ? 1 : -1);
    });

    const isEditDeleteEnabled = Boolean(selectedEvent);

    return (
        <Container component="main">
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    marginTop: 2,
                    marginBottom: 2,
                }}
            >
                <Typography variant="h4">Events from {user.username}</Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => navigate("/organizer-locations")}
                    >
                        Locations Table
                    </Button>
                </Box>
            </Box>

            {error && (
                <Alert severity="error" sx={{ marginBottom: 2 }}>
                    {error}
                </Alert>
            )}

            {loading ? (
                <Typography variant="body1">Loading...</Typography>
            ) : (
                <ClickAwayListener onClickAway={handleTableClickAway}>
                    <TableContainer component={Paper}>
                        <Toolbar>
                            <Box sx={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
                                <IconButton
                                    color="primary"
                                    onClick={() => handleOpenUpdateForm(selectedEvent?.id)}
                                    disabled={!isEditDeleteEnabled}
                                >
                                    <Edit />
                                </IconButton>
                                <IconButton
                                    color="error"
                                    onClick={handleDeleteButtonClick}
                                    disabled={!isEditDeleteEnabled}
                                >
                                    <Delete />
                                </IconButton>
                                <IconButton
                                    color="primary"
                                    onClick={() => handleOpenOnSaleForm(selectedEvent?.id)}
                                    disabled={!isEditDeleteEnabled}
                                >
                                    <LocalOffer />
                                </IconButton>
                                <IconButton
                                    color="primary"
                                    onClick={handleOpenCharts}
                                    disabled={!isEditDeleteEnabled}
                                >
                                    <PieChart />
                                </IconButton>
                            </Box>
                        </Toolbar>

                        <Table size="small" stickyHeader>
                            <TableHead>
                                <TableRow>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'id'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('id')}
                                        >
                                            ID
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'name'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('name')}
                                        >
                                            Name
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'eventType'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('eventType')}
                                        >
                                            Event Type
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'eventDate'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('eventDate')}
                                        >
                                            Event Date
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'eventTime'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('eventTime')}
                                        >
                                            Event Time
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'location'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('location')}
                                        >
                                            Location
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'ticketsAvailable'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('ticketsAvailable')}
                                        >
                                            Tickets Available
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'price'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('price')}
                                        >
                                            Price
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'organizer'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('organizer')}
                                        >
                                            Organizer
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'onSale'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('onSale')}
                                        >
                                            On Sale
                                        </TableSortLabel>
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {sortedEvents.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((event) => (
                                    <TableRow
                                        key={event.id}
                                        hover
                                        selected={selectedEvent?.id === event.id}
                                        onClick={() => handleRowClick(event)}
                                        sx={{
                                            cursor: 'pointer',
                                            '&:hover': {
                                                backgroundColor: 'rgba(0, 0, 0, 0.04)',
                                            },
                                        }}
                                    >
                                        <TableCell>{event.id}</TableCell>
                                        <TableCell>{event.name}</TableCell>
                                        <TableCell>{event.eventType}</TableCell>
                                        <TableCell>{event.eventDate}</TableCell>
                                        <TableCell>{event.eventTime}</TableCell>
                                        <TableCell>{event.location.name}</TableCell>
                                        <TableCell>{event.ticketsAvailable}</TableCell>
                                        <TableCell>{event.price}</TableCell>
                                        <TableCell>{event.organizer.name}</TableCell>
                                        <TableCell>{event.onSale} %</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>

                        <TablePagination
                            rowsPerPageOptions={[5, 10, 25]}
                            component="div"
                            count={events.length}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                        />
                    </TableContainer>
                </ClickAwayListener>
            )}

            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    marginTop: 2,
                }}
            >
                <Button
                    variant="contained"
                    color="secondary"
                    onClick={handleLogout}
                >
                    Logout
                </Button>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleOpenAddForm}
                    >
                        Add Event
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleOpenUpdateCredentials}
                    >
                        Update User Credentials
                    </Button>
                </Box>
            </Box>

            <AddEvent open={isAddFormOpen} handleClose={handleCloseAddForm} />
            <UpdateEvent open={isUpdateFormOpen} eventId={selectedEventId} handleClose={handleCloseUpdateForm} />
            <OnSale open={isOnSaleFormOpen} eventId={selectedEventId} onClose={handleCloseOnSaleForm} />
            <Charts
                open={isChartsOpen}
                onClose={handleCloseCharts}
                locationCapacity={selectedEvent?.location.capacity}
                ticketsAvailable={selectedEvent?.ticketsAvailable}
            />
            <UpdateCredentials open={isUpdateCredentialsOpen} handleClose={handleCloseUpdateCredentials} />

            <Dialog open={deleteConfirmationOpen} onClose={handleCancelDelete}>
                <DialogTitle>Confirm Deletion</DialogTitle>
                <DialogContent>
                    <Typography>Are you sure you want to delete the selected event?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCancelDelete} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={handleDelete} color="error">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default OrganizerEvents;
