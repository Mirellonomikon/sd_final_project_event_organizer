# API Documentation

## Event Controller

### Get All Events
#### `GET /api/event/all`
Request:
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns a list of all events.
- `403 FORBIDDEN`: Access is denied.

### Get Event by ID
#### `GET /api/event/{id}`
Request:
- Path Parameters:
  - id: Event ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns the event with the specified ID.
- `404 NOT FOUND`: Event not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

### Create Event
#### `POST /api/event/create`
Request:
- Headers: 
  - Authorization: Bearer <token>
- Body (JSON):
```json
{
  "name": "Event Name",
  "eventType": "Type",
  "eventDate": "2023-01-01",
  "eventTime": "12:00",
  "location": 1,
  "ticketsAvailable": 100,
  "price": 50.00,
  "organizer": 1,
  "onSale": 10
}
```

Responses:
- `200 OK`: Event was successfully created.
- `403 FORBIDDEN`: Access is denied.

### Update Event
#### `PUT /api/event/{id}`
Request:
- Path Parameters:
  - id: Event ID
- Headers: 
  - Authorization: Bearer <token>
- Body (JSON):
```json
{
  "name": "Updated Event Name",
  "eventType": "Type",
  "eventDate": "2023-01-01",
  "eventTime": "12:00",
  "location": 1,
  "ticketsAvailable": 100,
  "price": 50.00,
  "organizer": 1,
  "onSale": 10
}
```

Responses:
- `200 OK`: Event was successfully updated.
- `404 NOT FOUND`: Event not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

### Delete Event
#### `DELETE /api/event/{id}`
Request:
- Path Parameters:
  - id: Event ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Event was successfully deleted.
- `404 NOT FOUND`: Event not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

### Get Events by Organizer
#### `GET /api/event/organizer/{organizerId}`
Request:
- Path Parameters:
  - organizerId: Organizer ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns a list of events organized by the specified organizer.
- `403 FORBIDDEN`: Access is denied.

### Set Event on Sale
#### `PUT /api/event/sale/{id}`
Request:
- Path Parameters:
  - id: Event ID
- Headers: 
  - Authorization: Bearer <token>
- Query Parameters:
  - salePercent: Sale percentage

Responses:
- `200 OK`: Event sale status was successfully updated.
- `404 NOT FOUND`: Event not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

## Location Controller

### Get All Locations
#### `GET /api/location/all`
Request:
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns a list of all locations.
- `403 FORBIDDEN`: Access is denied.

### Get Location by ID
#### `GET /api/location/{id}`
Request:
- Path Parameters:
  - id: Location ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns the location with the specified ID.
- `404 NOT FOUND`: Location not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

### Create Location
#### `POST /api/location/create`
Request:
- Headers: 
  - Authorization: Bearer <token>
- Body (JSON):
```json
{
  "name": "Location Name",
  "address": "Location Address",
  "capacity": 100
}
```

Responses:
- `200 OK`: Location was successfully created.
- `403 FORBIDDEN`: Access is denied.
- `409 CONFLICT`: Location with the same name already exists.

### Update Location
#### `PUT /api/location/{id}`
Request:
- Path Parameters:
  - id: Location ID
- Headers: 
  - Authorization: Bearer <token>
- Body (JSON):
```json
{
  "name": "Updated Location Name",
  "address": "Updated Location Address",
  "capacity": 150
}
```

Responses:
- `200 OK`: Location was successfully updated.
- `404 NOT FOUND`: Location not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.
- `409 CONFLICT`: Location with the same name already exists.

### Delete Location
#### `DELETE /api/location/{id}`
Request:
- Path Parameters:
  - id: Location ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Location was successfully deleted.
- `404 NOT FOUND`: Location not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

## Ticket Controller

### Create Tickets
#### `POST /api/ticket/create`
Request:
- Headers: 
  - Authorization: Bearer <token>
- Body (JSON):
```json
{
  "userId": 1,
  "eventId": 1,
  "purchasePrice": 50.00
}
```
- Query Parameters:
  - quantity: Number of tickets

Responses:
- `200 OK`: Tickets were successfully created.
- `404 NOT FOUND`: User or Event not found with the specified ID.
- `400 BAD REQUEST`: Not enough tickets available.

### Update Ticket
#### `PUT /api/ticket/update`
Request:
- Headers: 
  - Authorization: Bearer <token>
- Body (JSON):
```json
{
  "userId": 1,
  "eventId": 1,
  "purchasePrice": 60.00
}
```
- Query Parameters:
  - ticketId: Ticket ID

Responses:
- `200 OK`: Ticket was successfully updated.
- `404 NOT FOUND`: Ticket, User, or Event not found with the specified ID.

### Delete Ticket
#### `DELETE /api/ticket/{id}`
Request:
- Path Parameters:
  - id: Ticket ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Ticket was successfully deleted.
- `404 NOT FOUND`: Ticket not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

### Get Ticket by ID
#### `GET /api/ticket/{id}`
Request:
- Path Parameters:
  - id: Ticket ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns the ticket with the specified ID.
