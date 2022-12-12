<template>
  <NavBar></NavBar>
  <router-view :key="$route.fullPath"/>
  <div></div>
</template>

<script>
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap/dist/js/bootstrap';
import NavBar from './components/NavBar.vue';


export default{
  name:"App",
  components:{
    NavBar,
  },

}
</script>

<style scoped>
app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 0px;
}
body{
  margin:0;
  padding:0;
  border:0;
}
</style>