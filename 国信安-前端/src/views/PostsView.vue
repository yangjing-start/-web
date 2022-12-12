<template>
  <div :style="background" class="bgBackground">
    <SearchView class="search" @update="update"></SearchView>
    <div class="container home">
      <div class="row">
        <div class="col-9">
          <div class="row">
            <div class="col-6 box" v-for="posts in posts" :key="posts.id">
              <div class="card">
                <div class="card-body">
                  <p class="title a" @click="inter(posts)" v-html="posts.title"></p>
                  <div class="middle">
                    <span class="time">{{ posts.createTime.slice(0, 10) }}</span>
                    <span class="like"><img class="like-img" src="../assets/like2.svg" alt=""> {{ posts.likes }}</span>
                    <span class="comment"><img class="comment-img" src="../assets/comment.svg" alt="">{{ posts.remarks
                    }}</span>
                  </div>
                  <div class="content a" @click="inter(posts)" v-html="posts.summary"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="col-3">
          <div class="card b">
            <div class="card-body">
              <div>
                <div>
                  <span class="hot">贴吧热点</span><span><img @click="refresh" class="refresh" src="../assets/fresh.svg"></span>
                  <hr>
                </div>
                <div v-for="(hot, index) in hot" :key="index">
                  <span class="num1">{{ Number(index) + 1 }}</span> <span class="size a" @click="inter(hot)">{{ hot.title }}</span> <span
                    class="hotnum">{{ hot.score }}</span><img class="good" src="../assets/fire.svg">
                  <hr>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="row">
        <div class="col-2"></div>
        <div class="col-8">
          <el-pagination layout="prev, pager, next" :page-count="count.pageCount" pager-count="7"
            v-model:current-page="count.currentPage" @current-change="handle" />
        </div>
        <div class="col-2"></div>
      </div>
    </div>
  </div>
</template>
  
<script>
import { reactive, ref } from 'vue'
import axios from 'axios';
import SearchView from '@/components/SearchView.vue';
import router from '@/router';

export default {
  name: "PostsView",
  components: {
    SearchView
  },

  setup() {
    let posts = ref();
    let hot = ref();
    let main=JSON.parse(localStorage.getItem("main"))
    let count = reactive({
      //总页数
      pageCount: 100,
      //当前页数
      currentPage: 1,
    })

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

    const inter=(val)=>{
      console.log(val)
      if (localStorage.getItem("token")) {
                axios({
                    method: "post",
                    url: "http://47.112.132.80:9997/insertHistory",
                    headers: {
                        "token": localStorage.getItem("token")
                    },
                    data: {
                        "username": main.username,
                        "debateId": val.id,
                        "debateNickname": val.summary,
                        "userImage": "111",
                        "title": val.title
                    }
                })
            }
      router.push({
      name:"details",
      params:{
        "id":val.id,
        "ownerId":val.ownerId,
        "infoId":val.infoId
      }
     })
    }

    //获取帖子列表
    const getList = () => {
      axios.post("http://47.112.132.80/api/debate/content/getByKind", {
        "kindId": 1,
        "size": 10,
        "page": count.currentPage,
      })
        .then(res => {
          count.pageCount = Math.ceil(res.data.total / 10)
          posts.value = res.data.data
        })
        .catch(err => {
          console.log(err)
        })
    }; getList();

    //
    const handle = (val) => {
      count.currentPage = val
      getList();
    };

        //切换分页
    const refresh=()=>{
      hotlist();
    }

    //热点刷新、获取
    const hotlist=()=>{
      axios.post("http://47.112.132.80/api/debate/content/getHot")
      .then(res => {
        hot.value = res.data.data
      })
      .catch(err => {
        console.log(err)
      })
    };hotlist();

    //搜索更新页面
    const update=(content)=>{
        console.log("content",content)
        posts.value=content
      }

    return {
      posts,
      count,
      background,
      getList,
      handle,
      hot,
      update,
      inter,
      refresh
    }
  }
}
</script>
  
<style scoped>
.search{
  position:absolute;
  left: 50%; 
  margin-left: -300px;
  z-index:11;
}
.home {
  margin-top: 70px;
  margin-bottom: 100px;
  z-index:1;
}

.title {
  /* font-size: 20px; */
  font-weight: 800;
  color: rgb(94, 114, 228);
  text-align: center;
}

.content {
  margin-top: 5px;
  font-weight: 200;
}

.b {
  position: fixed;
  right: 5%;
  top: 130px;
  width: 20%;
  font-size: 16px;
}

.refresh {
  width: 8%;
  margin-right: 10px;
  float: right;
}

.refresh:hover {
  color: #D0D0D0;
  transition: 0.5s;
  cursor: pointer;
}

.num1 {
  font-size: 20px;
  color: firebrick;
  margin-right: 10px;
}

.num2 {
  font-size: 20px;
  color: darkorange;
  margin-right: 10px;
}

.hot {
  font-size: 20px;
  font-weight: 500;
}

.good {
  display: inline-block;
  border-radius: 15%;
  margin-right: 2px;
  padding: 2px 3px;
  width: 10%;
}

.hotnum {
  font-size: 13px;
  margin-left: 2px;
  font-weight: 200;
}

.size {
  font-size: 15px;
}

img {
  width: 20%;
}

.bgBackground {
  width: 100%;
  height: 100%;
  position: fixed;
  overflow: auto;
}

.middle {
  text-align: center;
}

.box {
  margin-bottom: 20px;
}

.bgBackground {
  width: 100%;
  height: 100%;
  position: fixed;
  overflow: auto;
}

.like-img {
  width: 18px;
}

.comment-img {
  width: 18px;
  margin-right: 10px;
}

.like {
  margin-left: 20px;
}

.comment {
  margin-left: 20px;
}
.a{
  cursor:pointer;
}
</style>