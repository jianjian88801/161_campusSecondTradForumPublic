import request from "@/utils/request"
import {uploadFile} from "@/utils/request"
import {qiniuUpload} from "@/utils/upload.js"
/**
 *添加话题评论
 */
export const addSell = (params)=>request({url:"/client/sell/add",method:"post",data:params});

/**
 *上传图片
 */
export const uploadImage = (params,task)=>uploadFile({url:"/common/upload",...params},task);


/**
 * 提交闲置
 * @param {Object} images 图片临时路径列表
 * @param {Object} params 闲置参数
 */
export const commit = async function(images,params){
	try{
		//上传图片
		const pictrueList = await uploadImages(images);	
		params.sellPictures = pictrueList;
		addSell(params);
	}catch(e){
		//TODO handle the exception
		uni.showToast({
			icon:"error",
			title:"发布失败",
			
		});
	}
	
}
 
/**
 * 上传图片列表的接口
 * @param {Object} images 图片列表[{url,name}]
 */
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