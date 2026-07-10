import http from './http'

export interface Course {
  id?: number
  courseNo: string
  name: string
  credit: number
  teacher: string
  semester: string
  maxStudents: number
  description?: string
  createdAt?: string
}

export interface CourseQuery {
  name?: string
  teacher?: string
  semester?: string
  page?: number
  pageSize?: number
}

export interface PageResponse<T> {
  total: number
  records: T[]
}

export function getCourses(query: CourseQuery = {}): Promise<{ data: PageResponse<Course> }> {
  return http.get('/courses', { params: { ...query, page: query.page || 1, pageSize: query.pageSize || 20 } })
}

export function getAllCourses(): Promise<{ data: Course[] }> {
  return http.get('/courses/all')
}

export function getCourse(id: number): Promise<{ data: Course }> {
  return http.get(`/courses/${id}`)
}

export function createCourse(course: Course): Promise<{ data: Course }> {
  return http.post('/courses', course)
}

export function updateCourse(id: number, course: Course): Promise<{ data: Course }> {
  return http.put(`/courses/${id}`, course)
}

export function deleteCourse(id: number): Promise<void> {
  return http.delete(`/courses/${id}`)
}
