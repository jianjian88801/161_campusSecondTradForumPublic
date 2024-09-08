// 查询角色列表
import request from '@/utils/request'

export function getUploadToken() {
  return request({
    url: '/common/getUploadToken',
    method: 'get'
  })
}

/**
 *上传图片
 */
export const uploadImage = (file)=> {
  return request({
    url:"/common/upload",
    method: 'post',
    data:file
  })
}

