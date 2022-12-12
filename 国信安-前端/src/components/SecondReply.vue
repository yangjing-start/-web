<template>
    <div class="head" v-for="comments in comments.subComments" :key="comments.subCommentId">
        <div class="row">
            <div class="col-1">
                <img class="img-fluid" style="border-radius: 50%;width: 30px;height: 30px;" :src="comments.userImage" alt="">
            </div>
            <div class="col-11">
                <p class="username">{{ comments.nickname }}<span class="reply_reply" v-if="comments.to">回复<span
                            class="comment_comment">@{{ comments.to }}</span></span></p>
                <div class="content">
                    {{ comments.content }}
                </div>
                <div class="button">
                    <span class="time">{{ comments.createTime }}</span>
                    <span class="c"><img class="comment" src="../assets/comment.svg" alt=""><span
                            @click="reply(comments)">回复</span></span>
                    <span v-if="!Number(comments.isLike)" class="likes"><img @click="like(comments)" class="like"
                            src="../assets/like.svg" alt=""><span v-if="(Number(comments.likes) != 0)"
                            class="like_count">{{ comments.likes }}</span></span>
                    <span v-else class="likes"><img @click="unlike(comments)" class="like" src="../assets/like2.svg"
                            alt=""><span class="like_count">{{ comments.likes }}</span></span>
                </div>
            </div>
        </div>
        <div class="text" v-if="comments.subCommentId === remark">
            <ReplyText @reply_comment="reply_comment"></ReplyText>
        </div>
    </div>
</template>
  
<script>
import ReplyText from './ReplyText.vue';
import { reactive, ref } from 'vue'
import axios from 'axios';

export default {

    name: 'SecondReply',
    props: {
        comments: {
            type: Object,
            required: true,
        }
    },
    components: {
        ReplyText,
    },

    setup(props) {
        let info = ref()
        //用户信息
        let main = JSON.parse(localStorage.getItem("main"))
        let count = 0;
        //传来的二级评论
        let comment = reactive(props.comments)
        const fid = comment.fid
        let remark = ref('')
        //点赞
        let islike = ref(false)
        //判断打开哪一个评论的回复
        const reply = (val) => {
            info.value = val
            remark.value = val.subCommentId;
        }

        //是否登陆
        let is_login = ref();
        setInterval(()=>{
            is_login.value = JSON.parse(localStorage.getItem("is_login"))||""
        },1000)

        //接收到replytext的数据
        const reply_comment = (content) => {
            if (!is_login.value) {
        alert("请先登陆!")
      } else {
            axios({
                method: "post",
                url: "http://47.112.132.80/api/comment/insertSubComment",
                headers: {
                    "token": localStorage.getItem("token"),
                },
                data: {
                    "fId": fid,
                    "nickname": main.nickname,
                    "userImage": main.userImage,
                    "reply": info.value.nickname,
                    "content": content,
                    "createTime": Date.parse(new Date()),
                    "username": main.username,
                }
            })
                .then(() => {
                    //vue添加数据
                    comment.subComments.push({
                        id: "999999999999999999999" + count++,
                        content: content,
                        createTime: new Date().toLocaleString(),
                        like: 0,
                        userImage:main.userImage,
                        nickname: main.nickname,
                        to: info.value.nickname,
                        username: main.username
                    })
                    //同时发送
                    axios({
                        method: "post",
                        url: "http://47.112.132.80/api/behavior/behavior/comment",
                        headers: {
                            "token": localStorage.getItem("token"),
                        },
                        data: {
                            "contentId": localStorage.getItem("id"),
                            "op": "0",
                            "username": main.username,
                        }
                    }).then.catch
                })
                .catch(err => {
                    console.log(err)
                })
            }
        }

        const like = (val) => {
            if (!is_login.value) {
        alert("请先登陆!")
      } else {
            axios({
                method: "post",
                url: "http://47.112.132.80/api/comment/addLikeSubComment",
                headers: {
                    "token": localStorage.getItem("token")
                },
                data: {
                    "id": val.subCommentId,
                    "username": main.username,
                }
            })
                .then(()=> {
                    val.isLike = true
                    val.likes++
                })
                .catch(err => {
                    console.log(err)
                })
            }
        }

        const unlike = (val) => {
            if (!is_login.value) {
        alert("请先登陆!")
      } else {
            axios({
                method: "post",
                url: "http://47.112.132.80/api/comment/reduceLikeSubComment",
                headers: {
                    "token": localStorage.getItem("token")
                },
                data: {
                    "id": val.subCommentId,
                    "username": main.username,
                }
            })
                .then(()=> {
                    val.isLike = false
                    val.likes--
                })
                .catch(err => {
                    console.log(err)
                })
            }
        }

        return {
            reply,
            remark,
            reply_comment,
            comment,
            islike,
            unlike,
            like,
        }
    }
}
</script>
  
<style scoped>
.username {
    font-size: 20px;
}

.content {
    font-size: 18px;
    margin-bottom: 5px;
    font-weight: 300;
    margin-right: 100px;
}

.time {
    font-weight: 300;
}

.comment {
    margin-right: 10px;
    width: 18px;
}

.c {
    font-size: 14px;
    margin-left: 30px;
    font-weight: 300;
}

.c:hover {
    cursor: pointer;
    color: blue;
}

button {
    position: absolute;
    margin-top: 5px;
    width: 70px;
    height: 48px;
    border: 0;
    border-radius: 5px;
    font-size: 16px;
    font-weight: 500;
    color: #fff;
    background-color: rgb(118, 211, 248);
    cursor: pointer;
    letter-spacing: 2px;
}

button:hover {
    font-weight: 600;
}

.text {
    margin-top: 10px;
    margin-bottom: 10px;
}

.reply_reply {
    margin-left: 5px;
    font-size: 18px;
}

.comment_comment {
    margin-left: 5px;
    font-size: 18px;
    color: rgba(0, 0, 255, 0.598);
}

.like {
    width: 18px;
}

.like:hover {
    cursor: pointer;
}

.likes {
    margin-left: 20px;
}

.like_count {
    font-size: 14px;
    font-weight: 300;
    margin-left: 5px;
}
</style>
  