// User and Authentication Types
export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: 'USER' | 'ADMIN';
  createdAt: string;
  updatedAt: string;
}

export interface AuthResponse {
  token: string;
  user: User;
  message: string;
}

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
}

// Sweet/Product Types
export type SweetCategory = 
  | 'MILK_BASED' 
  | 'DRY_FRUIT' 
  | 'SYRUP_BASED' 
  | 'FLOUR_BASED' 
  | 'GRAIN_BASED' 
  | 'COCONUT_BASED' 
  | 'FESTIVAL_SPECIAL' 
  | 'BENGALI' 
  | 'SOUTH_INDIAN' 
  | 'SUGAR_FREE' 
  | 'OTHER';

export type PricingType = 'PER_ITEM' | 'PER_KG';

export interface Sweet {
  id: number;
  name: string;
  description: string;
  category: SweetCategory;
  price: number;
  pricingType: PricingType;
  quantity: number;
  isAvailable: boolean;
  imageUrl?: string;
  ingredients?: string;
  allergens?: string;
  nutritionalInfo?: string;
  createdAt: string;
  updatedAt: string;
}

export interface SweetCreateRequest {
  name: string;
  description: string;
  category: SweetCategory;
  price: number;
  pricingType: PricingType;
  quantity: number;
  imageUrl?: string;
  ingredients?: string;
  allergens?: string;
  nutritionalInfo?: string;
}

export interface SweetUpdateRequest {
  name?: string;
  description?: string;
  category?: SweetCategory;
  price?: number;
  pricingType?: PricingType;
  quantity?: number;
  isAvailable?: boolean;
  imageUrl?: string;
  ingredients?: string;
  allergens?: string;
  nutritionalInfo?: string;
}

// Order Types
export interface OrderItem {
  id: number;
  sweetId: number;
  sweetName: string;
  sweetCategory?: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  notes?: string;
  createdAt?: string;
}

export interface Order {
  id: number;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  deliveryAddress: string;
  status: OrderStatus;
  totalAmount: number;
  notes?: string;
  createdAt: string;
  updatedAt: string;
  completedAt?: string;
  totalQuantity?: number;
  items: OrderItem[];
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  PREPARING = 'PREPARING',
  READY = 'READY',
  OUT_FOR_DELIVERY = 'OUT_FOR_DELIVERY',
  DELIVERED = 'DELIVERED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export interface OrderCreateRequest {
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  deliveryAddress: string;
  items: OrderItemCreateRequest[];
  notes?: string;
  deliveryDate?: string;
}

export interface OrderItemCreateRequest {
  sweetId: number;
  quantity: number;
  notes?: string;
}

export interface OrderUpdateRequest {
  customerName?: string;
  customerEmail?: string;
  customerPhone?: string;
  deliveryAddress?: string;
  notes?: string;
  deliveryDate?: string;
}

export interface OrderStatusUpdateRequest {
  status: OrderStatus;
  notes?: string;
}

export interface OrderItemAddRequest {
  sweetId: number;
  quantity: number;
  notes?: string;
}

export interface OrderItemUpdateRequest {
  quantity?: number;
  notes?: string;
}

// Cart Types
export interface CartItem {
  sweet: Sweet;
  quantity: number;
  notes?: string;
}

export interface Cart {
  items: CartItem[];
  totalItems: number;
  totalAmount: number;
}

// API Response Types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface ErrorResponse {
  message: string;
  timestamp: string;
  status: number;
  error: string;
  path: string;
}

// Statistics Types
export interface OrderStatistics {
  pending: number;
  confirmed: number;
  preparing: number;
  ready: number;
  outForDelivery: number;
  delivered: number;
  completed: number;
  cancelled: number;
}

export interface RevenueResponse {
  startDate: string;
  endDate: string;
  totalRevenue: number;
}

// UI State Types
export interface LoadingState {
  isLoading: boolean;
  error: string | null;
}

export interface AuthState extends LoadingState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
}

export interface SweetState extends LoadingState {
  sweets: Sweet[];
  selectedSweet: Sweet | null;
}

export interface OrderState extends LoadingState {
  orders: Order[];
  selectedOrder: Order | null;
  statistics: OrderStatistics | null;
}

export interface CartState {
  items: CartItem[];
  totalItems: number;
  totalAmount: number;
}

// Form Types
export interface FormError {
  field: string;
  message: string;
}

// Navigation Types
export interface NavigationItem {
  path: string;
  label: string;
  icon: string;
  requiredRole?: 'USER' | 'ADMIN';
}

// Theme Types
export interface ThemeConfig {
  mode: 'light' | 'dark';
  primaryColor: string;
  secondaryColor: string;
}

// Search and Filter Types
export interface SearchFilters {
  query?: string;
  minPrice?: number;
  maxPrice?: number;
  availability?: boolean;
  sortBy?: 'name' | 'price' | 'createdAt';
  sortOrder?: 'asc' | 'desc';
}

export interface OrderFilters {
  status?: OrderStatus;
  customerEmail?: string;
  startDate?: string;
  endDate?: string;
  sortBy?: 'orderDate' | 'totalAmount' | 'status';
  sortOrder?: 'asc' | 'desc';
}

// Pagination Types
export interface PaginationParams {
  page: number;
  size: number;
  sort?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}