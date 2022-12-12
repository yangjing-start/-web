<template>
  <div class="main">
        <div class="row">
            <div class="col-7 container">
              <div class="card">
                <div class="card-body">

                <div class="row">
                    <div class="col-3">
                        <img class="img-fluid" src="https://cdn.acwing.com/media/user/profile/photo/1_lg_844c66b332.jpg" alt="">
                        <br>
                        <div style="margin-top:40px;">
                            <div class="row">
                                <div class="col-10">
                                    <ul class="list-group">
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                他的粉丝
                                <span class="badge bg-dark rounded-pill">{{user.fans}}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                他的关注
                                <span class="badge bg-dark rounded-pill">{{user.follows}}</span>
                            </li>
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                他的帖子
                                <span class="badge bg-dark rounded-pill">{{user.posts}}</span>
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
                        <el-form  label-width="auto">
                            <el-form-item size="large" label="用户名">
                                <el-input v-model="user.username" disabled placeholder="Please input"/>
                            </el-form-item>
                            
                            <el-form-item size="large" label="昵称">
                            <el-input disabled/>
                            </el-form-item>

                            <el-form-item size="large" label="电话号码">
                            <el-input disabled/>
                            </el-form-item>

                            <el-form-item size="large" label="邮箱">
                            <el-input disabled/>
                            </el-form-item>

                            <el-form-item size="large" label="Sex">
                            <el-radio-group>
                                <el-radio disabled label="男" />
                                <el-radio disabled label="女" />
                            </el-radio-group>
                            </el-form-item>

                            <el-form-item size="large" label="个人简介">
                            <el-input disabled type="text"/>
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
import axios from 'axios'
import { ref } from '@vue/reactivity'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'

export default {
    name:"OtherView",
    
    setup(){
        const store=useStore()
        const route=useRoute()
        const main=JSON.parse(localStorage.getItem("main"))
        let is_login=ref()
        is_login.value=JSON.parse(localStorage.getItem("is_login"))||store.state.is_login
        const username=route.params.username
        let user=ref()
        const getInfo=()=>{
            axios({
                method:"post",
                usrl:"http://47.112.132.80/api/detail/userProduct/getOtherDetail",
                data:{
                    "otherUsername":username,
                    "selfUsername":main.usernaem,
                }
            })
            .then(res=>{
                user.value.res
                console.log(res)
            })
            .catch(err=>{
                console.log(err)
            })
        }

        return{
            getInfo,
        }
    }
}
</script>

<style scoped>
.u{
    font-size: 30px;
    font-weight: 500;
}
.main{
  background:url("../assets/1.jpg");
  width:100%;
  height:95%;
  position:fixed;
  background-size:100% 100%;
  padding-top: 8%;
}
</style>