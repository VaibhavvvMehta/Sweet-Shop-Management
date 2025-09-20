import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LoginRequest } from '../types';
import './auth.css';

interface LoginForm {
  usernameOrEmail: string;
  password: string;
}

interface FormErrors {
  usernameOrEmail?: string;
  password?: string;
  general?: string;
}

const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login, isLoading, error, clearError, user } = useAuth();
  
  const [form, setForm] = useState<LoginForm>({
    usernameOrEmail: '',
    password: ''
  });
  const [errors, setErrors] = useState<FormErrors>({});
  const [showPassword, setShowPassword] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  useEffect(() => {
    // Check for success message from registration
    if (location.state?.message) {
      setSuccessMessage(location.state.message);
      // Clear the state to prevent showing the message on refresh
      window.history.replaceState({}, document.title);
    }
  }, [location]);

  useEffect(() => {
    // Handle redirect after successful login
    if (user && !isLoading && !error) {
      if (user.role === 'ADMIN') {
        navigate('/dashboard'); // Admin goes to "Welcome Owner" page
      } else {
        navigate('/'); // Regular user goes to home page
      }
    }
  }, [user, isLoading, error, navigate]);

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};

    // Username or email validation
    if (!form.usernameOrEmail.trim()) {
      newErrors.usernameOrEmail = 'Username or email is required';
    } else if (form.usernameOrEmail.trim().length < 3) {
      newErrors.usernameOrEmail = 'Username or email must be at least 3 characters';
    }

    // Password validation
    if (!form.password) {
      newErrors.password = 'Password is required';
    } else if (form.password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({
      ...prev,
      [name]: value
    }));

    // Clear error when user starts typing
    if (errors[name as keyof FormErrors]) {
      setErrors(prev => ({
        ...prev,
        [name]: undefined
      }));
    }

    // Clear success message when user starts typing
    if (successMessage) {
      setSuccessMessage('');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      clearError();
      
      const loginData: LoginRequest = {
        usernameOrEmail: form.usernameOrEmail.trim(),
        password: form.password
      };
      
      await login(loginData);
      
      // Redirect based on user role - need to get fresh user data
      // Since login updates AuthContext, we'll use useEffect to handle redirect
    } catch (error: any) {
      console.error('Login error:', error);
      setErrors({
        general: error.response?.data?.message || error.message || 'Login failed. Please try again.'
      });
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-background">
        <div className="auth-pattern"></div>
      </div>
      
      <div className="auth-content">
        <div className="auth-card">
          <div className="auth-header">
            <div className="brand-logo">
              <div className="logo-icon">M</div>
              <div className="brand-text">
                <h1>Mithu Sweet Bhandar</h1>
                <p>Traditional Sweets Since 1950</p>
              </div>
            </div>
            <h2>Welcome Back</h2>
            <p>Sign in to your account and enjoy our delicious sweets</p>
          </div>

          <form onSubmit={handleSubmit} className="auth-form">
            {successMessage && (
              <div className="success-message">
                <i className="icon-check"></i>
                {successMessage}
              </div>
            )}

            {(errors.general || error) && (
              <div className="error-message general-error">
                <i className="icon-warning"></i>
                {errors.general || error}
              </div>
            )}

            <div className="form-group">
              <label htmlFor="usernameOrEmail" className="form-label">
                Username or Email <span className="required">*</span>
              </label>
              <div className="input-wrapper">
                <input
                  type="text"
                  id="usernameOrEmail"
                  name="usernameOrEmail"
                  value={form.usernameOrEmail}
                  onChange={handleInputChange}
                  className={`form-input ${errors.usernameOrEmail ? 'error' : ''}`}
                  placeholder="Enter your username or email"
                  disabled={isLoading}
                  autoComplete="username"
                />
                <i className="input-icon icon-user"></i>
              </div>
              {errors.usernameOrEmail && (
                <div className="error-message">{errors.usernameOrEmail}</div>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="password" className="form-label">
                Password <span className="required">*</span>
              </label>
              <div className="input-wrapper">
                <input
                  type={showPassword ? 'text' : 'password'}
                  id="password"
                  name="password"
                  value={form.password}
                  onChange={handleInputChange}
                  className={`form-input ${errors.password ? 'error' : ''}`}
                  placeholder="Enter your password"
                  disabled={isLoading}
                  autoComplete="current-password"
                />
                <i className="input-icon icon-lock"></i>
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setShowPassword(!showPassword)}
                  disabled={isLoading}
                  aria-label={showPassword ? 'Hide password' : 'Show password'}
                >
                  <i className={`icon-${showPassword ? 'eye-off' : 'eye'}`}></i>
                </button>
              </div>
              {errors.password && <div className="error-message">{errors.password}</div>}
            </div>

            <div className="form-options">
              <label className="checkbox-wrapper">
                <input type="checkbox" />
                <span className="checkmark"></span>
                Remember me
              </label>
              <Link to="/forgot-password" className="forgot-link">
                Forgot password?
              </Link>
            </div>

            <button
              type="submit"
              className={`btn btn-primary btn-large ${isLoading ? 'loading' : ''}`}
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <div className="loading-spinner"></div>
                  Signing In...
                </>
              ) : (
                <>
                  <i className="icon-login"></i>
                  Sign In
                </>
              )}
            </button>
          </form>

          <div className="auth-footer">
            <p>Don't have an account?</p>
            <Link to="/register" className="auth-link">
              Create a new account
            </Link>
          </div>
        </div>

        <div className="auth-features">
          <h3>Welcome to Mithu Sweet Bhandar</h3>
          <div className="features-grid">
            <div className="feature-item">
              <div className="feature-icon">🏆</div>
              <h4>Award Winning</h4>
              <p>Recognized for excellence in traditional sweet making</p>
            </div>
            <div className="feature-item">
              <div className="feature-icon">🌿</div>
              <h4>Pure Ingredients</h4>
              <p>Only the finest and purest ingredients used</p>
            </div>
            <div className="feature-item">
              <div className="feature-icon">👨‍🍳</div>
              <h4>Expert Craftsmanship</h4>
              <p>Made by master confectioners with decades of experience</p>
            </div>
            <div className="feature-item">
              <div className="feature-icon">🎁</div>
              <h4>Special Occasions</h4>
              <p>Perfect for festivals, celebrations, and gifts</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;