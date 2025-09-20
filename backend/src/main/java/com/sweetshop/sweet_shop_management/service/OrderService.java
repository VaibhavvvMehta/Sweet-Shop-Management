package com.sweetshop.sweet_shop_management.service;

import com.sweetshop.sweet_shop_management.dto.*;
import com.sweetshop.sweet_shop_management.model.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Order management operations.
 * Provides business logic for order processing, status management, and cart functionality.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
public interface OrderService {

    /**
     * Create a new order
     * 
     * @param request the order creation request
     * @return the created order response
     * @throws IllegalArgumentException if the request is invalid
     * @throws IllegalStateException if any sweet is not available or has insufficient stock
     */
    OrderResponse createOrder(OrderCreateRequest request);

    /**
     * Get an order by ID
     * 
     * @param id the order ID
     * @return the order response if found
     * @throws RuntimeException if order is not found
     */
    OrderResponse getOrderById(Long id);

    /**
     * Get all orders with optional pagination
     * 
     * @return list of all orders
     */
    List<OrderResponse> getAllOrders();

    /**
     * Update an existing order (only if it's in PENDING status)
     * 
     * @param id the order ID
     * @param request the order update request
     * @return the updated order response
     * @throws RuntimeException if order is not found
     * @throws IllegalStateException if order cannot be modified
     */
    OrderResponse updateOrder(Long id, OrderUpdateRequest request);

    /**
     * Update order status
     * 
     * @param id the order ID
     * @param request the status update request
     * @return the updated order response
     * @throws RuntimeException if order is not found
     * @throws IllegalArgumentException if status transition is invalid
     */
    OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request);

    /**
     * Cancel an order (only if it's cancellable)
     * 
     * @param id the order ID
     * @return the cancelled order response
     * @throws RuntimeException if order is not found
     * @throws IllegalStateException if order cannot be cancelled
     */
    OrderResponse cancelOrder(Long id);

    /**
     * Delete an order (only if it's in PENDING status)
     * 
     * @param id the order ID
     * @throws RuntimeException if order is not found
     * @throws IllegalStateException if order cannot be deleted
     */
    void deleteOrder(Long id);

    /**
     * Add an item to an existing order (only if order is modifiable)
     * 
     * @param orderId the order ID
     * @param request the order item addition request
     * @return the updated order response
     * @throws RuntimeException if order is not found
     * @throws IllegalStateException if order cannot be modified or sweet is unavailable
     */
    OrderResponse addItemToOrder(Long orderId, OrderItemAddRequest request);

    /**
     * Update an order item quantity
     * 
     * @param orderId the order ID
     * @param itemId the order item ID
     * @param request the order item update request
     * @return the updated order response
     * @throws RuntimeException if order or order item is not found
     * @throws IllegalStateException if order cannot be modified
     */
    OrderResponse updateOrderItem(Long orderId, Long itemId, OrderItemUpdateRequest request);

    /**
     * Remove an item from an order
     * 
     * @param orderId the order ID
     * @param itemId the order item ID
     * @return the updated order response
     * @throws RuntimeException if order or order item is not found
     * @throws IllegalStateException if order cannot be modified
     */
    OrderResponse removeItemFromOrder(Long orderId, Long itemId);

    /**
     * Get orders by customer email
     * 
     * @param customerEmail the customer's email
     * @return list of orders for the customer
     */
    List<OrderResponse> getOrdersByCustomerEmail(String customerEmail);

    /**
     * Get orders by status
     * 
     * @param status the order status
     * @return list of orders with the specified status
     */
    List<OrderResponse> getOrdersByStatus(Order.OrderStatus status);

    /**
     * Get pending orders
     * 
     * @return list of pending orders
     */
    List<OrderResponse> getPendingOrders();

    /**
     * Get active orders (orders in progress)
     * 
     * @return list of active orders
     */
    List<OrderResponse> getActiveOrders();

    /**
     * Get completed orders within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of completed orders within the date range
     */
    List<OrderResponse> getCompletedOrdersBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get recent orders
     * 
     * @param limit the maximum number of orders to return
     * @return list of recent orders
     */
    List<OrderResponse> getRecentOrders(int limit);

    /**
     * Search orders by customer name
     * 
     * @param customerName the customer name to search for
     * @return list of orders matching the customer name
     */
    List<OrderResponse> searchOrdersByCustomerName(String customerName);

    /**
     * Check if an order exists
     * 
     * @param id the order ID
     * @return true if the order exists
     */
    boolean orderExists(Long id);

    /**
     * Count orders by status
     * 
     * @param status the order status
     * @return count of orders with the specified status
     */
    long countOrdersByStatus(Order.OrderStatus status);

    /**
     * Get total revenue for completed orders in a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return total revenue from completed orders
     */
    Double getTotalRevenueBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get orders containing a specific sweet
     * 
     * @param sweetId the sweet ID
     * @return list of orders containing the specified sweet
     */
    List<OrderResponse> getOrdersContainingSweet(Long sweetId);

    /**
     * Convert Order entity to OrderResponse DTO
     * 
     * @param order the order entity
     * @return the order response DTO
     */
    OrderResponse convertToResponse(Order order);
}