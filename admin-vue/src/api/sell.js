import request from '@/utils/request'

/**
 * 查询闲置分页
 * @param query
 * @returns {AxiosPromise}
 */
export function listsell(query) {
  return request({
    url: '/admin/sell/list',
    method: 'get',
    params: query
  })
}



// 删除闲置
export function delsell(sellIds) {
  return request({
    url: '/admin/sell/delete/' + sellIds,
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



// 修改闲置
export function addsell(data) {
  return request({
    url: '/client/sell/add',
    method: 'post',
    data: data
  })
}

// 修改闲置
export function updatesell(data) {
  return request({
    url: '/admin/sell/update',
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
    url: '/admin/sell/changeStatus',
    method: 'put',
    data: data
  })
}

