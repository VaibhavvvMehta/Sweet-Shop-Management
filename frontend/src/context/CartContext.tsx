import React, { createContext, useContext, useReducer, useEffect, ReactNode } from 'react';
import { cartService } from '../services/cartService';
import { Cart, CartItem, Sweet } from '../types';

// Action types
type CartAction =
  | { type: 'LOAD_CART'; payload: Cart }
  | { type: 'ADD_TO_CART'; payload: { sweet: Sweet; quantity: number; notes?: string } }
  | { type: 'REMOVE_FROM_CART'; payload: number }
  | { type: 'UPDATE_QUANTITY'; payload: { sweetId: number; quantity: number } }
  | { type: 'UPDATE_NOTES'; payload: { sweetId: number; notes: string } }
  | { type: 'CLEAR_CART' }
  | { type: 'SYNC_WITH_SWEETS'; payload: Sweet[] };

// Cart reducer
const cartReducer = (state: Cart, action: CartAction): Cart => {
  switch (action.type) {
    case 'LOAD_CART':
      return action.payload;
    
    case 'ADD_TO_CART':
      return cartService.addToCart(
        action.payload.sweet,
        action.payload.quantity,
        action.payload.notes
      );
    
    case 'REMOVE_FROM_CART':
      return cartService.removeFromCart(action.payload);
    
    case 'UPDATE_QUANTITY':
      return cartService.updateCartItemQuantity(
        action.payload.sweetId,
        action.payload.quantity
      );
    
    case 'UPDATE_NOTES':
      return cartService.updateCartItemNotes(
        action.payload.sweetId,
        action.payload.notes
      );
    
    case 'CLEAR_CART':
      return cartService.clearCart();
    
    case 'SYNC_WITH_SWEETS':
      return cartService.syncCartWithSweets(action.payload);
    
    default:
      return state;
  }
};

// Context type
interface CartContextType extends Cart {
  addToCart: (sweet: Sweet, quantity: number, notes?: string) => void;
  removeFromCart: (sweetId: number) => void;
  updateQuantity: (sweetId: number, quantity: number) => void;
  updateNotes: (sweetId: number, notes: string) => void;
  clearCart: () => void;
  isItemInCart: (sweetId: number) => boolean;
  getItemQuantity: (sweetId: number) => number;
  validateCart: () => { isValid: boolean; errors: string[] };
  getCartSummary: () => { itemCount: number; totalAmount: number; itemsText: string };
  syncWithSweets: (sweets: Sweet[]) => void;
  convertToOrderItems: () => Array<{ sweetId: number; quantity: number; notes?: string }>;
}

// Create context
const CartContext = createContext<CartContextType | undefined>(undefined);

// Provider props
interface CartProviderProps {
  children: ReactNode;
}

// Initial state
const initialCart: Cart = {
  items: [],
  totalItems: 0,
  totalAmount: 0,
};

