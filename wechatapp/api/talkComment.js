import request from "@/utils/request";

/**
 *拿评论分页
 */

export const getTalkCommentPage = (params)=>request({url:"/client/talkComment/getPage",method:"get",data:params});



/**
 *添加评论
 */

export const addTalkComment = (params)=>request({url:"/client/talkComment/add",method:"post",data:params});



/**
 *删除评论
 */
export const deleteTalkComment = (params)=>request({url:"/client/talkComment/delete",method:"delete",data:params});

/**
 * 订阅
 */
export const subscribe = (params)=>request({url:"/client/talkComment/subscribe",method:"post",data:params});