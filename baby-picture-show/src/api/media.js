import request from './request'

/**
 * 获取媒体列表
 */
export function getMediaList(params) {
  return request({
    url: '/media/list',
    method: 'get',
    params
  })
}

/**
 * 获取媒体详情
 */
export function getMediaDetail(id) {
  return request({
    url: `/media/${id}`,
    method: 'get'
  })
}

/**
 * 上传媒体文件
 */
export function uploadMedia(formData) {
  return request({
    url: '/media/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 删除媒体
 */
export function deleteMedia(id) {
  return request({
    url: `/media/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除
 */
export function batchDeleteMedia(ids) {
  return request({
    url: '/media/delete',
    method: 'post',
    data: ids
  })
}

/**
 * 获取统计
 */
export function getMediaStats() {
  return request({
    url: '/media/stats',
    method: 'get'
  })
}