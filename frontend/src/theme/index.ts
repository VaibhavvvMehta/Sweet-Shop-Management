// Mithu Sweet Bhandar - Design System Theme Configuration

export const colors = {
  // Primary Colors - Warm earthy tones for traditional sweet shop feel
  primary: {
    50: '#fdf8f0',
    100: '#faebd7',
    200: '#f5d5a3',
    300: '#efbc6f',
    400: '#e8a547',
    500: '#d4851f', // Main brand color - rich golden brown
    600: '#b8701a',
    700: '#9b5a15',
    800: '#7e4612',
    900: '#65360e',
  },
  
  // Secondary Colors - Rich browns for depth
  secondary: {
    50: '#f7f3f0',
    100: '#ebe1d8',
    200: '#d6c3b1',
    300: '#c1a58a',
    400: '#ac8763',
    500: '#8b6914', // Deep brown
    600: '#7a5b11',
    700: '#694d0f',
    800: '#583f0c',
    900: '#47310a',
  },
  
  // Accent Colors - Subtle golden highlights
  accent: {
    50: '#fffcf5',
    100: '#fff8e6',
    200: '#ffefbf',
    300: '#ffe699',
    400: '#ffdd73',
    500: '#ffd54c', // Golden accent
    600: '#e6c043',
    700: '#ccab3a',
    800: '#b39632',
    900: '#998129',
  },
  
  // Neutral Colors - Clean grays
  gray: {
    50: '#fafafa',
    100: '#f5f5f5',
    200: '#e5e5e5',
    300: '#d4d4d4',
    400: '#a3a3a3',
    500: '#737373',
    600: '#525252',
    700: '#404040',
    800: '#262626',
    900: '#171717',
  },
  
  // Semantic Colors
  success: '#22c55e',
  warning: '#f59e0b',
  error: '#ef4444',
  info: '#3b82f6',
  
  // Background Colors
  background: {
    primary: '#ffffff',
    secondary: '#fafafa',
    tertiary: '#f8f6f3',
  },
  
  // Text Colors
  text: {
    primary: '#171717',
    secondary: '#404040',
    tertiary: '#737373',
    inverse: '#ffffff',
  },
};

export const typography = {
  fontFamily: {
    primary: '"Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif',
    heading: '"Playfair Display", Georgia, serif',
    mono: '"JetBrains Mono", Consolas, monospace',
  },
  fontSize: {
    xs: '0.75rem',    // 12px
    sm: '0.875rem',   // 14px
    base: '1rem',     // 16px
    lg: '1.125rem',   // 18px
    xl: '1.25rem',    // 20px
    '2xl': '1.5rem',  // 24px
    '3xl': '1.875rem', // 30px
    '4xl': '2.25rem', // 36px
    '5xl': '3rem',    // 48px
    '6xl': '3.75rem', // 60px
  },
  fontWeight: {
    light: 300,
    normal: 400,
    medium: 500,
    semibold: 600,
    bold: 700,
    extrabold: 800,
  },
  lineHeight: {
    tight: 1.25,
    normal: 1.5,
    relaxed: 1.75,
  },
};

export const spacing = {
  0: '0',
  0.5: '0.125rem',  // 2px
  1: '0.25rem',     // 4px
  1.5: '0.375rem',  // 6px
  2: '0.5rem',      // 8px
  2.5: '0.625rem',  // 10px
  3: '0.75rem',     // 12px
  3.5: '0.875rem',  // 14px
  4: '1rem',        // 16px
  5: '1.25rem',     // 20px
  6: '1.5rem',      // 24px
  7: '1.75rem',     // 28px
  8: '2rem',        // 32px
  9: '2.25rem',     // 36px
  10: '2.5rem',     // 40px
  11: '2.75rem',    // 44px
  12: '3rem',       // 48px
  14: '3.5rem',     // 56px
  16: '4rem',       // 64px
  20: '5rem',       // 80px
  24: '6rem',       // 96px
  32: '8rem',       // 128px
  40: '10rem',      // 160px
  48: '12rem',      // 192px
  56: '14rem',      // 224px
  64: '16rem',      // 256px
};

export const borderRadius = {
  none: '0',
  sm: '0.125rem',   // 2px
  DEFAULT: '0.25rem', // 4px
  md: '0.375rem',   // 6px
  lg: '0.5rem',     // 8px
  xl: '0.75rem',    // 12px
  '2xl': '1rem',    // 16px
  '3xl': '1.5rem',  // 24px
  full: '9999px',
};

export const shadows = {
  sm: '0 1px 2px 0 rgba(0, 0, 0, 0.05)',
  DEFAULT: '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px 0 rgba(0, 0, 0, 0.06)',
  md: '0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)',
  lg: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
  xl: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
  '2xl': '0 25px 50px -12px rgba(0, 0, 0, 0.25)',
  inner: 'inset 0 2px 4px 0 rgba(0, 0, 0, 0.06)',
  none: 'none',
};

export const breakpoints = {
  sm: '640px',
  md: '768px',
  lg: '1024px',
  xl: '1280px',
  '2xl': '1536px',
};

export const transitions = {
  all: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
  colors: 'color 0.3s cubic-bezier(0.4, 0, 0.2, 1), background-color 0.3s cubic-bezier(0.4, 0, 0.2, 1), border-color 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
  opacity: 'opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
  shadow: 'box-shadow 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
  transform: 'transform 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
};

// Complete theme object
export const theme = {
  colors,
  typography,
  spacing,
  borderRadius,
  shadows,
  breakpoints,
  transitions,
};

export type Theme = typeof theme;