<template>
    <div class="head">
        <!-- 评论框 -->
        <div class="row">
          <div class="col-1">
            <img class="img-fluid" :src="main.userImage"/>
          </div>
          <div class="col-9">
            <textarea v-model.lazy="text" class="form-control t" id="exampleFormControlTextarea1" rows="2"></textarea>
          </div>
          <div class="col-2">
            <button @click="reply_comment">发表</button>
          </div>
        </div> 
      </div>
      <hr>
  </template>
  
  <script>
  import {ref} from 'vue'

  export default{
    name:'ReplyText',

    setup(props,context){
      let main=ref()
      main.value=JSON.parse(localStorage.getItem("main"))
      let text=ref('');

      //回传数据
      let reply_comment=()=>{
        context.emit('reply_comment',text.value);
        text.value='';
      }

      return {
        text,
        reply_comment,
        main,
      }
    }
  }
  </script>
  
  <style scoped>
  .head {
    margin-top: 20px;
    position: relative;
    height: 75px;
    border-radius: 5px;
  }
  .head img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    position: absolute;
    left: 5px;
  }
  /* 评论框 */
  .head input {
    top: 13px;
    left: 80px;
    height: 45px;
    border-radius: 5px;
    outline: none;
    width: 65%;
    font-size: 20px;
    padding: 0 20px;
    border: 2px solid #f8f8f8;
  }
  /* 发布评论按钮 */
  .head button {
    position: absolute;
    width: 60px;
    height: 58px;
    border: 0;
    border-radius: 5px;
    font-size: 16px;
    font-weight: 500;
    color: #fff;
    background-color: rgb(118, 211, 248);
    cursor: pointer;
    letter-spacing: 2px;
  }
  /* 鼠标经过字体加粗 */
  .head button:hover {
    font-weight: 600;
  }
  .t{
    width: 100%;
  }
  </style>