// Cart provider component
export const CartProvider: React.FC<CartProviderProps> = ({ children }) => {
  const [cart, dispatch] = useReducer(cartReducer, initialCart);

  // Load cart from localStorage on mount
  useEffect(() => {
    const savedCart = cartService.getCart();
    dispatch({ type: 'LOAD_CART', payload: savedCart });
  }, []);

  // Add to cart function
  const addToCart = (sweet: Sweet, quantity: number, notes?: string): void => {
    dispatch({
      type: 'ADD_TO_CART',
      payload: { sweet, quantity, notes },
    });
  };

  // Remove from cart function
  const removeFromCart = (sweetId: number): void => {
    dispatch({
      type: 'REMOVE_FROM_CART',
      payload: sweetId,
    });
  };

  // Update quantity function
  const updateQuantity = (sweetId: number, quantity: number): void => {
    dispatch({
      type: 'UPDATE_QUANTITY',
      payload: { sweetId, quantity },
    });
  };

  // Update notes function
  const updateNotes = (sweetId: number, notes: string): void => {
    dispatch({
      type: 'UPDATE_NOTES',
      payload: { sweetId, notes },
    });
  };

  // Clear cart function
  const clearCart = (): void => {
    dispatch({ type: 'CLEAR_CART' });
  };

  // Check if item is in cart
  const isItemInCart = (sweetId: number): boolean => {
    return cartService.isItemInCart(sweetId);
  };

  // Get item quantity
  const getItemQuantity = (sweetId: number): number => {
    return cartService.getItemQuantity(sweetId);
  };

  // Validate cart
  const validateCart = (): { isValid: boolean; errors: string[] } => {
    return cartService.validateCart();
  };

  // Get cart summary
  const getCartSummary = (): { itemCount: number; totalAmount: number; itemsText: string } => {
    return cartService.getCartSummary();
  };

  // Sync cart with updated sweets
  const syncWithSweets = (sweets: Sweet[]): void => {
    dispatch({
      type: 'SYNC_WITH_SWEETS',
      payload: sweets,
    });
  };

  // Convert cart to order items format
  const convertToOrderItems = (): Array<{ sweetId: number; quantity: number; notes?: string }> => {
    return cartService.convertCartToOrderItems();
  };

  // Context value
  const contextValue: CartContextType = {
    ...cart,
    addToCart,
    removeFromCart,
    updateQuantity,
    updateNotes,
    clearCart,
    isItemInCart,
    getItemQuantity,
    validateCart,
    getCartSummary,
    syncWithSweets,
    convertToOrderItems,
  };

  return (
    <CartContext.Provider value={contextValue}>
      {children}
    </CartContext.Provider>
  );
};

// Custom hook to use cart context
export const useCart = (): CartContextType => {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
};

// Hook for cart operations with notifications
export const useCartOperations = () => {
  const cart = useCart();

  const addWithNotification = (sweet: Sweet, quantity: number, notes?: string) => {
    cart.addToCart(sweet, quantity, notes);
    // You can add toast notifications here
    console.log(`Added ${quantity} ${sweet.name}(s) to cart`);
  };

  const removeWithNotification = (sweetId: number) => {
    const item = cart.items.find(item => item.sweet.id === sweetId);
    cart.removeFromCart(sweetId);
    // You can add toast notifications here
    if (item) {
      console.log(`Removed ${item.sweet.name} from cart`);
    }
  };

  const updateQuantityWithValidation = (sweetId: number, quantity: number) => {
    const item = cart.items.find(item => item.sweet.id === sweetId);
    if (!item) return;

    // Check stock availability
    if (quantity > item.sweet.quantity) {
      alert(`Only ${item.sweet.quantity} ${item.sweet.name}(s) available in stock`);
      return;
    }

    cart.updateQuantity(sweetId, quantity);
  };

  const validateAndProceedToCheckout = (): boolean => {
    const validation = cart.validateCart();
    
    if (!validation.isValid) {
      alert(`Cannot proceed to checkout:\n${validation.errors.join('\n')}`);
      return false;
    }

    return true;
  };

  return {
    ...cart,
    addWithNotification,
    removeWithNotification,
    updateQuantityWithValidation,
    validateAndProceedToCheckout,
  };
};

// Hook for cart statistics
export const useCartStats = () => {
  const { items, totalItems, totalAmount } = useCart();

  const stats = {
    isEmpty: items.length === 0,
    hasItems: items.length > 0,
    uniqueItems: items.length,
    totalQuantity: totalItems,
    totalValue: totalAmount,
    averageItemPrice: items.length > 0 ? totalAmount / totalItems : 0,
    mostExpensiveItem: items.reduce(
      (max, item) => (item.sweet.price > max.sweet.price ? item : max),
      items[0] || null
    ),
    cheapestItem: items.reduce(
      (min, item) => (item.sweet.price < min.sweet.price ? item : min),
      items[0] || null
    ),
  };

  return stats;
};