import { get, post, del } from '@/utils/request'

export interface MediaItem {
  id: string
  fileName: string
  filePath: string
  fileSize: string
  fileType: string
}

export interface MediaListParams {
  page?: number
  size?: number
  type?: 'all' | 'photo' | 'video'
  keyword?: string
}

export interface MediaListResponse {
  list: MediaItem[]
  total: number
  page: number
  size: number
}

export interface UploadResult {
  successCount: number
  failedCount: number
  failedFiles: string[]
  successList: MediaItem[]
}

export interface MediaStats {
  total: number
  photoCount: number
  videoCount: number
}

/**
 * 获取媒体列表
 */
export function getMediaList(params: MediaListParams) {
  return get<MediaListResponse>('/media/list', params)
}

/**
 * 获取媒体详情
 */
export function getMediaDetail(id: string) {
  return get<MediaItem>(`/media/${id}`)
}

/**
 * 上传媒体文件（使用 uni.uploadFile）
 */
export function uploadMedia(filePath: string, formData?: any): Promise<UploadResult> {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    const baseUrl = import.meta.env.DEV ? '/api' : 'http://localhost:9003'

    uni.uploadFile({
      url: baseUrl + '/media/upload',
      filePath: filePath,
      name: 'files',
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      formData: formData,
      success: (res) => {
        try {
          const data = JSON.parse(res.data)
          if (data.code === 200 || data.success) {
            resolve(data.data)
          } else {
            reject(new Error(data.msg || '上传失败'))
          }
        } catch (e) {
          reject(new Error('解析响应失败'))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

/**
 * 批量上传媒体文件
 */
export async function batchUploadMedia(filePaths: string[]): Promise<UploadResult> {
  const results: UploadResult = {
    successCount: 0,
    failedCount: 0,
    failedFiles: [],
    successList: []
  }

  for (const filePath of filePaths) {
    try {
      const result = await uploadMedia(filePath)
      results.successCount += result.successCount
      results.failedCount += result.failedCount
      results.failedFiles.push(...result.failedFiles)
      results.successList.push(...result.successList)
    } catch (e) {
      results.failedCount++
      results.failedFiles.push(filePath + ' (上传失败)')
    }
  }

  return results
}

/**
 * 删除媒体
 */
export function deleteMedia(id: string) {
  return del(`/media/${id}`)
}

/**
 * 批量删除
 */
export function batchDeleteMedia(ids: string[]) {
  return post('/media/delete', ids)
}

/**
 * 获取统计
 */
export function getMediaStats() {
  return get<MediaStats>('/media/stats')
}
