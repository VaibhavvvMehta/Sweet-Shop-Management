import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';

// Base API configuration
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

// Create axios instance
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds timeout
});

// Request interceptor to add auth token
apiClient.interceptors.request.use(
  (config: AxiosRequestConfig): any => {
    const token = localStorage.getItem('authToken');
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for error handling
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error) => {
    // Handle 401 errors (Unauthorized)
    if (error.response?.status === 401) {
      // Clear token and redirect to login
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    
    // Handle other errors
    const errorMessage = error.response?.data?.message || error.message || 'An unexpected error occurred';
    console.error('API Error:', errorMessage);
    
    return Promise.reject({
      message: errorMessage,
      status: error.response?.status,
      data: error.response?.data,
    });
  }
);

// Generic API methods
export const apiService = {
  // GET request
  async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await apiClient.get<T>(url, config);
    return response.data;
  },

  // POST request
  async post<T, D = any>(url: string, data?: D, config?: AxiosRequestConfig): Promise<T> {
    const response = await apiClient.post<T>(url, data, config);
    return response.data;
  },

  // PUT request
  async put<T, D = any>(url: string, data?: D, config?: AxiosRequestConfig): Promise<T> {
    const response = await apiClient.put<T>(url, data, config);
    return response.data;
  },

  // DELETE request
  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await apiClient.delete<T>(url, config);
    return response.data;
  },

  // PATCH request
  async patch<T, D = any>(url: string, data?: D, config?: AxiosRequestConfig): Promise<T> {
    const response = await apiClient.patch<T>(url, data, config);
    return response.data;
  },
};

// Export the axios instance for direct use if needed
export { apiClient };

// Export API configuration
export const API_CONFIG = {
  BASE_URL: API_BASE_URL,
  ENDPOINTS: {
    AUTH: {
      LOGIN: '/api/auth/login',
      REGISTER: '/api/auth/register',
      HEALTH: '/api/auth/health',
    },
    SWEETS: {
      BASE: '/api/v1/sweets',
      BY_ID: (id: number) => `/api/v1/sweets/${id}`,
      AVAILABLE: '/api/v1/sweets/available',
      SEARCH: '/api/v1/sweets/search',
      CATEGORIES: '/api/v1/sweets/categories',
      LOW_STOCK: '/api/v1/sweets/low-stock',
    },
    ORDERS: {
      BASE: '/api/v1/orders',
      BY_ID: (id: number) => `/api/v1/orders/${id}`,
      STATUS: (id: number) => `/api/v1/orders/${id}/status`,
      CANCEL: (id: number) => `/api/v1/orders/${id}/cancel`,
      ITEMS: (id: number) => `/api/v1/orders/${id}/items`,
      ITEM_BY_ID: (orderId: number, itemId: number) => `/api/v1/orders/${orderId}/items/${itemId}`,
      BY_CUSTOMER: (email: string) => `/api/v1/orders/customer/${email}`,
      BY_STATUS: (status: string) => `/api/v1/orders/status/${status}`,
      PENDING: '/api/v1/orders/pending',
      ACTIVE: '/api/v1/orders/active',
      RECENT: '/api/v1/orders/recent',
      SEARCH: '/api/v1/orders/search',
      STATISTICS: '/api/v1/orders/statistics',
      REVENUE: '/api/v1/orders/revenue',
    },
  },
};