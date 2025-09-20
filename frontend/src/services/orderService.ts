import { apiService, API_CONFIG } from './api';
import {
  Order,
  OrderCreateRequest,
  OrderUpdateRequest,
  OrderStatusUpdateRequest,
  OrderItemAddRequest,
  OrderItemUpdateRequest,
  OrderStatus,
  OrderStatistics,
  RevenueResponse,
  OrderFilters,
} from '../types';

export const orderService = {
  /**
   * Get all orders
   */
  async getAllOrders(): Promise<Order[]> {
    try {
      return await apiService.get<Order[]>(API_CONFIG.ENDPOINTS.ORDERS.BASE);
    } catch (error) {
      console.error('Failed to fetch orders:', error);
      throw error;
    }
  },

  /**
   * Get order by ID
   */
  async getOrderById(id: number): Promise<Order> {
    try {
      return await apiService.get<Order>(API_CONFIG.ENDPOINTS.ORDERS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to fetch order with ID ${id}:`, error);
      throw error;
    }
  },

  /**
   * Create new order
   */
  async createOrder(orderData: OrderCreateRequest): Promise<Order> {
    try {
      return await apiService.post<Order>(
        API_CONFIG.ENDPOINTS.ORDERS.BASE,
        orderData
      );
    } catch (error) {
      console.error('Failed to create order:', error);
      throw error;
    }
  },

  /**
   * Update order
   */
  async updateOrder(id: number, orderData: OrderUpdateRequest): Promise<Order> {
    try {
      return await apiService.put<Order>(
        API_CONFIG.ENDPOINTS.ORDERS.BY_ID(id),
        orderData
      );
    } catch (error) {
      console.error(`Failed to update order with ID ${id}:`, error);
      throw error;
    }
  },

  /**
   * Update order status
   */
  async updateOrderStatus(id: number, statusData: OrderStatusUpdateRequest): Promise<Order> {
    try {
      return await apiService.put<Order>(
        API_CONFIG.ENDPOINTS.ORDERS.STATUS(id),
        statusData
      );
    } catch (error) {
      console.error(`Failed to update order status for ID ${id}:`, error);
      throw error;
    }
  },

  /**
   * Cancel order
   */
  async cancelOrder(id: number): Promise<Order> {
    try {
      return await apiService.put<Order>(API_CONFIG.ENDPOINTS.ORDERS.CANCEL(id));
    } catch (error) {
      console.error(`Failed to cancel order with ID ${id}:`, error);
      throw error;
    }
  },

  /**
   * Delete order
   */
  async deleteOrder(id: number): Promise<void> {
    try {
      await apiService.delete(API_CONFIG.ENDPOINTS.ORDERS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to delete order with ID ${id}:`, error);
      throw error;
    }
  },

  /**
   * Add item to order
   */
  async addItemToOrder(orderId: number, itemData: OrderItemAddRequest): Promise<Order> {
    try {
      return await apiService.post<Order>(
        API_CONFIG.ENDPOINTS.ORDERS.ITEMS(orderId),
        itemData
      );
    } catch (error) {
      console.error(`Failed to add item to order ${orderId}:`, error);
      throw error;
    }
  },

  /**
   * Update order item
   */
  async updateOrderItem(
    orderId: number,
    itemId: number,
    itemData: OrderItemUpdateRequest
  ): Promise<Order> {
    try {
      return await apiService.put<Order>(
        API_CONFIG.ENDPOINTS.ORDERS.ITEM_BY_ID(orderId, itemId),
        itemData
      );
    } catch (error) {
      console.error(`Failed to update item ${itemId} in order ${orderId}:`, error);
      throw error;
    }
  },

  /**
   * Remove item from order
   */
  async removeItemFromOrder(orderId: number, itemId: number): Promise<Order> {
    try {
      return await apiService.delete<Order>(
        API_CONFIG.ENDPOINTS.ORDERS.ITEM_BY_ID(orderId, itemId)
      );
    } catch (error) {
      console.error(`Failed to remove item ${itemId} from order ${orderId}:`, error);
      throw error;
    }
  },

  /**
   * Get orders by customer email
   */
  async getOrdersByCustomer(email: string): Promise<Order[]> {
    try {
      return await apiService.get<Order[]>(
        API_CONFIG.ENDPOINTS.ORDERS.BY_CUSTOMER(email)
      );
    } catch (error) {
      console.error(`Failed to fetch orders for customer ${email}:`, error);
      throw error;
    }
  },

  /**
   * Get orders by status
   */
  async getOrdersByStatus(status: OrderStatus): Promise<Order[]> {
    try {
      return await apiService.get<Order[]>(
        API_CONFIG.ENDPOINTS.ORDERS.BY_STATUS(status)
      );
    } catch (error) {
      console.error(`Failed to fetch orders with status ${status}:`, error);
      throw error;
    }
  },

  /**
   * Get pending orders
   */
  async getPendingOrders(): Promise<Order[]> {
    try {
      return await apiService.get<Order[]>(API_CONFIG.ENDPOINTS.ORDERS.PENDING);
    } catch (error) {
      console.error('Failed to fetch pending orders:', error);
      throw error;
    }
  },

  /**
   * Get active orders (in progress)
   */
  async getActiveOrders(): Promise<Order[]> {
    try {
      return await apiService.get<Order[]>(API_CONFIG.ENDPOINTS.ORDERS.ACTIVE);
    } catch (error) {
      console.error('Failed to fetch active orders:', error);
      throw error;
    }
  },

  /**
   * Get recent orders
   */
  async getRecentOrders(limit = 10): Promise<Order[]> {
    try {
      const url = `${API_CONFIG.ENDPOINTS.ORDERS.RECENT}?limit=${limit}`;
      return await apiService.get<Order[]>(url);
    } catch (error) {
      console.error('Failed to fetch recent orders:', error);
      throw error;
    }
  },

  /**
   * Search orders by customer name
   */
  async searchOrdersByCustomer(customerName: string): Promise<Order[]> {
    try {
      const url = `${API_CONFIG.ENDPOINTS.ORDERS.SEARCH}?customerName=${encodeURIComponent(customerName)}`;
      return await apiService.get<Order[]>(url);
    } catch (error) {
      console.error('Failed to search orders:', error);
      throw error;
    }
  },

  /**
   * Get order statistics
   */
  async getOrderStatistics(): Promise<OrderStatistics> {
    try {
      return await apiService.get<OrderStatistics>(
        API_CONFIG.ENDPOINTS.ORDERS.STATISTICS
      );
    } catch (error) {
      console.error('Failed to fetch order statistics:', error);
      throw error;
    }
  },

  /**
   * Get revenue for date range
   */
  async getRevenue(startDate: string, endDate: string): Promise<RevenueResponse> {
    try {
      const url = `${API_CONFIG.ENDPOINTS.ORDERS.REVENUE}?startDate=${startDate}&endDate=${endDate}`;
      return await apiService.get<RevenueResponse>(url);
    } catch (error) {
      console.error('Failed to fetch revenue data:', error);
      throw error;
    }
  },

  /**
   * Get filtered orders
   */
  async getFilteredOrders(filters: OrderFilters): Promise<Order[]> {
    try {
      const params = new URLSearchParams();
      
      if (filters.status) params.append('status', filters.status);
      if (filters.customerEmail) params.append('customerEmail', filters.customerEmail);
      if (filters.startDate) params.append('startDate', filters.startDate);
      if (filters.endDate) params.append('endDate', filters.endDate);
      if (filters.sortBy) params.append('sortBy', filters.sortBy);
      if (filters.sortOrder) params.append('sortOrder', filters.sortOrder);

      const url = `${API_CONFIG.ENDPOINTS.ORDERS.BASE}?${params.toString()}`;
      return await apiService.get<Order[]>(url);
    } catch (error) {
      console.error('Failed to fetch filtered orders:', error);
      throw error;
    }
  },

  /**
   * Validate order data before submission
   */
  validateOrderData(data: OrderCreateRequest): string[] {
    const errors: string[] = [];
    
    if (!data.customerName || data.customerName.trim().length < 2) {
      errors.push('Customer name must be at least 2 characters long');
    }
    
    if (!data.customerEmail || !this.isValidEmail(data.customerEmail)) {
      errors.push('Valid email address is required');
    }
    
    if (!data.customerPhone || data.customerPhone.trim().length < 10) {
      errors.push('Valid phone number is required');
    }
    
    if (!data.deliveryAddress || data.deliveryAddress.trim().length < 5) {
      errors.push('Delivery address is required');
    }
    
    if (!data.items || data.items.length === 0) {
      errors.push('At least one item is required');
    }
    
    if (data.items) {
      data.items.forEach((item, index) => {
        if (!item.sweetId) {
          errors.push(`Item ${index + 1}: Sweet selection is required`);
        }
        if (!item.quantity || item.quantity <= 0) {
          errors.push(`Item ${index + 1}: Quantity must be greater than 0`);
        }
      });
    }
    
    return errors;
  },

  /**
   * Helper: Validate email format
   */
  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  },

  /**
   * Helper: Calculate order total
   */
  calculateOrderTotal(items: Array<{ quantity: number; unitPrice: number }>): number {
    return items.reduce((total, item) => total + (item.quantity * item.unitPrice), 0);
  },

  /**
   * Helper: Get order status color for UI
   */
  getStatusColor(status: OrderStatus): string {
    const colors = {
      [OrderStatus.PENDING]: '#f57c00',
      [OrderStatus.CONFIRMED]: '#2196f3',
      [OrderStatus.PREPARING]: '#ff9800',
      [OrderStatus.READY]: '#4caf50',
      [OrderStatus.OUT_FOR_DELIVERY]: '#9c27b0',
      [OrderStatus.DELIVERED]: '#4caf50',
      [OrderStatus.COMPLETED]: '#8bc34a',
      [OrderStatus.CANCELLED]: '#f44336',
    };
    return colors[status] || '#9e9e9e';
  },

  /**
   * Helper: Get next possible status transitions
   */
  getNextStatusOptions(currentStatus: OrderStatus): OrderStatus[] {
    const transitions = {
      [OrderStatus.PENDING]: [OrderStatus.CONFIRMED, OrderStatus.CANCELLED],
      [OrderStatus.CONFIRMED]: [OrderStatus.PREPARING, OrderStatus.CANCELLED],
      [OrderStatus.PREPARING]: [OrderStatus.READY, OrderStatus.CANCELLED],
      [OrderStatus.READY]: [OrderStatus.OUT_FOR_DELIVERY, OrderStatus.DELIVERED],
      [OrderStatus.OUT_FOR_DELIVERY]: [OrderStatus.DELIVERED],
      [OrderStatus.DELIVERED]: [OrderStatus.COMPLETED],
      [OrderStatus.COMPLETED]: [],
      [OrderStatus.CANCELLED]: [],
    };
    
    return transitions[currentStatus] || [];
  },

  /**
   * Helper: Check if order can be modified
   */
  canModifyOrder(status: OrderStatus): boolean {
    return status === OrderStatus.PENDING;
  },

  /**
   * Helper: Check if order can be cancelled
   */
  canCancelOrder(status: OrderStatus): boolean {
    return [OrderStatus.PENDING, OrderStatus.CONFIRMED].includes(status);
  },
};