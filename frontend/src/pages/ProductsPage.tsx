import React, { useEffect, useState } from 'react';
import {
  Container,
  Paper,
  Typography,
  Box,
  Card,
  CardContent,
  CardMedia,
  CardActions,
  Button,
  TextField,
  InputAdornment,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Chip,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  Alert,
  CircularProgress,
  Fab,
} from '@mui/material';
import {
  Search,
  FilterList,
  Add,
  Remove,
  ShoppingCart,
  Inventory,
  Close,
  AttachMoney,
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { sweetService } from '../services/sweetService';
import { Sweet, SearchFilters } from '../types';
import { formatPrice } from '../utils/currency';

const ProductsPage: React.FC = () => {
  const { hasRole } = useAuth();
  const { addToCart, isItemInCart, getItemQuantity } = useCart();

  const [sweets, setSweets] = useState<Sweet[]>([]);
  const [filteredSweets, setFilteredSweets] = useState<Sweet[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  
  // Filters
  const [filters, setFilters] = useState<SearchFilters>({
    query: '',
    availability: true,
    sortBy: 'name',
    sortOrder: 'asc',
  });

  // Product detail dialog
  const [selectedSweet, setSelectedSweet] = useState<Sweet | null>(null);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [addQuantity, setAddQuantity] = useState(1);
  const [addingToCart, setAddingToCart] = useState<Record<number, boolean>>({});

  useEffect(() => {
    loadProducts();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [sweets, filters]);

  const loadProducts = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = filters.availability ? 
        await sweetService.getAvailableSweets() : 
        await sweetService.getAllSweets();
      setSweets(data);
    } catch (error: any) {
      console.error('Failed to load products:', error);
      setError(error.message || 'Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = () => {
    let filtered = [...sweets];

    // Text search
    if (filters.query) {
      const query = filters.query.toLowerCase();
      filtered = filtered.filter(sweet =>
        sweet.name.toLowerCase().includes(query) ||
        sweet.description.toLowerCase().includes(query)
      );
    }

    // Availability filter
    if (filters.availability !== undefined) {
      filtered = filtered.filter(sweet => sweet.isAvailable === filters.availability);
    }

    // Sorting
    if (filters.sortBy) {
      filtered.sort((a, b) => {
        let aValue: any = a[filters.sortBy as keyof Sweet];
        let bValue: any = b[filters.sortBy as keyof Sweet];

        if (filters.sortBy === 'price') {
          aValue = Number(aValue);
          bValue = Number(bValue);
        }

        if (filters.sortOrder === 'desc') {
          return aValue > bValue ? -1 : 1;
        }
        return aValue < bValue ? -1 : 1;
      });
    }

    setFilteredSweets(filtered);
  };

  const handleFilterChange = (field: keyof SearchFilters, value: any) => {
    setFilters(prev => ({ ...prev, [field]: value }));
  };

  const handleAddToCart = async (sweet: Sweet, quantity: number = 1) => {
    if (quantity <= 0 || quantity > sweet.quantity) return;
    if (addingToCart[sweet.id]) return; // Prevent double-clicking
    
    setAddingToCart(prev => ({ ...prev, [sweet.id]: true }));
    
    try {
      // Add a small delay to prevent rapid successive clicks
      await new Promise(resolve => setTimeout(resolve, 100));
      addToCart(sweet, quantity);
      console.log(`Added ${quantity} ${sweet.name}(s) to cart`);
      
      // Additional delay before re-enabling
      await new Promise(resolve => setTimeout(resolve, 400));
    } finally {
      setAddingToCart(prev => ({ ...prev, [sweet.id]: false }));
    }
  };

  const openProductDetail = (sweet: Sweet) => {
    setSelectedSweet(sweet);
    setAddQuantity(1);
    setDialogOpen(true);
  };

  const closeProductDetail = () => {
    setDialogOpen(false);
    setSelectedSweet(null);
  };

  const ProductCard: React.FC<{ sweet: Sweet }> = ({ sweet }) => {
    const inCart = isItemInCart(sweet.id);
    const cartQuantity = getItemQuantity(sweet.id);

    return (
      <Card
        sx={{
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          transition: 'transform 0.2s',
          '&:hover': { transform: 'translateY(-4px)' },
        }}
      >
        <CardMedia
          component="div"
          sx={{
            height: 200,
            backgroundColor: 'grey.200',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontSize: '4rem',
            cursor: 'pointer',
          }}
          onClick={() => openProductDetail(sweet)}
        >
          {sweet.imageUrl ? (
            <img
              src={sweet.imageUrl}
              alt={sweet.name}
              style={{ width: '100%', height: '100%', objectFit: 'cover' }}
            />
          ) : (
            '🍭'
          )}
        </CardMedia>

        <CardContent sx={{ flexGrow: 1 }}>
          <Typography
            gutterBottom
            variant="h6"
            component="h2"
            sx={{ cursor: 'pointer' }}
            onClick={() => openProductDetail(sweet)}
          >
            {sweet.name}
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
            sx={{
              display: '-webkit-box',
              WebkitLineClamp: 2,
              WebkitBoxOrient: 'vertical',
              overflow: 'hidden',
              mb: 1,
            }}
          >
            {sweet.description}
          </Typography>

          <Box display="flex" alignItems="center" gap={1} mb={1}>
            {sweet.quantity === 0 && (
              <Chip
                label="Out of Stock"
                size="small"
                color="error"
                variant="filled"
              />
            )}
          </Box>

          <Box display="flex" justifyContent="space-between" alignItems="center">
            <Typography variant="h6" color="primary" fontWeight="bold">
              {formatPrice(sweet.price)} {sweet.pricingType === 'PER_KG' ? 'per kg' : 'per item'}
            </Typography>
          </Box>

          {inCart && (
            <Box mt={1}>
              <Chip
                label={`${cartQuantity} in cart`}
                size="small"
                color="primary"
                variant="filled"
              />
            </Box>
          )}
        </CardContent>

        <CardActions sx={{ p: 2, pt: 0 }}>
          <Button
            variant="contained"
            startIcon={<ShoppingCart />}
            onClick={() => handleAddToCart(sweet)}
            disabled={!sweet.isAvailable || sweet.quantity === 0 || addingToCart[sweet.id]}
            fullWidth
          >
            {addingToCart[sweet.id] 
              ? 'Adding...' 
              : sweet.isAvailable && sweet.quantity > 0 
                ? 'Add to Cart' 
                : 'Out of Stock'
            }
          </Button>
        </CardActions>
      </Card>
    );
  };

  if (loading) {
    return (
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Box display="flex" justifyContent="center" alignItems="center" minHeight="60vh">
          <CircularProgress size={60} />
        </Box>
      </Container>
    );
  }

  return (
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      {/* Header */}
      <Box mb={4}>
        <Typography variant="h4" component="h1" gutterBottom fontWeight="bold">
          Sweet Products 🍭
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Discover our delicious collection of sweets and treats
        </Typography>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Filters */}
      <Paper sx={{ p: 3, mb: 3 }}>
        <Box display="flex" flexWrap="wrap" gap={2} alignItems="center">
          <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 50%', md: '1 1 33.33%' }, minWidth: '200px' }}>
            <TextField
              fullWidth
              label="Search products..."
              value={filters.query}
              onChange={(e) => handleFilterChange('query', e.target.value)}
              InputProps={{
                startAdornment: (
                  <InputAdornment position="start">
                    <Search />
                  </InputAdornment>
                ),
              }}
            />
          </Box>

          <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 50%', md: '1 1 16.67%' }, minWidth: '120px' }}>
            <FormControl fullWidth>
              <InputLabel>Sort By</InputLabel>
              <Select
                value={filters.sortBy || 'name'}
                label="Sort By"
                onChange={(e) => handleFilterChange('sortBy', e.target.value)}
              >
                <MenuItem value="name">Name</MenuItem>
                <MenuItem value="price">Price</MenuItem>
                <MenuItem value="createdAt">Newest</MenuItem>
              </Select>
            </FormControl>
          </Box>

          <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 50%', md: '1 1 16.67%' }, minWidth: '120px' }}>
            <FormControl fullWidth>
              <InputLabel>Order</InputLabel>
              <Select
                value={filters.sortOrder || 'asc'}
                label="Order"
                onChange={(e) => handleFilterChange('sortOrder', e.target.value)}
              >
                <MenuItem value="asc">Ascending</MenuItem>
                <MenuItem value="desc">Descending</MenuItem>
              </Select>
            </FormControl>
          </Box>

          <Box sx={{ flex: { xs: '1 1 100%', sm: '1 1 100%', md: '1 1 8.33%' }, minWidth: '80px' }}>
            <Button
              variant="outlined"
              onClick={() => setFilters({
                query: '',
                availability: true,
                sortBy: 'name',
                sortOrder: 'asc',
              })}
              fullWidth
            >
              Clear
            </Button>
          </Box>
        </Box>
      </Paper>

      {/* Products Grid */}
      {filteredSweets.length === 0 ? (
        <Paper sx={{ p: 4, textAlign: 'center' }}>
          <Typography variant="h6" color="text.secondary">
            No products found matching your criteria
          </Typography>
          <Typography variant="body2" color="text.secondary" mt={1}>
            Try adjusting your filters or search terms
          </Typography>
        </Paper>
      ) : (
        <Box display="flex" flexWrap="wrap" gap={3}>
          {filteredSweets.map((sweet) => (
            <Box key={sweet.id} sx={{ flex: { xs: '1 1 100%', sm: '1 1 calc(50% - 12px)', md: '1 1 calc(33.33% - 16px)', lg: '1 1 calc(25% - 18px)' }, minWidth: '280px' }}>
              <ProductCard sweet={sweet} />
            </Box>
          ))}
        </Box>
      )}

      {/* Product Detail Dialog */}
      <Dialog
        open={dialogOpen}
        onClose={closeProductDetail}
        maxWidth="md"
        fullWidth
      >
        {selectedSweet && (
          <>
            <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Typography variant="h5" fontWeight="bold">
                {selectedSweet.name}
              </Typography>
              <IconButton onClick={closeProductDetail}>
                <Close />
              </IconButton>
            </DialogTitle>

            <DialogContent>
              <Box display="flex" flexWrap="wrap" gap={3}>
                <Box sx={{ flex: { xs: '1 1 100%', md: '1 1 50%' }, minWidth: '300px' }}>
                  <Box
                    sx={{
                      width: '100%',
                      height: 300,
                      backgroundColor: 'grey.200',
                      borderRadius: 1,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      fontSize: '6rem',
                    }}
                  >
                    {selectedSweet.imageUrl ? (
                      <img
                        src={selectedSweet.imageUrl}
                        alt={selectedSweet.name}
                        style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 4 }}
                      />
                    ) : (
                      '🍭'
                    )}
                  </Box>
                </Box>

                <Box sx={{ flex: { xs: '1 1 100%', md: '1 1 50%' }, minWidth: '300px' }}>
                  <Typography variant="h4" color="primary" fontWeight="bold" gutterBottom>
                    {formatPrice(selectedSweet.price)} {selectedSweet.pricingType === 'PER_KG' ? 'per kg' : 'per item'}
                  </Typography>

                  <Box display="flex" gap={1} mb={2}>
                    <Chip
                      label={selectedSweet.isAvailable ? 'Available' : 'Out of Stock'}
                      color={selectedSweet.isAvailable ? 'success' : 'error'}
                    />
                  </Box>

                  <Typography variant="body1" paragraph>
                    {selectedSweet.description}
                  </Typography>

                  {selectedSweet.ingredients && (
                    <Box mb={2}>
                      <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                        Ingredients:
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {selectedSweet.ingredients}
                      </Typography>
                    </Box>
                  )}

                  {selectedSweet.allergens && (
                    <Box mb={2}>
                      <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
                        Allergens:
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        {selectedSweet.allergens}
                      </Typography>
                    </Box>
                  )}

                  {selectedSweet.isAvailable && selectedSweet.quantity > 0 && (
                    <Box display="flex" alignItems="center" gap={2}>
                      <Typography variant="body2">Quantity:</Typography>
                      <Box display="flex" alignItems="center" gap={1}>
                        <IconButton
                          onClick={() => setAddQuantity(Math.max(1, addQuantity - 1))}
                          disabled={addQuantity <= 1}
                        >
                          <Remove />
                        </IconButton>
                        <Typography variant="h6" sx={{ minWidth: 40, textAlign: 'center' }}>
                          {addQuantity}
                        </Typography>
                        <IconButton
                          onClick={() => setAddQuantity(Math.min(selectedSweet.quantity, addQuantity + 1))}
                          disabled={addQuantity >= selectedSweet.quantity}
                        >
                          <Add />
                        </IconButton>
                      </Box>
                    </Box>
                  )}
                </Box>
              </Box>
            </DialogContent>

            <DialogActions sx={{ p: 3 }}>
              <Button onClick={closeProductDetail}>
                Close
              </Button>
              {selectedSweet.isAvailable && selectedSweet.quantity > 0 && (
                <Button
                  variant="contained"
                  startIcon={<ShoppingCart />}
                  disabled={addingToCart[selectedSweet.id]}
                  onClick={async () => {
                    await handleAddToCart(selectedSweet, addQuantity);
                    closeProductDetail();
                  }}
                >
                  {addingToCart[selectedSweet.id] ? 'Adding...' : `Add ${addQuantity} to Cart`}
                </Button>
              )}
            </DialogActions>
          </>
        )}
      </Dialog>
    </Container>
  );
};

export default ProductsPage;