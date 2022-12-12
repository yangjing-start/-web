<template>
  <div id="login">
    <div id="login-form">
      <h1>注册界面</h1>
      <label for="nickname"><i class="el-icon-user-solid" style="color: #c1c1c1"></i></label>
      <input type="text" placeholder="昵称" name="nickname" id="nickname" autocapitalize="off" v-model="nickname" aria-autocomplete="off">

      <label for="password"><i class="el-icon-right" style="color: #c1c1c1"></i></label>
      <input type="password" placeholder="密码" name="password" id="password" autocapitalize="off" v-model="password">

      <label for="password_confirm"><i class="el-icon-right" style="color: #c1c1c1"></i></label>
      <input type="password" placeholder="确认密码" name="password_confirm" id="password_confirm" autocapitalize="off" v-model="password_confirm">

      <label for="email"><i class="el-icon-right" style="color: #c1c1c1"></i></label>
      <input type="email" placeholder="邮箱" name="email" id="email" autocapitalize="off" v-model="email">

      <label for="verify"><i class="el-icon-right" style="color: #c1c1c1"></i></label>
      <input type="text" placeholder="请输入验证码" name="verify" id="verify" autocapitalize="off" v-model="verification">

      <div>
        <div class="error_message">{{ error_message }}</div>
        <el-button type="primary" @click="verify">验证</el-button>
        <el-button type="primary" @click="register">注册</el-button>
      </div>
    </div>
  </div>
</template>
  
  <script>
  import {ref} from 'vue';
  import axios from 'axios';
  import router from '@/router/index'

  export default{
    name:'RegisterView',

    setup(){
      let nickname=ref('');
      let password=ref('');
      let password_confirm=ref('');
      let email=ref('');
      let error_message=ref('');
      let verification=ref('');

      const verify=()=>{
        let regex =/^[a-z0-9]+([._\\\\-]*[a-z0-9])*@([a-z0-9]+[-a-z0-9]*[a-z0-9]+.){1,63}[a-z0-9]+$/;
        if(regex.test(email.value)){
          axios.get('http://47.112.132.80/api/user/verification/mail/'+email.value)
          .then(res=>{
            console.log(res);
          })
          .catch(err=>{
            alert(err)
          })
        }else{
          alert("邮箱格式错误")
        } 
      };

      const register=()=>{
        if(password.value != password_confirm.value){
          error_message.value="密码不一致";
        }else{
          error_message.value="";
            axios.post('http://47.112.132.80/api/user/register',{
            nickname:nickname.value,
            password:password.value,
            verification:verification.value,
            mail:email.value,
          })
          .then(res=>{
            console.log(res)
            localStorage.setItem("username",res.data.data.username)
            localStorage.setItem("password",password.value)
            router.push({name:'login'})
          })
          .catch(err=>{
            console.log(err)
          })
        }
        };

      return {
        nickname,
        password,
        password_confirm,
        email,
        register,
        verify,
        verification,
        error_message,
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
  #login-form input {
  -webkit-text-fill-color: #ffffff !important;
  transition: background-color 5000s ease-in-out ,width 1s ease-out!important;
}
.error_message {
  color: red;
}
  </style>