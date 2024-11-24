<h1 align="center">
  <br>
  <img src="https://github.com/spkdroid/TinyCRM/blob/master/img/logo.png" alt="TinyCRM Logo" width="220">
  <br>
  TinyCRM
  <br>
</h1>

<p align="center">
  <i>TinyCRM is a lightweight, efficient Customer Relationship Management (CRM) system that streamlines interaction management, support ticketing, and ticket comment tracking.</i>
</p>

---

## 📋 Table of Contents

- [Introduction](#introduction)  
- [Prerequisites](#prerequisites)  
- [Running the Application](#running-the-application)  
- [API Endpoints](#api-endpoints)  
- [Usage](#usage)  
- [Authors](#authors)  
- [License](#license)

---

## 🛠 Introduction

**TinyCRM** is built for organizations looking for a simple yet powerful tool to manage customer interactions, support tickets, and related comments. The system comprises:  
- **Backend:** Spring Boot  
- **Frontend:** Vue.js  
- **Database:** MySQL  

This application is containerized with **Docker**, ensuring smooth setup and deployment.

---

## ⚙️ Prerequisites

Ensure the following tools are installed before running the application:

- [Docker](https://www.docker.com/)  
- [Docker Compose](https://docs.docker.com/compose/)  
- [Java 11 or higher](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)  
- [Node.js and npm](https://nodejs.org/)  
- [Gradle](https://gradle.org/)  

---

## 🚀 Running the Application

Follow these steps to start the application:

1. **Clone the repository**  
   ```bash
   git clone https://github.com/spkdroid/TinyCRM.git
   cd TinyCRM
   ```

2. **Start the application using Docker Compose:**  
   ```bash
   docker-compose up --build
   ```

   This command will build and start all services defined in the `docker-compose.yml` file, including the Spring Boot backend, Vue.js frontend, and MySQL database.

3. **Access the application:**  
   Open your browser and navigate to `http://localhost:8080`.

---

## 📚 API Endpoints

### 🎫 Ticket Endpoints

- **Create a Ticket**  
  ```http
  POST /api/tickets
  Body: { "title": "Issue title", "description": "Issue description" }
  ```

- **Retrieve All Tickets**  
  ```http
  GET /api/tickets
  ```

- **Retrieve a Ticket by ID**  
  ```http
  GET /api/tickets/{id}
  ```

- **Delete a Ticket by ID**  
  ```http
  DELETE /api/tickets/{id}
  ```

### 💬 Comment Endpoints

- **Add a Comment to a Ticket**  
  ```http
  POST /api/tickets/{ticketId}/comments
  Body: { "text": "Comment text" }
  ```

- **Retrieve Comments for a Ticket**  
  ```http
  GET /api/tickets/{ticketId}/comments
  ```

---

## 💻 Usage

### Filing a Support Ticket

1. Navigate to the `/file-ticket` route in the application.  
2. Enter the **ticket title** and **description** in the provided form.  
3. Submit the form to create a new ticket.

### Viewing and Managing Comments

1. Go to the `/ticket-comments` route in the application.  
2. View all comments associated with a ticket.  
3. Use the form to add new comments to an existing ticket.

---

## 👨‍💻 Authors

- **Ramkumar Velmurugan**  
  [Portfolio](http://www.spkdroid.com/CV/)

---

## 📝 License

```
Copyright 2024 Ramkumar Velmurugan

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
