<template>
  <div class="main">
    <div class="box">
      <transition class="animate__heartBeat">
        <div class="middle">
          Welcome to Forum!
          <div>
            <button @click="inter" type="button" class="btn inter">进入</button>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script>
import router from '@/router';

export default {
  name: 'HomeView',


  setup() {
    const inter = () => {
      router.push({
        name:"posts"
      })
    }

    return {
      inter,
    }
  }
}
</script>

<style scoped>
.main {
  background: url("../assets/3.jpg");
  width: 100%;
  height: 100%;
  position: fixed;
  background-size: 100% 100%;
}

.middle {
  color: white;
  font-size: 3rem;
  position: absolute;
  left: 40%;
  top: 45%;
  margin-left: -50px;
  margin-top: -50px;
  /*text-align: center!important;*/
  animation-iteration-count: infinite;

}

.box {
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.3);
}

.animate__heartBeat {
  animate-iteration-count: infinite;
}

p {
  display: inline-block;
  margin-right: 40px;
  font-size: 3rem;
}

.inter {
  color: white;
  margin-left: 30%;
  font-size: 50px;
  padding: 0px 50px;
}
.inter:hover{
  color: black;
}
</style>
