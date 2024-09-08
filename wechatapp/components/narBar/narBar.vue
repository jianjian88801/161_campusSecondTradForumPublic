<template>
	<view class="top">
		<view class="left">
			<view @click="gotoNaBar(1)" class="item">
				<span>最新</span>
				<view v-show="type==1" class="active"></view>
			</view>
			<view @click="gotoNaBar(2)" class="item">
				<span>{{typeName}}</span>
				<view  v-show="type==2" class="active"></view>
			</view>
			<view @click="gotoNaBar(3)" class="item">
				<span>{{type2Name || "收藏"}}</span>
				<view  v-show="type==3" class="active"></view>
			</view>
		</view>
		<view class="right">
			<view class="search">
				<uni-easyinput maxlength="20" suffixIcon="search" v-model="search" placeholder="请输入关键字" @iconClick="iconClick" />
			</view>
		
		</view>
	</view>
</template>

<script>
	
	export default {
		name:"narBar",
		comments:{
			
		},
		data() {
			return {
				search:null
			};
		},
		props:{
			type:{
				type:Number,
				default:1
			},
			typeName:{
				type:String,
				default:"最热"
			},
			type2Name:{
				type:String,
				default:"已购"
			}
		},
		methods:{
			gotoNaBar(val){
				this.$emit('gotoNarBar',val);
			},
			iconClick(){
				
				this.$emit('serach',this.search)
			}
		},
		// watch:{
		// 	search(newVal){
		// 		this.find();
		// 	}
		// }
	}
</script>

<style>
	
.top{
	
	position: fixed;
	z-index: 999;
	top:-1px;
	box-sizing: border-box;
	width: 100vw;
	height: 100rpx;
	background-color: white;
	padding: 0 10px;
	display: flex;
	box-shadow: rgba(60, 64, 67, 0.3) 0px 1px 2px 0px, rgba(60, 64, 67, 0.15) 0px 1px 3px 1px;
}
.top > .left{
	flex: 1;
	display: flex;
	justify-content: space-around;
	align-items: center;
	font-family: 600;
}
.top >.right{
	width: 50%;
	display: flex;
	align-items: center;
}
.top >.right >.search{
	padding-left: 10px;
	
}


.active{
	border-style: solid;
	border-width: 0 0 4rpx 0;
	border-color: #f9c347;
	position: relative;
	border-radius: 5px;
	top: 4px;
	width: 90%;
	left: 5%;
	box-shadow: 0 0 1px 1px  #f9c347;
}
 
</style> 