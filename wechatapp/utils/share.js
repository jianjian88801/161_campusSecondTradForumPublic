export default{
    data(){
        return {
            //设置默认的分享参数
            share:{
                title:'xh校友圈',
                path:'/pages/index/index',
              
                desc:'',
                content:''
            },

        }
    },
	//1. 分享到朋友
  onShareAppMessage(res) {
  if (res.from === 'button') {// 来自页面内分享按钮
        console.log(res.target)
      }
        return {
            title:this.share.title,
            path:this.share.path,
            imageUrl:this.share.imageUrl,
            desc:this.share.desc,
            content:this.share.content,
        }
    },
	
	//2.分享到朋友圈
	onShareTimeline(res) {
		return {
			title: this.share.title,
			path: this.share.path,
			desc: this.share.desc,
			imageUrl: this.share.imageUrl,
		}
	},

}