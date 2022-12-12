import axios from "axios";
import router from "@/router";

const ModuleUser = {
    state: {
        id: "",
        username: "",
        password: "",
        nickname: "",
        userImage: "",
        sex: false,
        address: "",
        email: "",
        phone: "",
        synopsis: "",
        follows: 0,
        fans: 0,
        posts: 0,
        token: "",
        is_login: Boolean(false),
        commentId: "",
    },
    getters: {
    },
    mutations: {
        updateMain(state, main) {
            state.username = main.username;
            state.password = main.password;
            state.email = main.email;
            state.userId = main.id;
            state.userImage = main.userImage;
            state.nickname = main.nickname;
        },
        updateToken(state, val) {
            state.token = val
        },
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.password = user.password;
            state.nickname = user.nickname;
            state.phone = user.phone;
            state.email = user.email;
            state.userImage = user.userImage;
            state.sex = user.sex;
            state.address = user.address;
            state.synopsis = user.synopsis;
            state.follows = user.follows;
            state.fans = user.fans;
            state.posts = user.posts;
            state.is_login = user.is_login;
        },

        logout(state) {
            axios({
                method: "post",
                url: "http://47.112.132.80/api/user/logout",
                headers:{
                    "token":(localStorage.getItem("token")).substring(3)
                }
            })
            .then(() => {
                state.is_login=false
                localStorage.setItem("is_login","")
                localStorage.setItem("token","")
            }).catch

            state.userId = "";
            state.username = "";
            state.nickname = "";
            state.password = "";
            state.email = "";
            state.phone = "";
            state.userImage = "";
            state.sex = false;
            state.address = "";
            state.synopsis = "";
            state.follows = 0;
            state.fans = 0;
            state.posts = 0;
            state.token = "";
            state.is_login = false;
        },

        logoff(state) {
            axios({
                method: "post",
                url: "http://47.112.132.80/api/user/deleteUser",
                headers: {
                    "token": localStorage.getItem("token")
                },
                data: {
                    "username":JSON.parse(localStorage.getItem("main")).username
                }
            })
                .then(()=> {
                    localStorage.setItem("main",JSON.stringify(""))
                    localStorage.setItem("is_login","")
                    localStorage.setItem("token","")
                    state.token = "";
                    state.is_login = false;

                })
                .catch(err => {
                    console.log(err)
                })
        }
    },
    actions: {
        login(context, data) {
            axios.post("http://47.112.132.80/api/user/login", {
                "username": data.username,
                "password": data.password,
                "imageVerify": data.imageVerify,
                "time": data.time + '',
            })
                .then(res => {
                    localStorage.setItem("is_login", true)
                    localStorage.setItem("token", res.headers.authorization)
                    context.commit("updateToken", {
                        ...res.headers.authorization
                    })
                    context.commit("updateMain", {
                        ...res.data.userMessage,
                        is_login: true,
                    })
                    axios({
                        method: "post",
                        url: "http://47.112.132.80/api/detail/userProduct/getUserDetail",
                        headers: {
                            "token": localStorage.getItem("token")
                        },
                        data: {
                            "username": res.data.userMessage.username
                        }
                    })
                        .then(res => {
                            localStorage.setItem("main", JSON.stringify(res.data.data))
                            router.push({
                                name: "home",
                            })
                        })
                        .catch(err => {
                            console.log(err)
                        })
                })
                .catch(()=> {
                    alert("登陆失败，请检查数据是否正确！")
                })

        }
    },
    modules: {
    }
}

export default ModuleUser;