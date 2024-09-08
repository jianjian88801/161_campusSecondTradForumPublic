<template>
	<view>
		<view class="my-card card">
			<!-- 头像，名称 ,删除按钮，收藏按钮 -->
			<view class="headr l_r">
				<view @click="gotoUserDetail(c_item.user.userId)" style="font-weight: 600;" class="left user-info">
					<image style="width: 60rpx;height: 60rpx;margin-right: 10px;" :src="setUrl(c_item.user.avatar)"></image>
					<!-- <uni-tag size="mini" circle :inverted="true" type="warning" text="管理员"></uni-tag> -->
					<span>{{c_item.user.nickName}}</span>
					
				</view>
				<view class="right">
					
					<view  @click="talkCollection()">
						<image v-if="star" style="margin-left: 8px;width: 20px;height: 20px;"  class="item"  src="../../static/images/icon/star-active.png"></image>
						<image v-else   style="margin-left: 8px;width: 20px;height: 20px;"  class="item"  src="../../static/images/icon/star.png"></image>
						
					</view>
					 
					 <image v-if="getUserId&&c_item.userId==getUserId"  @click="()=>{this.$refs.deleteTalkPoput.open()}" style="margin-left: 8px;width: 20px;height: 20px;"  class="item"  src="../../static/images/icon/delete.png"></image>
														
				</view>
			</view>
			<!-- 图片，内容 -->
			<view  @click="gotoChatDetail()" class="card-text">
				<uni-tag  :inverted="true" size="mini"  circle="true" :text="getTypeName(c_item.typeName)" custom-style="border-color: #fac843;color:black;font-weight: 600;"></uni-tag>
				<text space="ensp" selectable="true" >{{c_item.content}}</text>
				<view class="card-image" v-if="c_item.pictrueList!=null">
					<view v-for="(item,index) in imageList" class="image-item" @tap.stop="gotoShowImage(index)" >
						<image mode="aspectFill" :src="item"></image>
					</view>
				</view>
			</view>
			<!-- 页脚:时间，点赞，评论按钮 -->
			<view class="card-footer l_r">
				<view class="left mid-front">{{c_item.date||''}}</view>
				<view class="right">
					<view @click="talkThumbs()" class="item">
						<image v-if="thumbs" style="width: 20px;height: 20px;" src="../../static/images/icon/like-active.png"></image>
						<image v-else style="width: 20px;height: 20px;" src="../../static/images/icon/like.png"></image>				
						<!-- <span :class="[thumbs?'iconfont icon-dianzan1 icon-active':'iconfont icon-dianzan1']"></span> -->
						<span class="item-text " >{{thumbsNum}}</span>
					</view>
					<view @click="gotoChatDetail()" class="item">
						<!-- <span class="iconfont icon-pinglun"></span> -->
						<image style="width: 20px;height: 20px;" src="../../static/images/icon/comment-active.png"></image>
						<span class="item-text " >{{c_item.comment.total}}</span>
					</view>
				</view>
			</view>
			<!-- 回复区 -->
			<view  v-if="c_item.comment.total>0" class="interactive-box" >
				<!-- 评论区 -->
				<view @click="gotoChatDetail()"  class="bottom">
					<!-- <span class="iconfont icon-dibudaohanglan-"></span> -->
					<image style="width: 20px;height: 20px;margin-right: 8px;" src="../../static/images/icon/comment(2).png"></image>
					
					<view class="chat-list mid-front">
						<view v-for="(item,index) in talkCommentList" class="item mg" :key="index">
							<myComment :item="item"></myComment>
						</view>
						<view  class="more"><span>共{{total}}条回复 ></span></view>
					</view>
					
				</view>
				
			</view>
			
			
		</view>

		<!-- 输入框弹窗 -->
		<!-- <bottomInput :isOpenInput="isOpenInput" @closeInput="closeInput" @send="send"></bottomInput> -->
		
		<!-- 删除确认框 -->
		<my-poput ref="deleteTalkPoput" @submitForm="deleteTalk()" >
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
	import{addTalkComment} from "@/api/talkComment.js"
	import {talkThumbs,collectionTalk,deleteTalkById} from "@/api/talk.js"
	import {mapGetters,mapState} from "vuex"
	export default {
		name:"myChat",
		components:{
			myComment,
			myPoput
		},
		data() {
			return {
				star:0,
				isOpenInput:false,
				//回复评论id
				parentId:null,
				thumbsNum:0,
				thumbs:0,
				userBy:"",
				imageList:[],
				talkCommentList:[],
				Height:0,
				KeyboardHeight:0,
				total:0
				
			};
		},
		props:{
			c_item:{
				type:Object,
				default:null
			}
			
		},
		computed:{
			...mapState('user',['user']),
			...mapGetters('user',['getUserId'])
			
		},
		created() {
			if(this.c_item.pictrueList!=null){
				 this.c_item.pictrueList.forEach(item=>{
					this.imageList.push(this.setUrl(item))
				})	
			}
			this.talkCommentList = this.c_item.comment.records;
			this.total = this.c_item.comment.total
			this.thumbsNum = parseInt(this.c_item.thumbsNum)
			this.thumbs = parseInt(this.c_item.thumbs)
			this.star = this.c_item.star
		},
		methods:{
			getTypeName(name){
				return "#"+name;
			},	
			//用户详情
			gotoUserDetail(userId){
				uni.navigateTo({
					url:`/pages/userDetail/userDetail?userId=${userId}`
				})
			},			
			//收藏
			talkCollection(){
			
				this.star^=1;
				collectionTalk(this.c_item.id);
			},
			gotoChatDetail(){
				uni.navigateTo({
					url:`/pages/chatDetail/chatDetail?id=${this.c_item.id}`
				})
			},
			//点赞
			talkThumbs(){
				this.thumbs^=1;
				
				this.thumbsNum = this.thumbsNum + (this.thumbs==1?1:-1)
				
				talkThumbs(this.c_item.id)
			},
			deleteTalk(){
				
				deleteTalkById(this.c_item.id).then((res)=>{
					uni.showToast({
						title: "删除成功！",
						icon:'exception',
						duration:850
					});
					this.$emit("remove",this.c_item.id);
					// uni.navigateBack();
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
				
				uni.previewImage({
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
				this.addTalkComment(val);
			},
			closeInput(){
				this.isOpenInput = false;
			},
			//向评论/帖子添加评论
			openInput(parentId){

				// this.parentId = parentId
				// this.isOpenInput=true;
			this.gotoChatDetail()
			
				// //触发输入框
				// this.$store.commit("OPEN_INPUT");
			},
			//添加评论
			
			addTalkComment(val){
				const param = {
					//评论的帖子id
					talkId:this.c_item.id,
					//被评论的人
					parentId:this.parentId,
					//评论的内容
					content:val
				}
				console.log(param)
				addTalkComment(param).then(()=>{
					
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
	padding-top: 5px;
}
.headr > .iconfont{

	margin-right: 20px;
}


/* 内容 */
.card-text{
	
	padding: 20px 0;
	
}
.card-text uni-tag{
	margin-right: 5px;
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
	
	overflow: hidden;
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
	margin-top: 5px;
	
}
.image-item > image{
	width: 205rpx;
	height: 205rpx;
	margin: 2px;
	border-radius: 5px;
}

.more{
	margin-top: 5px;
}
</style>