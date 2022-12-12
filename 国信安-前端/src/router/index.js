import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import PostsView from '../views/PostsView.vue'
import ListView from '../views/ListView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import NotfoundView from '../views/NotfoundView.vue'
import DetailsView from '../views/DetailsView.vue'
import OwnView from '../views/OwnView.vue'
import HistoryPosts from '../views/HistoryPostsView.vue'
import UserView from '../views/UserView.vue'
import NewpasswordView from '../views/NewpasswordView.vue'
import ShowPosts from '../components/ShowPosts.vue'
import AddImage from '../components/AddImage.vue'
import AddPost from '../components/AddPost.vue'

const routes = [
  {
    path:'/',
    name:'home',
    component:HomeView
  },
  {
    path: '/posts/',
    name:'posts',
    component: PostsView,
  },
  {
    path: '/list/:choose/:username/',
    name: 'list',
    component: ListView,
  },
  {
    path: '/login/',
    name:'login',
    component: LoginView,
  },
  {
    path: '/register/',
    name:'register',
    component: RegisterView
  },
  {
    path: '/details/:id/:ownerId/:infoId/',
    name: 'details',
    component: DetailsView
  },
  {
    path: '/user/',
    name: 'user',
    component: UserView,
    children:[
      {
      path:'/showposts',
      name:'showposts',
      component:ShowPosts
    },
    {
      path:'/addimage',
      name:'addimage',
      component:AddImage,
    },
    {
      path:'/addpost',
      name:'addpost',
      component:AddPost,
    }
    ],redirect:'/showposts'
  },
  {
    path: '/own/',
    name: 'own',
    component: OwnView,
  },
  {
    path:'/historyposts/',
    name:'historyposts',
    component:HistoryPosts,
  },
  {
    path:'/newpassword/',
    name:'newpassword',
    component:NewpasswordView,
  },
  {
    path: '/404/',
    name: '404',
    component: NotfoundView
  },
  {
    path: '/:catchAll(.*)',
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
