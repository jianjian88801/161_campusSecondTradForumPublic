import request from "@/utils/request"


/**
 *添加评论
 */
export const addComment = (params)=>request({url:"/client/comment/add",method:"post",data:params});
