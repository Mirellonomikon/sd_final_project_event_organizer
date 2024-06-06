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
    TablePagination,
    TableSortLabel,
    ClickAwayListener,
    IconButton,
} from '@mui/material';
import { Favorite, FavoriteBorder } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import UpdateCredentials from '../shared/dialog/UpdateCredentials';
import BuyTickets from './dialog/BuyTickets';

const ClientEvents = () => {
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');
    const [selectedEvent, setSelectedEvent] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [isUpdateCredentialsOpen, setIsUpdateCredentialsOpen] = useState(false);
    const [isBuyTicketsOpen, setIsBuyTicketsOpen] = useState(false);
    const [selectedEventId, setSelectedEventId] = useState(null);
    const [wishlist, setWishlist] = useState([]);

    const navigate = useNavigate();
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));
    const userId = user.id;

    const fetchEvents = useCallback(async () => {
        try {
            const response = await axios.get('http://localhost:8081/api/event/all', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setEvents(response.data);
        } catch (err) {
            setError(`Failed to fetch events: ${err.response?.data || err.message}`);
        } finally {
            setLoading(false);
        }
    }, [token]);

    const fetchUserWishlist = useCallback(async () => {
        try {
            const response = await axios.get(`http://localhost:8081/api/user/id?userId=${userId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setWishlist(response.data.wishlistEvents.map(event => event.id));
        } catch (err) {
            setError(`Failed to fetch wishlist: ${err.response?.data || err.message}`);
        }
    }, [token, userId]);

    useEffect(() => {
        fetchEvents();
        fetchUserWishlist();
    }, [fetchEvents, fetchUserWishlist]);

    const handleLogout = () => {
        localStorage.removeItem('user');
        localStorage.removeItem('token');
        navigate('/login');
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

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleOpenUpdateCredentials = () => {
        setIsUpdateCredentialsOpen(true);
    };

    const handleCloseUpdateCredentials = (submitSuccessful) => {
        setIsUpdateCredentialsOpen(false);
    };

    const handleOpenBuyTickets = (eventId) => {
        setSelectedEventId(eventId);
        setIsBuyTicketsOpen(true);
    };

    const handleCloseBuyTickets = (submitSuccessful) => {
        setIsBuyTicketsOpen(false);
        if (submitSuccessful) {
            fetchEvents();
        }
    };

    const handleWishlistToggle = async (eventId) => {
        const isWishlisted = wishlist.includes(eventId);
        try {
            if (isWishlisted) {
                await axios.delete(`http://localhost:8081/api/user/wishlist/remove?userId=${userId}&eventId=${eventId}`, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setWishlist(wishlist.filter(id => id !== eventId));
            } else {
                await axios.post(`http://localhost:8081/api/user/wishlist/add?userId=${userId}&eventId=${eventId}`, {}, {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setWishlist([...wishlist, eventId]);
            }
        } catch (err) {
            setError(`Failed to update wishlist: ${err.response?.data || err.message}`);
        }
    };

    const handleTableClickAway = () => {
        setSelectedEvent(null);
    };

    const sortedEvents = events.sort((a, b) => {
        const aField = a[sortField] || '';
        const bField = b[sortField] || '';

        return sortDirection === 'asc' ? (aField > bField ? 1 : -1) : (aField < bField ? 1 : -1);
    });

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
                <Typography variant="h4">Events</Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => navigate("/wishlist")}
                    >
                        Wishlist
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => navigate("/tickets")}
                    >
                        Tickets
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
                                    <TableCell>Action</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {sortedEvents.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(event => (
                                    <TableRow
                                        key={event.id}
                                        onClick={() => handleRowClick(event)}
                                        selected={selectedEvent?.id === event.id}
                                        hover
                                    >
                                        <TableCell>{event.id}</TableCell>
                                        <TableCell>{event.name}</TableCell>
                                        <TableCell>{event.eventType}</TableCell>
                                        <TableCell>{event.eventDate}</TableCell>
                                        <TableCell>{event.eventTime}</TableCell>
                                        <TableCell>{event.location.name}</TableCell>
                                        <TableCell>{event.ticketsAvailable}</TableCell>
                                        <TableCell>{event.price} €</TableCell>
                                        <TableCell>{event.organizer.name}</TableCell>
                                        <TableCell>{event.onSale} %</TableCell>
                                        <TableCell>
                                            <IconButton
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    handleWishlistToggle(event.id);
                                                }}
                                            >
                                                {wishlist.includes(event.id) ? (
                                                    <Favorite color="error" />
                                                ) : (
                                                    <FavoriteBorder />
                                                )}
                                            </IconButton>
                                            <Button
                                                variant="contained"
                                                color="primary"
                                                onClick={(e) => {
                                                    e.stopPropagation();
                                                    handleOpenBuyTickets(event.id);
                                                }}
                                                disabled={!event.onSale || !event.ticketsAvailable}
                                            >
                                                Buy Tickets
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                        <TablePagination
                            rowsPerPageOptions={[10, 25, 50]}
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
                    marginTop: 2,
                }}
            >
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleOpenUpdateCredentials}
                >
                    Update Credentials
                </Button>
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleLogout}
                >
                    Logout
                </Button>
            </Box>

            <UpdateCredentials
                open={isUpdateCredentialsOpen}
                onClose={handleCloseUpdateCredentials}
            />
            <BuyTickets
                open={isBuyTicketsOpen}
                onClose={handleCloseBuyTickets}
                eventId={selectedEventId}
            />
        </Container>
    );
};

export default ClientEvents;
