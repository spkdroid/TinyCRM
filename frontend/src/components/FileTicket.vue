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
            title: this.title,
            description: this.description,
          }),
        });
  
        if (response.ok) {
          alert('Ticket filed successfully');
          this.title = '';
          this.description = '';
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
  