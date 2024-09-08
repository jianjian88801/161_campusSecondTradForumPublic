import request from '@/utils/request'

/**
 * 查询帖子分页
 * @param query
 * @returns {AxiosPromise}
 */
export function listTalk(query) {
  return request({
    url: '/admin/talk/list',
    method: 'get',
    params: query
  })
}

/**
 * 获取分类列表
 * @param query
 * @returns {AxiosPromise}
 */
export function typeList(query) {
  return request({
    url: '/admin/talk/getTypeList',
    method: 'get',
    params: query
  })
}

// 删除帖子
export function delTalk(talkIds) {
  return request({
    url: '/admin/talk/delete/' + talkIds,
    method: 'delete'
  })
}

// 上传
export function upload(data) {
  return request({
    url: '/common/upload',
    method: 'post',
    data: data,
  })
}



// 修改帖子
export function addTalk(data) {
  return request({
    url: '/client/talk/add',
    method: 'post',
    data: data
  })
}

// 修改帖子
export function updateTalk(data) {
  return request({
    url: '/admin/talk/update',
    method: 'put',
    data: data
  })
}

// 状态修改
export function changeStatus(id, delFlag) {
  const data = {
    id,
    delFlag
  }
  return request({
    url: '/admin/talk/changeStatus',
    method: 'put',
    data: data
  })
}

