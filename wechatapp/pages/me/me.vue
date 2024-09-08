<template>
	<view class="me">
		<!-- 背景 -->
		<view :style="{backgroundImage: 'linear-gradient(to bottom, rgba(255, 255, 255, 0), rgba(0, 0, 0, 0.5)),url(' + getBackGround() + ')', 'background-repeat':'no-repeat', backgroundSize:'cover',backgroundPosition:'center center'}"  class="bar">
			<!-- <image mode="aspectFill"  class="backgroundImage" src="../../static/logo.jpg"></image> -->
			
			<view v-if="isLogin()"  class="mine-user-info">
				<view class="mine-avatar">
					<image class="me-user-av"  @click="gotoUserInfo()"  :src="setUrl(user.avatar)"></image>
				
				</view>
				<view  class="mine-username ">
					<span>{{user.nickName}}</span>
					<!-- <span>微信用户</span> --> 
					<image v-if="user.sex==1" style="width: 12px;height: 12px;margin-left: 3px;" src="../../static/images/icon/g (1).png"></image>
					<image v-if="user.sex==0" style="width: 12px;height: 12px;margin-left: 3px;"src="../../static/images/icon/b.png"></image>
				</view>
				<view class="info-list">
					<view @click="gotoFanList()" class="item">
						<span>{{fansNum}}</span>
						<span>粉丝</span>
					</view>
					<view @click="gotoFollowList()" class="item">
						<span>{{followNum}}</span>
						<span>关注</span>
					</view>
				
				</view>
				<view  class="tag-view">
					<view  v-for="(item,index) in user.roles" class="item">
						<uni-tag size="small" :inverted="true" :circle="true" :text="item" custom-style="background-color:#4f4f4f; color: #ffffff; "/>
					</view>
				</view>
			</view>
			<view v-else  class="mine-user-info">
				<view  class="mine-avatar">
					<image class="me-user-av"  @click="gotoLogin()" ></image>
				
				</view>
				<view  class="mine-username "  @click="gotoLogin()" >
					<!-- <span>{{user.nickName}}</span> -->
					<span>点我登录</span>
				</view>		
				<view class="tag-view">
					<uni-tag size="small" :inverted="true" :circle="true" text="游客" custom-style="background-color:#4f4f4f; color: #ffffff; "/>
				</view>
			</view>		
			
		</view>
		<view style="margin: 20px 15px;">
			<view class="item-list card">
				<view @click="gotoInfoList()" class="item">
					<list-item showRight >
						<template  slot="left">
							<view style="display: flex;align-items: center;">
								<image  style="width: 20px;height: 20px;margin-right: 10px;" src="../../static/images/icon/comment-active.png"></image>
								<span>信息</span>
							</view>
						</template>
						
					</list-item>
				</view>
				<view @click="gotoTalkList()" class="item">
					<list-item showRight >
						<template  slot="left">
							<view style="display: flex;align-items: center;">
								<image style="width: 20px;height: 20px;margin-right: 10px;" src="../../static/images/icon/comment(2).png"></image>
								<span>帖子</span>
							</view>
						</template>
					</list-item>
				</view>
				<view @click="gotoStarList()" class="item">
					<list-item showRight >
						<template  slot="left">
							<view style="display: flex;align-items: center;">
								<image  style="width: 20px;height: 20px;margin-right: 10px;" src="../../static/images/icon/star-active.png"></image>
								<span>收藏</span>
							</view>
						</template>
						
					</list-item>
				</view>
			
				<view @click="goToMore()" class="item">
					<list-item showRight >
						<template  slot="left">
							<view style="display: flex;align-items: center;">
								<image style="width: 20px;height: 20px;margin-right: 10px;" src="../../static/images/icon/about.png"></image>
								<span>更多</span>
							</view>
						</template>
						
					</list-item>
				</view>			
		</view>
	

	</view>
		
	
	</view>
	 
</template>

