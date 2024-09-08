
import request from '@/utils/request'

/**
 * 查询下拉用户列表
 * @param query
 * @returns {AxiosPromise}
 */
export function listUserByUserNickName(query) {
  return request({
    url: '/system/talkComment/listUser',
    method: 'get',
    params: query
  })
}

/**
 * 查询评论分页
 * @param query
 * @returns {AxiosPromise}
 */
export function listTalkComment(query) {
  return request({
    url: '/admin/talkComment/list',
    method: 'get',
    params: query
  })
}

/**
 * 删除评论
 * @param talkCommentIds
 * @returns {AxiosPromise}
 */
export function delTalkComment(talkCommentIds) {
  return request({
    url: '/admin/talkComment/delete/' + talkCommentIds,
    method: 'delete'
  })
}
