import request from "@/utils/request";




export const getSellList = (params)=>request({url:"/client/sell/getPage",method:"get",data:params});




export const getSellById = (params)=>request({url:"/client/sell/getSellById",method:"get",data:params});


export const saveOrUpdate = (params)=>request({url:"/client/sell/add",method:"post",data:params});


export const deleteSellById = (params)=>request({url:"/client/sell/delete",method:"delete",data:params});


export const updateSellById = (params)=>request({url:"/client/sell/update",method:"post",data:params});
