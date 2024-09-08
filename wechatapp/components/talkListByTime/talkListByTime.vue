<template>
	<view class="talk-list">
		<view >
			<view class="main-content">
				<view v-for="(item,index) in talkList" style="margin-bottom: 10px;" :key="index">
					<myChat  @remove="removeTalk()" :c_item="item"></myChat>
				</view>
			</view>
		</view>
		
		
		<myLoad :status="status"></myLoad>
	</view>
</template>


<script>
	import {getFollowTalk,getCollectionTalk} from "@/api/talk.js"
	export default {
		name:"talkListByTime",
		data() {
			return {
				talkList:[],
				status:"more",
				// 查询参数
				queryParams: {
					time:11682503740290,
					offSet:0,
					pageSize: 4,
				},
				//总条数
				total:0		
			}
		},
		created() {
		  this.getTalkList();
		},
		//1：获取关注的人的帖子 //2：获取收藏的人的帖子
		props:{
			type:{
				type:Number,
				default:1
			},
		},
		
		methods: {
			//刷新
			async reFresh(){
				this.queryParams.pageNum = 4;
				this.queryParams.offSet = 0;
				this.queryParams.time = 11682503740290;
				this.talkList = [];
				await  this.getTalkList()
			},
			//删除
			removeTalk(talkId){
				
				 this.talkList = this.talkList.filter(item=>{return item.id != talkId})
				
			},
			//分页
			 handleCurrentChange(){
				if(this.status =="more"){
					this.getTalkList();
				}
			},
			/**
			 * 拿话题列表
			 */
			async getTalkList(){
			
				this.status = "loading"
				
				let res = []
				if(this.type==1)
					res = await getFollowTalk(this.queryParams);
				else
					res = await getCollectionTalk(this.queryParams);
				
				this.talkList = [...this.talkList,...res.data.rows];
				this.total =  res.data.total
				this.queryParams.offSet = res.data.offSet
				this.queryParams.time = res.data.time
				if(res.data.rows==null||res.data.rows.length==0||res.data.rows.length<this.queryParams.pageSize){
					this.status = "nomore"
				}
				else{
					this.status = "more"
				}
				
					
			}
			
		}
	}
</script>

<style>
	/* 为了触发下拉底部刷新建议把父容器的h设置到 101vh;或者把page的h设置成101vh */
.talk-list{
	padding: 10px 0;
	/* height: 101vh; */
}
</style>
