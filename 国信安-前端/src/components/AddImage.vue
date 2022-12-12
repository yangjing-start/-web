<template>
    <div>
        <div class="img">
            <img v-for="image in images" :key="image.id" :src="image.image" alt="">
        </div>
        <div class="el">
            <el-upload class="avatar-uploader" action="http://47.112.132.80/api/debate/material/save" method="post"
                :data="data" :show-file-list="false" :headers="headers" :before-upload="beforeAvatarUpload"
                :on-success="handleAvatarSuccess">
                <img v-if="imageUrl" :src="imageUrl" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon">
                    <Plus />
                </el-icon>
            </el-upload>
        </div>
    </div>
</template>

<script>
import { Message, Plus } from '@element-plus/icons-vue'
import { ref } from '@vue/reactivity'
import { onMounted } from '@vue/runtime-core'
import axios from 'axios'

export default {
    name: "AddImage",
    components: {
        Plus
    },
    setup() {
        //imageUrl是图片地址，imgsize判断图片大小，imgtype判断图片格式
        const imageUrl = ref('')
        let imgsize = ref('')
        let imgtype = ref('')
        
        //取得浏览器储存的main
        let main = JSON.parse(localStorage.getItem("main"))

        //上传图片的参数,FormDate形式
        let data = ref(new FormData())
        data.value.append('username', main.username)
        //图片列表
        let images = ref([])

        let headers = ref({
            "token": localStorage.getItem("token"),
            "ContentType": 'multipart/form-data',
        });

        //开始渲染是调用，加载所有的图片
        onMounted(() => {
            axios({
                method: "post",
                url: "http://47.112.132.80/api/debate/material/getAll",
                data: {
                    "username": main.username,
                },
                headers: {
                    "token": localStorage.getItem("token")
                }
            })
                .then(res => {
                    images.value = res.data.data
                    console.log("images", images.value)
                })
                .catch(err => {
                    console.log(err)
                })
        })

        //成功是调用的函数
        const handleAvatarSuccess = () => {
            axios({
                method: "post",
                url: "http://47.112.132.80/api/debate/material/save",
                headers: {
                    "token": localStorage.getItem("token"),
                    "ContentType": 'multipart/form-data',
                },
                data: data.value
            })
                .then(() => {
                    alert("添加成功!")
                })
                .catch(err => {
                    console.log(err)
                })
        }

        //上传文件之前是调用的函数
        const beforeAvatarUpload = (file) => {
            data.value.append('multipartFile', file)
            //判断照片类型
            imgtype.value = file.type === 'image/jpeg' ||
                file.type === 'image/png' ||
                file.type === 'image/jpg' ||
                file.type === 'image/gif'

            //图片类型
            if (!imgtype.value) {
                alert("请选择图片")
                Message.error("请选择图片")
            }
            //将图片大小转换成兆
            imgsize.value = file.size / 1024 / 1024
            //图片大小
            if (imgsize.value > 5) {
                alert("图片请在5M以内")
                Message.error("图片请在2M以内")
            }
        }

        return {
            beforeAvatarUpload,
            imageUrl,
            data,
            handleAvatarSuccess,
            images,
            headers,
        }
    }
}
</script>

<style scoped>
.avatar-uploader .avatar {
    width: 178px;
    height: 178px;
    display: block;
}
</style>
<style>
.avatar-uploader .el-upload {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    text-align: center;
}

.img img {
    width: 24%;
    margin-right: 1%;
}

.el {
    margin-top: 20px;
}
</style>