import { Cart, CartItem, Sweet } from '../types';

const CART_STORAGE_KEY = 'sweetShopCart';

export const cartService = {
  /**
   * Get cart from localStorage
   */
  getCart(): Cart {
    try {
      const cartData = localStorage.getItem(CART_STORAGE_KEY);
      if (cartData) {
        const cart = JSON.parse(cartData);
        return this.calculateCartTotals(cart);
      }
    } catch (error) {
      console.error('Error loading cart from localStorage:', error);
    }
    
    return {
      items: [],
      totalItems: 0,
      totalAmount: 0,
    };
  },

  /**
   * Save cart to localStorage
   */
  saveCart(cart: Cart): void {
    try {
      const cartWithTotals = this.calculateCartTotals(cart);
      localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cartWithTotals));
    } catch (error) {
      console.error('Error saving cart to localStorage:', error);
    }
  },

  /**
   * Add item to cart
   */
  addToCart(sweet: Sweet, quantity: number, notes?: string): Cart {
    const cart = this.getCart();
    const existingItemIndex = cart.items.findIndex(item => item.sweet.id === sweet.id);
    
    if (existingItemIndex >= 0) {
      // Update existing item
      cart.items[existingItemIndex].quantity += quantity;
      if (notes) {
        cart.items[existingItemIndex].notes = notes;
      }
    } else {
      // Add new item
      cart.items.push({
        sweet,
        quantity,
        notes,
      });
    }
    
    const updatedCart = this.calculateCartTotals(cart);
    this.saveCart(updatedCart);
    return updatedCart;
  },

  /**
   * Remove item from cart
   */
  removeFromCart(sweetId: number): Cart {
    const cart = this.getCart();
    cart.items = cart.items.filter(item => item.sweet.id !== sweetId);
    
    const updatedCart = this.calculateCartTotals(cart);
    this.saveCart(updatedCart);
    return updatedCart;
  },

  /**
   * Update item quantity in cart
   */
  updateCartItemQuantity(sweetId: number, quantity: number): Cart {
    const cart = this.getCart();
    const itemIndex = cart.items.findIndex(item => item.sweet.id === sweetId);
    
    if (itemIndex >= 0) {
      if (quantity <= 0) {
        // Remove item if quantity is 0 or negative
        cart.items.splice(itemIndex, 1);
      } else {
        cart.items[itemIndex].quantity = quantity;
      }
    }
    
    const updatedCart = this.calculateCartTotals(cart);
    this.saveCart(updatedCart);
    return updatedCart;
  },

  /**
   * Update item notes in cart
   */
  updateCartItemNotes(sweetId: number, notes: string): Cart {
    const cart = this.getCart();
    const itemIndex = cart.items.findIndex(item => item.sweet.id === sweetId);
    
    if (itemIndex >= 0) {
      cart.items[itemIndex].notes = notes;
    }
    
    const updatedCart = this.calculateCartTotals(cart);
    this.saveCart(updatedCart);
    return updatedCart;
  },

  /**
   * Clear entire cart
   */
  clearCart(): Cart {
    const emptyCart: Cart = {
      items: [],
      totalItems: 0,
      totalAmount: 0,
    };
    
    this.saveCart(emptyCart);
    return emptyCart;
  },

  /**
   * Calculate cart totals
   */
  calculateCartTotals(cart: Cart): Cart {
    const totalItems = cart.items.reduce((sum, item) => sum + item.quantity, 0);
    const totalAmount = cart.items.reduce(
      (sum, item) => sum + (item.quantity * item.sweet.price),
      0
    );
    
    return {
      ...cart,
      totalItems,
      totalAmount: Number(totalAmount.toFixed(2)),
    };
  },

  /**
   * Get cart item count
   */
  getCartItemCount(): number {
    const cart = this.getCart();
    return cart.totalItems;
  },

  /**
   * Get cart total amount
   */
  getCartTotal(): number {
    const cart = this.getCart();
    return cart.totalAmount;
  },

  /**
   * Check if item is in cart
   */
  isItemInCart(sweetId: number): boolean {
    const cart = this.getCart();
    return cart.items.some(item => item.sweet.id === sweetId);
  },

  /**
   * Get item quantity in cart
   */
  getItemQuantity(sweetId: number): number {
    const cart = this.getCart();
    const item = cart.items.find(item => item.sweet.id === sweetId);
    return item ? item.quantity : 0;
  },

  /**
   * Validate cart before checkout
   */
  validateCart(): { isValid: boolean; errors: string[] } {
    const cart = this.getCart();
    const errors: string[] = [];
    
    if (cart.items.length === 0) {
      errors.push('Cart is empty');
      return { isValid: false, errors };
    }
    
    // Check for items with zero or negative quantities
    const invalidItems = cart.items.filter(item => item.quantity <= 0);
    if (invalidItems.length > 0) {
      errors.push('Some items have invalid quantities');
    }
    
    // Check for items with zero prices
    const zeroPriceItems = cart.items.filter(item => item.sweet.price <= 0);
    if (zeroPriceItems.length > 0) {
      errors.push('Some items have invalid prices');
    }
    
    // Check for unavailable items
    const unavailableItems = cart.items.filter(item => !item.sweet.isAvailable);
    if (unavailableItems.length > 0) {
      errors.push('Some items are no longer available');
    }
    
    // Check stock availability
    const outOfStockItems = cart.items.filter(
      item => item.quantity > item.sweet.quantity
    );
    if (outOfStockItems.length > 0) {
      errors.push('Some items exceed available stock');
    }
    
    return {
      isValid: errors.length === 0,
      errors,
    };
  },

  /**
   * Get cart summary for display
   */
  getCartSummary(): {
    itemCount: number;
    totalAmount: number;
    itemsText: string;
  } {
    const cart = this.getCart();
    
    return {
      itemCount: cart.totalItems,
      totalAmount: cart.totalAmount,
      itemsText: cart.totalItems === 1 ? 'item' : 'items',
    };
  },

  /**
   * Merge cart with updated sweet data (e.g., after price changes)
   */
  syncCartWithSweets(updatedSweets: Sweet[]): Cart {
    const cart = this.getCart();
    
    // Update sweet data in cart items
    cart.items = cart.items.map(item => {
      const updatedSweet = updatedSweets.find(s => s.id === item.sweet.id);
      if (updatedSweet) {
        return {
          ...item,
          sweet: updatedSweet,
        };
      }
      return item;
    }).filter(item => item.sweet.isAvailable); // Remove unavailable items
    
    const updatedCart = this.calculateCartTotals(cart);
    this.saveCart(updatedCart);
    return updatedCart;
  },

  /**
   * Convert cart to order items format
   */
  convertCartToOrderItems(): Array<{
    sweetId: number;
    quantity: number;
    notes?: string;
  }> {
    const cart = this.getCart();
    return cart.items.map(item => ({
      sweetId: item.sweet.id,
      quantity: item.quantity,
      notes: item.notes,
    }));
  },

  /**
   * Import cart from another device/session
   */
  importCart(cartData: Cart): Cart {
    try {
      const updatedCart = this.calculateCartTotals(cartData);
      this.saveCart(updatedCart);
      return updatedCart;
    } catch (error) {
      console.error('Error importing cart:', error);
      return this.getCart();
    }
  },

  /**
   * Export cart data for sharing
   */
  exportCart(): string {
    try {
      const cart = this.getCart();
      return JSON.stringify(cart);
    } catch (error) {
      console.error('Error exporting cart:', error);
      return '{}';
    }
  },
};