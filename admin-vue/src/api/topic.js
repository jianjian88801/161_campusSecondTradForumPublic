import request from '@/utils/request'

/**
 * 查询推文分页
 * @param query
 * @returns {AxiosPromise}
 */
export function listtopic(query) {
  return request({
    url: '/admin/topic/list',
    method: 'get',
    params: query
  })
}



// 删除推文
export function deltopic(topicIds) {
  return request({
    url: '/admin/topic/delete/' + topicIds,
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



// 修改推文
export function addtopic(data) {
  return request({
    url: '/client/topic/add',
    method: 'post',
    data: data
  })
}

// 修改推文
export function updatetopic(data) {
  return request({
    url: '/admin/topic/update',
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
    url: '/admin/topic/changeStatus',
    method: 'put',
    data: data
  })
}

