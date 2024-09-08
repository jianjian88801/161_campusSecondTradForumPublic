<template>
	<view :class="[radius?'xhx radius':'xhx']" :style="{width: width +'px'}" >
		
	</view>
</template>

<script>
	export default {
		name:"myXhx",
		data() {
			return {
				
			};
		},
		props:{
			//是否圆角
			radius:{		
				type:Boolean,
				default:false
			},
			//宽度
			width:{
				type:Number,
				default:40
			}
		}
		
	}
</script>

<style>
.xhx{
	border-style: solid;
	border-width: 0 0 6rpx 0;
	border-color: #f9c347;
	box-shadow: 0 0 1px 1px  #f9c347;
	margin-top: 5px;
	border-radius: ;
}
.radius{
	border-radius: 5px;
}
</style>