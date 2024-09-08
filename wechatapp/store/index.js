
import user from './user'

import Vue from 'vue'
import Vuex from 'vuex'
import {uploadImages,uploadImages2} from '@/utils/upload.js'
import {addTalk} from "@/api/addTalk.js"
import {addSell} from "@/api/addSell.js"
import {showToast} from "@/utils/validate.js"
Vue.use(Vuex)

		
const store = new Vuex.Store({
	modules:{
		user
	},
	state: {
		//是否打开输入框
		isOpenInput:false,
		//回复信息
		msg:{
			id:"",
			name:"",
			msg:"",
			type:1,
			
		}
		
	},
	
	mutations: {
		OPEN_INPUT(state){
			Vue.set(state,'isOpenInput',true);
		},
		CLOSE_INPUT(state){
			Vue.set(state,'isOpenInput',false);
		}
	}, 
	actions: {

		async addTalk({commit},{images,params}){
			try{
				//上传图片
				const pictrueList = await uploadImages(images);	
				params.pictrueList = pictrueList;
				addTalk(params);
			}catch(e){
				//TODO handle the exception
				showToast({title:"发布失败"})
			}
			
		},
		async addSell({commit},{images,params}){
			try{
				//上传图片
				const pictrueList = await uploadImages2(images);	
				params.sellPictures = pictrueList;
				addSell(params);
			}catch(e){
				//TODO handle the exception
				showToast({title:"发布失败"})
			}
			
		}
		 
		
	},
	getters:{
		isOpenInput : (state) => state.isOpenInput,
		permissions: state => state.user.msg
	}
})

export default store