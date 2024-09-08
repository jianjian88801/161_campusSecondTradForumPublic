import request from "@/utils/request"

import {uploadFile} from "@/utils/request.js"
/**
 *添加话题评论
 */
export const addDiscuss = (params)=>request({url:"/client/discuss/add",method:"post",data:params});

/**
 *上传图片
 */
export const uploadImage = (params,task)=>uploadFile({url:"/common/upload",...params},task);
