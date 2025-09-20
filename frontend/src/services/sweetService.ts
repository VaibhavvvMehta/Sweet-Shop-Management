import { apiService, API_CONFIG } from './api';
import {
  Sweet,
  SweetCreateRequest,
  SweetUpdateRequest,
  SearchFilters,
} from '../types';

export const sweetService = {
  /**
   * Get all sweets
   */
  async getAllSweets(): Promise<Sweet[]> {
    try {
      return await apiService.get<Sweet[]>(API_CONFIG.ENDPOINTS.SWEETS.BASE);
    } catch (error) {
      console.error('Failed to fetch sweets:', error);
      throw error;
    }
  },

  /**
   * Get sweet by ID
   */
  async getSweetById(id: number): Promise<Sweet> {
    try {
      return await apiService.get<Sweet>(API_CONFIG.ENDPOINTS.SWEETS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to fetch sweet with ID ${id}:`, error);
      throw error;
    }
  },

  /**
   * Get available sweets only
   */
  async getAvailableSweets(): Promise<Sweet[]> {
    try {
      return await apiService.get<Sweet[]>(API_CONFIG.ENDPOINTS.SWEETS.AVAILABLE);
    } catch (error) {
      console.error('Failed to fetch available sweets:', error);
      throw error;
    }
  },

  /**
   * Create new sweet
   */
  async createSweet(sweetData: SweetCreateRequest): Promise<Sweet> {
    try {
      return await apiService.post<Sweet>(
        API_CONFIG.ENDPOINTS.SWEETS.BASE,
        sweetData
      );
    } catch (error) {
      console.error('Failed to create sweet:', error);
      throw error;
    }
  },

  /**
   * Update sweet
   */
  async updateSweet(id: number, sweetData: SweetUpdateRequest): Promise<Sweet> {
    try {
      return await apiService.put<Sweet>(
        API_CONFIG.ENDPOINTS.SWEETS.BY_ID(id),
        sweetData
      );
    } catch (error) {
      console.error(`Failed to update sweet with ID ${id}:`, error);
      throw error;
    }
  },

  /**
   * Delete sweet
   */
  async deleteSweet(id: number): Promise<void> {
    try {
      await apiService.delete(API_CONFIG.ENDPOINTS.SWEETS.BY_ID(id));
    } catch (error) {
      console.error(`Failed to delete sweet with ID ${id}:`, error);
      throw error;
    }
  },

  /**
   * Search sweets with filters
   */
  async searchSweets(filters: SearchFilters): Promise<Sweet[]> {
    try {
      const params = new URLSearchParams();
      
      if (filters.query) params.append('query', filters.query);
      if (filters.minPrice !== undefined) params.append('minPrice', filters.minPrice.toString());
      if (filters.maxPrice !== undefined) params.append('maxPrice', filters.maxPrice.toString());
      if (filters.availability !== undefined) params.append('available', filters.availability.toString());
      if (filters.sortBy) params.append('sortBy', filters.sortBy);
      if (filters.sortOrder) params.append('sortOrder', filters.sortOrder);

      const url = `${API_CONFIG.ENDPOINTS.SWEETS.SEARCH}?${params.toString()}`;
      return await apiService.get<Sweet[]>(url);
    } catch (error) {
      console.error('Failed to search sweets:', error);
      throw error;
    }
  },

  /**
   * Get unique categories
   */
  async getCategories(): Promise<string[]> {
    try {
      return await apiService.get<string[]>(API_CONFIG.ENDPOINTS.SWEETS.CATEGORIES);
    } catch (error) {
      console.error('Failed to fetch categories:', error);
      throw error;
    }
  },

  /**
   * Get sweets with low stock
   */
  async getLowStockSweets(threshold = 10): Promise<Sweet[]> {
    try {
      const url = `${API_CONFIG.ENDPOINTS.SWEETS.LOW_STOCK}?threshold=${threshold}`;
      return await apiService.get<Sweet[]>(url);
    } catch (error) {
      console.error('Failed to fetch low stock sweets:', error);
      throw error;
    }
  },

  /**
   * Update sweet stock quantity
   */
  async updateStock(id: number, quantity: number): Promise<Sweet> {
    try {
      return await apiService.patch<Sweet>(
        API_CONFIG.ENDPOINTS.SWEETS.BY_ID(id),
        { stockQuantity: quantity }
      );
    } catch (error) {
      console.error(`Failed to update stock for sweet with ID ${id}:`, error);
      throw error;
    }
  },

  /**
   * Toggle sweet availability
   */
  async toggleAvailability(id: number, isAvailable: boolean): Promise<Sweet> {
    try {
      return await apiService.patch<Sweet>(
        API_CONFIG.ENDPOINTS.SWEETS.BY_ID(id),
        { isAvailable }
      );
    } catch (error) {
      console.error(`Failed to toggle availability for sweet with ID ${id}:`, error);
      throw error;
    }
  },

  /**
   * Bulk update sweet prices
   */
  async bulkUpdatePrices(updates: Array<{ id: number; price: number }>): Promise<Sweet[]> {
    try {
      const promises = updates.map(update =>
        this.updateSweet(update.id, { price: update.price })
      );
      return await Promise.all(promises);
    } catch (error) {
      console.error('Failed to bulk update prices:', error);
      throw error;
    }
  },

  /**
   * Get sweet analytics/statistics
   */
  async getSweetStatistics(): Promise<any> {
    try {
      // This would be implemented if the backend provides statistics endpoint
      const sweets = await this.getAllSweets();
      
      return {
        totalSweets: sweets.length,
        availableSweets: sweets.filter(s => s.isAvailable).length,
        lowStockSweets: sweets.filter(s => s.quantity <= 10).length,
        totalValue: sweets.reduce((sum, s) => sum + (s.price * s.quantity), 0),
        categories: Array.from(new Set(sweets.map(s => s.category))).length,
      };
    } catch (error) {
      console.error('Failed to calculate sweet statistics:', error);
      throw error;
    }
  },

  /**
   * Validate sweet data before submission
   */
  validateSweetData(data: SweetCreateRequest | SweetUpdateRequest): string[] {
    const errors: string[] = [];
    
    if ('name' in data && data.name && data.name.length < 2) {
      errors.push('Sweet name must be at least 2 characters long');
    }
    
    if ('price' in data && data.price !== undefined && data.price <= 0) {
      errors.push('Price must be greater than 0');
    }
    
    if ('quantity' in data && data.quantity !== undefined && data.quantity < 0) {
      errors.push('Stock quantity cannot be negative');
    }
    
    if ('category' in data && data.category && data.category.length < 2) {
      errors.push('Category must be at least 2 characters long');
    }
    
    return errors;
  },
};