import Vue from 'vue'
import Router from 'vue-router'
import Server from '@/components/Server'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Server',
      component: Server
    }
  ]
})
