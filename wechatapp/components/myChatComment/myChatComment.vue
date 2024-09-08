<template>
	<view @click="openInput()" class="chat-comment">
		<view class="left ">
			<image @tap.stop="gotoUserDetail(c_item.user.userId)"  class="av-border" style="width: 30px;height: 30px;border-radius: 50%;" :src="setUrl(c_item.user.avatar)"></image>
		</view>
		<view class="right">
			<view class="user-box">
				<view class="comment-user-info">
					<view class="mid-front"><span >{{c_item.user.nickName}}</span></view>
					<view class="min-front"><span >{{c_item.date||""}}</span></view>						
				</view>
				<!-- <image v-if="getUserId&&c_item.userId==getUserId"  @click="()=>{this.$refs.deleteTalkPoput.open()}" style="margin-left: 8px;width: 20px;height: 20px;"  class="item"  src="../../static/images/icon/delete.png"></image>			 -->
			</view>	
			<view class="comment-user-content">
				<view v-if="c_item.parentId!=null" class="reply-content-box">
					<view v-if="c_item.userBy" class="reply-user">
						<span>回复：{{c_item.userBy.nickName}}</span>
					</view>
					<view v-else class="reply-user">
						<span>回复：</span>
					</view>
					<view class="reply-content">
						<text selectable="true" >{{c_item.commentBy}}</text>
					</view>
				</view>
				<text  selectable="true"  >{{c_item.content}}</text>	
				<view v-if="getUserId&&c_item.userId==getUserId"  style="margin-top: 5px;font-weight: 600;" class="min-front"><span @tap.stop="()=>{this.$refs.deleteTalkComment.open()}" >删除</span></view>
				<!-- <view class="card-image" >
					<view v-for="(item,index) in imageList" class="image-item" @click="gotoShowImage(index)" >
						<image mode="aspectFill" :src="item"></image>
					</view>
				</view> -->
			</view>
		</view>
	
		<!-- 删除确认框 -->
		<my-poput ref="deleteTalkComment" @submitForm="deleteTalkComment" >
			<template slot="title">
				<span>删除</span>
			</template>
			<template slot="content">
				<span>确定删除该评论？</span>
			</template>
		</my-poput>
		
	</view>
	
</template>

<script>
	import {mapGetters} from "vuex";
	import {deleteTalkComment} from "@/api/talkComment.js"
	import  myPoput from "@/components/myPoput/myPoput.vue"
	export default {
		name:"myChatComment",
		data() {
			return {
				imageList:[
					"http://localhost:8088/preview/file/upload/d9082234e84e855c05dd596a25d62cf4.jpg",
					"http://localhost:8088/preview/file/upload/d9082234e84e855c05dd596a25d62cf4.jpg",
					"http://localhost:8088/preview/file/upload/d9082234e84e855c05dd596a25d62cf4.jpg",
					"http://localhost:8088/preview/file/upload/d9082234e84e855c05dd596a25d62cf4.jpg"
					// "http://localhost:8088/preview/file/upload/d9082234e84e855c05dd596a25d62cf4.jpg",
					// "http://localhost:8088/preview/file/upload/d9082234e84e855c05dd596a25d62cf4.jpg"
				]
			};
		},
		props:{
			c_item:{
				type:Object,
				default:null
			}
			
		},
		components:{
			myPoput,
		},
		
		computed:{
			...mapGetters("user",['getUserId'])
		},
		methods:{
			
			setUrl(url){
				return this.getUrl(url)
			},
			//用户详情
			gotoUserDetail(userId){
				uni.navigateTo({
					url:`/pages/userDetail/userDetail?userId=${userId}`
				})
			},	
		
			gotoShowImage(index){
				
				this.fullScreen(index);
			},
			//全屏预览图片函数方法
			fullScreen(i) {
				wx.previewImage({
					urls: this.imageList, //需要预览的图片http链接列表，多张的时候，url直接写在后面就行了
					current: i, // 当前显示图片的http链接，默认是第一个
					success: function(res) {console.log("打开成功")},
					fail: function(res) {console.log("打开失败")},
					complete: function(res) {console.log("完成打开")},
				})
			},
			openInput(){
				this.$emit("openInput",this.c_item.id)
			},
			deleteTalkComment(){
				deleteTalkComment(this.c_item.id)
				this.$emit('removeTalkComment',this.c_item.id);
			}
		}
	
	}
</script>

<style>
.chat-comment{
	
	display: flex;
	flex-direction: row;
	padding: 10px 30rpx;
	/* box-shadow: rgba(0, 0, 0, 0.02) 0px 1px 3px 0px, rgba(27, 31, 35, 0.15) 0px 0px 0px 1px; */
	box-shadow: rgba(27, 31, 35, 0.04) 0px 1px 0px, rgba(255, 255, 255, 0.25) 0px 1px 0px inset;
	
	/* align-items: flex-start; */
}
.chat-comment > .left{
	
}
.chat-comment > .right{
	margin-left: 10px;
	flex: 1;
}
.chat-comment > .right > .user-box{
	display: flex;
	align-items: center;
	/* justify-content: space-around; */
}

.comment-user-info{
	display: flex;
	flex-direction: column;
	
}
.comment-user-info >.mid-front  {
	margin-bottom: 3px;
}
.comment-user-content{
	padding: 5px 0px;
}



.card-image{
	margin-top: 5px;
	display: flex;
	flex-wrap: wrap;
	
}
.image-item > image{
	width: 110px;
	height: 110px;
	margin: 1px;
	border-radius: 5px;
}

.reply-content-box{
	display: flex;
	flex-direction: column;
	align-items: flex-start;
	background-color: #efefefef;
	border-radius: 5px;
	padding: 10px;
	margin: 10px 0;
	width: 100%;
	box-sizing: border-box;
	
}
.reply-content-box > .reply-user{
	font-size: 12px;
	font-weight: 600;
}
.reply-content-box > .reply-content{
	margin-top: 8px;
}
.comment-footer{
	padding: 8px 0;
}
.comment-footer > .l_r > .left >.item:nth-child(2){
	margin: 0 15px;
}


</style>