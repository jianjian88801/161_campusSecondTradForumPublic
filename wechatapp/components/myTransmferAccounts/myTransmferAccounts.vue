<template>
	<view style="width: 100%;">
		<view :class="dir==0?'left-info':'right-info'">
			<view class="zw"></view>
			<view v-show="dir==0" class="avatar" @click="gotoUserDetails()">
				<image class="av-border" :src="avatarUrl"></image>
			</view>
			
			<view  :class="type==0?'info':'src'">
			
			<!-- 右边 -->
			<image v-if="dir==1&&zz_type==0" mode="widthFix" src="../../static/images/transferAccounts/zz1.png"></image>
			<image v-if="dir==1&&zz_type==1" mode="widthFix" src="../../static/images/transferAccounts/ac-zz1.png"></image>
			<image v-if="dir==1&&zz_type==2" mode="widthFix" src="../../static/images/transferAccounts/ysky.png"></image>
			<image v-if="dir==0&&zz_type==0" mode="widthFix" src="../../static/images/transferAccounts/zz3.png"></image>		
			<image v-if="dir==0&&zz_type==1" mode="widthFix" src="../../static/images/transferAccounts/ac-zz2.png"></image>
			<image v-if="dir==0&&zz_type==2" mode="widthFix" src="../../static/images/transferAccounts/ysk.png"></image>
			
			<view class="money">
				<span>￥{{money}}</span>
			</view>
			
			</view>
			
			<view v-show="dir==1" class="avatar" @click="gotoUserDetails()">
				<image :src="avatarUrl"></image>
			</view>
			
		</view>	
	</view>
</template>

<script>
	export default {
		name:"myTransmferAccounts",
		data() {
			return {
				
				gotoUserDetails(){
					uni.navigateTo({
						url:`/pages/userDetail/userDetail?userId=${this.userId}`
					})
				}
			};
		},
	
		methods:{
			cType(type){
				if(type==0)
					return 'info';
				else if(type==1)
					return 'src';
			}
		},
		props:{
			// 对话框方向  0表示左 1边是右边
			userId:{
				type: String,
				default:""
			},
			dir: {
			  type: Number,
			  default:0
			},
			// 用户头像
			avatarUrl: {
			  type: String,
			  default:require("../../static/logo.png")
			},
			// 用户id
			id: {
			  type: String,
			  default:undefined
			},
			
			//发送类型  0:正常转账
			type:{
				type: Number,
				default:0
			},
			//转账状态 0未接 1已接受 2被接受
			zz_type:{
				type: Number,
				default:0
			},
			money:{
				type:String,
				default:"0.01"
			}
		}
		
	}
</script>

<style>

/* 文字和布局开始 */
.right-info{
	
	
	display: flex;
	justify-content: right;
	margin-bottom: 10px;
	
}
.right-info>.zw{
	flex:1;
}
/* 文本类型 */
.right-info>.info{	
	
	position: relative;
	width: 225px;
	height: 75px;
	
	word-wrap:break-word; 
	word-break:break-all;
	position: relative;
	/* min-height: 40px; */
	display: flex;
	align-items: center;
	margin: 0 5px;
	/* background-color: #f99f3e; */
	/* line-height: 40px; */
	border-radius: 5px;
	/* padding: 8px 12px; */
}

.right-info>.info>.money{
	position: absolute;
	font-size: 14px;
	left: 51px;
	top: 10.5px;
	color: #fffffd;
	font-weight: 500;
}
.right-info>.info>image{
	width: 100%;
	
}
.right-info>.avatar{
	width: 30px;
	height: 30px;
	border-radius: 5px;
}



.left-info{
	display: flex;
	justify-content: left;
	margin-bottom: 10px;
	
}
.left-info>.info{
	max-width: 230px;
	word-wrap:break-word; 
	word-break:break-all;
	position: relative;
	/* min-height: 40px; */
	display: flex;
	align-items: center;
	margin: 0 10px;
	/* background-color: #f99f3e; */
	/* line-height: 40px; */
	border-radius: 5px;
	/* padding: 8px 12px; */
}

.left-info>.info>.money{
	position: absolute;
	font-size: 14px;
	left: 57px;
	top: 12px;
	color: #fffffd;
	font-weight: 500;
}

/* 文字和布局结束 */

/* 头像开始 */

.avatar{
	width: 30px;
	height: 30px;
	border-radius: 5px;
	
}
.avatar>image{
	
	width: 30px;
	height: 30px;
	border-radius: 5px;
}

/* 头像结束 */

/* 三角形 */
.right-sjx{
	
	position: absolute;
	right: -5px;
	top: 12px;
	width: 0;
	height: 0;
	border-top: 8px solid transparent;
	border-bottom: 8px solid transparent;
	border-left: 10px solid #f99f3e;
	
	
}

.left-sjx{
	position: absolute;
	left: -5px;
	top: 12px;
	width: 0;
	height: 0;
	border-top: 8px solid transparent;
	border-bottom: 8px solid transparent;
	border-right: 10px solid #f99f3e;
	
}

/* 三角形结束 */

</style>