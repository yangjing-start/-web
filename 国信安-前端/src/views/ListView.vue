<template>
  <div :style="background" class="bgBackground">
    <div class="row a">
      <div class="col-8 container">
        <div class="card">
          <div class="card-body">
            <div class="list">
              <div class="row" v-for="list in listInfo" :key="list.id">
                <div class="col-1">
                  <img class="img-fluid" style="border-radius: 50%;width: 100%;" :src="list.userImage" alt="">
                </div>
                <div class="col-8">
                  <div class="username">{{list.nickname}}</div>
                  <div class="synopsis">{{list.synopsis}}</div>
                </div>
                <div class="col-3">
                  <div class="fans">粉丝数：<span class="num">{{list.fans}}</span></div> 
                </div>
              </div>
              <hr>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
  
  <script>
import {ref} from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'
  
  export default{
    name:'RegisterView',
 
    setup(){
      let background=ref({
        // 背景图片地址
        backgroundImage: 'url(' + require('../assets/login.jpeg') + ')',
            // 背景图片是否重复
            backgroundRepeat: 'no-repeat',
            // 背景图片大小
            backgroundSize: 'cover',
            // 背景颜色
            backgroundColor: '#000',
            // 背景图片位置
            backgroundPosition: 'center top'
      })
      
      const route=useRoute()
      //列表人员信息
      let listInfo=ref("")
      //选择进入那个请求，关注、好友、粉丝
      let choose=route.params.choose;
      console.log(choose)
      //得到发过来的用户usename
      let username=route.params.username;
      console.log(username)
      if(choose == 0){
        axios({
          method:"POST",
          url:"http://47.112.132.80/api/detail/userProduct/fans",
          headers:{
            "token":localStorage.getItem("token")
          },
          data:{
            "username":username,
          },
        })
        .then(res=>{
          listInfo.value=res.data.data;
        })
        .catch(err=>{
          console.log(err)
        })
      }else if(choose == 1){
        axios({
          method:"post",
          url:"http://47.112.132.80/api/detail/userProduct/follows",
          headers:{
            "token":localStorage.getItem("token")
          },
          data:{
            "username":username,
          }
        })
        .then(res=>{
          listInfo.value=res.data.data;
        })
        .catch(err=>{
          console.log(err)
        })
      }else if(choose==2){
        axios({
          method:"post",
          url:"http://47.112.132.80/api/detail/userProduct/friends",
          headers:{
            "token":localStorage.getItem("token")
          },
          data:{
            "username":username,
          }
        })
        .then(res=>{
          listInfo.value=res.data.data;
        })
        .catch(err=>{
          console.log(err)
        })
      }

      return {
        background,
        choose,
        username,
        listInfo,
      }
    }
  }
  </script>
  
  <style scoped>
  .username{
    font-weight: 700;
  }
  .synopsis{
    margin-top: 5px;
    font-weight: 200;
    font-size: 18px;
  }
  .fans{
    font-size: 20px;
    margin-top: 32px;
  }
  .num{
    font-size: 14px;
    color: red;
  }
  .bgBackground{
    width:100%;
    height:100%;
    position: fixed;
    overflow: auto;
  }
  .a{
    margin-top: 10px;
    margin-bottom: 10px;
  }
  </style>