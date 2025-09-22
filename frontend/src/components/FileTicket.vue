<template>
    <div>
      <h1>File a Support Ticket</h1>
      <form @submit.prevent="submitTicket">
        <div>
          <label for="title">Title:</label>
          <input type="text" id="title" v-model="title" required />
        </div>
        <div>
          <label for="description">Description:</label>
          <textarea id="description" v-model="description" required></textarea>
        </div>
        <div>
          <label for="priority">Priority:</label>
          <select id="priority" v-model="priority" required>
            <option value="Low">Low</option>
            <option value="Medium">Medium</option>
            <option value="High">High</option>
          </select>
        </div>
        <div>
          <label for="category">Category:</label>
          <input type="text" id="category" v-model="category" required />
        </div>
        <button type="submit">Submit</button>
      </form>
    </div>
  </template>
  
  <script>
  export default {
    name: 'FileTicket',
    data() {
      return {
        title: '',
        description: '',
        priority: 'Medium',
        category: '',
      };
    },
    methods: {
      async submitTicket() {
        // Replace with your API endpoint
        const response = await fetch('http://localhost:8080/api/tickets', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            subject: this.title,
            description: this.description,
            priority: this.priority,
            category: this.category,
          }),
        });
  
        if (response.ok) {
          alert('Ticket filed successfully');
          this.title = '';
          this.description = '';
          this.priority = 'Medium';
          this.category = '';
        } else {
          alert('Failed to file ticket');
        }
      },
    },
  };
  </script>
  
  <style>
  /* Add styles for the form */
  </style>
