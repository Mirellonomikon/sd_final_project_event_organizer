import React, { useEffect, useState } from 'react';
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
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    ClickAwayListener,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Edit, Delete } from '@mui/icons-material';
import AddLocation from '../shared/dialog/AddLocation';
import UpdateLocation from '../shared/dialog/UpdateLocation';

const AdminLocations = () => {
    const [locations, setLocations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');
    const [selectedLocation, setSelectedLocation] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [isAddFormOpen, setIsAddFormOpen] = useState(false);
    const [isUpdateFormOpen, setIsUpdateFormOpen] = useState(false);
    const [selectedLocationId, setSelectedLocationId] = useState(null);
    const [deleteConfirmationOpen, setDeleteConfirmationOpen] = useState(false);

    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    const fetchLocations = async () => {
        try {
            const response = await axios.get('http://localhost:8081/api/location/all', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setLocations(response.data);
        } catch (err) {
            setError(`Failed to fetch locations: ${err.response?.data || err.message}`);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchLocations();
    }, []);

    const handleDelete = async () => {
        try {
            await axios.delete(`http://localhost:8081/api/location/${selectedLocation?.id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setDeleteConfirmationOpen(false);
            fetchLocations();
        } catch (err) {
            setError(`Failed to delete location: ${err.response?.data || err.message}`);
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

    const handleRowClick = (location) => {
        setSelectedLocation(selectedLocation?.id === location.id ? null : location);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleOpenAddForm = () => {
        setIsAddFormOpen(true);
    };

    const handleCloseAddForm = () => {
        setIsAddFormOpen(false);
        fetchLocations();
    };

    const handleOpenUpdateForm = (locationId) => {
        setSelectedLocationId(locationId);
        setIsUpdateFormOpen(true);
    };

    const handleCloseUpdateForm = () => {
        setIsUpdateFormOpen(false);
        fetchLocations();
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleTableClickAway = () => {
        setSelectedLocation(null);
    };

    const sortedLocations = locations.sort((a, b) => {
        const aField = a[sortField] || '';
        const bField = b[sortField] || '';

        return sortDirection === 'asc' ? (aField > bField ? 1 : -1) : (aField < bField ? 1 : -1);
    });

    const isEditDeleteEnabled = Boolean(selectedLocation);

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
                <Typography variant="h4">Locations</Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => navigate("/admin-users")}
                    >
                        Users Table
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => navigate("/admin-events")}
                    >
                        Events Table
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
                                    onClick={() => handleOpenUpdateForm(selectedLocation?.id)}
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
                                            active={sortField === 'address'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('address')}
                                        >
                                            Address
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'capacity'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('capacity')}
                                        >
                                            Capacity
                                        </TableSortLabel>
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {sortedLocations
                                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map((location) => (
                                        <TableRow
                                            key={location.id}
                                            hover
                                            onClick={() => handleRowClick(location)}
                                            selected={selectedLocation?.id === location.id}
                                        >
                                            <TableCell>{location.id}</TableCell>
                                            <TableCell>{location.name}</TableCell>
                                            <TableCell>{location.address}</TableCell>
                                            <TableCell>{location.capacity}</TableCell>
                                        </TableRow>
                                    ))}
                            </TableBody>
                        </Table>

                        <TablePagination
                            component="div"
                            count={locations.length}
                            page={page}
                            onPageChange={handleChangePage}
                            rowsPerPage={rowsPerPage}
                            onRowsPerPageChange={handleChangeRowsPerPage}
                            rowsPerPageOptions={[10, 25, 50]}
                            labelRowsPerPage="Rows per page"
                        />
                    </TableContainer>
                </ClickAwayListener>
            )}

            <Dialog
                open={deleteConfirmationOpen}
                onClose={handleCancelDelete}
            >
                <DialogTitle>Delete Confirmation</DialogTitle>
                <DialogContent>
                    <Typography>Are you sure you want to delete this location?</Typography>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCancelDelete} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={handleDelete} color="primary">
                        Delete
                    </Button>
                </DialogActions>
            </Dialog>

            <Box sx={{ display: 'flex', justifyContent: 'space-between', marginTop: 2 }}>
                <Button variant="contained" color="secondary" onClick={handleLogout}>
                    Logout
                </Button>

                <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleOpenAddForm}
                    >
                        Add Location
                    </Button>
                </Box>
            </Box>

            <AddLocation open={isAddFormOpen} onClose={handleCloseAddForm} />
            <UpdateLocation
                open={isUpdateFormOpen}
                onClose={handleCloseUpdateForm}
                locationId={selectedLocationId}
            />
        </Container>
    );
};

export default AdminLocations;
