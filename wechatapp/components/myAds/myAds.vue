<template>
	<view class="my-ads">
		<swiper class="swiper-box" @change="change" previous-margin="60rpx" next-margin="60rpx"  indicator-dots autoplay  circular >
			<swiper-item @click="gotoPath(item)"  v-for="(item,index) in info"  :class="[{'ads-active':index == current} ]" :key="index">
				<image  mode="scaleToFill" :src="setUrl(item.url)"></image>
			</swiper-item>
		</swiper>	
	</view>
</template>

<script>
	export default {
		name:"myAds",
		data() {
			return {
				len:0,
				current:0,
				per:0
			};
		},
		created() {
			this.len = this.info.length
			
		},
		methods:{
			gotoPath(item){
				// console.log("浏览量")
				if(item.path!=null&&item.path!="")
					uni.navigateTo({
						url:item.path
					})
			},
			change(e){
				// console.log(e)
				this.current = e.detail.current;
				
			},
			setUrl(url){
				return this.getUrl(url);
			}
		},
		props:{
			info:{
				type:Array,
				default:null
			}
		}
	}
</script>

<style>

.my-ads{
	width: 100%;
	height: 100%;
	
}
.swiper-box{
	height: 100%;
	
}
.swiper-box swiper-item{
	display: flex;
	align-items: center;
	justify-content: center;
	
}
.swiper-box swiper-item image{
	height: 90%;
	border-radius: 10px;
	width: 560rpx ;
	
}
.ads-active image{
	height: 97% !important;
	width: 610rpx !important;
	transition-property: height,width;
	transition-duration: 500ms,500ms;
	box-shadow: rgba(60, 64, 67, 0.3) 0px 1px 2px 0px, rgba(60, 64, 67, 0.15) 0px 1px 3px 1px;
}

.my-ads  swiper-item image{
	 
	
 }
/* 
.swiper-l image{
	position: relative;
	height: 90% !important;
	width: 90% !important;
	left: calc(5% + 45px);
	z-index: 999;
}
.swiper-r image{
	position: relative;
	height: 90% !important;
	width: 90% !important;
	left: calc(-5% - 45px);
	z-index: 999;
}
 */



</style>