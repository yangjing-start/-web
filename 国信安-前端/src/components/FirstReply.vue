<template>
    <div class="head" v-for="remarks in remarks" :key="remarks.commentId">
        <div class="row">
            <div class="col-1">
                <img class="img-fluid" style="border-radius: 100%;width: 30px;height: 30px;" :src="remarks.userImage"
                    alt="">
            </div>
            <div class="col-11">
                <p class="username">{{ remarks.nickname }}</p>
                <div class="content">
                    {{ remarks.content }}
                </div>
                <div class="button">
                    <span class="time">{{ remarks.createTime }}</span>
                    <span class="c"><img class="comment" src="../assets/comment.svg" alt=""><span
                            @click="reply(remarks)">回复</span></span>
                    <span v-if="(!Number(remarks.isLike))" class="likes"><img @click="like(remarks)" class="like"
                            src="../assets/like.svg" alt=""><span v-if="(Number(remarks.likes) != 0)"
                            class="like_count">{{ remarks.likes
                            }}</span></span>
                    <span v-else class="likes"><img @click="unlike(remarks)" class="like" src="../assets/like2.svg"
                            alt=""><span class="like_count">{{ remarks.likes }}</span></span>
                </div>
            </div>
        </div>

        <div v-if="remarks.commentId === show">
            <ReplyText @reply_comment="reply_comment"></ReplyText>
        </div>
        <!-- 加载更多 -->
        <div class="more" v-if="remarks.subComments" @click.once="showMore(remarks)"><span
                v-if="remarks.subComments && !remarks.state" class="updata">加载更多<span class="">({{
                        remarks.subComments
                }})</span></span>
            <div v-for="comments in comments.lists" :key="comments.fid">
                <div v-if="remarks.commentId === comments.fid">
                    <div class="row">
                        <div class="col-1"></div>
                        <div class="col-11">
                            <div>
                                <SecondReply :comments="comments"></SecondReply>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
  
<script>
import SecondReply from './SecondReply.vue';
import ReplyText from './ReplyText.vue';
import { ref } from 'vue'
import axios from 'axios';

export default {
    name: 'FirstReply',
    components: {
        SecondReply,
        ReplyText
    },

    props: {
        remarks: {
            type: Object,
            required: true,
        }
    },

    setup() {
        let is_login = ref();
        setInterval(()=>{
            is_login.value = JSON.parse(localStorage.getItem("is_login"))||""
        },1000)
        let islike = ref(false)
        //是否显示
        let show = ref('');
        //某一级评论的数据
        let info = ref();
        let main = JSON.parse(localStorage.getItem("main"))
        // let main=localStorage.getItem("main")
        //二级评论列表
        let comments = ref({
            lists: [
            ]
        });
        //点击回复
        const reply = (content) => {
            if (!content.state) {
                showMore(content)
            }
            info.value = content
            show.value = content.commentId
        }

        //replytext传过来的数据，并发表评论
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
                        "fId": info.value.commentId,
                        "nickname": main.nickname,
                        "userImage": main.userImage,
                        "reply": "",
                        "content": content,
                        "createTime": Date.parse(new Date()),
                        "username": main.username,
                    }
                })
                    .then(() => {
                    })
                    .catch(err => {
                        console.log(err)
                    })

                comments.value.lists.push({
                    "fid": info.value.commentId,
                    "subComments": [
                        {
                            "fId": info.value.commentId,
                            "nickname": main.nickname,
                            "userImage": main.userImage,
                            "reply": "",
                            "content": content,
                            "createTime": new Date().toLocaleString(),
                            "username": main.username,
                        }
                    ]
                })
                console.log(comments.value.comments)
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
            }
        };

        //获取二级评论,加载更多
        const showMore = (val) => {
            axios.post("http://47.112.132.80/api/comment/getSubComments", {
                "fId": val.commentId,
                "pageSize": 0,
                "begin": 0,
            })
                .then(res => {
                    comments.value.lists.push({
                        ...res.data.data
                    })
                    console.log("res", res.data.data)
                })
                .catch(err => {
                    console.log(err)
                })
            val.state = true
        }

        //点赞
        const like = (val) => {
            if (!is_login.value) {
                alert("请先登陆!")
            } else {
                axios({
                    method: "post",
                    url: "http://47.112.132.80/api/comment/addLikeComment",
                    headers: {
                        "token": localStorage.getItem("token")
                    },
                    data: {
                        "id": val.commentId,
                        "username": main.username,
                    }
                })
                    .then(() => {
                        val.isLike = true
                        val.likes++
                    })
                    .catch(err => {
                        console.log(err)
                    })
            }
        }

        //取消点赞
        const unlike = (val) => {
            if (!is_login.value) {
                alert("请先登陆!")
            } else {
                axios({
                    method: "post",
                    url: "http://47.112.132.80/api/comment/reduceLikeComment",
                    headers: {
                        token: localStorage.getItem("token")
                    },
                    data: {
                        "id": val.commentId,
                        "username": main.username,
                    }
                })
                    .then(() => {
                        val.isLike = false
                        val.likes--
                    })
                    .catch(err => {
                        console.log(err)
                    })
            }
        }

        return {
            comments,
            reply,
            show,
            reply_comment,
            showMore,
            info,
            islike,
            like,
            unlike,
        }
    }
}
</script>
  
<style scoped>
.username {
    font-size: 18px;
}

.content {
    font-size: 18px;
    margin-bottom: 5px;
    font-weight: 350;
    margin-right: 120px;
}

.time {
    font-weight: 300;
}

.comment {
    margin-right: 10px;
    width: 20px;
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

.more {
    font-size: 14px;
    margin-left: 50px;
    margin-top: 10px;
    margin-bottom: 10px;
}

.updata {
    cursor: pointer;
    color: blue;
}

.like {
    width: 18px;
    cursor: pointer;
}

.likes {
    margin-left: 10px;
}

.like_count {
    font-size: 14px;
    font-weight: 300;
    margin-left: 5px;
}
</style>
  