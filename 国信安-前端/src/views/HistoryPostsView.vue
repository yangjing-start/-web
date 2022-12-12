<template>
  <div :style="background" class="bgBackground">
    <div class="row a">
      <div class="col-8 container">
        <div class="card">
          <div class="card-body">
            <div class="list" v-for="history in history" :key="history.id">
              <div class="row">
                <div class="col-10">
                  <div class="title">
                    {{ history.title }}
                  </div>
                  <div class="nick">
                    {{ history.debateNickname }}
                  </div>
                  <div class="time">
                    {{ history.createTime }}
                  </div>
                </div>
                <div class="col-2">
                  <button @click="Delete(history)" type="button" class="btn btn-primary">删除</button>
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
import { ref } from 'vue'
import axios from 'axios'

export default {
  name: "HistoryPostsView",

  setup() {
    let background = ref({
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

    let history = ref()
    let main = JSON.parse(localStorage.getItem("main"))

    const get = () => {
      axios({
        method: "post",
        url: "http://47.112.132.80/api/comment/getHistory",
        headers: {
          "token": localStorage.getItem("token")
        },
        data: {
          "username": main.username,
          "pageSize": 0,
          "beginPage": 0,
        }
      })
        .then(res => {
          history.value = res.data.data;
          console.log(history.value)
        })
        .catch(err => {
          console.log(err)
        })
    }; get();

    const Delete=(val)=>{
      axios({
        method:"post",
        url:"http://47.112.132.80/api/comment/deleteHistory",
        headers:{
          "token":localStorage.getItem("token")
        },
        data:{
          "debateId":val.debateId,
          "username":main.username,
        }
      })
      .then(()=>{
        get();
      }).catch
    }

    return {
      get,
      history,
      background,
      Delete,
    }
  }
}
</script>

<style scoped>
.bgBackground {
  width: 100%;
  height: 100%;
  position: fixed;
  overflow: auto;
}

.title {
  font-size: 25px;
  margin-bottom: 5px;
  color: red;
  font-weight: 500;
}

.nick {
  font-size: 20px;
}

.time {
  font-size: 12px;
  font-weight: 200;
}
</style>