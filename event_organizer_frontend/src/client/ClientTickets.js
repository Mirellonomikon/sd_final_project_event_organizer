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
    Toolbar,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    ClickAwayListener,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import UpdateCredentials from '../shared/dialog/UpdateCredentials';

const ClientTickets = () => {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');
    const [selectedTicket, setSelectedTicket] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [isUpdateCredentialsOpen, setIsUpdateCredentialsOpen] = useState(false);
    const [exportDialogOpen, setExportDialogOpen] = useState(false);
    const [confirmRefundOpen, setConfirmRefundOpen] = useState(false);

    const navigate = useNavigate();
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));
    const userId = user.id;

    const fetchTickets = useCallback(async () => {
        try {
            const response = await axios.get(`http://localhost:8081/api/ticket/user/${userId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setTickets(response.data);
        } catch (err) {
            setError(`Failed to fetch tickets: ${err.response?.data || err.message}`);
        } finally {
            setLoading(false);
        }
    }, [token, userId]);

    useEffect(() => {
        fetchTickets();
    }, [fetchTickets]);

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

    const handleRowClick = (ticket) => {
        setSelectedTicket(selectedTicket?.id === ticket.id ? null : ticket);
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

    const handleOpenConfirmRefund = () => {
        setConfirmRefundOpen(true);
    };

    const handleCloseConfirmRefund = () => {
        setConfirmRefundOpen(false);
    };

    const handleRefund = async () => {
        try {
            await axios.delete(`http://localhost:8081/api/ticket/${selectedTicket.id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSelectedTicket(null);
            fetchTickets();
            handleCloseConfirmRefund();
        } catch (err) {
            setError(`Failed to delete ticket: ${err.response?.data || err.message}`);
        }
    };

    const handleOpenExportDialog = () => {
        setExportDialogOpen(true);
    };

    const handleCloseExportDialog = () => {
        setExportDialogOpen(false);
    };

    const handleExport = async (format) => {
        try {
            const response = await axios.get(`http://localhost:8081/api/ticket/export/${selectedTicket.id}?format=${format}`, {
                headers: { Authorization: `Bearer ${token}` },
                responseType: 'blob'
            });

            const blob = new Blob([response.data], { type: format === 'csv' ? 'text/csv' : 'text/plain' });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `ticket.${format}`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);

            handleCloseExportDialog();
        } catch (err) {
            setError(`Failed to export ticket: ${err.response?.data || err.message}`);
        }
    };

    const sortedTickets = tickets.sort((a, b) => {
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
                <Typography variant="h4">Tickets for {user.name}</Typography>
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
                        onClick={() => navigate("/client-events")}
                    >
                        Events
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
                <ClickAwayListener onClickAway={() => setSelectedTicket(null)}>
                    <TableContainer component={Paper}>
                        <Toolbar>
                            <Box sx={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'center', gap: 2 }}>
                                <Button
                                    variant="contained"
                                    color="error"
                                    onClick={handleOpenConfirmRefund}
                                    disabled={!selectedTicket}
                                >
                                    Refund
                                </Button>
                                <Button
                                    variant="contained"
                                    color="primary"
                                    onClick={handleOpenExportDialog}
                                    disabled={!selectedTicket}
                                >
                                    Export
                                </Button>
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
                                            active={sortField === 'event.name'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('event.name')}
                                        >
                                            Event Name
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'event.eventDate'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('event.eventDate')}
                                        >
                                            Event Date
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'event.eventTime'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('event.eventTime')}
                                        >
                                            Event Time
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'event.location.name'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('event.location.name')}
                                        >
                                            Location
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'user.name'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('user.name')}
                                        >
                                            Owner
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'purchasePrice'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('purchasePrice')}
                                        >
                                            Purchase Price
                                        </TableSortLabel>
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {sortedTickets.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map(ticket => (
                                    <TableRow
                                        key={ticket.id}
                                        onClick={() => handleRowClick(ticket)}
                                        selected={selectedTicket?.id === ticket.id}
                                        hover
                                    >
                                        <TableCell>{ticket.id}</TableCell>
                                        <TableCell>{ticket.event.name}</TableCell>
                                        <TableCell>{ticket.event.eventDate}</TableCell>
                                        <TableCell>{ticket.event.eventTime}</TableCell>
                                        <TableCell>{ticket.event.location.name}</TableCell>
                                        <TableCell>{ticket.user.name}</TableCell>
                                        <TableCell>{ticket.purchasePrice} €</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                        <TablePagination
                            rowsPerPageOptions={[10, 25, 50]}
                            component="div"
                            count={tickets.length}
                            rowsPerPage={rowsPerPage}
                            page={page}
                            onPageChange={handleChangePage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                        />
                    </TableContainer>
                </ClickAwayListener>
            )}

            <Dialog open={exportDialogOpen} onClose={handleCloseExportDialog}>
                <DialogTitle>Export Ticket</DialogTitle>
                <DialogContent>
                    <Typography>Choose the format to export:</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => handleExport('csv')}>CSV</Button>
                    <Button onClick={() => handleExport('txt')}>TXT</Button>
                    <Button onClick={handleCloseExportDialog}>Cancel</Button>
                </DialogActions>
            </Dialog>

            <Dialog open={confirmRefundOpen} onClose={handleCloseConfirmRefund}>
                <DialogTitle>Confirm Refund</DialogTitle>
                <DialogContent>
                    <Typography>Are you sure you want to refund this ticket?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseConfirmRefund}>Cancel</Button>
                    <Button onClick={handleRefund} color="error">Refund</Button>
                </DialogActions>
            </Dialog>
            <Box
                sx={{
                    display: 'flex',
                    justifyContent: 'space-between',
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
                <Button
                    variant="contained"
                    color="primary"
                    onClick={handleOpenUpdateCredentials}
                >
                    Update Credentials
                </Button>
            </Box>
            <UpdateCredentials
                open={isUpdateCredentialsOpen}
                handleClose={handleCloseUpdateCredentials}
            />
        </Container>
    );
};

export default ClientTickets;
