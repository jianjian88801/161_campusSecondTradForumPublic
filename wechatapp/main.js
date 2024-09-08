// #ifndef VUE3
import Vue from 'vue'
import App from './App'
import store from '@/store/index.js'
//全局样式
import './static/icon/font/iconfont.css'
import './static/css/com.css'
import share from '@/utils/share.js'
import {getUrl} from "@/utils/request.js"
import bottomInput from './components/bottomInput/bottomInput.vue'
import myXhx from './components/myXhx/myXhx.vue'
import scroolPage from "./components/scrollPage/scrollPage.vue"
import myLoad from "./components/myLoad/myLoad.vue"
import { $showToast } from './utils/validate'
import StringUtils from "@/utils/StringUtils.js"
Vue.mixin(share)
//全局挂载函数
Vue.prototype.$store = store
Vue.prototype.getUrl = getUrl
Vue.prototype.stringUtils = StringUtils

//全局注册组件
Vue.component('bottomInput',bottomInput);
Vue.component('myXhx',myXhx);
Vue.component('scroolPage',scroolPage);
Vue.component('myLoad',myLoad);

//自定义v-指令
// Vue.directive('hasPermi', hasPermi)


Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
	store,
    ...App
})
app.$mount()

// #endif