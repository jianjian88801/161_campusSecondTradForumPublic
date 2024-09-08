/**
 * xlf
 */
import errorCode from "@/utils/errorCode";
import {getToken,clearToken,isToken,TOKEN} from "@/utils/auth.js"
import { isExternal,showToast } from "./validate";
import stare from "@/store/index.js"
export const BaseUrl = 'http://localhost:8088'  //设置基本的url路径
export const getUrl = function(url){
	if(url==null||url=="")
		return "";
	if(isExternal(url))
		return url;
	return BaseUrl + url;
}
let isGoToLogin = {judge:false}
export default ({ //对页面穿过来的参数进行解构
	url,
	method,
	data,
	headers
}) => {
	// 请求前
	let header = headers||{	'Content-Type':'application/json;charset=utf-8'};
	
	
	if(isToken())
	{  
		//设置token
		const token = getToken();
		header={...header,"Authorization":token}
	}
	// console.log(header)
	// uni.showLoading({
	// 	title: "加载中"
	// });

	//发起请求
	return new Promise(function(resolve, reject) {
	
		uni.request({
			url: BaseUrl + url,  // 开发者服务器接口地址
			method: method,   
			timeout: 60000, //请求超时时间
			dataType: "json", 
			responseType: 'text',
			data: data,  //请求的参数
			withCredentials:true,
			header: header,
			// 成功时回调
			success(res) {//对请求请求到的信息进行处理
				
				// 未设置状态码则默认成功状态
				const code = res.data.code || 200;
				// 获取错误信息
				const msg =  res.data.msg || errorCode[code] || errorCode['default'];
				//统一异常处理 能处理即处理，之后把异常抛出
				switch (code) {
				    //认证失败，没有登录，也可能token过期，直接踢出去登录
				    case 401:
						goToLogin()				
				        break;
					
				    //业务逻辑错误
				    case 500:
					showToast({title:msg})
				        break;
					//成功的回调
				    case 200:
						resolve(res.data);
						break;
				    //具体的业务错误，抛出去给业务调用者处理即可
				    default:
					showToast({title:msg})
				}
				
				//错误回调
				reject(res)
			
			},
			//失败回调
			fail(err) {
				reject(err)
			}
		})
	})
}

/**
 * 是否已经在登录页了
 */
function goToLogin(){
	
	//是否已经在登录页了
	if(isGoToLogin.judge==false){

		//double验证
		if(judgeLogin()){
			isGoToLogin.judge = true
			stare.dispatch("user/logout")
			uni.navigateTo({
				url:"/pages/login/login"
			,complete() {
				 isGoToLogin.judge = false
			}})
		}		
	}		
}
function judgeLogin(){
	// 获取当前页面路径
	const currentPages = getCurrentPages();
	const currentPage = currentPages[currentPages.length - 1];
	const currentPagePath = currentPage.route; // 当前页面路径
	// console.log(currentPagePath)
	if(currentPagePath =='pages/login/login')
		return false;
	return true;
}
/**
 * options:上传参数
 * task:可监听上传进度变化事件，以及取消上传任务。
 */
export const uploadFile = (options,task) => {
	
	// console.log("wll")
    let header = options.header || {};
	
	
	if(isToken())
	{  
		//设置token
		const token = getToken();
		header={...header,Authorization:token,'content-type': 'multipart/form-data'}
	}
	
	return new Promise((resolve, reject) => {
		/**
		 * https://uniapp.dcloud.net.cn/api/request/network-file.html#uploadfile
		 * files:可上传多个文件
		 * filePath:文件地址，files和filePath选其一
		 * name:后端接口参数名
		 */
	
	 const uploadTask = uni.uploadFile({
			url: BaseUrl + options.url,
			files: options.files,
			filePath: options.filePath,
			name: options.name||"file",
			formData: options.formData||{},
			header: header,
			
			// 成功时回调
			success(res) {//对请求请求到的信息进行处理
				// console.log(res)
				res.data = JSON.parse(res.data)
				// 未设置状态码则默认成功状态
				const code = res.data.code || 200;
				// 获取错误信息
				const msg =  res.data.msg || errorCode[code] || errorCode['default'];
				//统一异常处理 能处理即处理，之后把异常抛出
				switch (code) {
				    //认证失败，可能token过期，直接踢出去登录
				    case 401:
					uni.showToast({
									title: "登录生效，请重新登录",
									icon:'none',
									duration:850
								});
				        //把本地的token清了,然后跳登录页
				        // window.sessionStorage.clear()
						goToLogin()		
				        break;
				    //业务逻辑错误
				    case 500:
				    uni.showToast({
							title: msg,
							icon:'none',
							duration:850
						});
				        break;
					//成功的回调
				    case 200:
						resolve(res.data);
						break;
				    //具体的业务错误，抛出去给业务调用者处理即可
				    default:
				       uni.showToast({
							title: msg,
							icon:'none',
							duration:850
						});
				}			
				//错误回调
				reject(res)
			
			},
			//失败回调
			fail(err) {
				reject(err)
			}
		})
		
		//通过 uploadTask，可监听上传进度变化事件，以及取消上传任务。
		if(task!=undefined&&task!=null){
			task(uploadTask);
		}
	}
	
	)
}



