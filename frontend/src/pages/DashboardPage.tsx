import React from 'react';
import {
  Container,
  Typography,
  Box,
  Paper,
} from '@mui/material';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { Store, ShoppingCart, Receipt } from '@mui/icons-material';
import { useCart } from '../context/CartContext';
import { formatPrice } from '../utils/currency';

const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { totalItems, totalAmount } = useCart();
  
  // Check if user is admin
  const isAdmin = user?.role === 'ADMIN';

  if (isAdmin) {
    // Simple admin homepage
    return (
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Paper 
          elevation={3} 
          sx={{ 
            p: 6, 
            textAlign: 'center',
            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
            color: 'white',
            borderRadius: 3
          }}
        >
          <Typography 
            variant="h2" 
            component="h1" 
            fontWeight="bold"
            sx={{ mb: 2 }}
          >
            Welcome Owner! 👋
          </Typography>
          <Typography 
            variant="h5" 
            sx={{ opacity: 0.9 }}
          >
            Manage your sweet shop from the navigation menu
          </Typography>
        </Paper>
      </Container>
    );
  }

  // Regular user dashboard (existing functionality)
  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box display="flex" flexDirection="column" gap={4}>
        {/* Welcome Section */}
        <Paper sx={{ p: 4, background: 'linear-gradient(135deg, #FF6B6B 0%, #4ECDC4 100%)', color: 'white' }}>
          <Typography variant="h4" component="h1" fontWeight="bold" gutterBottom>
            Welcome to Sweet Shop! 🍬
          </Typography>
          <Typography variant="h6" sx={{ opacity: 0.9 }}>
            Discover our delicious collection of traditional Indian sweets
          </Typography>
        </Paper>

        {/* Quick Actions */}
        <Box display="grid" gridTemplateColumns={{ xs: '1fr', sm: '1fr 1fr', md: '1fr 1fr 1fr' }} gap={3}>
          <Paper 
            sx={{ p: 3, cursor: 'pointer', '&:hover': { elevation: 8 } }}
            onClick={() => navigate('/products')}
          >
            <Box display="flex" alignItems="center" gap={2}>
              <Store color="primary" fontSize="large" />
              <Box>
                <Typography variant="h6" fontWeight="bold">
                  Browse Products
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Explore our sweet collection
                </Typography>
              </Box>
            </Box>
          </Paper>

          <Paper 
            sx={{ p: 3, cursor: 'pointer', '&:hover': { elevation: 8 } }}
            onClick={() => navigate('/cart')}
          >
            <Box display="flex" alignItems="center" gap={2}>
              <ShoppingCart color="primary" fontSize="large" />
              <Box>
                <Typography variant="h6" fontWeight="bold">
                  My Cart ({totalItems})
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {formatPrice(totalAmount)}
                </Typography>
              </Box>
            </Box>
          </Paper>

          <Paper 
            sx={{ p: 3, cursor: 'pointer', '&:hover': { elevation: 8 } }}
            onClick={() => navigate('/orders')}
          >
            <Box display="flex" alignItems="center" gap={2}>
              <Receipt color="primary" fontSize="large" />
              <Box>
                <Typography variant="h6" fontWeight="bold">
                  My Orders
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  Track your orders
                </Typography>
              </Box>
            </Box>
          </Paper>
        </Box>
      </Box>
    </Container>
  );
};

export default DashboardPage;