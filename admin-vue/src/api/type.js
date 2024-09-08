import request from '@/utils/request'

/**
 * 查询分类分页
 * @param query
 * @returns {AxiosPromise}
 */
export function listType(query) {
  return request({
    url: '/admin/type/list',
    method: 'get',
    params: query
  })
}


// 新增分类
export function addType(data) {
  return request({
    url: '/admin/type/add',
    method: 'post',
    data: data
  })
}
// 删除分类
export function delType(typeIds) {
  return request({
    url: '/admin/type/delete/' + typeIds,
    method: 'delete'
  })
}


// 修改分类
export function updateType(data) {
  return request({
    url: '/admin/type/update',
    method: 'put',
    data: data
  })
}

// 状态修改
export function changeTypeStatus(id, status) {
  const data = {
    id,
    status
  }
  return request({
    url: '/admin/type/update',
    method: 'put',
    data: data
  })
}

