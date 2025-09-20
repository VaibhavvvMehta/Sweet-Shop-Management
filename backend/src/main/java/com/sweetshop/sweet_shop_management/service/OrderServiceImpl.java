package com.sweetshop.sweet_shop_management.service;

import com.sweetshop.sweet_shop_management.dto.*;
import com.sweetshop.sweet_shop_management.model.Order;
import com.sweetshop.sweet_shop_management.model.OrderItem;
import com.sweetshop.sweet_shop_management.model.Sweet;
import com.sweetshop.sweet_shop_management.repository.OrderItemRepository;
import com.sweetshop.sweet_shop_management.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of OrderService interface.
 * Provides business logic for order management operations.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final SweetService sweetService;

    @Override
    public OrderResponse createOrder(OrderCreateRequest request) {
        log.info("Creating new order for customer: {}", request.getCustomerEmail());
        
        // Validate request
        if (request == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }
        
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
        
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        
        if (request.getCustomerEmail() == null || request.getCustomerEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer email is required");
        }
        
        // Validate and check stock for all items first
        for (OrderCreateRequest.OrderItemCreateRequest itemRequest : request.getItems()) {
            if (itemRequest.getSweetId() == null) {
                throw new IllegalArgumentException("Sweet ID cannot be null");
            }
            
            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("Invalid quantity: " + itemRequest.getQuantity());
            }
            
            Sweet sweet = sweetService.findById(itemRequest.getSweetId())
                    .orElseThrow(() -> new IllegalArgumentException("Sweet not found with ID: " + itemRequest.getSweetId()));
            
            if (!sweet.getIsAvailable()) {
                throw new IllegalStateException("Sweet '" + sweet.getName() + "' is not available for purchase");
            }
            
            if (sweet.getQuantity() < itemRequest.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for sweet '" + sweet.getName() + 
                        "'. Available: " + sweet.getQuantity() + ", Requested: " + itemRequest.getQuantity());
            }
        }

        // Create the order
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setNotes(request.getNotes());
        order.setStatus(Order.OrderStatus.PENDING);

        // Save order first to get ID
        order = orderRepository.save(order);

        // Add order items and reduce stock
        for (OrderCreateRequest.OrderItemCreateRequest itemRequest : request.getItems()) {
            Sweet sweet = sweetService.findById(itemRequest.getSweetId())
                    .orElseThrow(() -> new IllegalArgumentException("Sweet not found with ID: " + itemRequest.getSweetId()));
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setSweet(sweet);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(sweet.getPrice());
            orderItem.setNotes(itemRequest.getNotes());
            orderItem.calculateSubtotal();
            
            order.addOrderItem(orderItem);
            
            // Reduce stock
            sweetService.reduceStock(sweet.getId(), itemRequest.getQuantity());
        }

        // Recalculate total and save
        order.recalculateTotal();
        order = orderRepository.save(order);

        log.info("Order created successfully with ID: {}", order.getId());
        return convertToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        log.debug("Fetching order with ID: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        
        return convertToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.debug("Fetching all orders");
        
        return orderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderUpdateRequest request) {
        log.info("Updating order with ID: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        
        if (!order.canBeModified()) {
            throw new IllegalStateException("Order with status '" + order.getStatus() + "' cannot be modified");
        }

        // Update fields if provided
        if (request.getCustomerName() != null) {
            order.setCustomerName(request.getCustomerName());
        }
        if (request.getCustomerEmail() != null) {
            order.setCustomerEmail(request.getCustomerEmail());
        }
        if (request.getCustomerPhone() != null) {
            order.setCustomerPhone(request.getCustomerPhone());
        }
        if (request.getDeliveryAddress() != null) {
            order.setDeliveryAddress(request.getDeliveryAddress());
        }
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }

        order = orderRepository.save(order);
        log.info("Order updated successfully");
        
        return convertToResponse(order);
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        log.info("Updating status for order ID: {} to {}", id, request.getStatus());
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        order.setStatus(request.getStatus());
        if (request.getNotes() != null) {
            order.setNotes(request.getNotes());
        }

        order = orderRepository.save(order);
        log.info("Order status updated successfully");
        
        return convertToResponse(order);
    }

    @Override
    public OrderResponse cancelOrder(Long id) {
        log.info("Cancelling order with ID: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        
        if (!order.canBeCancelled()) {
            throw new IllegalStateException("Order with status '" + order.getStatus() + "' cannot be cancelled");
        }

        // Restore stock for all items
        for (OrderItem item : order.getOrderItems()) {
            sweetService.increaseStock(item.getSweet().getId(), item.getQuantity());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        
        log.info("Order cancelled successfully");
        return convertToResponse(order);
    }

    @Override
    public void deleteOrder(Long id) {
        log.info("Deleting order with ID: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Only pending orders can be deleted");
        }

        // Restore stock for all items
        for (OrderItem item : order.getOrderItems()) {
            sweetService.increaseStock(item.getSweet().getId(), item.getQuantity());
        }

        orderRepository.delete(order);
        log.info("Order deleted successfully");
    }

    @Override
    public OrderResponse addItemToOrder(Long orderId, OrderItemAddRequest request) {
        log.info("Adding item to order ID: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        if (!order.canBeModified()) {
            throw new IllegalStateException("Order with status '" + order.getStatus() + "' cannot be modified");
        }

        Sweet sweet = sweetService.findById(request.getSweetId())
                .orElseThrow(() -> new IllegalArgumentException("Sweet not found with ID: " + request.getSweetId()));
        
        if (!sweet.getIsAvailable()) {
            throw new IllegalStateException("Sweet '" + sweet.getName() + "' is not available for purchase");
        }
        
        if (sweet.getQuantity() < request.getQuantity()) {
            throw new IllegalStateException("Insufficient stock for sweet '" + sweet.getName() + 
                    "'. Available: " + sweet.getQuantity() + ", Requested: " + request.getQuantity());
        }

        // Check if item already exists in order
        OrderItem existingItem = order.getOrderItems().stream()
                .filter(item -> item.getSweet().getId().equals(request.getSweetId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Update existing item quantity
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            existingItem.setQuantity(newQuantity);
            existingItem.calculateSubtotal();
        } else {
            // Create new order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setSweet(sweet);
            orderItem.setQuantity(request.getQuantity());
            orderItem.setUnitPrice(sweet.getPrice());
            orderItem.setNotes(request.getNotes());
            orderItem.calculateSubtotal();
            
            order.addOrderItem(orderItem);
        }

        // Reduce stock
        sweetService.reduceStock(sweet.getId(), request.getQuantity());

        // Recalculate total and save
        order.recalculateTotal();
        order = orderRepository.save(order);

        log.info("Item added to order successfully");
        return convertToResponse(order);
    }

    @Override
    public OrderResponse updateOrderItem(Long orderId, Long itemId, OrderItemUpdateRequest request) {
        log.info("Updating order item ID: {} in order ID: {}", itemId, orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        if (!order.canBeModified()) {
            throw new IllegalStateException("Order with status '" + order.getStatus() + "' cannot be modified");
        }

        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + itemId));
        
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Order item does not belong to the specified order");
        }

        // Restore previous stock and reduce new stock
        int originalQuantity = orderItem.getQuantity();
        sweetService.increaseStock(orderItem.getSweet().getId(), originalQuantity);
        
        // Check new stock availability
        Sweet sweet = orderItem.getSweet();
        if (sweet.getQuantity() < request.getQuantity()) {
            // Restore original stock and throw exception
            sweetService.reduceStock(sweet.getId(), originalQuantity);
            throw new IllegalStateException("Insufficient stock for sweet '" + sweet.getName() + 
                    "'. Available: " + sweet.getQuantity() + ", Requested: " + request.getQuantity());
        }

        // Update item
        orderItem.setQuantity(request.getQuantity());
        if (request.getNotes() != null) {
            orderItem.setNotes(request.getNotes());
        }
        orderItem.calculateSubtotal();

        // Reduce new stock
        sweetService.reduceStock(sweet.getId(), request.getQuantity());

        // Recalculate total and save
        order.recalculateTotal();
        order = orderRepository.save(order);

        log.info("Order item updated successfully");
        return convertToResponse(order);
    }

    @Override
    public OrderResponse removeItemFromOrder(Long orderId, Long itemId) {
        log.info("Removing order item ID: {} from order ID: {}", itemId, orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        if (!order.canBeModified()) {
            throw new IllegalStateException("Order with status '" + order.getStatus() + "' cannot be modified");
        }

        OrderItem orderItem = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found with ID: " + itemId));
        
        if (!orderItem.getOrder().getId().equals(orderId)) {
            throw new IllegalArgumentException("Order item does not belong to the specified order");
        }

        // Restore stock
        sweetService.increaseStock(orderItem.getSweet().getId(), orderItem.getQuantity());

        // Remove item from order
        order.removeOrderItem(orderItem);
        orderItemRepository.delete(orderItem);

        // Recalculate total and save
        order.recalculateTotal();
        order = orderRepository.save(order);

        log.info("Order item removed successfully");
        return convertToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomerEmail(String customerEmail) {
        log.debug("Fetching orders for customer: {}", customerEmail);
        
        return orderRepository.findByCustomerEmailIgnoreCase(customerEmail).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(Order.OrderStatus status) {
        log.debug("Fetching orders with status: {}", status);
        
        return orderRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getPendingOrders() {
        log.debug("Fetching pending orders");
        
        return orderRepository.findPendingOrders().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrders() {
        log.debug("Fetching active orders");
        
        return orderRepository.findActiveOrders().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getCompletedOrdersBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching completed orders between {} and {}", startDate, endDate);
        
        return orderRepository.findCompletedOrdersBetween(startDate, endDate).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getRecentOrders(int limit) {
        log.debug("Fetching recent orders with limit: {}", limit);
        
        return orderRepository.findRecentOrders().stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> searchOrdersByCustomerName(String customerName) {
        log.debug("Searching orders by customer name: {}", customerName);
        
        return orderRepository.findByCustomerNameContainingIgnoreCase(customerName).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean orderExists(Long id) {
        return orderRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalRevenueBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getTotalRevenueBetween(startDate, endDate)
                .orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersContainingSweet(Long sweetId) {
        log.debug("Fetching orders containing sweet ID: {}", sweetId);
        
        return orderRepository.findOrdersContainingSweet(sweetId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse convertToResponse(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setCompletedAt(order.getCompletedAt());
        response.setNotes(order.getNotes());
        response.setTotalQuantity(order.getTotalQuantity());

        // Convert order items
        if (order.getOrderItems() != null) {
            List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                    .map(this::convertOrderItemToResponse)
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }

        return response;
    }

    private OrderResponse.OrderItemResponse convertOrderItemToResponse(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        OrderResponse.OrderItemResponse response = new OrderResponse.OrderItemResponse();
        response.setId(orderItem.getId());
        response.setSweetId(orderItem.getSweet().getId());
        response.setSweetName(orderItem.getSweetName());
        response.setSweetCategory(orderItem.getSweetCategory() != null ? 
                orderItem.getSweetCategory().toString() : null);
        response.setQuantity(orderItem.getQuantity());
        response.setUnitPrice(orderItem.getUnitPrice());
        response.setSubtotal(orderItem.getSubtotal());
        response.setNotes(orderItem.getNotes());
        response.setCreatedAt(orderItem.getCreatedAt());

        return response;
    }
}