import request from "@/utils/request";

/**
 *拿话题list
 */

export const getTopicList = (params)=>request({url:"/client/topic/getTopicList",method:"get",data:params});

/**
 * 通过id拿话题
 */
export const getTopicById = (params)=>request({url:"/client/topic/getTopicById",method:"get",data:params});


/**
 *获取轮播图
 */
export const getAdsList = (params)=>request({url:"/client/ads/list",method:"get",data:params});

