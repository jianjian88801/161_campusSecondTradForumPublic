import axios from "axios";
import NProgress from "nprogress";
import errorCode from "@/utils/errorCode";
import { Message, MessageBox } from "element-ui";
import { getToken, removeToken } from "@/utils/auth";
import store from "@/store";
import router from "@/router";
import { mapState } from "vuex";
import { tansParams } from "@/utils/ruoyi";
import { isExternal } from "@/utils/validate";
// import qs from 'qs';
const request = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // 注意！！ 这里是全局统一加上了 '/api' 前缀，也就是说所有接口都会加上'/api'前缀在，页面里面写接口的时候就不要加 '/api'了，否则会出现2个'/api'，类似 '/api/api/user'这样的报错，切记！！！
  timeout: 5000,
});

export const getUrl = function (url) {
  if (isExternal(url)) return url;
  else return process.env.VUE_APP_BASE_API + url;
};
// 是否显示重新登录
export let isRelogin = { show: false };

// request 拦截器
// 可以自请求发送前对请求做一些处理
// 比如统一加token，对请求参数统一加密
request.interceptors.request.use(
  (config) => {
    // 是否需要设置 token
    const isToken = (config.headers || {}).isToken === false;

    NProgress.start();
    config.headers["Content-Type"] = "application/json;charset=utf-8";
    //带token访问
    if (getToken() && !isToken) {
      config.headers["Authorization"] = "Bearer " + getToken(); // 让每个请求携带自定义token 请根据实际情况自行修改
    }

    // get请求映射params参数
    if (config.method === "get" && config.params) {
      //把对象全部解构拼接到url
      let url = config.url + "?" + tansParams(config.params);
      url = url.slice(0, -1);
      config.params = {};
      config.url = url;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
// 可以在接口响应后统一处理结果
// 响应拦截器
request.interceptors.response.use(
  (res) => {
    NProgress.done();
    // 二进制数据则直接返回
    if (
      res.request.responseType === "blob" ||
      res.request.responseType === "arraybuffer"
    ) {
      // console.log("收到二进制数据")
      //res.data为blob对象。
      return res;
    }
    // 未设置状态码则默认成功状态
    const code = res.data.code || 200;
    // 获取错误信息
    const msg = res.data.msg || errorCode[code] || errorCode["default"];

    //统一异常处理 能处理即处理，之后把异常抛出
    switch (code) {
      case 401: //认证失败，
        if (!isRelogin.show) {
          //在页面访问后端接口认证失败，可能没token被拦截了，或者token，redis过期，或者伪造token解析出错。要先把token清除了。然后丢出去就行，全局路由守卫会跳到登录界面的
          isRelogin.show = true;
          MessageBox.confirm(
            "需要登录才能访问，或者您的登录状态已过期。",
            "系统提示",
            {
              confirmButtonText: "重新登录",
              cancelButtonText: "留在这里",
              type: "warning",
            }
          )
            .then(() => {
              //清除token
              isRelogin.show = false;
              //跳到首页就行
              store.dispatch("LogOut").then(() => {
                location.href = "/";
              });
            })
            .catch(() => {
              //留在这个页面，什么都不管他
              isRelogin.show = false;
            });
        } else {
          //在跳转路由时拿用户数据认证失败，可能没token被拦截了，或者token，redis过期，或者伪造token解析出错。统统跳到登录页进行登录
          Message({
            message: msg,
            type: "error",
          });
          removeToken();
          //跳到首页就行
          store.dispatch("LogOut").then(() => {
            location.href = "/";
          });
        }
        break;
      case 500: //后端代码错/
        Message({ message: msg, type: "error" });
        console.error(msg);
        break;
      case 200: //成功返回
        return res.data;
      default: //具体的业务错误，显示错误。抛出去给业务调用者处理即可
        Message({ message: msg, type: "error" });
    }
    //上面已经显示错误信息了，页面接送到就不要再打印了
    //以上错误抛出去。返回值：一个被拒绝的 Promise对象。根据接口的需求进行捕获
    return Promise.reject(new Error(msg));
  },
  (error) => {
    console.log("错误响应：");
    console.log(error);
    let { message } = error;
    if (message == "Network Error") {
      message = "后端接口连接异常";
    } else if (message.includes("timeout")) {
      message = "系统接口请求超时";
    } else if (message.includes("Request failed with status code")) {
      message = "系统接口" + message.substr(message.length - 3) + "异常";
    }
    Message({
      message: message,
      type: "error",
      duration: 5 * 1000,
    });
    return Promise.reject(error);
  }
);
export default request;
