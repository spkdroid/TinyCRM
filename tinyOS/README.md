  # Support Module API Documentation

## Overview

The Support Module provides RESTful endpoints for managing support tickets, ticket comments, and users. Below are the details of each available endpoint.

## Endpoints

### Support Tickets

#### Get All Tickets
- **URL**: `/api/tickets`
- **Method**: `GET`
- **Description**: Retrieves a list of all support tickets.
- **Response**:
  - `200 OK`: Returns a list of all support tickets.

#### Get Ticket by ID
- **URL**: `/api/tickets/{id}`
- **Method**: `GET`
- **Description**: Retrieves a support ticket by its ID.
- **Parameters**:
  - `id` (path): The ID of the support ticket.
- **Response**:
  - `200 OK`: Returns the support ticket with the specified ID.
  - `404 Not Found`: If the ticket does not exist.

#### Create a New Ticket
- **URL**: `/api/tickets`
- **Method**: `POST`
- **Description**: Creates a new support ticket.
- **Request Body**:
  - `SupportTicket` (JSON): The details of the support ticket to be created.
- **Response**:
  - `201 Created`: Returns the created support ticket.

#### Update an Existing Ticket
- **URL**: `/api/tickets/{id}`
- **Method**: `PUT`
- **Description**: Updates an existing support ticket.
- **Parameters**:
  - `id` (path): The ID of the support ticket.
- **Request Body**:
  - `SupportTicket` (JSON): The updated details of the support ticket.
- **Response**:
  - `200 OK`: Returns the updated support ticket.
  - `404 Not Found`: If the ticket does not exist.

#### Delete a Ticket
- **URL**: `/api/tickets/{id}`
- **Method**: `DELETE`
- **Description**: Deletes a support ticket by its ID.
- **Parameters**:
  - `id` (path): The ID of the support ticket.
- **Response**:
  - `204 No Content`: If the deletion was successful.
  - `404 Not Found`: If the ticket does not exist.

### Ticket Comments

#### Get All Comments
- **URL**: `/api/comments`
- **Method**: `GET`
- **Description**: Retrieves a list of all ticket comments.
- **Response**:
  - `200 OK`: Returns a list of all ticket comments.

#### Get Comment by ID
- **URL**: `/api/comments/{id}`
- **Method**: `GET`
- **Description**: Retrieves a ticket comment by its ID.
- **Parameters**:
  - `id` (path): The ID of the ticket comment.
- **Response**:
  - `200 OK`: Returns the ticket comment with the specified ID.
  - `404 Not Found`: If the comment does not exist.

#### Create a New Comment
- **URL**: `/api/comments`
- **Method**: `POST`
- **Description**: Creates a new ticket comment.
- **Request Body**:
  - `TicketComment` (JSON): The details of the ticket comment to be created.
- **Response**:
  - `201 Created`: Returns the created ticket comment.

#### Update an Existing Comment
- **URL**: `/api/comments/{id}`
- **Method**: `PUT`
- **Description**: Updates an existing ticket comment.
- **Parameters**:
  - `id` (path): The ID of the ticket comment.
- **Request Body**:
  - `TicketComment` (JSON): The updated details of the ticket comment.
- **Response**:
  - `200 OK`: Returns the updated ticket comment.
  - `404 Not Found`: If the comment does not exist.

#### Delete a Comment
- **URL**: `/api/comments/{id}`
- **Method**: `DELETE`
- **Description**: Deletes a ticket comment by its ID.
- **Parameters**:
  - `id` (path): The ID of the ticket comment.
- **Response**:
  - `204 No Content`: If the deletion was successful.
  - `404 Not Found`: If the comment does not exist.

### Users

#### Get All Users
- **URL**: `/api/users`
- **Method**: `GET`
- **Description**: Retrieves a list of all users.
- **Response**:
  - `200 OK`: Returns a list of all users.

#### Get User by ID
- **URL**: `/api/users/{id}`
- **Method**: `GET`
- **Description**: Retrieves a user by their ID.
- **Parameters**:
  - `id` (path): The ID of the user.
- **Response**:
  - `200 OK`: Returns the user with the specified ID.
  - `404 Not Found`: If the user does not exist.

#### Create a New User
- **URL**: `/api/users`
- **Method**: `POST`
- **Description**: Creates a new user.
- **Request Body**:
  - `User` (JSON): The details of the user to be created.
- **Response**:
  - `201 Created`: Returns the created user.

#### Update an Existing User
- **URL**: `/api/users/{id}`
- **Method**: `PUT`
- **Description**: Updates an existing user.
- **Parameters**:
  - `id` (path): The ID of the user.
- **Request Body**:
  - `User` (JSON): The updated details of the user.
- **Response**:
  - `200 OK`: Returns the updated user.
  - `404 Not Found`: If the user does not exist.

#### Delete a User
- **URL**: `/api/users/{id}`
- **Method**: `DELETE`
- **Description**: Deletes a user by their ID.
- **Parameters**:
  - `id` (path): The ID of the user.
- **Response**:
  - `204 No Content`: If the deletion was successful.
  - `404 Not Found`: If the user does not exist.