- `404 NOT FOUND`: Ticket not found with the specified ID.

### Get All Tickets
#### `GET /api/ticket/all`
Request:
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns a list of all tickets.
- `403 FORBIDDEN`: Access is denied.

### Get All Tickets by User
#### `GET /api/ticket/user/{userId}`
Request:
- Path Parameters:
  - userId: User ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns a list of all tickets for the specified user.
- `404 NOT FOUND`: User not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

### Get All Tickets by Event
#### `GET /api/ticket/event/{eventId}`
Request:
- Path Parameters:
  - eventId: Event ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns a list of all tickets for the specified event.
- `404 NOT FOUND`: Event not found with the specified ID.

### Export Ticket
#### `GET /api/ticket/export/{ticketId}`
Request:
- Path Parameters:
  - ticketId: Ticket ID
- Headers: 
  - Authorization: Bearer <token>
- Query Parameters:
  - format: Export format (csv or txt)

Responses:
- `200 OK`: Returns the exported ticket.
- `404 NOT FOUND`: Ticket not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

## User Controller

### Register User
#### `POST /api/user/register`
Request:
- Body (JSON):
```json
{
  "username": "username",
  "name": "User Name",
  "email": "user@example.com",
  "password": "password"
}
```

Responses:
- `200 OK`: User was successfully registered.
- `409 CONFLICT`: Username already exists.

### Login User
#### `POST /api/user/login`
Request:
- Body (JSON):
```json
{
  "username": "username",
  "password": "password"
}
```

Responses:
- `200 OK`: Returns the user and JWT token.
- `404 NOT FOUND`: User not found.
- `400 BAD REQUEST`: Invalid password.

### Update User Credentials
#### `PUT /api/user/update`
Request:
- Headers: 
  - Authorization: Bearer <token>
- Query Parameters:
  - userId: User ID
-

 Body (JSON):
```json
{
  "username": "newusername",
  "name": "New User Name",
  "email": "newuser@example.com",
  "password": "newpassword"
}
```

Responses:
- `200 OK`: User credentials were successfully updated.
- `404 NOT FOUND`: User not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.
- `409 CONFLICT`: Username already exists.

### Get All Users
#### `GET /api/user/all`
Request:
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns a list of all users.
- `403 FORBIDDEN`: Access is denied.

### Get User by Role
#### `GET /api/user/role/{role}`
Request:
- Path Parameters:
  - role: User role
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns a list of users with the specified role.
- `403 FORBIDDEN`: Access is denied.

### Get User by ID
#### `GET /api/user/id`
Request:
- Query Parameters:
  - userId: User ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns the user with the specified ID.
- `404 NOT FOUND`: User not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

### Add User
#### `POST /api/user/add`
Request:
- Headers: 
  - Authorization: Bearer <token>
- Body (JSON):
```json
{
  "username": "newuser",
  "name": "New User",
  "email": "newuser@example.com",
  "password": "newpassword",
  "userType": "CLIENT"
}
```

Responses:
- `200 OK`: User was successfully added.
- `409 CONFLICT`: Username already exists.

### Update User
#### `PUT /api/user/id`
Request:
- Headers: 
  - Authorization: Bearer <token>
- Query Parameters:
  - id: User ID
- Body (JSON):
```json
{
  "username": "updateduser",
  "name": "Updated User",
  "email": "updateduser@example.com",
  "password": "updatedpassword",
  "userType": "CLIENT"
}
```
Responses:
- `200 OK`: User was successfully updated.
- `404 NOT FOUND`: User not found with the specified ID.
- `409 CONFLICT`: Username already exists.

### Delete User
#### `DELETE /api/user/id`
Request:
- Headers: 
  - Authorization: Bearer <token>
- Query Parameters:
  - id: User ID

Responses:
- `200 OK`: User was successfully deleted.
- `404 NOT FOUND`: User not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

### Add Event to Wishlist
#### `POST /api/user/wishlist/add`
Request:
- Headers: 
  - Authorization: Bearer <token>
- Query Parameters:
  - userId: User ID
  - eventId: Event ID

Responses:
- `200 OK`: Event was successfully added to wishlist.
- `404 NOT FOUND`: User or Event not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

### Remove Event from Wishlist
#### `DELETE /api/user/wishlist/remove`
Request:
- Headers: 
  - Authorization: Bearer <token>
- Query Parameters:
  - userId: User ID
  - eventId: Event ID

Responses:
- `200 OK`: Event was successfully removed from wishlist.
- `404 NOT FOUND`: User or Event not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.

### Get User Wishlist Events
#### `GET /api/user/{userId}/wishlist`
Request:
- Path Parameters:
  - userId: User ID
- Headers: 
  - Authorization: Bearer <token>

Responses:
- `200 OK`: Returns a list of events in the user's wishlist.
- `404 NOT FOUND`: User not found with the specified ID.
- `403 FORBIDDEN`: Access is denied.