<template>
    <div id="login">
    <div id="login-form">
      <h1>登陆界面</h1>
      <label for="username"><i class="el-icon-user-solid" style="color: #c1c1c1"></i></label>
      <input v-model="username" type="text" placeholder="账号&邮箱" name="username" id="username" autocapitalize="off" aria-autocomplete="off">
      <label for="password"><i class="el-icon-right" style="color: #c1c1c1"></i></label>
      <input v-model="password" type="password" placeholder="密码" name="password" id="password" autocapitalize="off">
      <img class="verify" :src="'data:image/jpg;base64,'+image" alt="">
      <label for="imageVerify"><i class="el-icon-user-solid" style="color: #c1c1c1"></i></label>
      <input v-model="imageVerify" type="text" placeholder="请输入验证码" name="imageVerify" id="imageVerify" autocapitalize="off">
      <div>
        <div class="error_message">{{ error_message }}</div>
        <el-button type="primary"  @click.prevent="submit">登录</el-button>
        <el-button type="info" @click="newpassword">修改密码</el-button>
      </div>
    </div>
  </div>
  </template>
  
  <script>
  import {ref} from 'vue';
  import {useStore} from 'vuex';
  import axios from 'axios';
  import router from '@/router';

  export default{
    name:'LoginView',
    components:{
    },

    setup(){
      const store=useStore();
      let error_message=ref('');
      let username=ref('');
      let password=ref('');
      let image=ref('');
      let imageVerify=ref('');
      let time=ref('');

      const newpassword=()=>{
        router.push({name:"newpassword"})
      } 

      username.value=localStorage.getItem('username') || '';
      password.value=localStorage.getItem('password') || '';
      axios.get("http://47.112.132.80/api/user/vc.jpg")
      .then(res=>{
        time.value=res.data.data.time
        image.value=res.data.data.base64
      })
      .catch(err=>{
        console.log(err);
      })

      //登陆
      const submit=()=>{
        store.dispatch("login",{
          username:username.value,
          password:password.value,
          imageVerify:imageVerify.value,
          time:time.value
        }
        )
      }
      
      return {
        image,
        submit,
        username,
        password,
        imageVerify,
        time,
        error_message,
        newpassword,
      }
    }
  }
  </script>
  
  <style scoped>
  #login {
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  position: relative;
  background-image: url("../assets/login.jpeg");
  background-repeat: no-repeat;
  background-attachment: fixed;
  background-position: center;
  background-size: cover;
}

#login-form {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 50vw;
  min-width: 300px;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  background-color: rgba(0, 0, 0, 0.7);
  border-radius: 15px;
  box-shadow: 0 15px 25px rgba(0, 0, 0, .5);
}

#login-form h1 {
    width: 60%;
    margin: 50px auto 20px;
    color: #c1c1c1;
    text-align: center;
  }

  #login-form input {
    width: 60%;
    margin: 15px auto;
    outline: none;
    border: none;
    padding: 10px;
    border-bottom: 1px solid #fff;
    background: transparent;
    color: white;
  }

  #login-form label {
    width: 60%;
    margin: 0 auto;
    position: relative;
    top: 30px;
    left: -15px;
  }

  #login-form div {
    width: 60%;
    margin: 10px auto;
    display: flex;
    justify-content: center;
    align-content: center;
  }

  #login-form button {
    background-color: rgba(9, 108, 144, 0.5);
    margin: 10px 25px 40px 25px;
  }

  #login-form p {
    width: 60%;
    margin: 8px auto;
    position: relative;
    left: -15px;
    color: #ff0000;
    font-size: 8px;
  }

  .verify{
    width: 100px;
    height: 50px;
    margin-left: 120px;
  }
  #login-form input {
  -webkit-text-fill-color: #ffffff !important;
  transition: background-color 5000s ease-in-out ,width 1s ease-out!important;
}
.error_message {
  color: red;
}
  </style>