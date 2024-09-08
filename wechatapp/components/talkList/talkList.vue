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
	import {getTalkPage} from "@/api/talk.js"
	export default {
		data() {
			return {
				talkList:[],
				status:"more",
				// 查询参数
				queryParams: {
					talkId:"",
					typeId: "",
					userId:"",
					content:""
				},
				//总条数
				total:0		
			}
		},
		props:{
			typeId:{		
				type:String,
				default:""
			},
			userId:{
				type:String,
				default:""
			},
			content:{
				type:String,
				default:""
			}
		},
		//created可能在props执行导致取不到值，正确的做法应该保证props的参数取到值后在加载这个组件
		created() {	
		  this.queryParams.typeId = this.typeId
		  this.queryParams.userId = this.userId
		  this.queryParams.content = this.content
		  this.getTalkList();
		},
		//使用监听器的话如果有多个属性可能会执行getTalkList多次
		watch:{
			// userId(newVal){
			// 	this.getTalkList();
			// },
			// typeId(newVal){
			// 	this.getTalkList();
			// },
			// name(newVal){
			// 	this.getTalkList();
			// }
		},
		
		methods: {
			//刷新
			reFresh(){
				this.talkList = [];
				this.queryParams.talkId = ''
				this.getTalkList()
			},
			//删除
			removeTalk(talkId){			
				 this.talkList = this.talkList.filter(item=>{return item.id != talkId})			
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
				this.status = "more"
				this.talkList.push(...res.data);							
					
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
