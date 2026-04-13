import { get, post, put, del } from '@/utils/request'
import type { MediaItem } from './media'

export interface DiaryItem {
  id: string
  title: string
  content: string
  mood?: string
  weather?: string
  createTime: string
  updateTime?: string
  mediaList?: MediaItem[]
}

export interface DiaryListParams {
  page?: number
  size?: number
  keyword?: string
}

export interface DiaryListResponse {
  list: DiaryItem[]
  total: number
  page: number
  size: number
}

export interface CreateDiaryRequest {
  title?: string
  content: string
  mood?: string
  weather?: string
  mediaIds?: string[]
}

export interface UpdateDiaryRequest extends CreateDiaryRequest {
  id: string
}

export interface DiaryStats {
  diaryCount: number
}

/**
 * 获取日记列表
 */
export function getDiaryList(params: DiaryListParams) {
  return get<DiaryListResponse>('/diary/list', params)
}

/**
 * 获取日记详情
 */
export function getDiaryDetail(id: string) {
  return get<DiaryItem>(`/diary/${id}`)
}

/**
 * 创建日记
 */
export function createDiary(data: CreateDiaryRequest) {
  return post<{ id: string; title: string; createTime: string }>('/diary/create', data)
}

/**
 * 更新日记
 */
export function updateDiary(id: string, data: CreateDiaryRequest) {
  return put(`/diary/${id}`, data)
}

/**
 * 删除日记
 */
export function deleteDiary(id: string) {
  return del(`/diary/${id}`)
}

/**
 * 获取日记统计
 */
export function getDiaryStats() {
  return get<DiaryStats>('/diary/stats')
}

// 心情选项
export const MOOD_OPTIONS = [
  { value: '开心', emoji: '😊' },
  { value: '平静', emoji: '😐' },
  { value: '难过', emoji: '😢' },
  { value: '兴奋', emoji: '🤩' },
  { value: '生气', emoji: '😠' },
  { value: '幸福', emoji: '🥰' },
  { value: '疲惫', emoji: '😴' },
  { value: '惊喜', emoji: '😲' }
]

// 天气选项
export const WEATHER_OPTIONS = [
  { value: '晴天', emoji: '☀️' },
  { value: '多云', emoji: '⛅' },
  { value: '阴天', emoji: '☁️' },
  { value: '小雨', emoji: '🌧️' },
  { value: '大雨', emoji: '⛈️' },
  { value: '雪天', emoji: '❄️' },
  { value: '大风', emoji: '💨' },
  { value: '雾天', emoji: '🌫️' }
]
