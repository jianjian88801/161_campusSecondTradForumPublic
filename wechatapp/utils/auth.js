
export const TOKEN = "Authorization"


/**
 * 获取token
 */
export const getToken = function(){
	return uni.getStorageSync(TOKEN)
}



/**
 * 设置torken
 * @param {Object} token 
 */
export const setToken = function(token){
	uni.setStorageSync(TOKEN,token)
}
/**
 * 清除token
 */
export const clearToken = function(){
	uni.clearStorageSync(TOKEN)
}
/**
 * 是否有token
 */
export const isToken = function(){
	return getToken()!=null&&getToken()!="";
}
export default{
	
}