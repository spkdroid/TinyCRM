<template>
    <div>
      <h1>Ticket Details</h1>
      <div v-if="ticket">
        <p><strong>Subject:</strong> {{ ticket.subject }}</p>
        <p><strong>Status:</strong> {{ ticket.status }}</p>
        <p><strong>Priority:</strong> {{ ticket.priority }}</p>
        <p><strong>Category:</strong> {{ ticket.category }}</p>
      </div>
      <h1>Ticket Comments</h1>
      <ul>
        <li v-for="comment in comments" :key="comment.id">{{ comment.text }}</li>
      </ul>
      <form @submit.prevent="addComment">
        <div>
          <label for="comment">Add Comment:</label>
          <el-input
            id="comment"
            type="textarea"
            v-model="newComment"
            :rows="4"
            required
            placeholder="Type your comment here..."
          />
        </div>
        <button type="submit">Submit</button>
      </form>
    </div>
  </template>
  
  <script>
  export default {
    name: 'TicketComments',
    data() {
      return {
        ticket: null,
        comments: [],
        newComment: '',
      };
    },
    async created() {
      // Replace with your API endpoint and ticket ID
      const ticketResponse = await fetch('http://localhost:8080/api/tickets/1');
      if (ticketResponse.ok) {
        this.ticket = await ticketResponse.json();
      }
      const commentsResponse = await fetch('http://localhost:8080/api/tickets/1/comments');
      if (commentsResponse.ok) {
        this.comments = await commentsResponse.json();
      }
    },
    methods: {
      async addComment() {
        // Replace with your API endpoint and ticket ID
        const response = await fetch('http://localhost:8080/api/tickets/1/comments', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            text: this.newComment,
          }),
        });
  
        if (response.ok) {
          const newComment = await response.json();
          this.comments.push(newComment);
          this.newComment = '';
        } else {
          alert('Failed to add comment');
        }
      },
    },
  };
  </script>
  
  <style>
  /* Add styles for the comments section */
  </style>
