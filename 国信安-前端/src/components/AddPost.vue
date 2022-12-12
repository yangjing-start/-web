<template>
    <div>
        <form>
            <div class="mb-3">
                <label for="title" class="form-label">标题</label>
                <input type="text" class="form-control" id="title" placeholder="请输入标题" v-model.lazy="title">
            </div>
            <div class="mb-3">
                <label for="summary" class="form-label">摘要</label>
                <textarea class="form-control" id="summary" rows="3" placeholder="请输入摘要"
                    v-model.lazy="summary"></textarea>
            </div>
            <div class="mb-3">
                <label for="content" class="form-label">内容</label>
                <textarea class="form-control" id="content" rows="10" placeholder="请输入内容"
                    v-model.lazy="content"></textarea>
            </div>
            <div class="row">
                <div class="col-3"></div>
                <div class="col-3">
                    <el-button class="button" text @click="select_img">
                        选择图片
                    </el-button>

                    <el-dialog v-model="show" title="选择图片">
                        <div class="img">
                            <span v-for="image in images" :key="image.id">
                            <img v-if="image.id" @click="click_img(image)"
                                :src="image.image" alt="">
                            </span>
                        </div>
                        <div>
                            <button @click="confirm" type="button" class="btn btn-primary a">确认</button>
                        </div>
                    </el-dialog>
                </div>
                <div class="col-3">
                    <button type="button" @click="submit" class="btn btn-primary">
                        发布帖子
                    </button>
                </div>
                <div class="col-3"></div>
            </div>
        </form>
    </div>
</template>

<script>
import { ref } from '@vue/reactivity'
import axios from 'axios'
export default {
    name: "AddPost",

    setup() {
        //帖子信息
        let title = ref()
        let summary = ref()
        let content = ref()
        //所有照片
        let images = ref()
        //选择的照片
        let image = ref([])
        const imageNull = ref([])
        let main = JSON.parse(localStorage.getItem("main"))
        let show = ref(false);

        //打开选择图片
        const select_img = () => {
            axios({
                method: "post",
                url: "http://47.112.132.80/api/debate/material/getAll",
                data: {
                    "username": main.username
                },
                headers: {
                    "token": localStorage.getItem("token")
                }
            })
                .then(res => {
                    images.value = res.data.data
                })
                .catch(err => {
                    console.log(err)
                })
            show.value = true
        }

        //选择照片发布
        const click_img = (val) => {
            image.value.push(val.image)
            val.id=""
        }

        //关闭选择图片
        const confirm = () => {
            show.value = false
        }

        //发布
        const submit = () => {
            if (!title.value && !summary.value && !content.value) {
                alert("请填充完整！")
            } else {
                axios({
                    method: "post",
                    url: "http://47.112.132.80/api/debate/content/saveContent",
                    data: {
                        "content": content.value,
                        "summary": summary.value,
                        "title": title.value,
                        "username": main.username,
                        "url": image.value,
                    },
                    headers: {
                        "token": localStorage.getItem("token")
                    }
                })
                    .then(()=> {
                        content.value = '';
                        title.value = '';
                        summary.value = '';
                        image.value = imageNull.value;

                        axios({
                            method: "post",
                            url: "http://47.112.132.80/api/detail/userProduct/updateUserPosts/" + main.username,
                            headers: {
                                "token": localStorage.getItem("token")
                            }
                        }).then.catch
                    })
                    .catch(err => {
                        console.log(err)
                    })
            }
        }

        return {
            show,
            click_img,
            title,
            summary,
            content,
            submit,
            select_img,
            images,
            confirm,
        }
    }
}
</script>

<style scoped>
.img img {
    width: 24%;
    margin-right: 1%;
}

.a {
    margin-top: 20px;
    margin-left: 40%;
}
</style>