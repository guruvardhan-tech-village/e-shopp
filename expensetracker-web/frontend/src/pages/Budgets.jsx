import React, { useState, useEffect } from 'react';
import { Plus, Wallet, AlertTriangle, Trash2 } from 'lucide-react';
import api from '../services/api';
import { useCurrency } from '../context/CurrencyContext';

const Budgets = () => {
  const [budgets, setBudgets] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const currentMonth = new Date().getMonth() + 1;
  const currentYear = new Date().getFullYear();
  const { formatCurrency, currentSymbol } = useCurrency();

  // Modal state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  
  // Form state
  const [formData, setFormData] = useState({
    categoryId: '',
    limitAmount: '',
    month: currentMonth,
    year: currentYear
  });

  useEffect(() => {
    fetchBudgets();
    fetchCategories();
  }, []);

  const fetchBudgets = async () => {
    try {
      const res = await api.get(`/budgets?month=${currentMonth}&year=${currentYear}`);
      setBudgets(res.data.data || []);
    } catch (err) {
      console.error('Failed to fetch budgets', err);
    } finally {
      setLoading(false);
    }
  };

  const fetchCategories = async () => {
    try {
      const res = await api.get('/categories');
      setCategories(res.data.data || []);
      if (res.data.data && res.data.data.length > 0 && !formData.categoryId) {
        setFormData(prev => ({ ...prev, categoryId: res.data.data[0].id }));
      }
    } catch (err) {
      console.error('Failed to fetch categories', err);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleAddBudget = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      await api.post('/budgets', {
        categoryId: parseInt(formData.categoryId),
        limitAmount: parseFloat(formData.limitAmount),
        month: parseInt(formData.month),
        year: parseInt(formData.year)
      });
      setIsModalOpen(false);
      setFormData({
        categoryId: categories.length > 0 ? categories[0].id : '',
        limitAmount: '',
        month: currentMonth,
        year: currentYear
      });
      fetchBudgets();
    } catch (err) {
      console.error('Failed to add budget', err);
      alert(err.response?.data?.message || 'Failed to add budget');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this budget?')) return;
    try {
      await api.delete(`/budgets/${id}`);
      fetchBudgets();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to delete budget');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-[var(--text-main)]">Budgets</h1>
          <p className="text-[var(--text-muted)]">Track limits for {new Date().toLocaleString('default', { month: 'long' })} {currentYear}</p>
        </div>
        <button 
          onClick={() => setIsModalOpen(true)}
          className="btn-primary flex items-center shrink-0"
        >
          <Plus size={18} className="mr-2" />
          Set Budget
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {loading ? (
          <div className="col-span-full py-12 flex justify-center">
            <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin"></div>
          </div>
        ) : budgets.length === 0 ? (
          <div className="col-span-full py-12 text-center text-[var(--text-muted)] card">
            No budgets set for this month. Set one up!
          </div>
        ) : (
          budgets.map((budget) => {
            // Mocking spent amount since backend currently doesn't return it
            const spent = Math.random() * budget.limitAmount; 
            const percentage = Math.min((spent / budget.limitAmount) * 100, 100);
            const isNearLimit = percentage > 80;
            const isOverLimit = percentage >= 100;

            return (
              <div key={budget.id} className="card relative group">
                <div className="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button 
                    onClick={() => handleDelete(budget.id)}
                    className="p-1.5 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/30 rounded-lg"
                  >
                    <Trash2 size={14} />
                  </button>
                </div>
                <div className="flex items-center justify-between mb-4 pr-6">
                  <div className="flex items-center space-x-3">
                    <div className="p-2 bg-primary-100 dark:bg-primary-900/30 rounded-lg text-primary-600 dark:text-primary-400">
                      <Wallet size={20} />
                    </div>
                    <h3 className="font-bold text-[var(--text-main)]">{budget.category?.name}</h3>
                  </div>
                  {isNearLimit && !isOverLimit && <AlertTriangle size={20} className="text-yellow-500" />}
                  {isOverLimit && <AlertTriangle size={20} className="text-red-500" />}
                </div>

                <div className="mb-2 flex justify-between text-sm">
                  <span className="text-[var(--text-muted)]">Spent: <span className="font-bold text-[var(--text-main)]">{formatCurrency(spent)}</span></span>
                  <span className="text-[var(--text-muted)]">Limit: <span className="font-bold text-[var(--text-main)]">{formatCurrency(budget.limitAmount)}</span></span>
                </div>

                <div className="w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2.5 overflow-hidden">
                  <div 
                    className={`h-2.5 rounded-full transition-all duration-500 ${
                      isOverLimit ? 'bg-red-500' : isNearLimit ? 'bg-yellow-500' : 'bg-primary-500'
                    }`}
                    style={{ width: `${percentage}%` }}
                  ></div>
                </div>
                
                <div className="mt-2 text-xs text-right text-[var(--text-muted)]">
                  {percentage.toFixed(1)}% used
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Add Budget Modal */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <div className="bg-[var(--bg-card)] rounded-xl w-full max-w-md shadow-xl overflow-hidden border border-[var(--border-color)]">
            <div className="p-4 border-b border-[var(--border-color)] flex justify-between items-center">
              <h2 className="text-lg font-bold text-[var(--text-main)]">Set New Budget</h2>
              <button 
                onClick={() => setIsModalOpen(false)}
                className="text-[var(--text-muted)] hover:text-[var(--text-main)]"
              >
                &times;
              </button>
            </div>
            <form onSubmit={handleAddBudget} className="p-4 space-y-4">
              <div>
                <label className="block text-sm font-medium text-[var(--text-muted)] mb-1">Category</label>
                <select
                  name="categoryId"
                  className="input-field"
                  value={formData.categoryId}
                  onChange={handleInputChange}
                  required
                >
                  <option value="" disabled>Select a category</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-[var(--text-muted)] mb-1">Monthly Limit ({currentSymbol})</label>
                <input
                  type="number"
                  name="limitAmount"
                  step="0.01"
                  min="0.01"
                  className="input-field"
                  placeholder="0.00"
                  value={formData.limitAmount}
                  onChange={handleInputChange}
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
                  {isSubmitting ? 'Saving...' : 'Save Budget'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Budgets;
