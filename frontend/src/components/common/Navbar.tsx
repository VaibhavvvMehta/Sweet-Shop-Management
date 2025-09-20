import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useCart } from '../../context/CartContext';
import '../../styles/globals.css';

const Navbar: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { isAuthenticated, user, logout, hasRole } = useAuth();
  const { totalItems } = useCart();
  
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    setIsUserMenuOpen(false);
    navigate('/login');
  };

  const isActive = (path: string) => location.pathname === path;

  // Don't show navbar on login/register pages
  if (location.pathname === '/login' || location.pathname === '/register') {
    return null;
  }

  const NavLink = ({ to, children, icon, isActive: active }: {
    to: string;
    children: React.ReactNode;
    icon?: React.ReactNode;
    isActive?: boolean;
  }) => (
    <button
      onClick={() => navigate(to)}
      className={`nav-link ${active ? 'nav-link-active' : ''}`}
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: '0.5rem',
        padding: '0.75rem 1rem',
        background: active ? 'rgba(255, 255, 255, 0.1)' : 'transparent',
        border: 'none',
        borderRadius: 'var(--radius-md)',
        color: 'var(--color-text-inverse)',
        fontSize: '0.875rem',
        fontWeight: '500',
        cursor: 'pointer',
        transition: 'var(--transition-colors)',
        textDecoration: 'none'
      }}
      onMouseEnter={(e) => {
        if (!active) {
          e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.05)';
        }
      }}
      onMouseLeave={(e) => {
        if (!active) {
          e.currentTarget.style.backgroundColor = 'transparent';
        }
      }}
    >
      {icon}
      {children}
    </button>
  );

  return (
    <nav style={{
      position: 'sticky',
      top: 0,
      zIndex: 1000,
      background: 'linear-gradient(135deg, var(--color-primary-600) 0%, var(--color-primary-700) 100%)',
      boxShadow: 'var(--shadow-lg)',
      borderBottom: '1px solid var(--color-primary-800)'
    }}>
      <div className="container">
        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: '1rem 0',
          height: '70px'
        }}>
          {/* Logo/Brand */}
          <div
            onClick={() => navigate(hasRole('ADMIN') ? '/dashboard' : '/')}
            style={{
              display: 'flex',
              alignItems: 'center',
              cursor: 'pointer',
              gap: '0.75rem'
            }}
          >
            <div style={{
              width: '40px',
              height: '40px',
              borderRadius: '50%',
              background: 'linear-gradient(135deg, var(--color-accent-400), var(--color-accent-600))',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '1.25rem',
              fontWeight: 'bold',
              color: 'var(--color-text-primary)'
            }}>
              M
            </div>
            <div>
              <h1 style={{
                fontFamily: 'var(--font-heading)',
                fontSize: '1.5rem',
                fontWeight: '700',
                color: 'var(--color-text-inverse)',
                margin: 0,
                lineHeight: 1
              }}>
                Mithu Sweet Bhandar
              </h1>
              <p style={{
                fontSize: '0.75rem',
                color: 'rgba(255, 255, 255, 0.8)',
                margin: 0,
                fontWeight: '400'
              }}>
                Traditional Indian Sweets
              </p>
            </div>
          </div>

          {/* Desktop Navigation Links */}
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem'
          }} className="hidden-mobile">
            <NavLink 
              to={hasRole('ADMIN') ? "/dashboard" : "/"} 
              isActive={hasRole('ADMIN') ? isActive('/dashboard') : (isActive('/') || isActive('/home'))}
              icon={
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z"/>
                </svg>
              }
            >
              Home
            </NavLink>
            
            <NavLink 
              to="/products" 
              isActive={isActive('/products')}
              icon={
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M7 18c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12L8.1 13h7.45c.75 0 1.41-.41 1.75-1.03L21.7 4H5.21l-.94-2H1zm16 16c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
                </svg>
              }
            >
              Products
            </NavLink>
            
            {isAuthenticated && (
              <>
                <NavLink 
                  to="/orders" 
                  isActive={isActive('/orders')}
                  icon={
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M14 2H6c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 2 2h8c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-5 15l-3-3 1.41-1.41L9 14.17l7.59-7.59L18 8l-9 9z"/>
                    </svg>
                  }
                >
                  Orders
                </NavLink>

                {/* Admin Panel (Admin only) */}
                {hasRole('ADMIN') && (
                  <NavLink 
                    to="/admin" 
                    isActive={isActive('/admin')}
                    icon={
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4z"/>
                      </svg>
                    }
                  >
                    Admin
                  </NavLink>
                )}
              </>
            )}
          </div>

          {/* Right side - Cart and User menu */}
          {isAuthenticated ? (
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '0.75rem'
            }}>
              {/* Shopping Cart */}
              <button
                onClick={() => navigate('/cart')}
                style={{
                  position: 'relative',
                  padding: '0.75rem',
                  background: isActive('/cart') ? 'rgba(255, 255, 255, 0.1)' : 'transparent',
                  border: 'none',
                  borderRadius: 'var(--radius-md)',
                  color: 'var(--color-text-inverse)',
                  cursor: 'pointer',
                  transition: 'var(--transition-colors)'
                }}
                onMouseEnter={(e) => {
                  if (!isActive('/cart')) {
                    e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.05)';
                  }
                }}
                onMouseLeave={(e) => {
                  if (!isActive('/cart')) {
                    e.currentTarget.style.backgroundColor = 'transparent';
                  }
                }}
                title="Shopping Cart"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M7 18c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12L8.1 13h7.45c.75 0 1.41-.41 1.75-1.03L21.7 4H5.21l-.94-2H1zm16 16c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z"/>
                </svg>
                {totalItems > 0 && (
                  <span style={{
                    position: 'absolute',
                    top: '0.25rem',
                    right: '0.25rem',
                    backgroundColor: 'var(--color-accent-500)',
                    color: 'var(--color-text-primary)',
                    fontSize: '0.75rem',
                    fontWeight: 'bold',
                    padding: '0.125rem 0.375rem',
                    borderRadius: '9999px',
                    minWidth: '1.25rem',
                    height: '1.25rem',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    {totalItems}
                  </span>
                )}
              </button>

              {/* User Menu */}
              <div style={{ position: 'relative' }}>
                <button
                  onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.5rem',
                    padding: '0.5rem',
                    background: 'transparent',
                    border: 'none',
                    borderRadius: 'var(--radius-md)',
                    color: 'var(--color-text-inverse)',
                    cursor: 'pointer',
                    transition: 'var(--transition-colors)'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.backgroundColor = 'rgba(255, 255, 255, 0.05)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.backgroundColor = 'transparent';
                  }}
                >
                  <div style={{
                    width: '32px',
                    height: '32px',
                    borderRadius: '50%',
                    backgroundColor: 'var(--color-accent-500)',
                    color: 'var(--color-text-primary)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontSize: '0.875rem',
                    fontWeight: 'bold'
                  }}>
                    {user?.fullName?.charAt(0).toUpperCase() || user?.username?.charAt(0).toUpperCase()}
                  </div>
                  <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start' }} className="hidden-mobile">
                    <span style={{ fontSize: '0.875rem', fontWeight: '500' }}>
                      {user?.fullName || user?.username}
                    </span>
                    <span style={{ fontSize: '0.75rem', opacity: 0.8 }}>
                      {user?.role}
                    </span>
                  </div>
                  <svg 
                    width="16" 
                    height="16" 
                    viewBox="0 0 24 24" 
                    fill="currentColor"
                    style={{
                      transform: isUserMenuOpen ? 'rotate(180deg)' : 'rotate(0deg)',
                      transition: 'transform 0.2s ease'
                    }}
                  >
                    <path d="M7 10l5 5 5-5z"/>
                  </svg>
                </button>

                {/* User Dropdown Menu */}
                {isUserMenuOpen && (
                  <div style={{
                    position: 'absolute',
                    top: '100%',
                    right: 0,
                    marginTop: '0.5rem',
                    backgroundColor: 'var(--color-bg-primary)',
                    borderRadius: 'var(--radius-xl)',
                    boxShadow: 'var(--shadow-xl)',
                    border: '1px solid var(--color-gray-200)',
                    minWidth: '200px',
                    overflow: 'hidden',
                    zIndex: 50
                  }}>
                    <div style={{
                      padding: '1rem',
                      borderBottom: '1px solid var(--color-gray-200)'
                    }}>
                      <p style={{
                        margin: 0,
                        fontSize: '0.875rem',
                        fontWeight: '600',
                        color: 'var(--color-text-primary)'
                      }}>
                        {user?.fullName || user?.username}
                      </p>
                      <p style={{
                        margin: 0,
                        fontSize: '0.75rem',
                        color: 'var(--color-text-secondary)'
                      }}>
                        {user?.email}
                      </p>
                    </div>
                    <button
                      onClick={handleLogout}
                      style={{
                        width: '100%',
                        display: 'flex',
                        alignItems: 'center',
                        gap: '0.5rem',
                        padding: '0.75rem 1rem',
                        background: 'transparent',
                        border: 'none',
                        color: 'var(--color-error)',
                        fontSize: '0.875rem',
                        cursor: 'pointer',
                        transition: 'var(--transition-colors)'
                      }}
                      onMouseEnter={(e) => {
                        e.currentTarget.style.backgroundColor = 'var(--color-gray-50)';
                      }}
                      onMouseLeave={(e) => {
                        e.currentTarget.style.backgroundColor = 'transparent';
                      }}
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M17 7l-1.41 1.41L18.17 11H8v2h10.17l-2.58 2.59L17 17l5-5z"/>
                      </svg>
                      Logout
                    </button>
                  </div>
                )}
              </div>

              {/* Mobile Menu Button */}
              <button
                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                style={{
                  display: 'none',
                  padding: '0.5rem',
                  background: 'transparent',
                  border: 'none',
                  color: 'var(--color-text-inverse)',
                  cursor: 'pointer'
                }}
                className="mobile-menu-btn"
              >
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M3 18h18v-2H3v2zm0-5h18v-2H3v2zm0-7v2h18V6H3z"/>
                </svg>
              </button>
            </div>
          ) : (
            /* Login/Register buttons for non-authenticated users */
            <div style={{ display: 'flex', gap: '0.75rem' }}>
              <button
                onClick={() => navigate('/login')}
                className="btn btn-secondary"
                style={{
                  backgroundColor: 'transparent',
                  borderColor: 'rgba(255, 255, 255, 0.3)',
                  color: 'var(--color-text-inverse)'
                }}
              >
                Login
              </button>
              <button
                onClick={() => navigate('/register')}
                className="btn btn-accent"
              >
                Register
              </button>
            </div>
          )}
        </div>

        {/* Mobile Menu */}
        {isMobileMenuOpen && (
          <div style={{
            padding: '1rem 0',
            borderTop: '1px solid rgba(255, 255, 255, 0.1)',
            display: 'flex',
            flexDirection: 'column',
            gap: '0.5rem'
          }} className="mobile-menu">
            <NavLink 
              to={hasRole('ADMIN') ? "/dashboard" : "/"} 
              isActive={hasRole('ADMIN') ? isActive('/dashboard') : (isActive('/') || isActive('/home'))}
            >
              Home
            </NavLink>
            {isAuthenticated && (
              <>
                <NavLink to="/products" isActive={isActive('/products')}>
                  Products
                </NavLink>
                <NavLink to="/orders" isActive={isActive('/orders')}>
                  Orders
                </NavLink>
                {hasRole('ADMIN') && (
                  <NavLink to="/admin" isActive={isActive('/admin')}>
                    Admin
                  </NavLink>
                )}
              </>
            )}
          </div>
        )}
      </div>

      <style>{`
        @media (max-width: 768px) {
          .hidden-mobile {
            display: none !important;
          }
          .mobile-menu-btn {
            display: block !important;
          }
        }
        
        @media (min-width: 769px) {
          .mobile-menu {
            display: none !important;
          }
        }
      `}</style>
    </nav>
  );
};

export default Navbar;