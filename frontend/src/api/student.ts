import http from './http'

export interface Student {
  id?: number
  studentNo: string
  name: string
  gender?: string
  age?: number
  clazz?: string
  major?: string
  enrollDate?: string
  phone?: string
}

export interface PageResp<T> { total: number; records: T[] }

export function fetchStudents(params: any) {
  return http.get<PageResp<Student>>('/students', { params })
}

export function fetchStudent(id: number) {
  return http.get<Student>(`/students/${id}`)
}

export function createStudent(data: Student) {
  return http.post<Student>('/students', data)
}

export function updateStudent(id: number, data: Student) {
  return http.put<Student>(`/students/${id}` , data)
}

export function deleteStudent(id: number) {
  return http.delete(`/students/${id}`)
}

export function deleteStudentsBatch(ids: number[]) {
  return http.delete('/students', { data: ids })
}


