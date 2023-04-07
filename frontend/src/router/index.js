import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import BookView from '../views/Book.vue'
import ContentView from '../views/ContentBook.vue'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/book',
      name: 'book',
      component: BookView,
    },
    {
      path: '/content',
      name: 'content',
      component: ContentView
    }
  ]
})

export default router
