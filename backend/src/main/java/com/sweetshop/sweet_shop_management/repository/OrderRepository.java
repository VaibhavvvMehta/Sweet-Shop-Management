package com.sweetshop.sweet_shop_management.repository;

import com.sweetshop.sweet_shop_management.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity operations.
 * Provides custom queries for order management and analysis.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find orders by customer email
     * 
     * @param customerEmail the customer's email address
     * @return list of orders for the customer
     */
    List<Order> findByCustomerEmailIgnoreCase(String customerEmail);

    /**
     * Find orders by customer name (case-insensitive, partial match)
     * 
     * @param customerName the customer's name
     * @return list of orders matching the customer name
     */
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);

    /**
     * Find orders by status
     * 
     * @param status the order status
     * @return list of orders with the specified status
     */
    List<Order> findByStatus(Order.OrderStatus status);

    /**
     * Find orders by status ordered by creation date (newest first)
     * 
     * @param status the order status
     * @return list of orders with the specified status ordered by creation date
     */
    List<Order> findByStatusOrderByCreatedAtDesc(Order.OrderStatus status);

    /**
     * Find orders created between two dates
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of orders created within the date range
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find orders created on or after a specific date
     * 
     * @param date the date to search from
     * @return list of orders created on or after the specified date
     */
    List<Order> findByCreatedAtGreaterThanEqual(LocalDateTime date);

    /**
     * Find recent orders (last N orders)
     * 
     * @return list of recent orders ordered by creation date descending
     */
    @Query("SELECT o FROM Order o ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders();

    /**
     * Find pending orders (orders that can still be modified)
     * 
     * @return list of pending orders
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' ORDER BY o.createdAt ASC")
    List<Order> findPendingOrders();

    /**
     * Find active orders (orders that are in progress)
     * 
     * @return list of active orders
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('CONFIRMED', 'PREPARING', 'READY', 'OUT_FOR_DELIVERY') ORDER BY o.createdAt ASC")
    List<Order> findActiveOrders();

    /**
     * Find completed orders for a specific date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of completed orders within the date range
     */
    @Query("SELECT o FROM Order o WHERE o.status IN ('DELIVERED', 'COMPLETED') AND o.completedAt BETWEEN :startDate AND :endDate ORDER BY o.completedAt DESC")
    List<Order> findCompletedOrdersBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Count orders by status
     * 
     * @param status the order status
     * @return count of orders with the specified status
     */
    long countByStatus(Order.OrderStatus status);

    /**
     * Count orders created today
     * 
     * @param startOfDay the start of the current day
     * @return count of orders created today
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :startOfDay")
    long countOrdersCreatedToday(@Param("startOfDay") LocalDateTime startOfDay);

    /**
     * Get total revenue for completed orders in a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return total revenue from completed orders
     */
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status IN ('DELIVERED', 'COMPLETED') AND o.completedAt BETWEEN :startDate AND :endDate")
    Optional<Double> getTotalRevenueBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find orders containing a specific sweet
     * 
     * @param sweetId the ID of the sweet
     * @return list of orders containing the specified sweet
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN o.orderItems oi WHERE oi.sweet.id = :sweetId ORDER BY o.createdAt DESC")
    List<Order> findOrdersContainingSweet(@Param("sweetId") Long sweetId);

    /**
     * Find orders by customer email and status
     * 
     * @param customerEmail the customer's email
     * @param status the order status
     * @return list of orders matching the criteria
     */
    List<Order> findByCustomerEmailIgnoreCaseAndStatus(String customerEmail, Order.OrderStatus status);

    /**
     * Find orders with total amount greater than specified value
     * 
     * @param minAmount the minimum total amount
     * @return list of orders with total amount greater than the specified value
     */
    List<Order> findByTotalAmountGreaterThan(Double minAmount);

    /**
     * Check if an order exists by customer email and status
     * 
     * @param customerEmail the customer's email
     * @param status the order status
     * @return true if an order exists with the specified criteria
     */
    boolean existsByCustomerEmailIgnoreCaseAndStatus(String customerEmail, Order.OrderStatus status);

    /**
     * Find the most recent order for a customer
     * 
     * @param customerEmail the customer's email
     * @return the most recent order for the customer
     */
    @Query("SELECT o FROM Order o WHERE o.customerEmail = :customerEmail ORDER BY o.createdAt DESC")
    Optional<Order> findMostRecentOrderByCustomer(@Param("customerEmail") String customerEmail);

    /**
     * Get order statistics for a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of objects containing order statistics
     */
    @Query("SELECT o.status, COUNT(o), SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate GROUP BY o.status")
    List<Object[]> getOrderStatisticsBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}