<template>
  <div id="app" >
    <meta-table @onProcessing="onProcessing"  v-show="!show" />
    <div v-show="show">
      <h1>Scanning..</h1>
    </div>

  </div>
</template>

<script>
 import MetaTable from './components/MetaTable'

 export default {
   name: 'app',
   components: {
     MetaTable
   },

   data() {
     return {
       show: false
     }
   },

   methods: {
     onProcessing(toggle) {
       this.show = toggle || false
     }

   }
 }
</script>

<style>
 html {
   font-size: 14px;
 }
 #app {
   font-size: 14px;
   -webkit-font-smoothing: antialiased;
   -moz-osx-font-smoothing: grayscale;
   color: #2c3e50;
   margin-top: 60px;
 }

 pre {
   margin: 1em 0em 1em;
   padding: 0.1em 0.1em 0.1em;
   white-space: pre-wrap;
   white-space: -moz-pre-wrap;
   white-space: -pre-wrap;
   white-space: -o-pre-wrap;
   word-wrap: break-word;
   line-height: 1.4em;
 }

</style>
