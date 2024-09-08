<template>
	<view>
		<view class="my-card card">
			<!-- 头像，名称 ,删除按钮，收藏按钮 -->
			<view class="headr re-top l_r">
				<view style="font-weight: 600;" class="left user-info">
					<image style="width: 60rpx;height: 60rpx;margin-right: 10px;" :src="setUrl(c_item.user.avatar)"></image>
					<span>{{c_item.user.nickName}}</span>
				</view>
				<view class="right">
					<!-- <view  class="item"><span class="iconfont icon-shoucang"></span></view> -->
					<view @click="()=>{this.$refs.deleteDiscussPoput.open()}" style="margin-left: 8px;" class="item"><span class="iconfont icon-shanchu"></span></view>
				</view>
			</view>
			<!-- 图片，内容 -->
			<view class="card-text">
				<text>{{c_item.content}}</text>
				<view class="card-image" v-if="c_item.pictrueList!=null">
					<view v-for="(item,index) in imageList" class="image-item" @click="gotoShowImage(index)" >
						<image mode="aspectFill" :src="item"></image>
					</view>
				</view>
			</view>
			<!-- 页脚:时间，点赞，评论按钮 -->
			<view class="card-footer l_r">
				<view class="left dmin-front">{{c_item.createTime||''}}</view>
				<view class="right">
					<view @click="thumbsDiscuss()" class="item">
						<span :class="[thumbs?'iconfont icon-dianzan1 icon-active':'iconfont icon-dianzan1']"></span>
						<span class="item-text " >{{thumbsNum}}</span>
					</view>
					<view @click="openInput(null)" class="item">
						<span class="iconfont icon-pinglun"></span>
						<span class="item-text " >{{c_item.commentNum}}</span>
					</view>
				</view>
			</view>
			<!-- 回复区 -->
			<view  v-show="c_item.comment!=null&&c_item.comment.length>0" class="interactive-box" >
				<!-- 点赞区 -->
			<!-- 	<view class="top">
					<span class="iconfont icon-dibudaohanglan-"></span>
					<view  class="fabulous-list min-front">
						<view v-for="(item,index) in fabulousList" class="user-info mg":key="index" >
							<image  class="box-size av-border" src="../../static/logo.jpg"></image>
							<span class="user_name" >{{item.name}}</span>
						</view>
					</view>
				</view> -->
				<!-- 评论区 -->
				<view class="bottom">
					<span class="iconfont icon-dibudaohanglan-"></span>
					<view class="chat-list mid-front">
						<view v-for="(item,index) in c_item.comment" class="item mg" :key="index">
							<my-comment @openInput="openInput" :item="item"></my-comment>
						</view>
					</view>
				</view>
			</view>
			
			
		</view>

		<!-- 输入框弹窗 -->
		<bottomInput :isOpenInput="isOpenInput" @closeInput="closeInput" @send="send"></bottomInput>
		
		<!-- 删除确认框 -->
		<my-poput ref="deleteDiscussPoput" @submitForm="deleteDiscuss" >
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
	import myComment from "@/components/myComment/myComment.vue"
	import myPoput from "@/components/myPoput/myPoput.vue"
	import{addComment} from "@/api/comment.js"
	import {deleteDiscussById,thumbsDiscuss} from "@/api/discuss.js"
	export default {
		name:"myCard",
		components:{
			myComment,
			myPoput
		},
		data() {
			return {
				isOpenInput:false,
				//回复的人的id
				userId:null,
				thumbsNum:0,
				thumbs:0,
				imageList:[
						
				],
				Height:0,
				KeyboardHeight:0
			};
		},
		props:{
			c_item:{
				type:Object,
				default:null
			}
			
		},
		created() {
			if(this.c_item.pictrueList!=null){
				this.imageList = this.c_item.pictrueList
				
				for(let i=0;i<this.imageList.length;i++){
					this.imageList[i] = this.setUrl(this.imageList[i]);
				}
			}
			this.thumbsNum = this.c_item.thumbsNum
			this.thumbs = this.c_item.thumbs
		},
		methods:{
			//点赞
			thumbsDiscuss(){
				this.thumbs^=1;
				this.thumbsNum= this.thumbsNum + parseFloat(this.thumbs==1?1:-1)
				const param = {
					discussId : this.c_item.id
				}
				thumbsDiscuss(param).then(res=>{
					
				}).catch(err=>{
					
				})
			},
			deleteDiscuss(){
				const param = {
					discussId : this.c_item.id
				}
				deleteDiscussById(param).then((res)=>{
					uni.showToast({
						title: "删除成功！",
						icon:'exception',
						duration:850
					});
					uni.navigateBack();
				}).catch((err)={
					
				})
			},
			setUrl(url){
				return this.getUrl(url);
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
	
			//点击发送信息回调
			send(val){
				//获取到val
				this.addComment(val);
			},
			closeInput(){
				this.isOpenInput = false;
			},
			//向评论/帖子添加评论
			openInput(id){
				//id==null 回复c_item
				//id!=null 回复c_item的评论
				this.userId = id;
				this.isOpenInput=true;
			
				// //触发输入框
				// this.$store.commit("OPEN_INPUT");
			},
			//添加评论
			addComment(val){
				const param = {
					//评论的帖子
					discussId:this.c_item.id,
					//被评论的人
					parentId:this.userId,
					//评论的内容
					content:val
				}
				console.log(param)
				addComment(param).then(()=>{
					
				}).catch(err=>{
					
				})
				
				
				console.log(param);
				
			}
		
		}
	}
</script>

<style>
	/* 卡片内边距 */
.my-card{
	padding: 0 10px 10px 10px;
	/* margin: 30px 0; */
}

.headr{
	
}
.headr > .iconfont{

	margin-right: 20px;
}

.re-top{
	position: relative;
	top:-10px;
}
/* 内容 */
.card-text{
	
	padding: 20px 0;
	
}
/* 页脚 */
.card-footer{
	width: 100%;
	padding-bottom: 5px;
}

.card-footer >.right > .item {
	display: flex;
	align-items: center;
	padding-left: 8px;
}

.card-footer .item .item-text{
	margin-left: 4px;
}
/* 回复区开始 */
.interactive-box .iconfont{
	margin-right: 5px;
	margin-top: 5px;
}
.interactive-box >.top,.interactive-box > .bottom{
	display: flex;
}
/* 点赞列表和聊天列表 */
.fabulous-list,.chat-list{
	display: flex;
	padding: 0px 5px 5px 5px;
	background-color: #f4f4f4;
	flex: 1;
}
/* 头像大小 */
.box-size{
	width: 40rpx;
	height: 40rpx;
	margin-right: 1px;
	border-radius: 5px !important;
	
}

.mg{
	margin: 5px;
}
/* 点赞列表 */
.fabulous-list{
	flex-wrap: wrap;
}
/* 聊天列表 */
.chat-list{
	flex-direction: column;
}
/* 名字 */
.user_name{
	margin-left: 2px;
	line-height: 44rpx;
}
.card-image{
	display: flex;
	flex-wrap: wrap;
	
}
.image-item > image{
	width: 205rpx;
	height: 205rpx;
	margin: 2px;
	border-radius: 5px;
}

</style>