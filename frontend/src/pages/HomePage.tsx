import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import './HomePage.css';

const HomePage: React.FC = () => {
  const navigate = useNavigate();
  const { user, isAuthenticated } = useAuth();
  
  // Check if user is admin
  const isAdmin = user?.role === 'ADMIN';

  if (isAdmin) {
    // Simple admin homepage
    return (
      <div className="homepage">
        <section className="hero-section">
          <div className="hero-background">
            <div className="hero-pattern"></div>
          </div>
          <div className="hero-content">
            <div className="hero-text">
              <h1>Welcome Owner! 👋</h1>
              <p>Let's manage and make your sweet shop even better.</p>
              <div className="hero-actions">
                <button 
                  className="btn btn-primary btn-large"
                  onClick={() => navigate('/admin')}
                >
                  Manage Sweets
                </button>
                <button 
                  className="btn btn-outline btn-large"
                  onClick={() => navigate('/orders')}
                >
                  View Orders
                </button>
              </div>
            </div>
          </div>
        </section>
      </div>
    );
  }

  // Simple user homepage
  return (
    <div className="homepage">
      <section className="hero-section">
        <div className="hero-background">
          <div className="hero-pattern"></div>
        </div>
        <div className="hero-content">
          <div className="hero-text">
            <h1>Welcome to Mithu Sweet Bhandar! 🍬</h1>
            <p>Let's explore our delicious collection of traditional Indian sweets.</p>
            <div className="hero-actions">
              <button 
                className="btn btn-primary btn-large"
                onClick={() => navigate('/products')}
              >
                Order Products
              </button>
              {!isAuthenticated && (
                <button 
                  className="btn btn-outline btn-large"
                  onClick={() => navigate('/login')}
                >
                  Login to Order
                </button>
              )}
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default HomePage;
