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
    Popover,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { Edit, Delete, Notifications } from '@mui/icons-material';
import ClickAwayListener from '@mui/material/ClickAwayListener';
import AddUser from './dialog/AddUser';
import UpdateUser from './dialog/UpdateUser';

const AdminUsers = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [sortField, setSortField] = useState('id');
    const [sortDirection, setSortDirection] = useState('asc');
    const [selectedUser, setSelectedUser] = useState(null);
    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(10);
    const [isAddFormOpen, setIsAddFormOpen] = useState(false);
    const [isUpdateFormOpen, setIsUpdateFormOpen] = useState(false);
    const [selectedUserId, setSelectedUserId] = useState(null);
    const [deleteConfirmationOpen, setDeleteConfirmationOpen] = useState(false);
    const [pendingUsers, setPendingUsers] = useState([]);
    const [notificationAnchorEl, setNotificationAnchorEl] = useState(null);

    const navigate = useNavigate();
    const token = localStorage.getItem('token');

    const fetchUsers = async () => {
        try {
            const response = await axios.get('http://localhost:8081/api/user/all', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setUsers(response.data);
        } catch (err) {
            setError(`Failed to fetch users: ${err.response?.data || err.message}`);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const handleDelete = async () => {
        try {
            await axios.delete(`http://localhost:8081/api/user/id?id=${selectedUser?.id}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setDeleteConfirmationOpen(false);
            fetchUsers();
        } catch (err) {
            setError(`Failed to delete user: ${err.response?.data || err.message}`);
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

    const handleRowClick = (user) => {
        setSelectedUser(selectedUser?.id === user.id ? null : user);
    };

    const handleChangePage = (event, newPage) => {
        setPage(newPage);
    };

    const handleOpenAddForm = () => {
        setIsAddFormOpen(true);
    };

    const handleCloseAddForm = () => {
        setIsAddFormOpen(false);
        fetchUsers();
    };

    const handleOpenUpdateForm = (userId) => {
        setSelectedUserId(userId);
        setIsUpdateFormOpen(true);
    };

    const handleCloseUpdateForm = () => {
        setIsUpdateFormOpen(false);
        fetchUsers();
    };

    const handleChangeRowsPerPage = (event) => {
        setRowsPerPage(parseInt(event.target.value, 10));
        setPage(0);
    };

    const handleTableClickAway = () => {
        setSelectedUser(null);
    };


    const sortedUsers = users.sort((a, b) => {
        const aField = a[sortField] || '';
        const bField = b[sortField] || '';

        return sortDirection === 'asc' ? (aField > bField ? 1 : -1) : (aField < bField ? 1 : -1);
    });

    const isEditDeleteEnabled = Boolean(selectedUser);

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
                <Typography variant="h4">Users</Typography>
                <Box sx={{ display: 'flex', gap: 2 }}>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => navigate("/admin-events")}
                    >
                        Events Table
                    </Button>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={() => navigate("/admin-locations")}
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
                                    onClick={() => handleOpenUpdateForm(selectedUser?.id)}
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
                                    color="inherit"
                                    onClick={handleNotificationClick}
                                    disabled={pendingUsers.length === 0}
                                    sx={{ color: pendingUsers.length > 0 ? '#E4D00A' : 'grey' }}
                                >
                                    <Notifications />
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
                                            active={sortField === 'username'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('username')}
                                        >
                                            Username
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
                                            active={sortField === 'email'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('email')}
                                        >
                                            Email
                                        </TableSortLabel>
                                    </TableCell>
                                    <TableCell>
                                        <TableSortLabel
                                            active={sortField === 'userType'}
                                            direction={sortDirection}
                                            onClick={() => handleSort('userType')}
                                        >
                                            User Type
                                        </TableSortLabel>
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {sortedUsers
                                    .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                                    .map((user) => (
                                        <TableRow
                                            key={user.id}
                                            hover
                                            onClick={() => handleRowClick(user)}
                                            selected={selectedUser?.id === user.id}
                                        >
                                            <TableCell>{user.id}</TableCell>
                                            <TableCell>{user.username}</TableCell>
                                            <TableCell>{user.name}</TableCell>
                                            <TableCell>{user.email}</TableCell>
                                            <TableCell>{user.userType}</TableCell>
                                        </TableRow>
                                    ))}
                            </TableBody>
                        </Table>

                        <TablePagination
                            component="div"
                            count={users.length}
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
                    <Typography>Are you sure you want to delete this user?</Typography>
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
                        Add User
                    </Button>
                </Box>
            </Box>

            <AddUser open={isAddFormOpen} onClose={handleCloseAddForm} />
            <UpdateUser
                open={isUpdateFormOpen}
                onClose={handleCloseUpdateForm}
                userId={selectedUserId}
            />
        </Container>
    );
};

export default AdminUsers;
