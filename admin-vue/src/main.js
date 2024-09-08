import Vue from 'vue'
import Cookies from 'js-cookie'
import Element from 'element-ui'
import directive from './directive' // 自定义v-指令
import '@/assets/styles/index.scss' // global css
import '@/assets/styles/ruoyi.scss' // ruoyi css
import 'element-ui/lib/theme-chalk/index.css';
import router from './router'
import store from './store'
import './permission'
import App from '@/App' // permission control
import  '/public/css/reset.css'
import './assets/icons' // icon
import plugins from './plugins'
import { addDateRange, handleTree, parseTime, resetForm } from '@/utils/ruoyi' // plugins
import RightToolbar from '@/components/RightToolbar'
import Pagination from '@/components/Pagination'
import { getUrl } from '@/utils/request'
Vue.use(Element, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})
Vue.use(plugins)
Vue.use(directive)
//全局方法挂载
Vue.prototype.handleTree = handleTree
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.getUrl = getUrl
//全局组件挂载
//右侧工具栏
Vue.component('RightToolbar', RightToolbar)
//分页
Vue.component('Pagination',Pagination)
new Vue({
  render: h => h(App),
  router,
  store
}).$mount('#app')

