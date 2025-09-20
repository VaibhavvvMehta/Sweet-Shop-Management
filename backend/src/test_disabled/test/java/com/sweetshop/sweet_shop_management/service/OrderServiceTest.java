package com.sweetshop.sweet_shop_management.service;

import com.sweetshop.sweet_shop_management.dto.*;
import com.sweetshop.sweet_shop_management.model.Order;
import com.sweetshop.sweet_shop_management.model.OrderItem;
import com.sweetshop.sweet_shop_management.model.Sweet;
import com.sweetshop.sweet_shop_management.repository.OrderItemRepository;
import com.sweetshop.sweet_shop_management.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private SweetService sweetService;

    private OrderService orderService;

    private Order testOrder;
    private Sweet testSweet;
    private OrderItem testOrderItem;
    private OrderCreateRequest testCreateRequest;
    private OrderUpdateRequest testUpdateRequest;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, orderItemRepository, sweetService);

        // Setup test entities
        testSweet = new Sweet();
        testSweet.setId(1L);
        testSweet.setName("Test Chocolate");
        testSweet.setPrice(new BigDecimal("10.99"));
        testSweet.setQuantity(100);
        testSweet.setIsAvailable(true);
        testSweet.setCategory(Sweet.SweetCategory.CHOCOLATE);

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setSweet(testSweet);
        testOrderItem.setQuantity(2);
        testOrderItem.setUnitPrice(new BigDecimal("10.99"));
        testOrderItem.setSubtotal(new BigDecimal("21.98"));

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerName("John Doe");
        testOrder.setCustomerEmail("john@example.com");
        testOrder.setCustomerPhone("1234567890");
        testOrder.setDeliveryAddress("123 Main St");
        testOrder.setStatus(Order.OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("21.98"));
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setUpdatedAt(LocalDateTime.now());
        testOrder.setOrderItems(new ArrayList<>(Arrays.asList(testOrderItem)));
        testOrderItem.setOrder(testOrder);

        // Setup test DTOs
        OrderCreateRequest.OrderItemCreateRequest itemRequest = new OrderCreateRequest.OrderItemCreateRequest();
        itemRequest.setSweetId(1L);
        itemRequest.setQuantity(2);
        itemRequest.setNotes("No nuts please");

        testCreateRequest = new OrderCreateRequest();
        testCreateRequest.setCustomerName("John Doe");
        testCreateRequest.setCustomerEmail("john@example.com");
        testCreateRequest.setCustomerPhone("1234567890");
        testCreateRequest.setDeliveryAddress("123 Main St");
        testCreateRequest.setNotes("Urgent delivery");
        testCreateRequest.setItems(Arrays.asList(itemRequest));

        testUpdateRequest = new OrderUpdateRequest();
        testUpdateRequest.setCustomerName("John Smith");
        testUpdateRequest.setCustomerPhone("0987654321");
        testUpdateRequest.setDeliveryAddress("456 Oak St");
    }

    @Test
    @DisplayName("Should create order successfully")
    void shouldCreateOrderSuccessfully() {
        // Given
        when(sweetService.findById(1L)).thenReturn(Optional.of(testSweet));
        when(sweetService.reduceStock(1L, 2)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderResponse result = orderService.createOrder(testCreateRequest);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getCustomerName());
        assertEquals("john@example.com", result.getCustomerEmail());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        assertEquals(1, result.getItems().size());
        
        verify(sweetService).findById(1L);
        verify(sweetService).reduceStock(1L, 2);
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when creating order with unavailable sweet")
    void shouldThrowExceptionWhenCreatingOrderWithUnavailableSweet() {
        // Given
        testSweet.setIsAvailable(false);
        when(sweetService.findById(1L)).thenReturn(Optional.of(testSweet));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(testCreateRequest));
        assertTrue(exception.getMessage().contains("not available"));
        
        verify(sweetService).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when creating order with insufficient stock")
    void shouldThrowExceptionWhenCreatingOrderWithInsufficientStock() {
        // Given
        testSweet.setQuantity(1); // Less than requested quantity of 2
        when(sweetService.findById(1L)).thenReturn(Optional.of(testSweet));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(testCreateRequest));
        assertTrue(exception.getMessage().contains("insufficient stock"));
        
        verify(sweetService).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get order by ID successfully")
    void shouldGetOrderByIdSuccessfully() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        OrderResponse result = orderService.getOrderById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getCustomerName());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void shouldThrowExceptionWhenOrderNotFound() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.getOrderById(1L));
        assertTrue(exception.getMessage().contains("Order not found"));
        
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get all orders successfully")
    void shouldGetAllOrdersSuccessfully() {
        // Given
        when(orderRepository.findAll()).thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderResponse> result = orderService.getAllOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getCustomerName());
        
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("Should update order successfully when pending")
    void shouldUpdateOrderSuccessfullyWhenPending() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderResponse result = orderService.updateOrder(1L, testUpdateRequest);

        // Then
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-pending order")
    void shouldThrowExceptionWhenUpdatingNonPendingOrder() {
        // Given
        testOrder.setStatus(Order.OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.updateOrder(1L, testUpdateRequest));
        assertTrue(exception.getMessage().contains("cannot be modified"));
        
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update order status successfully")
    void shouldUpdateOrderStatusSuccessfully() {
        // Given
        OrderStatusUpdateRequest statusRequest = new OrderStatusUpdateRequest();
        statusRequest.setStatus(Order.OrderStatus.CONFIRMED);
        statusRequest.setNotes("Order confirmed");
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderResponse result = orderService.updateOrderStatus(1L, statusRequest);

        // Then
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should cancel order successfully when cancellable")
    void shouldCancelOrderSuccessfullyWhenCancellable() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderResponse result = orderService.cancelOrder(1L);

        // Then
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(sweetService).increaseStock(1L, 2); // Stock should be restored
    }

    @Test
    @DisplayName("Should throw exception when cancelling non-cancellable order")
    void shouldThrowExceptionWhenCancellingNonCancellableOrder() {
        // Given
        testOrder.setStatus(Order.OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.cancelOrder(1L));
        assertTrue(exception.getMessage().contains("cannot be cancelled"));
        
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete order successfully when pending")
    void shouldDeleteOrderSuccessfullyWhenPending() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        orderService.deleteOrder(1L);

        // Then
        verify(orderRepository).findById(1L);
        verify(orderRepository).delete(testOrder);
        verify(sweetService).increaseStock(1L, 2); // Stock should be restored
    }

    @Test
    @DisplayName("Should throw exception when deleting non-pending order")
    void shouldThrowExceptionWhenDeletingNonPendingOrder() {
        // Given
        testOrder.setStatus(Order.OrderStatus.CONFIRMED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.deleteOrder(1L));
        assertTrue(exception.getMessage().contains("cannot be deleted"));
        
        verify(orderRepository).findById(1L);
        verify(orderRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should add item to order successfully")
    void shouldAddItemToOrderSuccessfully() {
        // Given
        OrderItemAddRequest addRequest = new OrderItemAddRequest();
        addRequest.setSweetId(2L);
        addRequest.setQuantity(1);
        addRequest.setNotes("Extra sweet");

        Sweet newSweet = new Sweet();
        newSweet.setId(2L);
        newSweet.setName("Gummy Bear");
        newSweet.setPrice(new BigDecimal("5.99"));
        newSweet.setQuantity(50);
        newSweet.setIsAvailable(true);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(sweetService.findById(2L)).thenReturn(Optional.of(newSweet));
        when(sweetService.reduceStock(2L, 1)).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderResponse result = orderService.addItemToOrder(1L, addRequest);

        // Then
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(sweetService).findById(2L);
        verify(sweetService).reduceStock(2L, 1);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should update order item successfully")
    void shouldUpdateOrderItemSuccessfully() {
        // Given
        OrderItemUpdateRequest updateRequest = new OrderItemUpdateRequest();
        updateRequest.setQuantity(3);
        updateRequest.setNotes("Updated quantity");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderResponse result = orderService.updateOrderItem(1L, 1L, updateRequest);

        // Then
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(orderItemRepository).findById(1L);
        verify(sweetService).increaseStock(1L, 2); // Original quantity restored
        verify(sweetService).reduceStock(1L, 3); // New quantity reduced
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should remove item from order successfully")
    void shouldRemoveItemFromOrderSuccessfully() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderItemRepository.findById(1L)).thenReturn(Optional.of(testOrderItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderResponse result = orderService.removeItemFromOrder(1L, 1L);

        // Then
        assertNotNull(result);
        verify(orderRepository).findById(1L);
        verify(orderItemRepository).findById(1L);
        verify(sweetService).increaseStock(1L, 2); // Stock restored
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("Should get orders by customer email")
    void shouldGetOrdersByCustomerEmail() {
        // Given
        when(orderRepository.findByCustomerEmailIgnoreCase("john@example.com"))
                .thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderResponse> result = orderService.getOrdersByCustomerEmail("john@example.com");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john@example.com", result.get(0).getCustomerEmail());
        
        verify(orderRepository).findByCustomerEmailIgnoreCase("john@example.com");
    }

    @Test
    @DisplayName("Should get orders by status")
    void shouldGetOrdersByStatus() {
        // Given
        when(orderRepository.findByStatus(Order.OrderStatus.PENDING))
                .thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderResponse> result = orderService.getOrdersByStatus(Order.OrderStatus.PENDING);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Order.OrderStatus.PENDING, result.get(0).getStatus());
        
        verify(orderRepository).findByStatus(Order.OrderStatus.PENDING);
    }

    @Test
    @DisplayName("Should get pending orders")
    void shouldGetPendingOrders() {
        // Given
        when(orderRepository.findPendingOrders()).thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderResponse> result = orderService.getPendingOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Order.OrderStatus.PENDING, result.get(0).getStatus());
        
        verify(orderRepository).findPendingOrders();
    }

    @Test
    @DisplayName("Should get active orders")
    void shouldGetActiveOrders() {
        // Given
        testOrder.setStatus(Order.OrderStatus.CONFIRMED);
        when(orderRepository.findActiveOrders()).thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderResponse> result = orderService.getActiveOrders();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Order.OrderStatus.CONFIRMED, result.get(0).getStatus());
        
        verify(orderRepository).findActiveOrders();
    }

    @Test
    @DisplayName("Should get completed orders between dates")
    void shouldGetCompletedOrdersBetweenDates() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        testOrder.setStatus(Order.OrderStatus.COMPLETED);
        
        when(orderRepository.findCompletedOrdersBetween(startDate, endDate))
                .thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderResponse> result = orderService.getCompletedOrdersBetween(startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Order.OrderStatus.COMPLETED, result.get(0).getStatus());
        
        verify(orderRepository).findCompletedOrdersBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Should get recent orders with limit")
    void shouldGetRecentOrdersWithLimit() {
        // Given
        when(orderRepository.findRecentOrders()).thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderResponse> result = orderService.getRecentOrders(10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(orderRepository).findRecentOrders();
    }

    @Test
    @DisplayName("Should search orders by customer name")
    void shouldSearchOrdersByCustomerName() {
        // Given
        when(orderRepository.findByCustomerNameContainingIgnoreCase("John"))
                .thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderResponse> result = orderService.searchOrdersByCustomerName("John");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getCustomerName().contains("John"));
        
        verify(orderRepository).findByCustomerNameContainingIgnoreCase("John");
    }

    @Test
    @DisplayName("Should check if order exists")
    void shouldCheckIfOrderExists() {
        // Given
        when(orderRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = orderService.orderExists(1L);

        // Then
        assertTrue(result);
        verify(orderRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should count orders by status")
    void shouldCountOrdersByStatus() {
        // Given
        when(orderRepository.countByStatus(Order.OrderStatus.PENDING)).thenReturn(5L);

        // When
        long result = orderService.countOrdersByStatus(Order.OrderStatus.PENDING);

        // Then
        assertEquals(5L, result);
        verify(orderRepository).countByStatus(Order.OrderStatus.PENDING);
    }

    @Test
    @DisplayName("Should get total revenue between dates")
    void shouldGetTotalRevenueBetweenDates() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(orderRepository.getTotalRevenueBetween(startDate, endDate))
                .thenReturn(Optional.of(1000.50));

        // When
        Double result = orderService.getTotalRevenueBetween(startDate, endDate);

        // Then
        assertEquals(1000.50, result);
        verify(orderRepository).getTotalRevenueBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Should get orders containing sweet")
    void shouldGetOrdersContainingSweet() {
        // Given
        when(orderRepository.findOrdersContainingSweet(1L)).thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderResponse> result = orderService.getOrdersContainingSweet(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        verify(orderRepository).findOrdersContainingSweet(1L);
    }

    @Test
    @DisplayName("Should convert order to response DTO")
    void shouldConvertOrderToResponseDto() {
        // When
        OrderResponse result = orderService.convertToResponse(testOrder);

        // Then
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        assertEquals(testOrder.getCustomerName(), result.getCustomerName());
        assertEquals(testOrder.getCustomerEmail(), result.getCustomerEmail());
        assertEquals(testOrder.getStatus(), result.getStatus());
        assertEquals(testOrder.getTotalAmount(), result.getTotalAmount());
        assertEquals(testOrder.getOrderItems().size(), result.getItems().size());
    }
}