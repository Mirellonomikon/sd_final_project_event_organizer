import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import ProtectedRoute from './ProtectedRoute';
import Login from './shared/Login';
import DefaultPage from "./shared/DefaultPage";
import SignUp from './shared/SignUp';
import AdminEvents from './admin/AdminEvents';
import AdminLocations from './admin/AdminLocations';
import AdminUsers from './admin/AdminUsers';
import OrganizerEvents from './organizer/OrganizerEvents';
import OrganizerLocations from './organizer/OrganizerLocations';
import ClientEvents from './client/ClientEvents';
import Wishlist from './client/Wishlist';
import ClientTickets from './client/ClientTickets';
import theme from './theme';
import { ThemeProvider } from "@mui/material/styles";

const App = () => {

  return (
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<DefaultPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<SignUp />} />

          <Route
            path="/organizer-events"
            element={
              <ProtectedRoute
                element={<OrganizerEvents />}
                allowedRoles={['organizer']}
              />
            }
          />

          <Route
            path="/organizer-locations"
            element={
              <ProtectedRoute
                element={<OrganizerLocations />}
                allowedRoles={['organizer']}
              />
            }
          />


          <Route
            path="/client-events"
            element={
              <ProtectedRoute
                element={<ClientEvents />}
                allowedRoles={['client']}
              />
            }
          />

          <Route
            path="/client-tickets"
            element={
              <ProtectedRoute
                element={<ClientTickets />}
                allowedRoles={['client']}
              />
            }
          />

          <Route
            path="/wishlist"
            element={
              <ProtectedRoute
                element={<Wishlist />}
                allowedRoles={['client']}
              />
            }
          />

          <Route
            path="/admin-events"
            element={
              <ProtectedRoute
                element={<AdminEvents />}
                allowedRoles={['administrator']}
              />
            }
          />

          <Route
            path="/admin-locations"
            element={
              <ProtectedRoute
                element={<AdminLocations />}
                allowedRoles={['administrator']}
              />
            }
          />


          <Route
            path="/admin-users"
            element={
              <ProtectedRoute
                element={<AdminUsers />}
                allowedRoles={['administrator']}
              />
            }
          />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
};

export default App;
