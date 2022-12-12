<template>
  <div class="main">
    <div class="row">
      <div class="col-7 container">
        <div class="card">
          <div class="card-body">
            <div class="row">
              <div class="col-3">
                <img class="img-fluid" style="width:100%" :src="user.userImage" alt="">
                <br>
                <input type='file' accept="image/gif, image/jpeg, image/jpg, image/png, image/svg" @change='change'>
                <div style="margin-top:40px;">
                  <div class="row">
                    <div class="col-10">
                      <ul class="list-group">
                        <li @click="inter(0)" class="list-group-item d-flex justify-content-between align-items-center a">
                          我的粉丝
                          <span class="badge bg-dark rounded-pill">{{ user.fans }}</span>
                        </li>
                        <li @click="inter(1)" class="list-group-item d-flex justify-content-between align-items-center a">
                          我的关注
                          <span class="badge bg-dark rounded-pill">{{ user.follows }}</span>
                        </li>
                        <li @click="inter(2)" class="list-group-item d-flex justify-content-between align-items-center a">
                          我的帖子
                          <span class="badge bg-dark rounded-pill">{{ user.posts }}</span>
                        </li>
                        <li @click="inter" class="list-group-item d-flex justify-content-between align-items-center a">
                          历史记录
                        </li>
                      </ul>
                    </div>
                  </div>

                </div>
              </div>

              <div class="col-1"></div>

              <div class="col-7">
                <div class="u">个人信息</div>
                <hr>
                <el-form :model="user" label-width="auto">
                  <el-form-item size="large" label="用户名">
                    <el-input disabled v-model="user.username" />
                  </el-form-item>

                  <el-form-item size="large" label="昵称">
                    <el-input v-model="user.nickname" />
                  </el-form-item>

                  <el-form-item size="large" label="电话号码">
                    <el-input v-model="user.phone" />
                  </el-form-item>

                  <el-form-item size="large" label="邮箱">
                    <el-input v-model="user.email" />
                  </el-form-item>

                  <el-form-item size="large" label="Sex">
                    <el-radio-group v-model="user.sex">
                      <el-radio :label="0" size="small">男</el-radio>
                      <el-radio :label="1" size="small">女</el-radio>
                    </el-radio-group>
                  </el-form-item>

                  <el-form-item size="large" label="个人简介">
                    <el-input type="textarea" v-model="user.synopsis" />
                  </el-form-item>
                  <el-form-item>
                    <el-button size="large" type="primary" @click.prevent="submit">修改</el-button>
                  </el-form-item>
                </el-form>
              </div>
              <div class="col-1"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
// import { useStore } from "vuex";
import router from '@/router'
import axios from 'axios'

export default {
  name: "InformationView",

  setup() {
    // const store=useStore();
    const main = JSON.parse(localStorage.getItem("main"))

    let user = ref()

    const getInfo = () => {
      axios({
        method: "post",
        url: "http://47.112.132.80/api/detail/userProduct/getUserDetail",
        headers: {
          "token": localStorage.getItem("token")
        },
        data: {
          "username": main.username,
        }
      })
        .then(res => {
          user.value = res.data.data
          console.log("sex",user.value.sex)
        })
        .catch(err => {
          console.log(err)
        })
    }; getInfo();

    let change = (e) => {
      console.log(e)
      let file = e.target.files[0]
      let image = new FormData()
      image.append('file', file)
      console.log("8", image)
      axios({
        method: "post",
        url: "http://47.112.132.80/api/detail/userProduct/updateUserImage/" + main.username,
        withCredentials: true,
        headers: {
          "token": localStorage.getItem("token"),
          "ContentType": 'multipart/form-data',
        },
        data: image,
      })
        .then(res => {
          user.value.userImage = res.data.errorMessage
          localStorage.setItem("main",JSON.stringify(user.value))
        })
        .catch(err => {
          alert(err)
        })
    }

    const submit = () => {
      console.log(user.value)
      if (user.value.nickname) {
        axios({
          method: "post",
          url: "http://47.112.132.80/api/detail/userProduct/update",
          headers: {
            "token": localStorage.getItem("token")
          },
          data: {
            "description":"nickname",
            "value": user.value.nickname,
            "username": main.username
          }
        })
          .then(()=> {
            localStorage.setItem("main",JSON.stringify(user.value))
          })
          .catch(err => {
            console.log(err)
          })
      }
      if (user.value.phone) {
        axios({
          method: "post",
          url: "http://47.112.132.80/api/detail/userProduct/update",
          headers: {
            "token": localStorage.getItem("token")
          },
          data: {
            "description":"phone",
            "value": user.value.phone,
            "username": main.username,
          }
        })
          .then(err=> {
            if(err.data.errorMessage){
              alert(err.data.errorMessage)
            }else{
              localStorage.setItem("main",JSON.stringify(user.value))
            }
          })
          .catch(err => {
            console.log(err)
          })
      }
      if(user.value.email){
        axios({
          method: "post",
          url: "http://47.112.132.80/api/detail/userProduct/update",
          headers: {
            "token": localStorage.getItem("token")
          },
          data: {
            "description":"email",
            "value": user.value.email,
            "username": main.username,
          }
        })
          .then(()=> {
            localStorage.setItem("main",JSON.stringify(user.value))
          })
          .catch(err => {
            console.log(err)
          })
      }
      if((user.value.sex).toString()){
        axios({
          method: "post",
          url: "http://47.112.132.80/api/detail/userProduct/update",
          headers: {
            "token": localStorage.getItem("token")
          },
          data: {
            "description": "sex",
            "value": user.value.sex,
            "username": main.username,
          }
        })
          .then(()=> {
            localStorage.setItem("main",JSON.stringify(user.value))
          })
          .catch(err => {
            console.log(err)
          })
      }
      if(user.value.synopsis){
        axios({
          method: "post",
          url: "http://47.112.132.80/api/detail/userProduct/update",
          headers: {
            "token": localStorage.getItem("token")
          },
          data: {
            "description": "synopsis",
            "value": user.value.synopsis,
            "username": main.username,
          }
        })
          .then(()=> {
            localStorage.setItem("main",JSON.stringify(user.value))
          })
          .catch(err => {
            console.log(err)
          })
      }
    }

    const inter=(val)=>{
      if(val==0){
        router.push({
          name:"list",
          params:{
            "choose":0,
            "username":main.username
          }
        })
      }else if(val==1){
        router.push({
          name:"list",
          params:{
            "choose":1,
            "username":main.username
          }
        })
      }else if(val==2){
        console.log("")
      }else{
        router.push({
          name:"historyposts",
        })
      }
    }

    return {
      user,
      change,
      submit,
      getInfo,
      inter,
    }
  }

}
</script>

<style scoped>
.u {
  font-size: 30px;
  font-weight: 500;
}

.main {
  background: url("../assets/login.jpeg");
  width: 100%;
  height: 95%;
  position: fixed;
  background-size: 100% 100%;
  padding-top: 2%;
}
.a{
  cursor: pointer;
}
</style>