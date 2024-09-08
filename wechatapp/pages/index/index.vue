<template>
	<view class="index">
		<view class="search">
			<uni-easyinput prefixIcon="search" v-model="search" placeholder="请输入关键字" @iconClick="iconClick" />
		</view>
		<view class="ads">
			<my-ads :info="info" ></my-ads>
		</view>
		<view class="warp">
		<view class="main-content">
			<view class="narBar">
				<view  @click="()=>{this.narBar = ''}" class="item" :class="[{'active':narBar==''}]">
					<spn>最近</spn>
				</view>
				<view v-if="typeList.length>0" @click="()=>{this.narBar = item.id}" class="item" v-for="(item,index) in typeList" :class="[{'active':narBar==item.id}]" >
					<!-- <view style="margin-bottom: 3px;" class="iconfont icon-pinglun "></view> -->
					<spn>{{item.name}}</spn>
				</view>
			</view>
			<view v-for="(item,index) in talkList" style="margin-bottom: 10px;" :key="index">
				<myChat  @remove="removeTalk()" :c_item="item"></myChat>
			</view>
			
			
		</view>
		
		<myLoad :status="status"></myLoad>
		<!-- <view @click="gotoAddCard" class="add">
			<span></span>
		</view> -->
		</view>
		
		<uni-fab ref="fab"  :pattern="pattern" :content="content" :horizontal="horizontal" :vertical="vertical"
				:direction="direction" @trigger="trigger" @fabClick="fabClick" />
		
	</view>
</template>


<script>
	import bottomInput from '@/components/bottomInput/bottomInput.vue'
	import myChat from '@/components/myChat/myChat.vue'
	import {getTalkPage,getTypeList} from "@/api/talk.js"
	import myAds from "@/components/myAds/myAds.vue"
	import {getAdsList} from "@/api/index.js"
	import {mapGetters} from "vuex"
	export default {
		data() {
			return {
				typeList:[], 
				search:null,
				current:0,
				info: [],
				status: 'loading',
				narBar:'',
				talkList:[],
				// 查询参数
				queryParams: {
					talkId:"",
					typeId: "",
					content:""
				},
				//总条数
				total:0,
				
				//悬浮窗：
					pattern: {
						color: '#7A7E83',
						backgroundColor: '#fff',
						selectedColor: '#fac843',
						buttonColor: '#fac843',
						iconColor: '#fff'
					},
					
					horizontal: 'right',
					vertical: 'bottom',
					direction: 'vertical',
					content: [{
						iconPath: 'iconfont icon-fabiao icon-active',
						// selectedIconPath: '/static/image-active.png',
						text: '发布',
						active: false
					},
					{
						iconPath: 'iconfont icon-icon- icon-active',
						// selectedIconPath: '/static/home-active.png',
						text: '顶部',
						active: false
					},
					{
						iconPath: 'iconfont icon-shuaxin icon-active',
						// selectedIconPath: '/static/star-active.png',
						text: '刷新',
						active: false
					}
				]
			}
		},
		components:{
			myChat,
			bottomInput,
			myAds
			
		},
		created() {
			
		},
		onLoad() {
			
			this.getAdsList().then(()=>{
				this.getTypeList()
				this.getTalkList()
			})
			 
		},
		//下拉刷新
		onPullDownRefresh() {
			this.queryParams.content= '';
			this.queryParams.talkId = '';
			this.talkList = [];
			this.getAdsList().then(res=>{
				this.getTypeList()
				this.getTalkList().then(()=>{uni.stopPullDownRefresh()})
			})
			
		},
		computed:{
			...mapGetters('user',['hasPermi'])
		},
		methods: {
			async getAdsList(){
				const res = await getAdsList()
				this.info = res.data
			},
			removeTalk(talkId){
				
				 this.talkList = this.talkList.filter(item=>{return item.id != talkId})
				
			},
			async getTypeList(){
				const res = await getTypeList()
				this.typeList = res.data;
				
			},
			
			change(e){
				
				this.current = e.detail.current;
				
			},
			 pullTop() {
					uni.pageScrollTo({
						scrollTop: 0,//滚动到距离顶部为0
						duration: 500//滚动时长
					})
			},
			gotoAddTalk(){
				uni.navigateTo({
					url:`/pages/addTalk/addTalk`
				})
			},
			//分页
			async handleCurrentChange(){
				if(this.status!="more"){
					return ;
				}
				if(this.talkList.length>0)
					this.queryParams.talkId = this.talkList[this.talkList.length-1].id
					
				await this.getTalkList();
			},
			/**
			 * 拿话题列表
			 */
			async getTalkList(){
				
				this.status = "loading"
				const res = await getTalkPage(this.queryParams);
				//没有数据了
				if(res.data==null||res.data.length==0){
					this.status = "nomore"
					return;
				}
				this.talkList.push(...res.data);	
				this.status = "more"
					
			},		
			/**
			 * 悬浮按钮项点击事件
			 * @param {Object} e
			 */
			trigger(e) {
				this.$refs.fab.close();
				if(e.index==0){
					this.gotoAddTalk();
				} else if(e.index==1){
					this.pullTop();
				} else{
					// this.pullTop();
					uni.startPullDownRefresh();
				}
				// this.content[e.index].active = !e.item.active
			
				
			},
			/**
			 * 悬浮按钮点击事件
			 */
			fabClick() {
				// uni.showToast({
				// 	title: '点击了悬浮按钮',
				// 	icon: 'none'
				// })
			}
		},
		watch:{
			narBar(newval,oldval){
				this.talkList = []			
				this.queryParams.talkId = ''
				this.queryParams.typeId = newval
				this.getTalkList()
			},
			search(newval,oldval){
				this.talkList = []
				this.queryParams.talkId = ''
				this.queryParams.content = newval
				this.getTalkList()
			}
		},
		
		//触底
		onReachBottom(){
			 this.handleCurrentChange();
		}
	}
</script>

<style>

.index > .search{
	padding: 10px 30rpx;
	background-color: white;
	
	
}
.index >.search .is-input-border{
	border-radius: 50px !important;
}
.index > .ads{
	margin-bottom: 20px;
	height: 160px;
}

.main-content > .narBar{
	display: flex;
	width: 100%;
	overflow: scroll;
	border-radius: 10px;
	background-color: #fac843;
	box-shadow: rgba(0, 0, 0, 0.05) 0px 0px 0px 1px, rgb(209, 213, 219) 0px 0px 0px 1px inset;
	margin-bottom: 10px;
	
}
.main-content > .narBar > .active{
	background-color: white;
	
	
}
.main-content > .narBar > .active >.iconfont{
	color: #fac843;
}
.main-content > .narBar > .item{
	margin: 19rpx;
	width: 100rpx;
	/* height: 100rpx; */
	font-weight: 600;
	display: flex;
	align-items: center;
	flex-direction: column;
	justify-content: center;
	border-radius: 50px;
	flex-grow: 0;
	flex-shrink: 0;
	

	
}
</style>
