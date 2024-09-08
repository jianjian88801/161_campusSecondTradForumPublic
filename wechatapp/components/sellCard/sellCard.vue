<template>
	<view class="sellCard">
		
		<image v-if="sellItem.sellPictures!=null&&sellItem.sellPictures.length>0" mode="widthFix" :src="setUrl(sellItem.sellPictures[0].url)"></image>
		<view   v-else style="height: 20px;"></view>
		 <view class="sell-user-info">
			 <image class="av-border" :src="setUrl(sellItem.sysUser.avatar)"></image>
			 <span class="min-front">{{sellItem.sysUser.nickName}}</span>
		 </view>
		 <view class="sell-text mid-front">
		 	 <text>{{sellItem.content}}</text>
		 </view>
		 <view class="sell-footer">
			 <!-- <span   class="iconfont icon-pinglun mid-front"></span> -->
			 <span class="min-front" >{{getStatusLab}}</span>
			 <span style="margin: 0 5px;" class="min-front" >{{sellItem.date}}</span>
			
		 </view>
	</view>
</template>

<script>
	export default {
		name:"sellCard",
		data() {
			return {

			};
		},
		computed:{
			getStatusLab(){
				const status = this.sellItem.status;
				if(status == '1')
					return "未卖出";
				if(status == '2')
					return "待发货";
				if(status == '3')
					return "已发货";
				if(status == '4')
					return "交易完成";
				if(status == '5')
					return "已发货";
				return "未卖出"
			}
		},
		methods:{
			
			setUrl(url){
				// console.log(this.getUrl(url))
				return this.getUrl(url);
			},
		
		},
		props:{
			sellItem:{
				type:Object,
				default:{}
			},
			
		}
	}
</script>

<style>
.sellCard{
	
}
.sellCard > image{
	
	width: 100%;
	height: auto;
	
}

.sell-user-info{
	display: flex;
	align-items: center;
	padding: 0 5px;
	position: relative;
	top: -10px;
	
	
}
.sell-user-info > image{
	width: 20px;
	height: 20px;
	border-radius: 5px;
	margin-right: 3px;
	
}
.sell-text{
	padding: 0px 3px;
	display: -webkit-box;    
	-webkit-box-orient: vertical;    
	-webkit-line-clamp: 2;   
	overflow: hidden;
}
.sell-footer{
	display: flex;
	justify-content: flex-end;
	align-items: center;
	padding: 3px;
}
.sell-footer > .iconfont{
	margin-left: 3px;
}
</style>