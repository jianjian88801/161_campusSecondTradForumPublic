//  /**
//  * hasPermi 操作权限处理
//  */
 
// import store from '@/store'

// import { mapGetters } from 'vuex'

 
//   // ...
// 	/**
// 	 * @param {Object} value 权限列表
// 	 */

// default export hasPermi(value) {
			
// 		const all_permission = "*:*:*";
// 		const permission = ["11",'22']
// 		console.log(permissions)
// 		if (value && value instanceof Array && value.length > 0) {
// 		  const permissionFlag = value
	
			
// 			let ok = 0;
// 		  permissions.forEach(permission => {
// 			 if(all_permission === permission || permissionFlag.includes(permission))
// 				ok=1;
// 		  })
		  
// 		  if (!ok) {
// 			return false;
// 		  }
// 		  return true;
// 		} else {
// 		  throw new Error(`请设置操作权限标签值`)
// 		}
			
// }

