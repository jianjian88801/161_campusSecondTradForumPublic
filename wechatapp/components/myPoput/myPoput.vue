<template>
	<view class="my-poput">
		<uni-popup ref="popup" type="bottom">
			<view class="form">
				<view class="my-poput-title">
					<slot name="title"></slot>
				</view>
				<view class="my-poput-content">
					<slot name="content"></slot>
				</view>
				<view class="footer">
					<button class="btn ok"  @click="submitForm()">确定</button>
					<button class="btn ok"  @click="close()">取消</button>
				</view>
			</view>
			
		</uni-popup>
	</view>
</template>

<script>
	export default {
		name:"myPoput",
		data() {
			return {
				open(){
				    // 通过组件定义的ref调用uni-popup方法 ,如果传入参数 ，type 属性将失效 ，仅支持 ['top','left','bottom','right','center']
				    this.$refs.popup.open('center');
				},
				// submitForm(){
					
				// },
				close(){
					this.$refs.popup.close();
				},
				submitForm(status){
					this.$emit("submitForm",status);
					this.close();
				}
			};
		}
	}
</script>

<style>

.my-poput{
	border-radius: 8px;
	overflow: hidden;
}
.my-poput .footer{
	display: flex;
}
.my-poput .footer > .btn:first-child{
	margin-right: 10px;
}
.my-poput .form{
	background-color: white;
	padding: 15px;
}

 .form > .my-poput-content{
	padding: 15px 0;
}
.my-poput .ok{
	height: 20px;
	font-size: 14px;
}
</style>