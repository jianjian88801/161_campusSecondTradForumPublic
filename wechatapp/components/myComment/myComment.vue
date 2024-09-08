 <template>
	<view  @click="openInput()" class="info mid-front">
		<view class="top">
			<image  class="box-size av-border lf" :src="setUrl(item.user)"></image>
			<span class="weight_600 user_name lf" >{{item.user.nickName}}</span>
			<sapn v-if="item.parentId!=null" class="hf info-text lf ">回复</sapn>
			<image v-if="item.parentId!=null" class="box-size av-border lf" :src="setUrl(item.userBy)"></image>
			<span v-if="item.parentId!=null"  class="weight_600 user_name lf" >{{item.userBy.nickName}}</span>
			<sapn class="info-text lf">: {{item.content}}</sapn>
		</view>
		
	</view>
</template>

<script>
	export default {
		name:"myComment",
		data() {
			return {
				
			};
		},
		props:{
			item:{
				type:Object,
				default:null
			}
		},
		methods:{
			openInput(){
				this.$emit('openInput',this.item.id);
			},
			setUrl(item){
				if(item==null)
					return ""
				return this.getUrl(item.avatar);
			}
		}
	}
</script>

<style>

.info-text{
	line-height: 44rpx;
	margin-left: 2px;
}

.info{
	display: flex;
	flex-direction: column;
}
.user_name{
	margin-left: 3px;
	line-height: 44rpx;
}

.box-size{
	width: 40rpx;
	height: 40rpx;
	margin-right: 1px;
	border-radius: 50% !important;
	
	
}
.hf{
	margin-right: 2px;
}

.info > .bottom
{
	margin-top: 5px;
}
.simage{
	width: 120rpx;
	height: 120rpx;
	margin-right: 1px;
	border-radius: 5px !important;
	
}
</style>