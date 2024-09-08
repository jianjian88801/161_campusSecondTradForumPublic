import request from "@/utils/request";

/**
 *拿用户信息
 */
export const getUserInfoById = (params)=>request({url:"/client/user/getUserInfoById",method:"get",data:params});



/**
 *拿用户关注、粉丝信息
 */
export const getFollowInfoById = (params)=>request({url:"/client/user/getFollowInfo",method:"get",data:params});



/**
 *关注用户信息
 */
export const follow = (params)=>request({url:"/client/user/follow",method:"put",data:params});



/**
 *获取关注用户
 */
export const getFollowListByUserId = (params)=>request({url:"/client/user/getFollowList",method:"get",data:params});



/**
 *获取粉丝用户
 */
export const getFansListByUserId = (params)=>request({url:"/client/user/getFansList",method:"get",data:params});

