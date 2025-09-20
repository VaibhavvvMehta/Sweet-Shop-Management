/**
 * Utility functions for currency formatting
 * Formats all prices in Indian Rupees (₹)
 */

/**
 * Format price in Indian Rupees
 * @param amount - The amount to format
 * @returns Formatted string with rupee symbol
 */
export const formatPrice = (amount: number): string => {
  return `₹${amount.toFixed(2)}`;
};

/**
 * Format price for Indian audience with proper comma formatting
 * @param amount - The amount to format
 * @returns Formatted string with rupee symbol and Indian comma formatting
 */
export const formatPriceIndian = (amount: number): string => {
  const formattedAmount = amount.toLocaleString('en-IN', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
  return `₹${formattedAmount}`;
};

/**
 * Get currency symbol
 * @returns Indian Rupee symbol
 */
export const getCurrencySymbol = (): string => {
  return '₹';
};