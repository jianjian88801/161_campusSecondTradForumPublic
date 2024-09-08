import request from "@/utils/request";

/**
 *拿talk分页
 */

export const getTalkPage = (params)=>request({url:"/client/talk/getPage",method:"get",data:params});


/**
 *拿关注的人的talk分页
 */

export const getFollowTalk = (params)=>request({url:"/client/talk/getFollowTalk",method:"get",data:params});


/**
 *拿自己收藏的talk分页
 */

export const getCollectionTalk = (params)=>request({url:"/client/talk/getCollectionTalk",method:"get",data:params});


export const getTalkById = (params)=>request({url:"/client/talk/getTalkById",method:"get",data:params});


/**
 * 点赞
 */
export const talkThumbs = (params)=>request({url:"/client/talk/thumbs",method:"put",data:params});

/**
 * 获取分类列表
 */
export const getTypeList = (params)=>request({url:"/client/type/getList",method:"get",data:params});

/**
 * 收藏
 */
export const collectionTalk = (params)=>request({url:"/client/talk/collection",method:"put",data:params});

/**
 * 删除
 */
export const deleteTalkById = (params)=>request({url:"/client/talk/delete",method:"delete",data:params});


