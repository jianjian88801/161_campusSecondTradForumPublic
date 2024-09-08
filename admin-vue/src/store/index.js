import Vue from 'vue'
import Vuex from 'vuex'
import settings from '@/store/mode/settings'
import user from '@/store/mode/user'
import permission from '@/store/mode/permission'
import getters from '@/store/getters'
import app from '@/store/mode/app'
import tagsView from '@/store/mode/tagsView'
Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    settings,
    user,
    permission,
    app,
    tagsView
  },
  getters

})

export default store
