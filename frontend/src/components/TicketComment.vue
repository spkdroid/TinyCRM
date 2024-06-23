<template>
    <div>
      <h1>Ticket Comments</h1>
      <ul>
        <li v-for="comment in comments" :key="comment.id">{{ comment.text }}</li>
      </ul>
      <form @submit.prevent="addComment">
        <div>
          <label for="comment">Add Comment:</label>
          <textarea id="comment" v-model="newComment" required></textarea>
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
        comments: [],
        newComment: '',
      };
    },
    async created() {
      // Replace with your API endpoint and ticket ID
      const response = await fetch('http://localhost:8080/api/tickets/1/comments');
      this.comments = await response.json();
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
  