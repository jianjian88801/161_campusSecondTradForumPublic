<template>
	<view class="login">
		<view class="popup">
			<uni-popup   isMaskClick="false" ref="userInfo" backgroundColor="white" type="center">
				<view class="user-info-popup">
					<view @click="close()" class="close">
						<image style="width: 20px;height: 20px;" src="../../static/images/icon/close.png"></image>
					</view>
					<form @submit="submit">
						<view class="user-info-box">
							<button class="avatar-wrapper" open-type="chooseAvatar" @chooseavatar="onChooseAvatar">
								
							  <image v-if="avatar!=null" style="width: 100px;height: 100px;border-radius: 50%;" mode="aspectFill"  :src="avatar"></image>
							  <image v-else style="width: 100px;height: 100px;border-radius: 50%;" mode="aspectFill"  src="../../static/images/icon/_me.png"></image>
							</button>
							<view style="margin-top: 15px;" class="user-info-un">
								<!-- <input v-model="nikeName" type="nickname" placeholder="请输入昵称" /> -->
								<input maxlength="14" :value="nickName" type="nickname" name="nickName" placeholder="请输入昵称" />
							</view>
						</view>
						<view style="margin: 15px 0;color: #757575ef;" class="min-front"><span>*首次登录请设置头像与昵称</span></view>
						<view  style="padding-bottom: 10px;" class="send">
							<button form-type="submit" class="btn">确定</button>
						</view>
					</form>			
				</view>
			</uni-popup>
		</view>
	</view>
</template>

<script>
	import {wxLogin} from "@/api/login.js";
	import {mapState,mapMutations} from "vuex";
	import {uploadImage,updateInfo} from "@/api/login.js";
	import { showToast } from '@/utils/validate'
	import {qiniuUpload} from "@/utils/upload.js"
	
	export default {
		data() {
			return {
				loading:0,
				user:{},
				avatar:null,
				key:null,
				nickName:null
			}
		},
		onLoad() {
			this.login();
		},
		methods: {
			
			setUrl(url){
					
				return this.getUrl(url);
			},
			submit(e){
				
				if (!this.avatar) return showToast({ title: '请上传图片或正确图片' });
				if (!e.detail.value.nickName) return showToast({ title: '请输入昵称' });
				
				this.$store.dispatch("user/updateUserInfo",{
					key:this.key,
					avatar:this.avatar,
					nickName:e.detail.value.nickName
				})
				uni.reLaunch({url:'/pages/index/index'});	
			
								
			},
			onChooseAvatar(e) {	
				console.log()
				let { avatarUrl } = e.detail 	
				this.key = avatarUrl.split('/').pop()
				this.avatar = avatarUrl			
			 },
			close(){
				this.$refs.userInfo.close();
			 
			},
			 login(){
				// uni.showLoading({
				// 	mask:true,
				// 	title:"加载中..."
				// })
				this.$store.dispatch('user/wxLogin').then(()=>{
					uni.hideLoading();
					this.user = uni.getStorageSync("user");
					if(this.user.nickName==null||this.user.nickName=="微信用户")
						this.$refs.userInfo.open();
					else
						uni.reLaunch({url:'/pages/index/index'});	
				
				}).catch((err)=>{
					//登录失败，1.没网，2.后端出问题
					// uni.navigateBack();
					// uni.hideLoading();
				})
			}
		}
	}
</script>

<style>

.popup .uni-popup__wrapper{
	border-radius: 15px;
}
.user-info-popup{
	width: 70vw;
	border-radius: 15px;
	/* height: 500px; */
	padding: 10px 15px;
	
	
}
.user-info-popup > .close{
	display: flex;
	justify-content: flex-end;
	margin-bottom: 10px;
}

.user-info-box{
	display: flex;
	flex-direction: column;
	align-items: center;
}

.avatar-wrapper{
	width: 100px;
	height: 100px;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0;

	
}
</style>
