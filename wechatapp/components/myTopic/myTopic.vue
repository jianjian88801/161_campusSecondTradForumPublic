<template>
	<view class="day-active card">
		<view class="head"><span>{{topicItem.title||""}}</span></view>
		<view class="image-show">
			<image mode="aspectFill" class="active-img" :src="setUrl(topicItem.picture)"></image>
		</view>
		<view class="active-warp">
			<view class="text-show dmid-front-14">
				<text>{{topicItem.introduction||""}}</text>
			</view>
			<view v-if="showFooter" class="foot-show l_r">
				<view class="left">
					<view class="item">
						<span class="iconfont icon-liulan min-front"></span>
						<span class="item-text min-front ">{{topicItem.view}}</span>
					</view> 
				</view>
				<view class="right">
					<view class="item">
						<span class="iconfont icon-dianzan1 min-front" ></span>
						<span class="item-text min-front" >{{topicItem.thumbsNum}}</span>
					</view>
					<view class="item">
						<span class="iconfont icon-pinglun min-front"></span>
						<span class="item-text min-front" >{{topicItem.discussNum}}</span>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {getUrl} from '@/utils/request.js'
	export default {
		name:"myTopic",
		data() {
			return {
				setUrl(url){
					return this.getUrl(url)
				}
			};
		},
		props:{
			showFooter:{
				type:Boolean,
				default:true
			},
			topicItem:{
				type:Object,
				default:{}
			}
		},
		methods:{
			
		}
	}
</script>

<style>
	/* 布局 */
.day-active{
	width: 100%;
	
	min-height: 100px;
}
.day-active{
	z-index: 1;
	position: relative;
}
/*头部 */
.day-active > .head{
	width: 33%;
	height: 60rpx;
	background-color: #fed23e;
	position: absolute;
	left: 50%;
	z-index: 0;
	transform: translate(-50%,-50%);
	display: flex;
	align-items: center;
	justify-content: center;
	border-radius: 5px;
	overflow:hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  -o-text-overflow:ellipsis;
	
}
/* 图片 */
.image-show image{
	width: 100%;
	height: 300rpx;
	border-radius:  10px 10px 0 0;
}
.active-warp{
	margin: 0 20rpx;
}
.text-show{
	padding: 20px 0;
}

.foot-show{
	padding-bottom: 10px;
}
.foot-show .item {

	display: flex;
	align-items: center;
	margin-left: 1px;
}

</style>