<template>
  <div>
    <div class="search-from">
      <div class="search">
        <input type="text" placeholder="请输入搜索内容" class="s-input" v-model="keyword" @focus="focusCustomer"
          @blur="blurCustomer" />
        <button class="s-btn" @click="submit">搜索</button>
      </div>
      <ul class="s-ul" v-show="show && keyword">
        <li v-if="!keyword" style="color: #ccc; font-size:14px; text-align: center;">
          暂时没有数据啦.....
        </li>
        <li v-else v-for="( item, index ) in myData" :key="index" @mousedown="chooseCustomer(item)">
          <span>{{ item }}</span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script>
import { ref } from '@vue/reactivity'
import { watch } from '@vue/runtime-core'
import axios from 'axios';
export default {
  name: "SearchView",

  setup(props, context) {
    let state = ref(false)
    let myData = ref()
    let show = ref(false)
    let keyword = ref('')
    let content=ref('')
    //判断是否登陆
    let is_login = ref();
        setInterval(()=>{
            is_login.value = JSON.parse(localStorage.getItem("is_login"))||""
        },1000)


    const focusCustomer = () => {
      show.value = true
    }

    const blurCustomer = () => {
      show.value = false
    }

    const chooseCustomer = (val) => {
      keyword.value = val
    }

    watch(keyword, () => {
      state.value = true
      setTimeout(() => {
        if (state.value == true) {
          state.value = false
          console.log("state", state.value)
          axios.post("http://47.112.132.80/api/debate/content/suggestion", {
            "key": keyword.value
          })
            .then(res => {
              myData.value = res.data.data
            })
            .catch(err => {
              console.log(err)
            })
        }
      })
    })

    const submit = () => {
      axios.post("http://47.112.132.80/api/debate/content/getByKind", {
        "kindId": 1,
        "size": 10,
        "page": 1,
        "key": keyword.value
      })
        .then(res => {
          content.value=res.data.data
          pass(content.value)
        })
        .catch(err => {
          console.log(err)
        })
    }

    const pass=(val)=>{
      context.emit('update',val)
    }

    return {
      keyword,
      myData,
      show,
      focusCustomer,
      blurCustomer,
      chooseCustomer,
      state,
      submit,
      content,
      pass,
    }
  }
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
}

.search-from {
  margin: 0 auto;
  width: 500px;
  display: flex;
  flex-direction: column;
}

.search-from .search {
  margin: 0 auto;
  margin-top: 15px;
  width: 500px;
  display: flex;
}

.search-from .search .s-input {
  width: 450px;
  height: 40px;
  padding-left: 12px;
  border-radius: 5px 0px 0px 5px;
  border: 2px solid #ccc;
  border-right-style: none;
}

.search-from .search .s-input:focus {
  border-radius: 5px 0px 0px 0px;
  outline: 0;
  border: 2px solid #00a0e9;
  border-right-style: none;
  border-bottom-color: rgb(238, 235, 235);
}

.search-from .search .s-btn {
  width: 50px;
  height: 40px;
  color: #fff;
  background-color: #00a0e9;
  border: none;
  border-radius: 0px 5px 5px 0px;
}

.search-from .search .s-btn:hover {
  background-color: #002be9;
}


.search-from .s-ul {
  background-color: #fff;
  width: 450px;
  height: 100%;
  border-radius: 0px 0px 5px 5px;
  border: 2px solid #00a0e9;
  border-top-style: none;
}

.search-from .s-ul li {
  list-style: none;
  padding-left: 10px;
  font-size: 14px;
  padding-top: 5px;
  padding-bottom: 5px;
}

.ifacitve {
  color: #00a0e9;
}
</style>