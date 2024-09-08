import request from "@/utils/request";
import qiniuUploader from "@/utils/qiniuUploader"
import {uploadImage,saveOrUpdate} from "@/api/addTopic.js"
const config = {
   useCdnDomain: false, // 是否使用CDN加速域名
   region: 'z0', // 存储区域
   disableStatisticsReport: false // 禁止自动发送上传统计信息到七牛，默认允许发送
};

/**
 * @param {Object} filePath 文件路径
 * @param {Object} name 文件名
 * // 参考http://developer.qiniu.com/docs/v6/api/overview/up/response/simple-response.html
 */
export const qiniuUpload = function(filePath,name){
	
	return new Promise(function(resolve, reject) {
		
		// //获取token
		// request({url:"/common/getUploadToken",method:"get"}).then(res=>{

		// 	const token = res.data;
		// 	  // 交给七牛上传
		// 	qiniuUploader.upload(filePath, (res2) => {
		// 	 resolve(res2)
		// 	}, (error) => {
		// 	 console.log(error)
		// 	  reject(error)
		// 	},
		// 	{
		// 	  uploadURL: 'https://up-z2.qiniup.com',
		// 	  domain: 'xxxx', // // bucket 域名，下载资源时用到。如果设置，会在 success callback 的 res 参数加上可以直接使用的 ImageURL 字段。否则需要自己拼接
		// 	  key: name || undefined, // 自定义文件 key。如果不设置，默认为使用微信小程序 API 的临时文件名
		// 	  // 以下方法三选一即可，优先级为：uptoken > uptokenURL > uptokenFunc
		// 	  uptoken: token, // 由其他程序生成七牛 uptoken
		// 	  // uptokenURL: 'UpTokenURL.com/uptoken', // 从指定 url 通过 HTTP GET 获取 uptoken，返回的格式必须是 json 且包含 uptoken 字段，例如： {"uptoken": "0MLvWPnyy..."}
		// 	  // uptokenFunc: function() {return 'zxxxzaqdf';}
		// 	});
								
		// }).catch(err=>{
		// 	reject(err)
		// })
		const param = {
			name : "file",
			filePath:filePath
		}
		uploadImage(param,(task)=>{
			
		}).then((res)=>{
			resolve({imageURL : res.url});
		}).catch(err=>{
			reject(err)
		})	
	})
}


/**
 * 上传多张图片，返回图片url列表
 * @param {Object} images:[ {url,name},...]
 */
export const uploadImages = async function(images) {
		
	 if(images==null||images==undefined||images.length==0)
			return []
	  const results = []
	  //token有效时间：3600s
	  const rep = await request({url:"/common/getUploadToken",method:"get"})
	  const token = rep.data
	  for (const item of images) {
		const res = await qiniuUpload2(token,item.url,item.name)
		results.push(res.imageURL);
	  }
	  return results
}

/**
 * 上传多张图片，返回原格式列表
 * @param {Object} images:[ {url,name},...]
 */
export const uploadImages2 = async function(images) {
		
	 if(images==null||images==undefined||images.length==0)
			return []
	  //token有效时间：3600s
	 //  const rep = await request({url:"/common/getUploadToken",method:"get"})
	 //  const token = rep.data
	 //  for (const item of images) {
		// const res = await qiniuUpload2(token,item.url,item.name)
		// item.url = res.imageURL
	 //  }
	 for (const item of images) {
	 		const res = await qiniuUpload2("",item.url,item.name)
	 		item.url = res.imageURL
	 }
	  return images
}


 const qiniuUpload2 = function(token,filePath,name){
	 
	return new Promise(function(resolve, reject) {
		
		  // 交给七牛上传
		// qiniuUploader.upload(filePath, (res2) => {
		//  resolve(res2)
		// }, (error) => {
		//  console.log(error)
		//   reject(error)
		// },
		// {
		//   uploadURL: 'https://up-z2.qiniup.com',
		//   domain: 'xxxxx', // // bucket 域名，下载资源时用到。如果设置，会在 success callback 的 res 参数加上可以直接使用的 ImageURL 字段。否则需要自己拼接
		//   key: name || undefined, // 自定义文件 key。如果不设置，默认为使用微信小程序 API 的临时文件名
		//   uptoken: token, // 由其他程序生成七牛 uptoken
		// });		
		const param = {
			name : "file",
			filePath:filePath
		}
		uploadImage(param,(task)=>{
		}).then((res)=>{
			resolve({imageURL : res.url});
		}).catch(err=>{
			reject(err)
		})				
						
	})
}