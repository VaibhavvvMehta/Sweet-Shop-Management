import React, { useState, useEffect } from 'react';
import { Sweet, SweetCategory, PricingType } from '../types';
import { sweetService } from '../services/sweetService';
import { useAuth } from '../context/AuthContext';
import { formatPrice } from '../utils/currency';
import './AdminPage.css';

const AdminPage: React.FC = () => {
  const { hasRole } = useAuth();
  const [sweets, setSweets] = useState<Sweet[]>([]);
  const [loading, setLoading] = useState(true);
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingSweet, setEditingSweet] = useState<Sweet | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    price: '',
    quantity: '',
    imageUrl: '',
    pricingType: 'PER_ITEM' as PricingType
  });
  const [message, setMessage] = useState<{ type: 'success' | 'error', text: string } | null>(null);

  useEffect(() => {
    if (!hasRole('ADMIN')) {
      setMessage({ type: 'error', text: 'Access denied. Admin privileges required.' });
      return;
    }
    fetchSweets();
  }, [hasRole]);

  const fetchSweets = async () => {
    try {
      setLoading(true);
      const data = await sweetService.getAllSweets();
      setSweets(data);
    } catch (error) {
      setMessage({ type: 'error', text: 'Failed to fetch sweets' });
    } finally {
      setLoading(false);
    }
  };

  const showMessage = (type: 'success' | 'error', text: string) => {
    setMessage({ type, text });
    setTimeout(() => setMessage(null), 3000);
  };

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      price: '',
      quantity: '',
      imageUrl: '',
      pricingType: 'PER_ITEM' as PricingType
    });
  };

  const openAddModal = () => {
    resetForm();
    setIsAddModalOpen(true);
  };

  const openEditModal = (sweet: Sweet) => {
    setFormData({
      name: sweet.name,
      description: sweet.description,
      price: sweet.price.toString(),
      quantity: sweet.quantity.toString(),
      imageUrl: sweet.imageUrl || '',
      pricingType: sweet.pricingType || 'PER_ITEM'
    });
    setEditingSweet(sweet);
    setIsEditModalOpen(true);
  };

  const closeModals = () => {
    setIsAddModalOpen(false);
    setIsEditModalOpen(false);
    setEditingSweet(null);
    resetForm();
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!formData.name || !formData.price || !formData.quantity) {
      showMessage('error', 'Please fill in all required fields');
      return;
    }

    const sweetData = {
      name: formData.name.trim(),
      description: formData.description.trim(),
      price: parseFloat(formData.price),
      quantity: parseInt(formData.quantity),
      category: 'OTHER' as SweetCategory, // Default category since we removed category selection
      imageUrl: formData.imageUrl.trim() || undefined,
      pricingType: formData.pricingType as PricingType
    };

    try {
      if (isEditModalOpen && editingSweet) {
        await sweetService.updateSweet(editingSweet.id, sweetData);
        showMessage('success', 'Sweet updated successfully');
      } else {
        await sweetService.createSweet(sweetData);
        showMessage('success', 'Sweet added successfully');
      }
      
      closeModals();
      fetchSweets();
    } catch (error) {
      showMessage('error', isEditModalOpen ? 'Failed to update sweet' : 'Failed to add sweet');
    }
  };

  const handleDelete = async (sweetId: number, sweetName: string) => {
    if (!window.confirm(`Are you sure you want to delete "${sweetName}"?`)) {
      return;
    }

    try {
      await sweetService.deleteSweet(sweetId);
      showMessage('success', 'Sweet deleted successfully');
      fetchSweets();
    } catch (error) {
      showMessage('error', 'Failed to delete sweet');
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  if (!hasRole('ADMIN')) {
    return (
      <div className="admin-page">
        <div className="admin-header">
          <h1>Access Denied</h1>
          <p>You don't have permission to access this page.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="admin-page">
      {/* Header */}
      <div className="admin-header">
        <h1>Sweet Management</h1>
        <p>Manage your sweet inventory</p>
        <button className="btn-primary" onClick={openAddModal}>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
          </svg>
          Add New Sweet
        </button>
      </div>

      {/* Message */}
      {message && (
        <div className={`message message-${message.type}`}>
          {message.text}
        </div>
      )}

      {/* Content */}
      {loading ? (
        <div className="loading">
          <div className="spinner"></div>
          <p>Loading sweets...</p>
        </div>
      ) : (
        <div className="admin-content">
          {sweets.length === 0 ? (
            <div className="empty-state">
              <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
              </svg>
              <h3>No sweets found</h3>
              <p>Start by adding your first sweet to the inventory</p>
              <button className="btn-primary" onClick={openAddModal}>
                Add Sweet
              </button>
            </div>
          ) : (
            <div className="sweets-grid">
              {sweets.map(sweet => (
                <div key={sweet.id} className="sweet-card admin-card">
                  <div className="sweet-image">
                    {sweet.imageUrl ? (
                      <img src={sweet.imageUrl} alt={sweet.name} />
                    ) : (
                      <div className="image-placeholder">
                        <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z"/>
                        </svg>
                      </div>
                    )}
                    <div className="stock-badge">
                      Stock: {sweet.quantity}
                    </div>
                  </div>
                  
                  <div className="sweet-info">
                    <h3>{sweet.name}</h3>
                    <p className="description">{sweet.description}</p>
                    <div className="sweet-meta">
                      <span className="price">
                        {formatPrice(sweet.price)} {sweet.pricingType === 'PER_KG' ? 'per kg' : 'per item'}
                      </span>
                      <span className="stock">Stock: {sweet.quantity}</span>
                    </div>
                  </div>
                  
                  <div className="card-actions">
                    <button 
                      className="btn-secondary"
                      onClick={() => openEditModal(sweet)}
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
                      </svg>
                      Edit
                    </button>
                    <button 
                      className="btn-danger"
                      onClick={() => handleDelete(sweet.id, sweet.name)}
                    >
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                      </svg>
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* Add Modal */}
      {isAddModalOpen && (
        <div className="modal-overlay" onClick={closeModals}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Add New Sweet</h2>
              <button className="close-btn" onClick={closeModals}>
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                </svg>
              </button>
            </div>
            
            <form onSubmit={handleSubmit} className="modal-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="name">Sweet Name *</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    placeholder="e.g., Rasgulla"
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="pricingType">Pricing Type *</label>
                  <select
                    id="pricingType"
                    name="pricingType"
                    value={formData.pricingType}
                    onChange={handleInputChange}
                    required
                  >
                    <option value="PER_ITEM">Per Item</option>
                    <option value="PER_KG">Per Kg</option>
                  </select>
                </div>
                
                <div className="form-group">
                  <label htmlFor="price">
                    Price (₹) * {formData.pricingType === 'PER_KG' ? 'per kg' : 'per item'}
                  </label>
                  <input
                    type="number"
                    id="price"
                    name="price"
                    value={formData.price}
                    onChange={handleInputChange}
                    min="0"
                    step="0.01"
                    placeholder="0.00"
                    required
                  />
                </div>
              </div>
              
              <div className="form-group">
                <label htmlFor="description">Description</label>
                <textarea
                  id="description"
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  placeholder="Describe the sweet..."
                  rows={3}
                />
              </div>
              
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="quantity">Quantity *</label>
                  <input
                    type="number"
                    id="quantity"
                    name="quantity"
                    value={formData.quantity}
                    onChange={handleInputChange}
                    min="0"
                    placeholder="0"
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="imageUrl">Image URL</label>
                  <input
                    type="url"
                    id="imageUrl"
                    name="imageUrl"
                    value={formData.imageUrl}
                    onChange={handleInputChange}
                    placeholder="https://example.com/image.jpg"
                  />
                </div>
              </div>
              
              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={closeModals}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Add Sweet
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Edit Modal */}
      {isEditModalOpen && editingSweet && (
        <div className="modal-overlay" onClick={closeModals}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Edit Sweet</h2>
              <button className="close-btn" onClick={closeModals}>
                <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
                </svg>
              </button>
            </div>
            
            <form onSubmit={handleSubmit} className="modal-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="edit-name">Sweet Name *</label>
                  <input
                    type="text"
                    id="edit-name"
                    name="name"
                    value={formData.name}
                    onChange={handleInputChange}
                    placeholder="e.g., Rasgulla"
                    required
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="edit-pricingType">Pricing Type *</label>
                  <select
                    id="edit-pricingType"
                    name="pricingType"
                    value={formData.pricingType}
                    onChange={handleInputChange}
                    required
                  >
                    <option value="PER_ITEM">Per Item</option>
                    <option value="PER_KG">Per Kg</option>
                  </select>
                </div>
                
                <div className="form-group">
                  <label htmlFor="edit-price">
                    Price (₹) * {formData.pricingType === 'PER_KG' ? 'per kg' : 'per item'}
                  </label>
                  <input
                    type="number"
                    id="edit-price"
                    name="price"
                    value={formData.price}
                    onChange={handleInputChange}
                    min="0"
                    step="0.01"
                    placeholder="0.00"
                    required
                  />
                </div>
              </div>
              
              <div className="form-group">
                <label htmlFor="edit-description">Description</label>
                <textarea
                  id="edit-description"
                  name="description"
                  value={formData.description}
                  onChange={handleInputChange}
                  placeholder="Describe the sweet..."
                  rows={3}
                />
              </div>
              
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="edit-quantity">Quantity *</label>
                  <input
                    type="number"
                    id="edit-quantity"
                    name="quantity"
                    value={formData.quantity}
                    onChange={handleInputChange}
                    min="0"
                    placeholder="0"
                    required
                  />
                </div>
                
                <div className="form-group">
                  <label htmlFor="edit-imageUrl">Image URL</label>
                  <input
                    type="url"
                    id="edit-imageUrl"
                    name="imageUrl"
                    value={formData.imageUrl}
                    onChange={handleInputChange}
                    placeholder="https://example.com/image.jpg"
                  />
                </div>
              </div>
              
              <div className="modal-actions">
                <button type="button" className="btn-secondary" onClick={closeModals}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary">
                  Update Sweet
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminPage;