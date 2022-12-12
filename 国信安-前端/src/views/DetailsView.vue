<template>
  <div :style="background" class="bgBackground">
    <div class="row">
      <div class="col-1"></div>
      <div class="col-2 feix">
        <div class="card a">
          <div class="card-body">
            <div class="row top">
              <div class="col-4">
                <img @click="interInfo" class="img-fluid" style="border-radius: 50%;width: 50px;height: 50px;"
                  :src="userInfo.userImage" alt="">
              </div>
              <div class="col-8">
                <span class="nickname">{{ userInfo.nickname }}</span>
                <p class="info">{{ userInfo.synopsis }}</p>
              </div>
            </div>
            <div class="information">
              <div class="row">
                <div class="col-4">
                  <p class="num interList" @click="interList(0)">{{ userInfo.fans }}</p>
                  <p class="char">粉丝</p>
                </div>
                <div class="col-4">
                  <p class="num interList" @click="interList(1)">{{ userInfo.follows }}</p>
                  <p class="char">关注</p>
                </div>
                <div class="col-4">
                  <p class="num">{{ userInfo.posts }}</p>
                  <p class="char">帖子</p>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-3"></div>
              <div class="col-6">
                <div v-if="!is_owner">
                  <button v-if="!followed" @click="follow" type="button" class="btn btn-light btn-lg">+关注</button>
                  <button v-else @click="unfollow" type="button" class="btn btn-light btn-lg">已关注</button>
                </div>
              </div>
              <div class="col-3"></div>
            </div>
          </div>
        </div>

        <div>
          <div class="card a">
            <div class="card-header">
              热门帖子
            </div>
            <div class="card-body">
              <div v-for="hot in hotPost" :key="hot.id">
                <p @click="interPost(hot)" class="interPost">{{ hot.title }}</p>
              </div>
            </div>
          </div>

        </div>
      </div>

      <div class="col-6">
        <div class="card">
          <div class="card-body">
            <p class="title">{{ post.title }}</p>
            <div class="middle">
              <span class="c">{{ userInfo.nickname }}</span>
              <span class="c">{{ (contentid.createTime).slice(0, 10) }}</span>
              <span class="c" v-if="!is_like">
                <img class="like" @click="like" src="../assets/like.svg" alt=""><span class="t">{{ likenum
                }}</span>
              </span>
              <span class="c" v-else>
                <img class="like" @click="unlike" src="../assets/like2.svg" alt=""><span class="t">{{ likenum
                }}</span>
              </span>
              <span class="c"><img class="comment" src="../assets/comment.svg" alt=""><span
                  class="t">{{ contentid.remarks }}</span></span>
            </div>
            <div class="content">
              <div style="font-size:20px;" class="text">
                {{ post.content }}
              </div>
            </div>
            <div v-for="(image, index) in post.image" :key="index" style="text-align: center;margin-bottom: 10px;">
              <img style="width:80%;" class="img-fluid" :src="image" alt="">
            </div>
            <hr>
            <div>
              <p class="rem">评论</p>
            </div>

            <!-- 回复 -->
            <div>
              <ReplyText @reply_comment="reply_comment"></ReplyText>
            </div>

            <div>
              <div class="row">
                <div class="col-1"></div>
                <div class="col-11">
                  <div>
                    <FirstReply :remarks="remarks"></FirstReply>
                    <hr>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="col-3"></div>
    </div>
  </div>
</template>
  
<script>
import { ref } from 'vue'
import ReplyText from '../components/ReplyText.vue'
import FirstReply from '../components/FirstReply.vue'
import axios from 'axios'
import { useRoute } from 'vue-router';
import router from '@/router';
import { useStore } from 'vuex';

export default {
  name: "DetailsView",
  components: {
    ReplyText,
    FirstReply,
  },

  setup() {
    const store=useStore()
    const route = useRoute();
    //用户信息
    const userInfo = ref()
    //热帖列表
    const hotPost = ref()
    //帖子信息
    const post = ref()
    //是否点赞
    let is_like = ref()
    let likenum=ref()
    let contentid = ref()

    let is_login = localStorage.getItem("is_login")||store.state.is_login
    let followed = ref()

    //一级评论的数据
    let remarks = ref([])

    //接收不同页面传来的id
    const commentsId = route.params.infoId
    const ownerId = route.params.ownerId
    const id = route.params.id
    localStorage.setItem("id", id)

    //是否点赞
    let islike = ref(false);
    let main = JSON.parse(localStorage.getItem("main"))

    //是否本人
    let is_owner = ref(main.username == ownerId)

    //背景图片
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

    //获取用户信息
    const getInfo = () => {
      axios({
        method: "POST",
        url: "http://47.112.132.80/api/detail/userProduct/getOtherDetail",
        data: {
          "selfUsername": main.username,
          "otherUsername": (ownerId).toString(),
        }
      })
        .then(res => {
          userInfo.value = res.data.data
          followed.value = res.data.data.followed
        })
        .catch(err => {
          console.log(err)
        })
    }; getInfo();

    //获取帖子详细信息
    const detailPost = () => {
      axios.post("http://47.112.132.80/api/debate/content/getContentInfo", {
        "infoId": commentsId,
        "contentId": id,
        "username": is_login ? main.username : null
      })
        .then(res => {
          is_like.value = res.data.data.flag
          post.value = res.data.data
        })
        .catch(err => {
          console.log(err)
        })

      //加载详情的同时发送
      axios.post("http://47.112.132.80/api/behavior/behavior/read", {
        "contentId": id,
        "userId": is_login ? main.username : 0,
        "count": 1,
      })
        .then.catch;
    }; detailPost()

    //获取一栏
    const contentId = () => {
      axios.post("http://47.112.132.80/api/debate/content/getContentById", {
        "id": id
      })
        .then(res => {
          contentid.value = res.data.data
          likenum.value=res.data.data.likes
        })
        .catch(err => {
          console.log(err)
        })
    }; contentId();

    //获取热帖
    const hotPosts = () => {
      axios({
        method: "post",
        url: "http://47.112.132.80/api/debate/content/getHotByUser",
        data: {
          "username": ownerId.toString(),
        }
      })
        .then(res => {
          hotPost.value = res.data.data
        })
        .catch(err => {
          console.log(err)
        })
    }; hotPosts();

    //取得一级评论
    const comments = () => {
      axios.post("http://47.112.132.80/api/comment/getComments", {
        "debateId": commentsId,
        "pageSize": 0,
        "begin": 0,
      })
        .then(res => {
          remarks.value = res.data.data
        })
        .catch(err => {
          console.log(err)
        })
    }; comments();

    //点赞
    const like = () => {
      if (!is_login) {
        alert("请先登陆!")
      } else {
        axios({
          method: "post",
          url: "http://47.112.132.80/api/behavior/behavior/like",
          headers: {
            "token": localStorage.getItem("token")
          },
          data: {
            "contentId": id,
            "operation": "0",
            "username": main.username,
          }
        })
          .then(() => {
            is_like.value = true;
            likenum.value++
          })
          .catch(err => {
            console.log(err)
          })
      }
    }

    //取消点赞
    const unlike = () => {
      if (!is_login) {
        alert("请先登陆!")
      } else {
        axios({
          method: "post",
          url: "http://47.112.132.80/api/behavior/behavior/like",
          headers: {
            "token": localStorage.getItem("token")
          },
          data: {
            "contentId": id,
            "operation": "1",
            "username": main.username,
          }
        })
          .then(() => {
            is_like.value = false;
            likenum.value--
          })
          .catch(err => {
            console.log(err)
          })
      }
    }

    //添加一级评论
    const reply_comment = (content) => {
      if (!is_login) {
        alert("请先登陆!")
      } else {
        let commentId=Date.parse(new Date()).toString() + main.username
        axios({
          method: "post",
          url: "http://47.112.132.80/api/comment/insertComment",
          headers: {
            "token": localStorage.getItem("token"),
          },
          data: {
            "debateId": commentsId,
            "nickname": main.nickname,
            "userImage": main.userImage,
            "commentId": commentId,
            "title": "khgkyjgkjhg",
            "content": content,
            "createTime": Date.parse(new Date()),
            "username": main.username,
          }
        })
          .then(() => {
            //vue添加
            remarks.value.push({
              "commentId": commentId,
              "content": content,
              "createTime": new Date().toLocaleString(),
              "like": 0,
              "nickname": main.nickname,
              "subComments": 0,
              "userImage": main.userImage,
            })
          })
          .catch(err => {
            console.log(err)
          })

        //发送
        axios({
          method: "post",
          url: "http://47.112.132.80/api/behavior/behavior/comment",
          headers: {
            "token": localStorage.getItem("token"),
          },
          data: {
            "contentId": id,
            "op": "0",
            "username": main.username,
          }
        })
          .then.catch
      }
    }

    //关注
    const follow = () => {
      if (!is_login) {
        alert("请先登陆!")
      } else {
        axios({
          method: "post",
          url: "http://47.112.132.80/api/detail/userProduct/updateFans",
          headers: {
            "token": localStorage.getItem("token")
          },
          data: {
            "username": main.username,
            "updateUsername": ownerId,
            "nickname": main.nickname,
            "userImage": main.userImage
          }
        })
          .then(() => {
            followed.value = true
          })
          .catch(err => {
            console.log(err)
          })
      }
    }

    //取消关注
    const unfollow = () => {
      if (!is_login) {
        alert("请先登陆!")
      } else {
        axios({
          method: "post",
          url: "http://47.112.132.80/api/detail/userProduct/reduceFans",
          headers: {
            "token": localStorage.getItem("token")
          },
          data: {
            "username": main.username,
            "updateUsername": ownerId,
            "nickname": "1111",
            "userImage": "123"
          }
        })
          .then(() => {
            followed.value = false
          })
          .catch(err => {
            console.log(err)
          })
      }
    }

    //进入个人信息
    const interInfo = () => {
      if (!is_login) {
        alert("请先登陆!")
      } else {
        if (userInfo.value.username == main.username) {
          router.push({
            name: 'own',
          })
        }
      }
    }

    const interPost = (val) => {
      router.push({
        name: "details",
        params: {
          "id": val.id,
          "ownerId": val.ownerId,
          "infoId": val.infoId
        }
      })
    }

    const interList = (val) => {
      router.push({
        name: "list",
        params: {
          "choose": val,
          "username": userInfo.value.username,
        }
      })
    }

    return {
      background,
      remarks,
      reply_comment,
      islike,
      like,
      unlike,
      follow,
      unfollow,
      followed,
      interInfo,
      is_login,
      userInfo,
      hotPost,
      post,
      is_owner,
      interList,
      interPost,
      contentId,
      contentid,
      is_like,
      likenum,
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

.top {
  margin-bottom: 20px;
}

.a {
  margin-bottom: 20px;
}

.title {
  font-size: 40px;
}

.information {
  margin: 20px 0px;
  text-align: center;
}

.num {
  font-size: 16px;
  font-weight: 500;
}

.char {
  font-size: 16px;
  font-weight: 200;
}

.nickname {
  margin-top: 10px;
  font-size: 20px;
}

.info {
  font-weight: 200;
  margin-top: 2px;
}

.like {
  width: 25px;
}

.comment {
  width: 22px;
}

.c {
  margin-left: 30px;
}

.middle {
  padding: 10px 0px;
  background-color: rgba(248, 248, 248);
  font-weight: 300;
}

.content {
  margin-top: 20px;
  font-size: 30px;
}

.t {
  margin-left: 10px;
  font-weight: 300;
}

.rem {
  font-size: 30px;
}


.content .first {
  display: flex;
  position: relative;
  padding: 10px 10px 0px 0;
}


.comment {
  width: 22px;
  margin-right: 10px;
}

.c {
  margin-left: 30px;
  font-weight: 200;
}

.c:hover {
  cursor: pointer;
  color: blue;
}

.first-time {
  font-weight: 200;
}

.text {
  margin: 0px 50px;
  font-family: "等线";
  line-height: 40px;

  /* letter-spacing: 2px; */
}

.interPost {
  cursor: pointer;
}

.interList {
  cursor: pointer;
}
</style>