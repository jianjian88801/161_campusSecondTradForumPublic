import request from "@/utils/request";

import {uploadFile} from "@/utils/request.js"
/**
 *登录
 */
export const wxLogin = (params)=>request({url:"/wxLogin",method:"get",data:params});


/**
 *登录
 */
export const wxLogout = (params)=>request({url:"/logout",method:"get",data:params});


/**
 * 拿用户信息
 */
export const getInfo = (params)=>request({url:"/getInfo",method:"get",data:params});

/**
 *上传图片
 */
export const uploadImage = (params,task)=>uploadFile({url:"/common/upload",...params},task);

/**
 *更新资料
 */
export const updateInfo = (params)=> request({url:"/client/user/updateInfo",method:"put",data:params});


/**
 *更新背景
 */
export const updateBackground = (params)=> request({url:"/client/user/updateBackground",method:"put",data:params});

// export const updateNickName = (params)=> request({url:"/client/user/updateNickName",method:"post",data:params});

// export const updatePhonenumber = (params)=> request({url:"/client/user/updatePhonenumber",method:"post",data:params});

// export const updateSex = (params)=> request({url:"/client/user/updateSex",method:"post",data:params});