<script>
	import {mapState,mapGetters} from "vuex";
	import {listItem} from "@/components/listItem/listItem.vue";
	import {getFollowInfoById} from "@/api/user.js";
	import {qiniuUpload} from "@/utils/upload.js"
	export default {
		data() {
			return {
				status:0,
				followNum:0,
				fansNum:0,
				barImage:null,
				topImg: {
				  'top_1': require('../../static/logo.jpg')
				}
			
			}
		},
		//下拉刷新
		onPullDownRefresh() {
			//获取关注、粉丝信息
			getFollowInfoById({userId:this.user.userId}).then(res=>{
				this.followNum = res.data.followNum
				this.fansNum  = res.data.fansNum
				this.barImage = res.data.barImage
				uni.stopPullDownRefresh()
			})
		},
		onLoad() {
			//获取关注、粉丝信息
			getFollowInfoById({userId:this.user.userId}).then(res=>{
				this.followNum = res.data.followNum
				this.fansNum  = res.data.fansNum
				this.barImage = res.data.barImage
			})
		},
		computed:{
			...mapState('user',['user']),
			setBgColor (bgColor) {
				if (bgColor === '' || bgColor === '' || bgColor === undefined) {
				  return {background: 'url(' + this.topImg['top_1'] + ') -6px -4px no-repeat '}
				} else {
				  return {background: 'url(' + this.topImg[bgColor] + ') -6px -4px no-repeat '}
			  }
			}
		
		},
		
		components:{
			listItem
		},
		methods: {
			...mapGetters('user',['isLogin']),
			
			getBackGround(){
				if(this.stringUtils.isNoEmpty(this.barImage))
					return this.getUrl(this.barImage)
				return this.getUrl(this.user.avatar);
			},
			setUrl(url){
				return this.getUrl(url);
			},
			gotoLogin(){
				uni.navigateTo({
					url:'/pages/login/login'
				})
			},
			gotoUserInfo(){
				uni.navigateTo({
					url:'/pages/userInfo/userInfo'
				})
			},
			gotoTalkList(){
				
				uni.navigateTo({
					url:`/pages/talk/talk?userId=${this.user.userId}`
				})
			},
			gotoStarList(){
				uni.navigateTo({
					url:'/pages/collection/collection'
				})
			},
			goToMore(){
				uni.navigateTo({
					url:'/pages/more/more'
				})
			},
			gotoInfoList(){
				uni.navigateTo({
					url:'/pages/news/news'
				})
			},
			gotoFollowList(){
				uni.navigateTo({
					url:`/pages/follow/follow?userId=${this.user.userId}`
				})
			},
			gotoFanList(){
				uni.navigateTo({
					url:`/pages/fans/fans?userId=${this.user.userId}`
				})
			}
		
		}
	}
</script>

<style>
	/* 个人信息开始 */
	/* 背景 */
.bar{
	/* background-image: ; */
	position: relative;
	width: 100%; 
	height: 45vh;
	
	border-style: solid;
	border-width: 0 0 2px 0;
	border-color: black;
	/* filter: brightness(50%); */
	
	
}
.backgroundImage{
	/* background-image: ; */
	position: absolute;
	left: 0;
	top: 0;
	z-index: -1;
	width: 100%;
	height: 100%;
}

/* 头像 */
.me-user-av{
	
	position: relative;
	width: 70px;
	height: 70px;
	border-radius: 50%;
	border-style: solid;
	border-color: white;
	border-width: 3px;
	background: linear-gradient(-180deg, #BCC5CE 0%, #929EAD 98%), radial-gradient(at top left, rgba(255,255,255,0.30) 0%, rgba(0,0,0,0.30) 100%);
	 background-blend-mode: screen;
				
}
.mine-user-info{
	
	position: absolute;
	left: 60rpx;
	bottom: 15px;
	display: flex;
	flex-direction: column;
	align-items: flex-start;	
}
.mine-username{
	margin-top: 10px;
	color: white;
	font-size: 18px;	
}

.info-list{
	padding: 0 85rpx 0 25rpx;
	display: flex;
	
}
.info-list > .item{
	
	width: 145rpx;
	height: 145rpx;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	color: white;
}
.info-list > .item > span:first-child{
	font-size: 16px;
	margin-bottom: 5px;
	font-weight: 600;
}
.info-list > .item > span:nth-child(2){
	font-size: 12px;
}
.tag-view{
	display: flex;
	
	margin-top: 15px; 
	font-size: 10px;
	
	
}
.tag-view   > .item {
	margin-left: 10px;
}
/* 个人信息结束 */

/* 列表开始*/
.item-list{

	background-color: white;
	padding: 5px;
	

}
.item-list >.item{
	margin: 10px 0;
}



</style>
