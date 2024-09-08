import request from "@/utils/request"
// 我的服务器上传
import {uploadFile} from "@/utils/request.js"
// 七牛云oss上传
import {qiniuUpload} from "@/utils/upload.js"

/**
 *添加帖子
 */
export const addTalk = (params)=>request({url:"/client/talk/add",method:"post",data:params});

/**
 *上传图片
 */
export const uploadImage = (params,task)=>uploadFile({url:"/common/upload",...params},task);


export const commit = async function(images,params){
	try{
		//上传图片
		const pictrueList = await uploadImages(images);	
		params.pictrueList = pictrueList;
		// console.log(pictrueList)
		addTalk(params);
	}catch(e){
		//TODO handle the exception
		uni.showToast({
			icon:'error',
			title:"发布失败",
			
		});
	}
	
}
 
async function uploadImages(images) {
  const results = []
  for (const item of images) {
	// const params = {
	//   name: 'file',
	//   filePath: item.url
	// }
	// const res = await uploadImage(params)
	// results.push(res.url)
	const res = await qiniuUpload(item.url,item.name)
	results.push(res.imageURL);
  }
  return results
}