<template>
    <div>
        <div class="posts" v-for="post in posts" :key="post.id">
            <div class="card a">
                <div class="card-body">
                    <div class="row">
                        <div class="col-10" @click="inter(post)">
                            {{ post.title }}
                        </div>
                        <div class="col-2 delete" @click="deletePost(post)">
                            <button type="button" class="btn btn-primary">删除</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from '@vue/reactivity'
import axios from 'axios'
import router from '@/router'
export default {
    name: "ShowPosts",

    setup() {
        let main = JSON.parse(localStorage.getItem("main"))
        let posts = ref()

        const getAll = () => {
            axios({
                method: "post",
                url: "http://47.112.132.80/api/debate/content/getContentByUserId",
                data: {
                    "username": main.username
                },
                headers: {
                    "token": localStorage.getItem("token")
                }
            })
                .then(res => {
                    console.log(res)
                    posts.value = res.data.data
                })
                .catch(err => {
                    console.log(err)
                })
        }; getAll();

        const inter = (val) => {
            router.push({
                name: "details",
                params: {
                    "id": val.id,
                    "ownerId": val.ownerId,
                    "infoId": val.infoId
                }
            })
        }

        const deletePost = (val) => {
            axios({
                method: "post",
                url: "http://47.112.132.80/api/debate/content/deleteAllById",
                headers: {
                    "token": localStorage.getItem("token")
                },
                data: {
                    "contentId": (val.id).toString(),
                    "username": (main.username).toString(),
                }
            }).then(()=>{
                axios({
          method: "post",
          url: "http://47.112.132.80/api/detail/userProduct/update",
          headers: {
            "token": localStorage.getItem("token")
          },
          data: {
            "description":"posts",
            "value": "111",
            "username": main.username
          }
        })
          .then(()=>{
            alert("删除成功")
          }).catch
            }).catch
        }
        return {
            posts,
            inter,
            deletePost,
        }
    }
}
</script>

<style scoped>
.posts {
    margin-bottom: 15px;
}

.a {
    cursor: pointer;
}

.delete:hover {
    color: red;
}
</style>