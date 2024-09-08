<template>
	<view style="padding: 1px;">
		<view class="list-item">
			<view class="left">
				<slot name="left">
					<span>{{text}}</span>
				</slot>
			</view>
			<view class="right">
				<view class="right-content">
					<slot name="right"></slot>
				</view>
				<uni-icons v-if="showRight" type="right" size="16"></uni-icons>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name:"listItem",
		data() {
			return {
				
			};
		},
		props:{
			text:{
				Type:String,
				default:""
			},
			showRight:{
				Type:Boolean,
				default:false
			}
		}
	}
</script>

<style>
.list-item{
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 10px;
	background-color: white; 
	/* 下划细线 */
	box-shadow: rgba(27, 31, 35, 0.04) 0px 1px 0px, rgba(255, 255, 255, 0.25) 0px 1px 0px inset;
	
}
.list-item > .left{
	display: flex;
	align-items: center;
	font-weight: 600;
}
.list-item > .right{
	display: flex;
	align-items: center;
}
.right-content{
	margin-right: 5px;
}
</style>