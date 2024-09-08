import request from '@/utils/request'

/**
 * 查询轮播图分页
 * @param query
 * @returns {AxiosPromise}
 */
export function listAds(query) {
  return request({
    url: '/admin/ads/list',
    method: 'get',
    params: query
  })
}


// 新增分类
export function addAds(data) {
  return request({
    url: '/admin/ads/addOrUpdate',
    method: 'post',
    data: data
  })
}
// 删除分类
export function delAds(typeIds) {
  return request({
    url: '/admin/ads/delete/' + typeIds,
    method: 'delete'
  })
}


