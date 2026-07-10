import http from './http'

export interface Enrollment {
  id?: number
  studentId: number
  courseId: number
  enrollDate?: string
  score?: number
  status?: string
}

export function getEnrollments(params: { studentId?: number; courseId?: number; page?: number; pageSize?: number } = {}): Promise<{ data: { total: number; records: Enrollment[] } }> {
  return http.get('/enrollments', { params: { ...params, page: params.page || 1, pageSize: params.pageSize || 20 } })
}

export function getStudentEnrollments(studentId: number): Promise<{ data: Enrollment[] }> {
  return http.get(`/enrollments/student/${studentId}`)
}

export function enroll(data: { studentId: number; courseId: number }): Promise<{ data: Enrollment }> {
  return http.post('/enrollments', data)
}

export function updateEnrollment(id: number, data: Partial<Enrollment>): Promise<{ data: Enrollment }> {
  return http.put(`/enrollments/${id}`, data)
}

export function dropEnrollment(id: number): Promise<void> {
  return http.delete(`/enrollments/${id}`)
}
