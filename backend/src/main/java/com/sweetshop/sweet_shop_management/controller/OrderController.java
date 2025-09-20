package com.sweetshop.sweet_shop_management.controller;

import com.sweetshop.sweet_shop_management.dto.*;
import com.sweetshop.sweet_shop_management.model.Order;
import com.sweetshop.sweet_shop_management.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Order Management operations.
 * Provides endpoints for order processing, status management, and cart functionality.
 * 
 * @author Sweet Shop Management System
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for managing orders, cart functionality, and order processing")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {

    private final OrderService orderService;

    @Operation(
        summary = "Create a new order",
        description = "Creates a new order with the specified items. Validates stock availability and reduces inventory."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient stock",
                content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Sweet not found")
    })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Parameter(description = "Order creation request with customer details and items")
            @Valid @RequestBody OrderCreateRequest request) {
        log.info("Creating new order for customer: {}", request.getCustomerEmail());
        log.info("Order request details: {}", request);
        
        try {
            OrderResponse response = orderService.createOrder(request);
            log.info("Order created successfully with ID: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Failed to create order: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Operation(
        summary = "Get order by ID",
        description = "Retrieves a specific order by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id) {
        log.debug("Fetching order with ID: {}", id);
        
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all orders",
        description = "Retrieves all orders in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.debug("Fetching all orders");
        
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Update order details",
        description = "Updates order information. Only pending orders can be modified."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order updated successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data or order cannot be modified"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Order update request")
            @Valid @RequestBody OrderUpdateRequest request) {
        log.info("Updating order with ID: {}", id);
        
        OrderResponse response = orderService.updateOrder(id, request);
        log.info("Order updated successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update order status",
        description = "Updates the status of an order (e.g., PENDING -> CONFIRMED -> PREPARING -> READY -> DELIVERED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status updated successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid status or status transition"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Status update request")
            @Valid @RequestBody OrderStatusUpdateRequest request) {
        log.info("Updating status for order ID: {} to {}", id, request.getStatus());
        
        OrderResponse response = orderService.updateOrderStatus(id, request);
        log.info("Order status updated successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Cancel an order",
        description = "Cancels an order and restores stock. Only pending or confirmed orders can be cancelled."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order cancelled successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id) {
        log.info("Cancelling order with ID: {}", id);
        
        OrderResponse response = orderService.cancelOrder(id);
        log.info("Order cancelled successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete an order",
        description = "Permanently deletes an order. Only pending orders can be deleted."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Order cannot be deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id) {
        log.info("Deleting order with ID: {}", id);
        
        orderService.deleteOrder(id);
        log.info("Order deleted successfully");
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Add item to order",
        description = "Adds a new item to an existing order. Only pending orders can be modified."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item added successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or order cannot be modified"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Order or sweet not found")
    })
    @PostMapping("/{id}/items")
    public ResponseEntity<OrderResponse> addItemToOrder(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Item addition request")
            @Valid @RequestBody OrderItemAddRequest request) {
        log.info("Adding item to order ID: {}", id);
        
        OrderResponse response = orderService.addItemToOrder(id, request);
        log.info("Item added successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update order item",
        description = "Updates quantity or notes for an existing order item"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order item updated successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or insufficient stock"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Order or order item not found")
    })
    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderResponse> updateOrderItem(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long orderId,
            @Parameter(description = "Order item ID", example = "1")
            @PathVariable Long itemId,
            @Parameter(description = "Order item update request")
            @Valid @RequestBody OrderItemUpdateRequest request) {
        log.info("Updating order item ID: {} in order ID: {}", itemId, orderId);
        
        OrderResponse response = orderService.updateOrderItem(orderId, itemId, request);
        log.info("Order item updated successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Remove item from order",
        description = "Removes an item from an order and restores stock"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Item removed successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "400", description = "Order cannot be modified"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
        @ApiResponse(responseCode = "404", description = "Order or order item not found")
    })
    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderResponse> removeItemFromOrder(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long orderId,
            @Parameter(description = "Order item ID", example = "1")
            @PathVariable Long itemId) {
        log.info("Removing order item ID: {} from order ID: {}", itemId, orderId);
        
        OrderResponse response = orderService.removeItemFromOrder(orderId, itemId);
        log.info("Item removed successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get orders by customer email",
        description = "Retrieves all orders for a specific customer"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomerEmail(
            @Parameter(description = "Customer email address", example = "customer@example.com")
            @PathVariable String email) {
        log.debug("Fetching orders for customer: {}", email);
        
        List<OrderResponse> orders = orderService.getOrdersByCustomerEmail(email);
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Get orders by status",
        description = "Retrieves all orders with a specific status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(
            @Parameter(description = "Order status", example = "PENDING")
            @PathVariable Order.OrderStatus status) {
        log.debug("Fetching orders with status: {}", status);
        
        List<OrderResponse> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Get pending orders",
        description = "Retrieves all orders with PENDING status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending orders retrieved successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/pending")
    public ResponseEntity<List<OrderResponse>> getPendingOrders() {
        log.debug("Fetching pending orders");
        
        List<OrderResponse> orders = orderService.getPendingOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Get active orders",
        description = "Retrieves all orders that are currently being processed (CONFIRMED, PREPARING, READY, OUT_FOR_DELIVERY)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active orders retrieved successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/active")
    public ResponseEntity<List<OrderResponse>> getActiveOrders() {
        log.debug("Fetching active orders");
        
        List<OrderResponse> orders = orderService.getActiveOrders();
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Get recent orders",
        description = "Retrieves the most recent orders with an optional limit"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Recent orders retrieved successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/recent")
    public ResponseEntity<List<OrderResponse>> getRecentOrders(
            @Parameter(description = "Maximum number of orders to return", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("Fetching recent orders with limit: {}", limit);
        
        List<OrderResponse> orders = orderService.getRecentOrders(limit);
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Search orders by customer name",
        description = "Searches for orders by customer name (case-insensitive partial match)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/search")
    public ResponseEntity<List<OrderResponse>> searchOrdersByCustomerName(
            @Parameter(description = "Customer name to search for", example = "John")
            @RequestParam String customerName) {
        log.debug("Searching orders by customer name: {}", customerName);
        
        List<OrderResponse> orders = orderService.searchOrdersByCustomerName(customerName);
        return ResponseEntity.ok(orders);
    }

    @Operation(
        summary = "Get order statistics",
        description = "Retrieves order count by status for dashboard analytics"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/statistics")
    public ResponseEntity<OrderStatistics> getOrderStatistics() {
        log.debug("Fetching order statistics");
        
        OrderStatistics statistics = OrderStatistics.builder()
                .pending(orderService.countOrdersByStatus(Order.OrderStatus.PENDING))
                .confirmed(orderService.countOrdersByStatus(Order.OrderStatus.CONFIRMED))
                .preparing(orderService.countOrdersByStatus(Order.OrderStatus.PREPARING))
                .ready(orderService.countOrdersByStatus(Order.OrderStatus.READY))
                .outForDelivery(orderService.countOrdersByStatus(Order.OrderStatus.OUT_FOR_DELIVERY))
                .delivered(orderService.countOrdersByStatus(Order.OrderStatus.DELIVERED))
                .completed(orderService.countOrdersByStatus(Order.OrderStatus.COMPLETED))
                .cancelled(orderService.countOrdersByStatus(Order.OrderStatus.CANCELLED))
                .build();
        
        return ResponseEntity.ok(statistics);
    }

    @Operation(
        summary = "Get revenue for date range",
        description = "Calculates total revenue from completed orders within a specific date range"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Revenue calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid date range"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token")
    })
    @GetMapping("/revenue")
    public ResponseEntity<RevenueResponse> getRevenue(
            @Parameter(description = "Start date", example = "2024-01-01T00:00:00")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date", example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.debug("Calculating revenue between {} and {}", startDate, endDate);
        
        Double totalRevenue = orderService.getTotalRevenueBetween(startDate, endDate);
        
        RevenueResponse revenueResponse = RevenueResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalRevenue(totalRevenue != null ? totalRevenue : 0.0)
                .build();
        
        return ResponseEntity.ok(revenueResponse);
    }

    /**
     * DTO for order statistics response
     */
    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "Order statistics response")
    public static class OrderStatistics {
        @Schema(description = "Number of pending orders", example = "5")
        private long pending;
        
        @Schema(description = "Number of confirmed orders", example = "3")
        private long confirmed;
        
        @Schema(description = "Number of orders being prepared", example = "2")
        private long preparing;
        
        @Schema(description = "Number of ready orders", example = "1")
        private long ready;
        
        @Schema(description = "Number of orders out for delivery", example = "4")
        private long outForDelivery;
        
        @Schema(description = "Number of delivered orders", example = "10")
        private long delivered;
        
        @Schema(description = "Number of completed orders", example = "15")
        private long completed;
        
        @Schema(description = "Number of cancelled orders", example = "2")
        private long cancelled;
    }

    /**
     * DTO for revenue response
     */
    @Data
    @Builder
    @AllArgsConstructor
    @Schema(description = "Revenue calculation response")
    public static class RevenueResponse {
        @Schema(description = "Start date of the revenue calculation", example = "2024-01-01T00:00:00")
        private LocalDateTime startDate;
        
        @Schema(description = "End date of the revenue calculation", example = "2024-12-31T23:59:59")
        private LocalDateTime endDate;
        
        @Schema(description = "Total revenue in the specified period", example = "1250.75")
        private Double totalRevenue;
    }
}