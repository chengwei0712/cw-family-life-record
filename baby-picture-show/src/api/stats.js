import request from './request'

/**
 * 获取系统总统计
 */
export function getTotalStats() {
  return request({
    url: '/stats/total',
    method: 'get'
  })
}