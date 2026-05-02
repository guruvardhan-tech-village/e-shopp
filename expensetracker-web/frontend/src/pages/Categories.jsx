import React, { useState, useEffect } from 'react';
import { Plus, Tags, Edit2, Trash2 } from 'lucide-react';
import api from '../services/api';

const Categories = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [newCategoryName, setNewCategoryName] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const res = await api.get('/categories');
      setCategories(res.data.data || []);
    } catch (err) {
      console.error('Failed to fetch categories', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAddCategory = async (e) => {
    e.preventDefault();
    if (!newCategoryName.trim()) return;
    
    setIsSubmitting(true);
    try {
      await api.post('/categories', { name: newCategoryName });
      setNewCategoryName('');
      setIsModalOpen(false);
      fetchCategories(); // Refresh the list
    } catch (err) {
      console.error('Failed to add category', err);
      alert(err.response?.data?.message || 'Failed to add category');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this category? Expenses in this category will be moved to "Other".')) return;
    try {
      await api.delete(`/categories/${id}`);
      fetchCategories();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to delete category');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-[var(--text-main)]">Categories</h1>
          <p className="text-[var(--text-muted)]">Manage your expense categories</p>
        </div>
        <button 
          onClick={() => setIsModalOpen(true)}
          className="btn-primary flex items-center shrink-0"
        >
          <Plus size={18} className="mr-2" />
          Add Category
        </button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">
        {loading ? (
          <div className="col-span-full py-12 flex justify-center">
            <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin"></div>
          </div>
        ) : categories.length === 0 ? (
          <div className="col-span-full py-12 text-center text-[var(--text-muted)] card">
            No categories found. Create one to get started!
          </div>
        ) : (
          categories.map((cat) => (
            <div key={cat.id} className="card group relative">
              <div className="absolute top-2 right-2 flex space-x-1 opacity-0 group-hover:opacity-100 transition-opacity">
                {!cat.isDefault && (
                  <button 
                    onClick={() => handleDelete(cat.id)}
                    className="p-1.5 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/30 rounded-lg"
                  >
                    <Trash2 size={14} />
                  </button>
                )}
              </div>
              <div className="flex items-center space-x-3 mb-2">
                <div className="p-2 bg-primary-100 dark:bg-primary-900/30 rounded-lg text-primary-600 dark:text-primary-400">
                  <Tags size={20} />
                </div>
                <h3 className="font-bold text-[var(--text-main)]">{cat.name}</h3>
              </div>
              {cat.isDefault && (
                <span className="inline-block mt-2 px-2 py-1 text-xs font-medium bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400 rounded-md">
                  Default Category
                </span>
              )}
            </div>
          ))
        )}
      </div>

      {/* Add Category Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <div className="bg-[var(--bg-card)] rounded-xl w-full max-w-md shadow-xl overflow-hidden border border-[var(--border-color)]">
            <div className="p-4 border-b border-[var(--border-color)] flex justify-between items-center">
              <h2 className="text-lg font-bold text-[var(--text-main)]">Add New Category</h2>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="text-[var(--text-muted)] hover:text-[var(--text-main)]"
              >
                &times;
              </button>
            </div>
            <form onSubmit={handleAddCategory} className="p-4 space-y-4">
              <div>
                <label className="block text-sm font-medium text-[var(--text-muted)] mb-1">Category Name</label>
                <input
                  type="text"
                  className="input-field"
                  placeholder="e.g., Groceries"
                  value={newCategoryName}
                  onChange={(e) => setNewCategoryName(e.target.value)}
                  required
                  autoFocus
                />
              </div>
              <div className="flex justify-end space-x-2 pt-4">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="btn-secondary"
                  disabled={isSubmitting}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn-primary"
                  disabled={isSubmitting}
                >
                  {isSubmitting ? 'Saving...' : 'Save Category'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Categories;
