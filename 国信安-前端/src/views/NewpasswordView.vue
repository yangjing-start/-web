<template>
    <div id="login">
    <div id="login-form">
      <h1>修改密码界面</h1>
      <label for="oldpassword"><i class="el-icon-user-solid" style="color: #c1c1c1"></i></label>
      <input type="text" placeholder="旧密码" name="oldpassword" id="oldpassword" autocapitalize="off" aria-autocomplete="off" v-model="oldpassword">
      <label for="newpassword"><i class="el-icon-right" style="color: #c1c1c1"></i></label>
      <input type="password" placeholder="新密码" name="newpassword" id="newpassword" autocapitalize="off" v-model="newpassword">
      <label for="newpassword_confirm"><i class="el-icon-right" style="color: #c1c1c1"></i></label>
      <input type="password" placeholder="确认密码" name="newpassword_confirm" id="newpassword_confirm" autocapitalize="off" v-model="confirm">
      <div>
        <div class="error-message">{{ error_message }}</div>
      </div>
      <div>
        <el-button type="info" @click="submit">确定修改</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import {useStore} from 'vuex'
import { ref } from '@vue/reactivity';
import axios from 'axios';
export default{
    name:"NewpasswordView",
    
    setup(){
      let store=useStore()
      let oldpassword=ref();
      let newpassword=ref();
      let confirm=ref();
      let error_message=ref();
      let token=localStorage.getItem("token");
      
      const submit=()=>{
        if(newpassword.value != confirm.value){
          error_message.value="密码不一致"
          return
        }
        axios({
          method:"post",
          url:"http://47.112.132.80/api/user/updatePassword",
          headers:{
            "token":token
          },
          data:{
            "username":store.state.user.username,
            "newPassword":newpassword.value,
            "oldPassword":oldpassword.value,
          }
        })
        .then.catch(err=>{
          console.log(err)
        })
      }

      return {
        oldpassword,
        newpassword,
        confirm,
        submit,
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
  background-image: url("../assets/1.jpg");
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
.error-message{
  color: red;
}
</style>