<template>
  <div>
    <h2>File a Support Ticket</h2>
    <form @submit.prevent="fileTicket">
      <label for="title">Title:</label>
      <input type="text" id="title" v-model="ticket.title" required />
      
      <label for="description">Description:</label>
      <textarea id="description" v-model="ticket.description" required></textarea>
      
      <button type="submit">Submit</button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      ticket: {
        title: '',
        description: ''
      }
    };
  },
  methods: {
    async fileTicket() {
      try {
        const response = await fetch('http://localhost:8080/api/tickets', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(this.ticket)
        });
        if (response.ok) {
          alert('Ticket filed successfully!');
          this.ticket.title = '';
          this.ticket.description = '';
        } else {
          alert('Failed to file ticket.');
        }
      } catch (error) {
        alert('Error filing ticket.');
        console.error(error);
      }
    }
  }
};
</script>

<style>
/* Add styles for the form */
</style>
