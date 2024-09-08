import request from "@/utils/request"

import {uploadFile} from "@/utils/request.js"


/**
 *上传图片
 */
export const uploadImage = (params,task)=> uploadFile({url:"/common/upload",name:params.name||file,files:params.files,filePath:params.filePath},task);


/**
 * 更新,修改话题
 */
export const saveOrUpdate = (params)=>request({url:"/client/topic/add",method:"post",data:params});
