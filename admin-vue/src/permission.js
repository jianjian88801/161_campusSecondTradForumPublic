import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/auth-redirect', '/bind', '/register']

router.beforeEach((to, from, next) => {
  NProgress.start()
  //有token
  if (getToken()) {

    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)

    /* 想要去登录*/
    if (to.path === '/login') {
      next()
      NProgress.done()
      return;
    } else { //跳转页面
      //有token没信息的原因。1.刚登录 2.刷新了页面
      if (store.getters.roles.length === 0) {
        //路由跳转页面不开启提示框，认证失败直接跳到登录页
        isRelogin.show = true

        // 拉取完user_info信息
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          //拉路由
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            router.addRoutes(accessRoutes) // 动态添加可访问路由表
            next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
          })
        }).catch(err => {
          //出问题了，如果是后端代码错就寄了。
          })
      } else {
        next()
      }
    }
  } else {
    // 没有token
    if (whiteList.indexOf(to.path) !== -1) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${to.fullPath}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
