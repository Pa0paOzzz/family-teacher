import axios from 'axios';

// 创建 axios 实例
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器，添加token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器，处理错误
api.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    if (error.response && error.response.status === 401) {
      // 未授权，跳转到登录页
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      localStorage.removeItem('role');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 用户相关API
export const userApi = {
  login: (data) => api.post('/users/login', data),
  register: (data) => api.post('/users/register', data),
  getProfile: () => api.get('/users/profile'),
  updateProfile: (data) => api.put('/users/profile', data),
  getStudentProfile: () => api.get('/users/student/profile'),
  getTeacherProfile: () => api.get('/users/teacher/profile'),
  updateStudentProfile: (data) => api.put('/users/student/update', data),
  updateTeacherProfile: (data) => api.put('/users/teacher/update', data)
};

// 家教求职相关API
export const jobPostApi = {
  create: (data) => api.post('/resources/teacher/job-post', data),
  getList: () => api.get('/resources/teacher/job-posts'),
  getMyList: () => api.get('/resources/teacher/my-job-posts'),
  update: (data) => api.put('/resources/teacher/job-post', data),
  deactivate: (id) => api.put(`/resources/teacher/job-post/${id}/deactivate`)
};

// 学生家教需求相关API
export const tutoringRequestApi = {
  create: (data) => api.post('/resources/tutoring-requests', data),
  getList: () => api.get('/resources/tutoring-requests'),
  getMyList: () => api.get('/resources/my-tutoring-requests'),
  getById: (id) => api.get(`/resources/tutoring-requests/${id}`),
  update: (data) => api.put('/resources/tutoring-request', data),
  deactivate: (id) => api.put(`/resources/tutoring-request/${id}/deactivate`)
};

// 预约相关API
export const appointmentApi = {
  create: (data) => api.post('/transactions/appointments', data),
  createForTeacher: (data) => api.post('/transactions/appointments', data),
  getList: () => api.get('/transactions/appointments'),
  getById: (id) => api.get(`/transactions/appointments/${id}`),
  update: (id, data) => api.put(`/transactions/appointments/${id}`, data),
  confirmLongTerm: (id) => api.put(`/transactions/appointments/${id}/confirm-long-term`),
  delete: (id) => api.delete(`/transactions/appointments/${id}`)
};

// 订单相关API
export const orderApi = {
  getList: () => api.get('/transactions/orders'),
  getById: (id) => api.get(`/transactions/orders/${id}`)
};

// 评价相关API
export const evaluationApi = {
  create: (data) => api.post('/evaluations/create', data),
  myEvaluations: () => api.get('/evaluations/my-evaluations'),
  receivedEvaluations: () => api.get('/evaluations/received-evaluations'),
  check: (appointmentId) => api.get('/evaluations/check', { params: { appointmentId } })
};

// 推荐相关API
export const recommendationApi = {
  getTeachersForStudent: () => api.get('/recommendations/teachers-for-student'),
  getStudentsForTeacher: () => api.get('/recommendations/students-for-teacher')
};

// 收藏相关API
export const favoriteApi = {
  add: (data) => api.post('/favorites/add', data),
  remove: (data) => api.delete('/favorites/remove', { data }),
  getList: (resourceType) => api.get('/favorites/list', { params: { resourceType } }),
  check: (resourceType, resourceId) => api.get('/favorites/check', { params: { resourceType, resourceId } })
};

// 管理员相关API
export const adminApi = {
  getUsers: () => api.get('/admin/users'),
  getAppointments: () => api.get('/admin/appointments'),
  getEvaluations: () => api.get('/admin/evaluations'),
  getStatistics: () => api.get('/admin/statistics')
};

export default api;
