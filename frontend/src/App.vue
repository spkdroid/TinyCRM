<template>
  <div id="app">
    <router-link to="/">Home</router-link>
    <router-link to="/file-ticket">File Ticket</router-link>
    <router-link to="/ticket-comments">Ticket Comments</router-link>
    <router-view></router-view>
  </div>
</template>

<script>
export default {
  name: 'App',
};
</script>

<style>
/* Add styles for the navigation */
</style>
