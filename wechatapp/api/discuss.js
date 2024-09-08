import request from "@/utils/request";

/**
 *拿评论list
 */
export const getDisCussListByTopicId = (params)=>request({url:"/client/discuss/getDisCussListByTopicId",method:"get",data:params});


/**
 *点赞
 */
export const thumbs = (params)=>request({url:"/client/topic/thumbs",method:"put",data:params});



/**
 *删除话题
 */
export const deleteTopicById = (params)=>request({url:"/client/topic/delete",method:"delete",data:params});


/**
 *删除讨论
 */
export const deleteDiscussById = (params)=>request({url:"/client/discuss/delete",method:"get",data:params});

/**
 * 收藏
 */
export const collectionTopicById = (params)=>request({url:"/client/topic/collection",method:"put",data:params});



/**
 * 点赞
 */
export const thumbsDiscuss = (params)=>request({url:"/client/discuss/thumbs",method:"put",data:params});

