<template>
	<view class="warp">
		<view class="item-list card">
			<view @click="gotoGy()"  class="item">
				<list-item showRight >
					<template  slot="left">
						<view style="display: flex;align-items: center;">
							<image style="width: 20px;height: 20px;margin-right: 10px;" src="../../static/images/icon/comment(2).png"></image>
							<span>社区公约</span>
						</view>
					</template>
				</list-item>
			</view>
			<view @click="copy()" class="item">
				<list-item showRight >
					<template  slot="left">
						<view style="display: flex;align-items: center;">
							<image style="width: 20px;height: 20px;margin-right: 10px;" src="../../static/images/icon/star-active.png"></image>
							<span>关于我们</span>
						</view>
					</template>
					
				</list-item>
			</view>
		
		</view>

	</uni-popup>
		
	</view>
	
</template>


<script>
	import {listItem} from "@/components/listItem/listItem.vue"
	export default {
		data() {
			return {
				
			}
		},
		components:{
			listItem
		},
		methods: {
			gotoGy(){
				uni.navigateTo({
					url:'/pages/gy/gy'
				})
			},
			copy(msg){
				
				  uni.showModal({
					title:"关于我们",
					content:"联系方式xxxx",//模板中提示的内容
					confirmText:'复制内容',
					success:()=>{//点击复制内容的后调函数
					  //uni.setClipboardData方法就是讲内容复制到粘贴板
					  uni.setClipboardData({
						data:msg,//要被复制的内容
						success:()=>{//复制成功的回调函数
						  uni.showToast({//提示
							title:'复制成功'
						  })
						}
					  });
					}
				  });
			
			},
		}
	}
</script>

<style>
.more{
	margin: 20px 15px;
}

.item-list{
	margin-top: 10px;
	background-color: white;
	padding: 5px;
	

}
.item-list >.item{
	margin: 10px 0;
}

.poput{
	width: 70%;
	margin: 0 auto;
}
</style>
