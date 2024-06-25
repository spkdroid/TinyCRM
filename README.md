# TinyCRM

TinyCRM is a lightweight Customer Relationship Management (CRM) system that focuses on interaction management, support tickets, and ticket comments. This project is built using Spring Boot for the backend and Vue.js for the frontend, with MySQL as the database.

<h1 align="center" style="text-align: center; padding-bottom: 20px;">
  <br>
 <img src="https://github.com/spkdroid/TinyCRM/blob/master/img/logo.png" alt="Icon" width="220"/>
</h1>


## Table of Contents

- [Prerequisites](#prerequisites)
- [Running the Application](#running-the-application)
- [Usage](#usage)
- [License](#license)

## Prerequisites

Before you begin, ensure you have the following installed on your machine:

- Docker
- Docker Compose
- Java 11 or higher
- Node.js and npm
- Gradle

## Running the Application

1. **Start Docker Compose:**

   From the root directory of the project, run:

   ```bash
   docker-compose up --build
   ```

   This command will build and start all the services defined in the `docker-compose.yml` file.

## API Endpoints

### Ticket Endpoints

- **Create a ticket:**

  ```
  POST /api/tickets
  Body: { "title": "Issue title", "description": "Issue description" }
  ```

- **Get all tickets:**

  ```
  GET /api/tickets
  ```

- **Get a ticket by ID:**

  ```
  GET /api/tickets/{id}
  ```

- **Delete a ticket by ID:**

  ```
  DELETE /api/tickets/{id}
  ```

### Comment Endpoints

- **Add a comment to a ticket:**

  ```
  POST /api/tickets/{ticketId}/comments
  Body: { "text": "Comment text" }
  ```

- **Get comments for a ticket:**

  ```
  GET /api/tickets/{ticketId}/comments
  ```

## Usage

### Filing a Support Ticket

1. Navigate to the `/file-ticket` route in your browser.
2. Fill in the form with the ticket title and description.
3. Submit the form to create a new support ticket.

### Viewing and Adding Comments

1. Navigate to the `/ticket-comments` route in your browser.
2. View the list of comments for a ticket.
3. Use the form to add a new comment to the ticket.

## Authors

* **Ramkumar Velmurugan** - <a href="http://www.spkdroid.com/CV/">Portfolio</a>

## License
    Copyright 2024 Ramkumar Velmurugan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
