<template>
  <div :style="background" class="bgBackground">
      <div class="row" style="margin-top:50px;margin-bottom: 30px;">
        <div class="col-2"></div>
        <div class="col-8">
          <div class="card">
            <div class="card-body">
              <div class="row">
                <div class="col-2">
                  <div class="navbar">
                    <router-link class="a" :to="{name:'showposts'}">所有帖子</router-link>
                  </div>
                  <div class="navbar">
                    <router-link class="a" :to="{name:'addimage'}">添加图片</router-link>
                  </div>
                  <div class="navbar">
                    <router-link class="a" :to="{name:'addpost'}">发布帖子</router-link>
                  </div>
                </div>
                <div class="col-10">
                  <router-view></router-view>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="col-2"></div>
      </div>
    </div>
</template>

<script>
import { ref } from '@vue/reactivity'

export default{
  name:"UserView",
  setup(){
    let background = ref({
      // 背景图片地址
      backgroundImage: 'url(' + require('../assets/5.jpeg') + ')',
      // 背景图片是否重复
      backgroundRepeat: 'no-repeat',
      // 背景图片大小
      backgroundSize: 'cover',
      // 背景颜色
      backgroundColor: '#000',
      // 背景图片位置
      backgroundPosition: 'center top'
    })

    return {
      background,
    }
  }
}
</script>

<style scoped>
.bgBackground{
    width:100%;
    height:100%;
    position: fixed;
    overflow: auto;
  }
.a{
  text-decoration:none;
  color: black;
  line-height: 50px;
  font-size: 20px;
}

.a{
  margin-bottom: 20px;
}
</style>