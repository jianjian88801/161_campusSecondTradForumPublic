<template>
	<view>
		<view>{{ itemName }}</view>
		<view class="active"></view>
	</view>
</template>

<script>
	export default {
		name:"narItem",
		data() {
			return {
				
			};
		},
		props: {
			itemName:{
				type:String,
				default:""
			}
		}
	}
</script>

<style>

</style>