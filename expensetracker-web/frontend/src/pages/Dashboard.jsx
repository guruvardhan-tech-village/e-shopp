import React, { useState, useEffect } from 'react';
import { 
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, AreaChart, Area 
} from 'recharts';
import { ArrowUpRight, ArrowDownRight, DollarSign, CreditCard, Activity } from 'lucide-react';
import api from '../services/api';
import { useCurrency } from '../context/CurrencyContext';

const Dashboard = () => {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);
  const { formatCurrency, currentSymbol } = useCurrency();
  
  // Balance editing state
  const [initialBalance, setInitialBalance] = useState(() => {
    return parseFloat(localStorage.getItem('initialBalance')) || 0;
  });
  const [isEditingBalance, setIsEditingBalance] = useState(false);
  const [balanceInput, setBalanceInput] = useState(initialBalance);

  useEffect(() => {
    const fetchExpenses = async () => {
      try {
        const res = await api.get('/expenses?size=50');
        setExpenses(res.data.data.content || []);
      } catch (err) {
        console.error('Failed to fetch expenses', err);
      } finally {
        setLoading(false);
      }
    };
    fetchExpenses();
  }, []);

  const handleSaveBalance = () => {
    const newBalance = parseFloat(balanceInput) || 0;
    setInitialBalance(newBalance);
    localStorage.setItem('initialBalance', newBalance.toString());
    setIsEditingBalance(false);
  };

  // Simple aggregations for demo
  const totalExpenses = expenses.reduce((sum, exp) => sum + exp.amount, 0);
  const currentBalance = initialBalance - totalExpenses;
  
  // Prepare chart data (group by date)
  const chartDataMap = expenses.reduce((acc, exp) => {
    const date = new Date(exp.expenseDate).toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    acc[date] = (acc[date] || 0) + exp.amount;
    return acc;
  }, {});

  const chartData = Object.keys(chartDataMap)
    .slice(-7) // last 7 days roughly
    .map(date => ({ date, amount: chartDataMap[date] }))
    .reverse();

  if (chartData.length === 0 && !loading) {
    chartData.push({ date: 'No Data', amount: 0 });
  }

  const StatCard = ({ title, amount, icon: Icon, trend, trendUp, isCurrency = true, onEdit, isEditing, editInput, onEditChange, onEditSave, onEditCancel }) => (
    <div className="card flex flex-col relative overflow-hidden group">
      <div className="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition-opacity">
        <Icon size={64} className="text-primary-500" />
      </div>
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center space-x-2">
          <h3 className="text-[var(--text-muted)] font-medium">{title}</h3>
          {onEdit && !isEditing && (
            <button onClick={onEdit} className="text-blue-500 hover:text-blue-600 opacity-0 group-hover:opacity-100 transition-opacity">
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
            </button>
          )}
        </div>
        <div className="p-2 bg-primary-50 dark:bg-primary-900/30 rounded-lg text-primary-500">
          <Icon size={20} />
        </div>
      </div>
      
      {isEditing ? (
        <div className="flex items-center space-x-2 mb-2">
          <div className="relative flex-1">
            <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">{currentSymbol}</span>
            <input 
              type="number" 
              className="input-field pl-8 py-1 h-9 text-lg font-bold" 
              value={editInput}
              onChange={(e) => onEditChange(e.target.value)}
              autoFocus
            />
          </div>
          <button onClick={onEditSave} className="p-1.5 bg-green-100 text-green-600 rounded hover:bg-green-200">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
          </button>
          <button onClick={onEditCancel} className="p-1.5 bg-gray-100 text-gray-600 rounded hover:bg-gray-200">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        </div>
      ) : (
        <div className="text-3xl font-bold text-[var(--text-main)] mb-2">
          {isCurrency ? formatCurrency(amount) : amount}
        </div>
      )}
      
      <div className="flex items-center text-sm">
        {trend && (
          <span className={`flex items-center font-medium ${trendUp ? 'text-red-500' : 'text-green-500'}`}>
            {trendUp ? <ArrowUpRight size={16} className="mr-1" /> : <ArrowDownRight size={16} className="mr-1" />}
            {trend}
          </span>
        )}
        {title === 'Total Balance' ? (
          <span className="text-[var(--text-muted)] ml-2">Initial funding minus expenses</span>
        ) : (
          <span className="text-[var(--text-muted)] ml-2">vs last month</span>
        )}
      </div>
    </div>
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-[var(--text-main)]">Dashboard</h1>
          <p className="text-[var(--text-muted)]">Your financial overview at a glance.</p>
        </div>
      </div>

      {loading ? (
        <div className="h-64 flex items-center justify-center">
          <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin"></div>
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <StatCard 
              title="Total Balance" 
              amount={currentBalance} 
              icon={DollarSign} 
              onEdit={() => setIsEditingBalance(true)}
              isEditing={isEditingBalance}
              editInput={balanceInput}
              onEditChange={setBalanceInput}
              onEditSave={handleSaveBalance}
              onEditCancel={() => { setIsEditingBalance(false); setBalanceInput(initialBalance); }}
            />
            <StatCard 
              title="Total Expenses" 
              amount={totalExpenses} 
              icon={CreditCard} 
              trend="+12.5%" 
              trendUp={true} 
            />
            <StatCard 
              title="Active Budgets" 
              amount={4} 
              icon={Activity} 
              trend="2 near limit" 
              trendUp={true} 
              isCurrency={false}
            />
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2 card">
              <h3 className="text-lg font-bold text-[var(--text-main)] mb-4">Spending Overview</h3>
              <div className="h-72 w-full">
                <ResponsiveContainer width="100%" height="100%">
                  <AreaChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                    <defs>
                      <linearGradient id="colorAmount" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="5%" stopColor="#8b5cf6" stopOpacity={0.3} />
                        <stop offset="95%" stopColor="#8b5cf6" stopOpacity={0} />
                      </linearGradient>
                    </defs>
                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="var(--border-color)" />
                    <XAxis 
                      dataKey="date" 
                      axisLine={false} 
                      tickLine={false} 
                      tick={{ fill: 'var(--text-muted)', fontSize: 12 }} 
                      dy={10}
                    />
                    <YAxis 
                      axisLine={false} 
                      tickLine={false} 
                      tick={{ fill: 'var(--text-muted)', fontSize: 12 }}
                      tickFormatter={(value) => `${currentSymbol}${value}`}
                    />
                    <Tooltip 
                      contentStyle={{ backgroundColor: 'var(--bg-card)', borderColor: 'var(--border-color)', borderRadius: '8px' }}
                      itemStyle={{ color: 'var(--text-main)' }}
                      formatter={(value) => [formatCurrency(value), 'Amount']}
                    />
                    <Area 
                      type="monotone" 
                      dataKey="amount" 
                      stroke="#8b5cf6" 
                      strokeWidth={3}
                      fillOpacity={1} 
                      fill="url(#colorAmount)" 
                    />
                  </AreaChart>
                </ResponsiveContainer>
              </div>
            </div>

            <div className="card">
              <h3 className="text-lg font-bold text-[var(--text-main)] mb-4">Recent Transactions</h3>
              <div className="space-y-4">
                {expenses.slice(0, 5).map((exp) => (
                  <div key={exp.id} className="flex items-center justify-between p-3 hover:bg-[var(--bg-main)] rounded-lg transition-colors">
                    <div className="flex items-center">
                      <div className="h-10 w-10 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center text-primary-600 mr-3">
                        <CreditCard size={20} />
                      </div>
                      <div>
                        <p className="font-medium text-[var(--text-main)] text-sm">{exp.description}</p>
                        <p className="text-xs text-[var(--text-muted)]">{exp.category?.name || 'Category'}</p>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="font-bold text-[var(--text-main)]">-{formatCurrency(exp.amount)}</p>
                      <p className="text-xs text-[var(--text-muted)]">
                        {new Date(exp.expenseDate).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                ))}
                {expenses.length === 0 && (
                  <p className="text-center text-[var(--text-muted)] py-4">No recent transactions</p>
                )}
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default Dashboard;
