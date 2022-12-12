<template>
  <div class="body">
    <nav class="navbar navbar-expand-lg navbar-light">
      <div class="container">
        <router-link class="navbar-brand active b" :to="{ name: 'home' }">论坛</router-link>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText"
          aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarText">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <router-link class="nav-link" :to="{ name: 'home' }">首页</router-link>
            </li>
            <li class="nav-item">
            </li>
            <li v-if="is_login" class="nav-item">
              <router-link class="nav-link" :to="{ name: 'user' }">用户动态</router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" :to="{ name: 'posts' }">帖子</router-link>
            </li>
          </ul>
          <ul class="navbar-nav me-auto mb-2 mb-lg-0" style="margin-left:500px;">
            <li v-if="is_login" class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown"
                aria-expanded="false">
                <img style="width:30px;height:30px;border-radius: 50px;" :src="image" alt="">
              </a>
              <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                <li><router-link class="dropdown-item" :to="{ name: 'own' }">个人信息</router-link></li>
                <li>
                  <hr class="dropdown-divider">
                </li>
                <li><router-link class="dropdown-item" @click="logout" :to="{ name: 'home' }">退出</router-link></li>
                <li><router-link class="dropdown-item" @click="logoff" :to="{ name: 'home' }">注销</router-link></li>
              </ul>
            </li>
            <li v-if="!is_login" class="nav-item">
              <router-link class="nav-link" :to="{ name: 'login' }">登录</router-link>
            </li>
            <li v-if="!is_login" class="nav-item">
              <router-link class="nav-link" :to="{ name: 'register' }">注册</router-link>
            </li>
            <li v-if="is_login" class="nav-item">
              <el-tooltip class="tip" placement="top">
                <template #content>
                  <div v-for="message in messageList" :key="message.id">
                    <span><img class="img-fluid" :src="message.image" alt=""></span>{{ message.message }}<span></span>
                  </div>
                  <div>
                    <span class="ok" @click="delete_message">删除</span>
                  </div>
                </template>
                <el-button class="button"><el-icon size="25px" class="message">
                    <BellFilled />
                  </el-icon></el-button>
              </el-tooltip>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </div>
</template>
<script>
import { BellFilled } from '@element-plus/icons-vue'
import { useStore } from 'vuex'
import { ref } from '@vue/reactivity'
import axios from 'axios'

export default {
  name: "NavBar",
  components: {
    BellFilled,
  },

  setup() {
    const store = useStore()
    let main =ref(JSON.parse(localStorage.getItem("main"))) ||""
    let image = ref()
    image.value=main.value.userImage
    let is_login = ref()
    is_login.value = localStorage.getItem("is_login") ||""

    setInterval(()=>{
      is_login.value=localStorage.getItem("is_login")||""
      image.value = main.value.userImage ||""
      main.value=JSON.parse(localStorage.getItem("main"))||""
    },100)
    let messageList = ref()

    //消息提示
    const message = () => {
      axios({
        method: "get",
        url: "http://47.112.132.80/api/comment/postMessage/" + main.value.username,
        headers: {
          "token": localStorage.getItem("token")
        }
      })
        .then(res => {
          messageList.value = res.data.data
        })
        .catch(err => {
          console.log(err)
        })
    }; message();

    //注销
    const logoff = () => {
      store.commit("logoff")
      localStorage.setItem("is_login","")
    }

    //退出
    const logout = () => {
      store.commit("logout")
      localStorage.setItem("is_login","")
    }

    //删除消息提示
    const delete_message = () => {
      axios({
        method: "get",
        url: "http://47.112.132.80/api/comment/deletePostMessage/" + main.value.username,
        headers: {
          "token": localStorage.getItem("token")
        }
      })
        .then(() => {
          messageList.value = ""
        })
        .catch(err => {
          console.log(err)
        })
    }

    return {
      is_login,
      message,
      messageList,
      logoff,
      logout,
      delete_message,
      image,
    }
  }
}
</script>
  
<style scoped>
.body {
  background-color: rgba(255, 255, 255, 0.99);
}

.message {
  margin-top: 10px;
}

.button {
  border: none;
}

.button:hover {
  background-color: #fff;
}

.ok {
  cursor: pointer;
}

.ok:hover {
  color: red;
}

.b{
  margin-left: 50px;
}
</style>

