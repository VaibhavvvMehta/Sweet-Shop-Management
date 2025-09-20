import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
  Card,
  CardContent,
  CardMedia,
  IconButton,
  Button,
  TextField,
  Divider,
  Alert,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Chip,
} from '@mui/material';
import {
  Add,
  Remove,
  Delete,
  ShoppingCartCheckout,
  ArrowBack,
  Clear,
} from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { formatPrice } from '../utils/currency';

const CartPage: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();
  const {
    items,
    totalItems,
    totalAmount,
    updateQuantity,
    updateNotes,
    removeFromCart,
    clearCart,
    validateCart,
  } = useCart();

  const [checkoutDialog, setCheckoutDialog] = useState(false);
  const [clearDialog, setClearDialog] = useState(false);

  const handleQuantityChange = (sweetId: number, newQuantity: number) => {
    if (newQuantity <= 0) {
      removeFromCart(sweetId);
    } else {
      updateQuantity(sweetId, newQuantity);
    }
  };

  const handleNotesChange = (sweetId: number, notes: string) => {
    updateNotes(sweetId, notes);
  };

  const handleCheckout = () => {
    const validation = validateCart();
    
    if (!validation.isValid) {
      alert(`Cannot proceed to checkout:\n${validation.errors.join('\n')}`);
      return;
    }
    
    setCheckoutDialog(true);
  };

  const proceedToCheckout = () => {
    setCheckoutDialog(false);
    // Navigate to checkout/order form
    navigate('/orders', { state: { fromCart: true } });
  };

  const CartItem: React.FC<{ item: any }> = ({ item }) => {
    const [localNotes, setLocalNotes] = useState(item.notes || '');

    return (
      <Card sx={{ mb: 2 }}>
        <CardContent>
          <Box display="flex" flexWrap="wrap" gap={2} alignItems="flex-start">
            {/* Product Image */}
            <Box sx={{ flexBasis: { xs: '100%', sm: '25%', md: '16.67%' }, minWidth: '120px' }}>
              <CardMedia
                component="div"
                sx={{
                  height: 100,
                  backgroundColor: 'grey.200',
                  borderRadius: 1,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '2rem',
                }}
              >
                {item.sweet.imageUrl ? (
                  <img
                    src={item.sweet.imageUrl}
                    alt={item.sweet.name}
                    style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 4 }}
                  />
                ) : (
                  '🍭'
                )}
              </CardMedia>
            </Box>

            {/* Product Details */}
            <Box sx={{ flexBasis: { xs: '100%', sm: '50%', md: '41.67%' }, flexGrow: 1 }}>
              <Typography variant="h6" fontWeight="bold" gutterBottom>
                {item.sweet.name}
              </Typography>
              <Typography variant="body2" color="text.secondary" gutterBottom>
                {item.sweet.description}
              </Typography>
              <Box display="flex" gap={1} mb={1}>
                <Chip label={item.sweet.category} size="small" variant="outlined" />
                <Chip 
                  label={`${formatPrice(item.sweet.price)} each`} 
                  size="small" 
                  color="primary" 
                />
              </Box>
            </Box>

            {/* Quantity Controls */}
            <Box sx={{ flexBasis: { xs: '100%', sm: '25%', md: '16.67%' }, minWidth: '120px' }}>
              <Box display="flex" flexDirection="column" alignItems="center" gap={1}>
                <Typography variant="body2" color="text.secondary">
                  Quantity
                </Typography>
                <Box display="flex" alignItems="center" gap={1}>
                  <IconButton
                    size="small"
                    onClick={() => handleQuantityChange(item.sweet.id, item.quantity - 1)}
                    disabled={item.quantity <= 1}
                  >
                    <Remove />
                  </IconButton>
                  <Typography variant="h6" sx={{ minWidth: 40, textAlign: 'center' }}>
                    {item.quantity}
                  </Typography>
                  <IconButton
                    size="small"
                    onClick={() => handleQuantityChange(item.sweet.id, item.quantity + 1)}
                    disabled={item.quantity >= item.sweet.quantity}
                  >
                    <Add />
                  </IconButton>
                </Box>
                {item.quantity > item.sweet.quantity && (
                  <Typography variant="caption" color="error">
                    Exceeds stock
                  </Typography>
                )}
              </Box>
            </Box>

            {/* Price and Actions */}
            <Box sx={{ flexBasis: { xs: '100%', sm: '100%', md: '25%' }, minWidth: '120px' }}>
              <Box display="flex" flexDirection="column" alignItems="flex-end" gap={1}>
                <Typography variant="h6" color="primary" fontWeight="bold">
                  {formatPrice(item.quantity * item.sweet.price)}
                </Typography>
                <IconButton
                  color="error"
                  onClick={() => removeFromCart(item.sweet.id)}
                  size="small"
                >
                  <Delete />
                </IconButton>
              </Box>
            </Box>

            {/* Notes */}
            <Box sx={{ flexBasis: '100%', width: '100%', mt: 2 }}>
              <TextField
                fullWidth
                size="small"
                label="Special notes for this item"
                value={localNotes}
                onChange={(e) => setLocalNotes(e.target.value)}
                onBlur={() => handleNotesChange(item.sweet.id, localNotes)}
                placeholder="Any special instructions..."
              />
            </Box>
          </Box>
        </CardContent>
      </Card>
    );
  };

  if (items.length === 0) {
    return (
      <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
        <Paper sx={{ p: 6, textAlign: 'center' }}>
          <Typography variant="h4" gutterBottom>
            Your cart is empty 🛒
          </Typography>
          <Typography variant="body1" color="text.secondary" gutterBottom>
            Looks like you haven't added any sweets to your cart yet.
          </Typography>
          <Button
            variant="contained"
            size="large"
            onClick={() => navigate('/products')}
            sx={{ mt: 2 }}
          >
            Browse Products
          </Button>
        </Paper>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box display="flex" alignItems="center" gap={2} mb={4}>
        <IconButton onClick={() => navigate('/products')}>
          <ArrowBack />
        </IconButton>
        <Typography variant="h4" component="h1" fontWeight="bold">
          Shopping Cart ({totalItems} items)
        </Typography>
      </Box>

      <Box display="flex" flexWrap="wrap" gap={3}>
        {/* Cart Items */}
        <Box sx={{ flex: { xs: '1 1 100%', md: '1 1 66.67%' }, minWidth: 0 }}>
          <Box mb={2} display="flex" justifyContent="space-between" alignItems="center">
            <Typography variant="h6">
              Cart Items
            </Typography>
            <Button
              startIcon={<Clear />}
              onClick={() => setClearDialog(true)}
              color="error"
              variant="outlined"
              size="small"
            >
              Clear Cart
            </Button>
          </Box>

          {items.map((item) => (
            <CartItem key={item.sweet.id} item={item} />
          ))}
        </Box>

        {/* Order Summary */}
        <Box sx={{ flex: { xs: '1 1 100%', md: '1 1 33.33%' }, minWidth: 0 }}>
          <Paper sx={{ p: 3, position: 'sticky', top: 20 }}>
            <Typography variant="h6" fontWeight="bold" gutterBottom>
              Order Summary
            </Typography>

            <Box display="flex" justifyContent="space-between" mb={1}>
              <Typography variant="body2">
                Items ({totalItems})
              </Typography>
              <Typography variant="body2">
                {formatPrice(totalAmount)}
              </Typography>
            </Box>

            <Box display="flex" justifyContent="space-between" mb={1}>
              <Typography variant="body2">
                Estimated Tax
              </Typography>
              <Typography variant="body2">
                {formatPrice(totalAmount * 0.08)}
              </Typography>
            </Box>

            <Divider sx={{ my: 2 }} />

            <Box display="flex" justifyContent="space-between" mb={2}>
              <Typography variant="h6" fontWeight="bold">
                Total
              </Typography>
              <Typography variant="h6" fontWeight="bold" color="primary">
                {formatPrice(totalAmount * 1.08)}
              </Typography>
            </Box>

            <Button
              fullWidth
              variant="contained"
              size="large"
              startIcon={<ShoppingCartCheckout />}
              onClick={handleCheckout}
              sx={{ mb: 2 }}
            >
              Proceed to Checkout
            </Button>

            <Button
              fullWidth
              variant="outlined"
              onClick={() => navigate('/products')}
            >
              Continue Shopping
            </Button>

            <Box mt={3} p={2} bgcolor="grey.50" borderRadius={1}>
              <Typography variant="subtitle2" gutterBottom>
                💡 Quick Summary:
              </Typography>
              <Typography variant="body2" color="text.secondary">
                • {items.length} unique products
              </Typography>
              <Typography variant="body2" color="text.secondary">
                • {totalItems} total items
              </Typography>
              <Typography variant="body2" color="text.secondary">
                • Average: {formatPrice(totalAmount / totalItems)} per item
              </Typography>
            </Box>
          </Paper>
        </Box>
      </Box>

      {/* Checkout Confirmation Dialog */}
      <Dialog
        open={checkoutDialog}
        onClose={() => setCheckoutDialog(false)}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>
          Proceed to Checkout?
        </DialogTitle>
        <DialogContent>
          <Typography variant="body1" gutterBottom>
            You're about to checkout with {totalItems} items for a total of {formatPrice(totalAmount * 1.08)} (including tax).
          </Typography>
          <Typography variant="body2" color="text.secondary">
            You'll be able to review and confirm your order details on the next page.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCheckoutDialog(false)}>
            Continue Shopping
          </Button>
          <Button variant="contained" onClick={proceedToCheckout}>
            Proceed to Checkout
          </Button>
        </DialogActions>
      </Dialog>

      {/* Clear Cart Confirmation Dialog */}
      <Dialog
        open={clearDialog}
        onClose={() => setClearDialog(false)}
        maxWidth="sm"
      >
        <DialogTitle>
          Clear Cart?
        </DialogTitle>
        <DialogContent>
          <Typography variant="body1">
            Are you sure you want to remove all items from your cart? This action cannot be undone.
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setClearDialog(false)}>
            Cancel
          </Button>
          <Button
            color="error"
            variant="contained"
            onClick={() => {
              clearCart();
              setClearDialog(false);
            }}
          >
            Clear Cart
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
};

export default CartPage;