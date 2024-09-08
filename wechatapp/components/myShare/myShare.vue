<template>
	<view class="my-share">
		<uni-popup ref="popup">
			<view class="share-box">
				<view class="share-title">
					<span>分享</span>
				</view>
				<view class="share-list">
					
					<button class="item" open-type="share">
						<image src="../../static/images/icon/wechat.png"></image>
						<span>分享给好友</span>
					</button>
				<!-- 	<button class="item" open-type="share">
						<image src="../../static/images/icon/朋友圈_friends-circle.png"></image>
						<span>分享到朋友圈</span>
					</button> -->
					
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
	export default {
		name:"myShare",
		data() {
			return {
				open(){
				    // 通过组件定义的ref调用uni-popup方法 ,如果传入参数 ，type 属性将失效 ，仅支持 ['top','left','bottom','right','center']
				    this.$refs.popup.open('bottom');
				},
				close(){
					this.$refs.popup.close();
				},
				submitForm(){
					this.$emit("submitForm");
					this.close();
				}
				
			};
		}
	}
</script>

<style>
.share-box{
	padding: 20rpx 25rpx;
	background-color: white;
}
.share-box > .share-title{
	display: flex;
	justify-content: center;
	font-size: 14px;
	font-weight: 600;
}
.share-list{
	display: flex;
	align-items: center;
	justify-content: flex-start;
	margin-top: 15px;
}
.share-list > .item{
	
	width: 175rpx;
	height: 175rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	font-size: 12px;
}
.share-list > .item image{
	width: 50px;
	height: 50px;
	margin-bottom: 8px;
	border-radius: 50%;
}




 
</style>