<template>
	<view class="myload">
		<uni-load-more :status="status" :content-text="contentText" ></uni-load-more>
	</view>
</template>

<script>
	export default {
		name:"myLoad",
		data() {
			return {
				contentText:
				{
					contentdown: '查看更多',
					contentrefresh: '加载中',
					contentnomore: '没有更多'
				}
			};
		},
		props:{
			status:{
				type:String,
				default:"more"
			}
		},
		
	}
</script>
.
<style>

.myload{
	padding-bottom: 30px;
}
</style>