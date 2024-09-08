

import {wxLogin,wxLogout,getInfo} from "@/api/login.js"
import {getToken, setToken} from "@/utils/auth.js"
import {uploadImage,updateInfo} from "@/api/login.js";
import { showToast } from '@/utils/validate'
import {qiniuUpload} from "@/utils/upload.js"
export default {
    namespaced: true,
    //用户接口
    actions: {
		/**
		 * @param {key}  头像名称
		 */
		 async updateUserInfo({commit,state},{key,avatar,nickName,phonenumber,sex}){
			let user = {...state.user}
			user.nickName = nickName || user.nickName
			user.phonenumber = phonenumber || user.phonenumber 
			user.sex = sex || user.sex
			try{
				let imageURL = undefined
				if(avatar!==undefined&&avatar!=null&&avatar!=""&&key!=undefined){
					const res= await qiniuUpload(avatar,'avatar/' + key);
					imageURL = res.imageURL
				}
				//不需要上传头像
				if(key===undefined)
					user.avatar = avatar || user.avatar
				else //上传头像
					user.avatar = imageURL || user.avatar	
				await updateInfo(user)
				showToast({title:"修改成功!"})
				commit('SET_USER',user);
			}catch(e){
				//TODO handle the exception
				console.error(e)
				showToast({title:"修改失败!"})
			}
			
		},
		async wxLogin({commit,state}) {
			
			
			try{
				//防止重复登录
				if(state.loging==true){
					return;
				}
					
				commit("SET_LOGING",true)
				const {code} = await  wx.login();
				const param = {
					 code : code
				}
				const {token} = await wxLogin(param);
				commit('SET_TOKEN',token)
				
				const res = await getInfo();
				commit('SET_USER',res.user)
				commit('SET_ROLES',res.roles)
				commit('SET_PERIMSSIONS',res.permissions)
			}catch(e){
				//TODO handle the exception
				throw e;
			}finally{
				commit("SET_LOGING",false)
			}
		},
		logout({commit}) {  
		  commit('SET_TOKEN',null)		
		  commit('SET_USER',{})
		  commit('SET_ROLES',[])
		  commit('SET_PERIMSSIONS',[])
		  uni.clearStorage();
		  // wxLogout()
        }
    },
    //操作数据
    mutations: {
        SET_TOKEN(state, token) {
            state.token = token
			// console.log("设置token成功")
			setToken(token)
        },
        SET_USER(state, user) {
			// console.log(user)
            state.user = user
			// console.log("设置user成功")
			uni.setStorageSync('user', user)
			
        },
		SET_NICK_NAME(state, nickName) {
			// console.log(user)
			Vue.set(state.user,nickName,nickName);
			// console.log("设置user成功")	
		},
		SET_ROLES(state,roles){
			state.roles = roles;
			uni.setStorageSync("roles",roles)
		},
		SET_PERIMSSIONS(state,permissions){
			state.permissions = permissions;
			
			uni.setStorageSync("permissions",state.permissions)
		},
		SET_LOGING(state,val){
			state.loging = val;
		}
		
    },
    //数据
    state: {
		loging:false,
        token: getToken(),  // 用户token
        user:uni.getStorageSync('user')!=""?uni.getStorageSync('user'):{},  // 用户
		roles:uni.getStorageSync('roles')!=""?uni.getStorageSync('roles'):[] ,//角色
		permissions:uni.getStorageSync('permissions')!=""?uni.getStorageSync('permissions'):[] ,//权限
    },
   getters: {
		isLogin(state){
			return state.token!=null&&state.token!="";
		},
		 getUserId(state){
			 return state.user.userId
		 },
		 permissions(state){
			 return state.permissions
		 },
		  // 鉴权
		 hasPermi: (state) => (perm) => {
		   if(perm==undefined||perm==null||perm=="")
				return false;
			if(perm=='*:*:*')
				return true;
		   return state.permissions.includes(perm);
		 }
   },

}
