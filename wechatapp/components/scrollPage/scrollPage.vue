<template>
	<view class="scroll-page">
		<slot></slot>
	</view>
</template>

<script>
	export default {
		name:"scrollPage",
		data() {
			return {
				handleCurrentChange(){
					if(this.pageNum*this.pageSize >= this.total){
						console.log("无数据")
						return ;
					}
					this.emit("pagination",{pageNum:this.pageNum,pageSize:this.pageSize});
				}
			};
		},
		props: {
		    total: {
		      required: true,
		      type: Number
		    },
		    pageNum: {
		      type: Number,
		      default: 1
		    },
		    pageSize: {
		      type: Number,
		      default: 5
		    }
		},
		//触底事件
		onReachBottom(){
			console.log("触底了")
			this.handleCurrentChange();
			
		}
	
	}
</script>

<style>

</style>