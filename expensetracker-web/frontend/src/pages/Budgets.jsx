import React, { useState, useEffect } from 'react';
import { Plus, Wallet, AlertTriangle } from 'lucide-react';
import api from '../services/api';

const Budgets = () => {
  const [budgets, setBudgets] = useState([]);
  const [loading, setLoading] = useState(true);
  const currentMonth = new Date().getMonth() + 1;
  const currentYear = new Date().getFullYear();

  useEffect(() => {
    fetchBudgets();
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

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-[var(--text-main)]">Budgets</h1>
          <p className="text-[var(--text-muted)]">Track limits for {new Date().toLocaleString('default', { month: 'long' })} {currentYear}</p>
        </div>
        <button className="btn-primary flex items-center shrink-0">
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
              <div key={budget.id} className="card">
                <div className="flex items-center justify-between mb-4">
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
                  <span className="text-[var(--text-muted)]">Spent: <span className="font-bold text-[var(--text-main)]">${spent.toFixed(2)}</span></span>
                  <span className="text-[var(--text-muted)]">Limit: <span className="font-bold text-[var(--text-main)]">${budget.limitAmount.toFixed(2)}</span></span>
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
    </div>
  );
};

export default Budgets;
