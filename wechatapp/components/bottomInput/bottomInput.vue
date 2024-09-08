<template>
	<view v-show="isOpenInput" class=" send-comment-box-active send-comment-box "  :style="[{ 'bottom':height + 'px'}]" >
			<view class="inpt">
				<input maxlength="800" v-model="value" ref="inpt" :focus="isOpenInput" :placeholder="placeholder"  cursor-spacing="15" @focus="crearMsg()" @blur="leaveInput" />
				<view @click="send()" class="send"><span>发送</span></view>
			</view>
	</view>
</template>

<script>
	import {mapState,mapGetters} from 'vuex'
	export default {
		name:"bottomInput",
		data() {
			return {
				height:0,
				value:"",
			};
		},
		props:{
			isOpenInput:{
				type:Boolean,
				default:false
			},
			placeholder:{
				type:String,
				default:"请输入评论"
			}
		},
		created(){
			console.log(this.isOpenInput)
		},
		methods:{
			//发送信息
			send(){
				//
				this.$emit('send',this.value);
			},
			//失去焦点触发隐藏输入框
			leaveInput(e){
				this.$emit('closeInput')
			},
			//获取焦点清除上一次的信息
			crearMsg(){
				this.value=""
			},
			//点击输入框触发
			getHeight(e){
				console.log(e,'我是获取输入法高度的')
				this.height = e.target.height;//获取输入法的高度，动态设置输入框距离顶部距离。
			}
		},
		computed:{
			// ...mapGetters(['isOpenInput'])
		},
		watch:{
			
		}
	}
</script>

<style>


/* 发送评论的表单 */
.send-comment-box{
	
	display: none;
	position: fixed;
	z-index: 999;
	bottom: 0;
	left: 0;
	width: 100%;
	background-color: white;
}

.inpt{
	display: flex;
	align-items: center;
	padding: 5px;
}
.inpt input{
	
	background-color: #efefef;
	border-radius: 40px;
	font-size: 10px;
	padding: 10rpx 20rpx;
	flex: 1;
}
.inpt > .send{
	height: 55rpx;
	width: 100rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	background-color: #faca42;
	border-radius: 50px;
	font-size: 12px;
	margin-left: 5px;
}
.send-comment-box-active{
	display: block;
}

</style>