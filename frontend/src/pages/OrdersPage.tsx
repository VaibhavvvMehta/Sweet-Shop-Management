import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Card,
  CardContent,
  Button,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  CircularProgress,
  Alert,
  TextField,
  MenuItem,
  Fab,
  Collapse,
  IconButton,
} from '@mui/material';
import {
  Add,
  Visibility,
  Receipt,
  LocalShipping,
  CheckCircle,
  Cancel,
  ExpandMore,
  ExpandLess,
  Refresh,
  Check,
  Close,
  Pending,
  Done,
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { orderService } from '../services/orderService';
import { Order, OrderStatus } from '../types';
import { formatPrice } from '../utils/currency';

const OrdersPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { user } = useAuth();
  const { items: cartItems, totalAmount, clearCart } = useCart();
  const queryClient = useQueryClient();

  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [detailsDialog, setDetailsDialog] = useState(false);
  const [statusFilter, setStatusFilter] = useState<OrderStatus | 'ALL'>('ALL');
  const [expandedCards, setExpandedCards] = useState<Set<number>>(new Set());
  const [orderFormDialog, setOrderFormDialog] = useState(false);
  const [orderFormData, setOrderFormData] = useState({
    customerPhone: '',
    deliveryAddress: '',
    notes: ''
  });

  // Check if user is admin
  const isAdmin = user?.role === 'ADMIN';

  // Check if navigated from cart
  const isFromCart = location.state?.fromCart;

  // Fetch orders based on user role
  const {
    data: orders = [],
    isLoading,
    error,
    refetch,
  } = useQuery({
    queryKey: ['orders', user?.id, user?.role, statusFilter],
    queryFn: () => {
      if (isAdmin) {
        // Admin sees all orders
        if (statusFilter === 'ALL') {
          return orderService.getAllOrders();
        }
        return orderService.getOrdersByStatus(statusFilter);
      } else {
        // Regular users see only their orders
        if (statusFilter === 'ALL') {
          return orderService.getOrdersByCustomer(user!.email);
        }
        return orderService.getOrdersByStatus(statusFilter);
      }
    },
    enabled: !!user,
  });

  // Create order mutation (only for regular users)
  const createOrderMutation = useMutation({
    mutationFn: orderService.createOrder,
    onSuccess: (newOrder) => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      clearCart();
      setOrderFormData({ customerPhone: '', deliveryAddress: '', notes: '' }); // Reset form
      setSelectedOrder(newOrder);
      setDetailsDialog(true);
      alert('Order placed successfully! Order ID: ' + newOrder.id);
    },
    onError: (error: any) => {
      console.error('Error creating order:', error);
      console.error('Error details:', error.response?.data);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to create order. Please try again.';
      alert('Failed to create order: ' + errorMessage);
    },
  });

  // Cancel order mutation
  const cancelOrderMutation = useMutation({
    mutationFn: orderService.cancelOrder,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      setDetailsDialog(false);
    },
  });

  // Update order status mutation (for admin)
  const updateOrderStatusMutation = useMutation({
    mutationFn: ({ orderId, status }: { orderId: number; status: OrderStatus }) =>
      orderService.updateOrderStatus(orderId, { status }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['orders'] });
      alert('Order status updated successfully!');
    },
    onError: (error: any) => {
      console.error('Error updating order status:', error);
      alert('Failed to update order status: ' + (error.response?.data?.message || error.message));
    },
  });

  const handleCreateOrder = () => {
    // Admin users cannot place orders
    if (isAdmin) {
      alert('Admin users cannot place orders. Admin can only manage existing orders.');
      return;
    }

    if (cartItems.length === 0) {
      alert('Your cart is empty!');
      return;
    }
    
    // Validate cart items have required data
    for (const item of cartItems) {
      if (!item.sweet || !item.sweet.id || !item.quantity || item.quantity <= 0) {
        alert('Invalid item in cart. Please refresh and try again.');
        return;
      }
    }
    
    // Open form dialog to collect customer details
    setOrderFormDialog(true);
  };

  const handleSubmitOrder = () => {
    // Check if user is logged in
    if (!user) {
      alert('You must be logged in to place an order');
      return;
    }

    // Validate form data
    if (!orderFormData.customerPhone.trim()) {
      alert('Phone number is required');
      return;
    }
    if (!orderFormData.deliveryAddress.trim()) {
      alert('Delivery address is required');
      return;
    }

    const orderData = {
      customerName: user.fullName,
      customerEmail: user.email,
      customerPhone: orderFormData.customerPhone.trim(),
      deliveryAddress: orderFormData.deliveryAddress.trim(),
      notes: orderFormData.notes.trim(),
      items: cartItems.map(item => ({
        sweetId: item.sweet.id,
        quantity: item.quantity,
        notes: item.notes || '',
      })),
    };

    console.log('Creating order with data:', orderData);
    console.log('Cart items:', cartItems);
    console.log('User:', user);
    
    // Validate order data before sending
    if (!orderData.items || orderData.items.length === 0) {
      alert('No items to order');
      return;
    }
    
    for (const item of orderData.items) {
      if (!item.sweetId || !item.quantity || item.quantity <= 0) {
        alert('Invalid order item data');
        return;
      }
    }

    createOrderMutation.mutate(orderData);
    setOrderFormDialog(false);
  };

  const handleViewDetails = (order: Order) => {
    setSelectedOrder(order);
    setDetailsDialog(true);
  };

  const handleCancelOrder = (orderId: number) => {
    if (window.confirm('Are you sure you want to cancel this order?')) {
      cancelOrderMutation.mutate(orderId);
    }
  };

  const handleUpdateOrderStatus = (orderId: number, newStatus: OrderStatus) => {
    if (window.confirm(`Are you sure you want to update this order status to ${newStatus}?`)) {
      updateOrderStatusMutation.mutate({ orderId, status: newStatus });
    }
  };

  const toggleCardExpansion = (orderId: number) => {
    const newExpanded = new Set(expandedCards);
    if (newExpanded.has(orderId)) {
      newExpanded.delete(orderId);
    } else {
      newExpanded.add(orderId);
    }
    setExpandedCards(newExpanded);
  };

  const getStatusIcon = (status: OrderStatus) => {
    switch (status) {
      case 'PENDING':
        return <Receipt color="warning" />;
      case 'CONFIRMED':
        return <CheckCircle color="info" />;
      case 'PREPARING':
        return <LocalShipping color="primary" />;
      case 'READY':
        return <CheckCircle color="success" />;
      case 'DELIVERED':
        return <CheckCircle color="success" />;
      case 'CANCELLED':
        return <Cancel color="error" />;
      default:
        return <Receipt />;
    }
  };

  const getStatusColor = (status: OrderStatus) => {
    switch (status) {
      case 'PENDING':
        return 'warning';
      case 'CONFIRMED':
        return 'info';
      case 'PREPARING':
        return 'primary';
      case 'READY':
        return 'success';
      case 'DELIVERED':
        return 'success';
      case 'CANCELLED':
        return 'error';
      default:
        return 'default';
    }
  };

  const filteredOrders = statusFilter === 'ALL' 
    ? orders 
    : orders.filter((order: Order) => order.status === statusFilter);

  if (isLoading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4, textAlign: 'center' }}>
        <CircularProgress size={60} />
        <Typography variant="h6" sx={{ mt: 2 }}>
          Loading your orders...
        </Typography>
      </Container>
    );
  }

  if (error) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Alert severity="error" action={
          <Button color="inherit" size="small" onClick={() => refetch()}>
            Retry
          </Button>
        }>
          Failed to load orders. Please try again.
        </Alert>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
        <Typography variant="h4" component="h1" fontWeight="bold">
          {isAdmin ? 'Order Management' : 'My Orders'}
        </Typography>
        <Box display="flex" gap={2}>
          <IconButton onClick={() => refetch()} disabled={isLoading}>
            <Refresh />
          </IconButton>
          <TextField
            select
            size="small"
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value as OrderStatus | 'ALL')}
            sx={{ minWidth: 120 }}
          >
            <MenuItem value="ALL">All Orders</MenuItem>
            <MenuItem value="PENDING">Pending</MenuItem>
            <MenuItem value="CONFIRMED">Confirmed</MenuItem>
            <MenuItem value="PREPARING">Preparing</MenuItem>
            <MenuItem value="READY">Ready</MenuItem>
            <MenuItem value="DELIVERED">Delivered</MenuItem>
            <MenuItem value="CANCELLED">Cancelled</MenuItem>
          </TextField>
        </Box>
      </Box>

      {/* Create Order from Cart - Only for regular users */}
      {!isAdmin && isFromCart && cartItems.length > 0 && (
        <Paper sx={{ p: 3, mb: 3, bgcolor: 'primary.50' }}>
          <Typography variant="h6" gutterBottom>
            🛒 Ready to Place Your Order?
          </Typography>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            You have {cartItems.length} items in your cart totaling {formatPrice(totalAmount)}.
          </Typography>
          <Button
            variant="contained"
            onClick={handleCreateOrder}
            disabled={createOrderMutation.isPending}
            sx={{ mt: 2 }}
          >
            {createOrderMutation.isPending ? 'Creating Order...' : 'Place Order'}
          </Button>
        </Paper>
      )}

      {/* Orders List */}
      {filteredOrders.length === 0 ? (
        <Paper sx={{ p: 6, textAlign: 'center' }}>
          <Typography variant="h5" gutterBottom>
            {statusFilter === 'ALL' ? 'No orders yet' : `No ${statusFilter.toLowerCase()} orders`}
          </Typography>
          <Typography variant="body1" color="text.secondary" gutterBottom>
            {statusFilter === 'ALL' 
              ? "You haven't placed any orders yet. Start shopping to create your first order!"
              : `You don't have any orders with ${statusFilter.toLowerCase()} status.`
            }
          </Typography>
          <Button
            variant="contained"
            onClick={() => navigate('/products')}
            sx={{ mt: 2 }}
          >
            Browse Products
          </Button>
        </Paper>
      ) : (
        <Box display="flex" flexDirection="column" gap={3}>
          {filteredOrders.map((order: Order) => (
            <Box key={order.id}>
              <Card>
                <CardContent>
                  <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                    <Box>
                      <Typography variant="h6" gutterBottom>
                        Order #{order.id}
                      </Typography>
                      {isAdmin && (
                        <Typography variant="body2" color="primary" gutterBottom>
                          Customer: {order.customerName} ({order.customerEmail})
                        </Typography>
                      )}
                      <Typography variant="body2" color="text.secondary">
                        {new Date(order.createdAt).toLocaleDateString('en-US', {
                          year: 'numeric',
                          month: 'long',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit',
                        })}
                      </Typography>
                    </Box>
                    <Box display="flex" alignItems="center" gap={1}>
                      <Chip
                        icon={getStatusIcon(order.status)}
                        label={order.status}
                        color={getStatusColor(order.status) as any}
                        variant="outlined"
                      />
                      <Typography variant="h6" color="primary" fontWeight="bold">
                        {formatPrice(order.totalAmount)}
                      </Typography>
                    </Box>
                  </Box>

                  {/* Order Items Summary */}
                  <Box mb={2}>
                    <Typography variant="body2" color="text.secondary">
                      {order.items.length} items • Total: {formatPrice(order.totalAmount)}
                    </Typography>
                  </Box>

                  {/* Expandable Items List */}
                  <Collapse in={expandedCards.has(order.id)}>
                    <TableContainer component={Paper} variant="outlined" sx={{ mt: 2 }}>
                      <Table size="small">
                        <TableHead>
                          <TableRow>
                            <TableCell>Product</TableCell>
                            <TableCell align="center">Quantity</TableCell>
                            <TableCell align="right">Price</TableCell>
                            <TableCell align="right">Subtotal</TableCell>
                          </TableRow>
                        </TableHead>
                        <TableBody>
                          {order.items.map((item: any) => (
                            <TableRow key={item.id}>
                              <TableCell>
                                <Box>
                                  <Typography variant="body2" fontWeight="medium">
                                    {item.sweetName}
                                  </Typography>
                                  {item.notes && (
                                    <Typography variant="caption" color="text.secondary">
                                      Note: {item.notes}
                                    </Typography>
                                  )}
                                </Box>
                              </TableCell>
                              <TableCell align="center">{item.quantity}</TableCell>
                              <TableCell align="right">{formatPrice(item.unitPrice)}</TableCell>
                              <TableCell align="right">
                                {formatPrice(item.subtotal)}
                              </TableCell>
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </TableContainer>
                  </Collapse>

                  {/* Actions */}
                  <Box display="flex" justifyContent="space-between" alignItems="center" mt={2}>
                    <Button
                      startIcon={expandedCards.has(order.id) ? <ExpandLess /> : <ExpandMore />}
                      onClick={() => toggleCardExpansion(order.id)}
                      variant="text"
                    >
                      {expandedCards.has(order.id) ? 'Hide' : 'Show'} Items
                    </Button>
                    
                    <Box display="flex" gap={1} flexWrap="wrap">
                      <Button
                        startIcon={<Visibility />}
                        onClick={() => handleViewDetails(order)}
                        variant="outlined"
                        size="small"
                      >
                        View Details
                      </Button>
                      
                      {/* Admin-specific actions */}
                      {isAdmin ? (
                        <>
                          {order.status === 'PENDING' && (
                            <>
                              <Button
                                startIcon={<Check />}
                                onClick={() => handleUpdateOrderStatus(order.id, 'CONFIRMED')}
                                color="success"
                                variant="outlined"
                                size="small"
                              >
                                Accept
                              </Button>
                              <Button
                                startIcon={<Close />}
                                onClick={() => handleUpdateOrderStatus(order.id, 'CANCELLED')}
                                color="error"
                                variant="outlined"
                                size="small"
                              >
                                Decline
                              </Button>
                            </>
                          )}
                          {order.status === 'CONFIRMED' && (
                            <Button
                              startIcon={<Pending />}
                              onClick={() => handleUpdateOrderStatus(order.id, 'PREPARING')}
                              color="primary"
                              variant="outlined"
                              size="small"
                            >
                              Start Preparing
                            </Button>
                          )}
                          {order.status === 'PREPARING' && (
                            <Button
                              startIcon={<Done />}
                              onClick={() => handleUpdateOrderStatus(order.id, 'READY')}
                              color="success"
                              variant="outlined"
                              size="small"
                            >
                              Mark Ready
                            </Button>
                          )}
                          {order.status === 'READY' && (
                            <Button
                              startIcon={<LocalShipping />}
                              onClick={() => handleUpdateOrderStatus(order.id, 'OUT_FOR_DELIVERY')}
                              color="primary"
                              variant="outlined"
                              size="small"
                            >
                              Out for Delivery
                            </Button>
                          )}
                          {order.status === 'OUT_FOR_DELIVERY' && (
                            <Button
                              startIcon={<CheckCircle />}
                              onClick={() => handleUpdateOrderStatus(order.id, 'DELIVERED')}
                              color="success"
                              variant="outlined"
                              size="small"
                            >
                              Mark Delivered
                            </Button>
                          )}
                        </>
                      ) : (
                        /* Regular user actions */
                        order.status === 'PENDING' && (
                          <Button
                            startIcon={<Cancel />}
                            onClick={() => handleCancelOrder(order.id)}
                            color="error"
                            variant="outlined"
                            size="small"
                          >
                            Cancel
                          </Button>
                        )
                      )}
                    </Box>
                  </Box>
                </CardContent>
              </Card>
            </Box>
          ))}
        </Box>
      )}

      {/* Floating Action Button - Only for regular users */}
      {!isAdmin && (
        <Fab
          color="primary"
          aria-label="add order"
          sx={{ position: 'fixed', bottom: 16, right: 16 }}
          onClick={() => navigate('/products')}
        >
          <Add />
        </Fab>
      )}

      {/* Order Details Dialog */}
      <Dialog
        open={detailsDialog}
        onClose={() => setDetailsDialog(false)}
        maxWidth="md"
        fullWidth
      >
        {selectedOrder && (
          <>
            <DialogTitle>
              <Box display="flex" justifyContent="space-between" alignItems="center">
                <Typography variant="h6">
                  Order #{selectedOrder.id} Details
                </Typography>
                <Chip
                  icon={getStatusIcon(selectedOrder.status)}
                  label={selectedOrder.status}
                  color={getStatusColor(selectedOrder.status) as any}
                />
              </Box>
            </DialogTitle>
            <DialogContent>
              <Box display="flex" flexWrap="wrap" gap={3}>
                <Box sx={{ flex: { xs: '1 1 100%', md: '1 1 50%' } }}>
                  <Typography variant="subtitle2" gutterBottom>
                    Order Information
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Order Date:</strong>{' '}
                    {new Date(selectedOrder.createdAt).toLocaleDateString('en-US', {
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                      hour: '2-digit',
                      minute: '2-digit',
                    })}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Status:</strong> {selectedOrder.status}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Total Amount:</strong> {formatPrice(selectedOrder.totalAmount)}
                  </Typography>
                </Box>

                <Box sx={{ flex: { xs: '1 1 100%', md: '1 1 50%' } }}>
                  <Typography variant="subtitle2" gutterBottom>
                    Customer Information
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Name:</strong> {selectedOrder.customerName}
                  </Typography>
                  <Typography variant="body2" gutterBottom>
                    <strong>Email:</strong> {selectedOrder.customerEmail}
                  </Typography>
                </Box>

                <Box sx={{ flex: '1 1 100%' }}>
                  <Typography variant="subtitle2" gutterBottom>
                    Order Items
                  </Typography>
                  <TableContainer component={Paper} variant="outlined">
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>Product</TableCell>
                          <TableCell align="center">Quantity</TableCell>
                          <TableCell align="right">Unit Price</TableCell>
                          <TableCell align="right">Subtotal</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {selectedOrder.items.map((item) => (
                          <TableRow key={item.id}>
                            <TableCell>
                              <Box>
                                <Typography variant="body2" fontWeight="medium">
                                  {item.sweetName}
                                </Typography>
                                {item.notes && (
                                  <Typography variant="caption" display="block" color="text.secondary">
                                    Note: {item.notes}
                                  </Typography>
                                )}
                              </Box>
                            </TableCell>
                            <TableCell align="center">{item.quantity}</TableCell>
                            <TableCell align="right">{formatPrice(item.unitPrice)}</TableCell>
                            <TableCell align="right">
                              <strong>{formatPrice(item.subtotal)}</strong>
                            </TableCell>
                          </TableRow>
                        ))}
                        <TableRow>
                          <TableCell colSpan={3}>
                            <strong>Total</strong>
                          </TableCell>
                          <TableCell align="right">
                            <strong>{formatPrice(selectedOrder.totalAmount)}</strong>
                          </TableCell>
                        </TableRow>
                      </TableBody>
                    </Table>
                  </TableContainer>
                </Box>
              </Box>
            </DialogContent>
            <DialogActions>
              {selectedOrder.status === 'PENDING' && (
                <Button
                  onClick={() => handleCancelOrder(selectedOrder.id)}
                  color="error"
                  disabled={cancelOrderMutation.isPending}
                >
                  Cancel Order
                </Button>
              )}
              <Button onClick={() => setDetailsDialog(false)}>
                Close
              </Button>
            </DialogActions>
          </>
        )}
      </Dialog>

      {/* Order Form Dialog - Only for regular users */}
      {!isAdmin && (
        <Dialog
          open={orderFormDialog}
          onClose={() => setOrderFormDialog(false)}
          maxWidth="sm"
          fullWidth
        >
        <DialogTitle>
          Complete Your Order
        </DialogTitle>
        <DialogContent>
          <Typography variant="body2" color="text.secondary" gutterBottom>
            Please provide your contact and delivery information to complete the order.
          </Typography>
          
          <Box sx={{ mt: 2 }}>
            <TextField
              autoFocus
              margin="dense"
              label="Phone Number"
              type="tel"
              fullWidth
              variant="outlined"
              value={orderFormData.customerPhone}
              onChange={(e) => setOrderFormData(prev => ({ ...prev, customerPhone: e.target.value }))}
              placeholder="Enter your phone number"
              helperText="We'll use this to contact you about your order"
            />
          </Box>

          <Box sx={{ mt: 2 }}>
            <TextField
              margin="dense"
              label="Delivery Address"
              multiline
              rows={3}
              fullWidth
              variant="outlined"
              value={orderFormData.deliveryAddress}
              onChange={(e) => setOrderFormData(prev => ({ ...prev, deliveryAddress: e.target.value }))}
              placeholder="Enter your complete delivery address"
              helperText="Include street address, city, state, and postal code"
            />
          </Box>

          <Box sx={{ mt: 2 }}>
            <TextField
              margin="dense"
              label="Special Instructions (Optional)"
              multiline
              rows={2}
              fullWidth
              variant="outlined"
              value={orderFormData.notes}
              onChange={(e) => setOrderFormData(prev => ({ ...prev, notes: e.target.value }))}
              placeholder="Any special delivery instructions or preferences"
            />
          </Box>

          <Box sx={{ mt: 2, p: 2, bgcolor: 'grey.50', borderRadius: 1 }}>
            <Typography variant="subtitle2" gutterBottom>
              Order Summary:
            </Typography>
            <Typography variant="body2" color="text.secondary">
              • {cartItems.length} unique products
            </Typography>
            <Typography variant="body2" color="text.secondary">
              • Total: {formatPrice(totalAmount * 1.08)} (including tax)
            </Typography>
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOrderFormDialog(false)}>
            Cancel
          </Button>
          <Button 
            variant="contained" 
            onClick={handleSubmitOrder}
            disabled={createOrderMutation.isPending}
          >
            {createOrderMutation.isPending ? 'Placing Order...' : 'Place Order'}
          </Button>
        </DialogActions>
      </Dialog>
      )}
    </Container>
  );
};

export default OrdersPage;