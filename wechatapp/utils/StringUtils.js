

/**
 * 判断字符串是否为空
 * @param {Object} str
 */
export const isEmpty  = function(str){
	
	return str==null||str.trim().length === 0;
}

/**
 * 判断字符串是否不为空
 * @param {Object} str
 */
export const isNoEmpty  = function(str){
	
	return !isEmpty(str)
}

export default {
  isEmpty,
  isNoEmpty
};