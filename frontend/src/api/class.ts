import http from './http'

export interface ClassEntity {
  id?: number
  classNo: string
  name: string
  grade?: string
  major?: string
  counselor?: string
  createdAt?: string
}

export interface PageResp<T> { total: number; records: T[] }

export function fetchClasses(params: any) {
  return http.get<PageResp<ClassEntity>>('/classes', { params })
}

export function fetchClass(id: number) {
  return http.get<ClassEntity>(`/classes/${id}`)
}

export function createClass(data: ClassEntity) {
  return http.post<ClassEntity>('/classes', data)
}

export function updateClass(id: number, data: ClassEntity) {
  return http.put<ClassEntity>(`/classes/${id}`, data)
}

export function deleteClass(id: number) {
  return http.delete(`/classes/${id}`)
}

export function deleteClassesBatch(ids: number[]) {
  return http.delete('/classes', { data: ids })
}


