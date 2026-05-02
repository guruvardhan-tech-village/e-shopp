import React, { useState, useEffect } from 'react';
import { 
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, AreaChart, Area 
} from 'recharts';
import { ArrowUpRight, ArrowDownRight, DollarSign, CreditCard, Activity } from 'lucide-react';
import api from '../services/api';

const Dashboard = () => {
  const [expenses, setExpenses] = useState([]);
  const [loading, setLoading] = useState(true);

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

  // Simple aggregations for demo
  const totalExpenses = expenses.reduce((sum, exp) => sum + exp.amount, 0);
  
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

  const StatCard = ({ title, amount, icon: Icon, trend, trendUp }) => (
    <div className="card flex flex-col relative overflow-hidden group">
      <div className="absolute top-0 right-0 p-4 opacity-10 group-hover:opacity-20 transition-opacity">
        <Icon size={64} className="text-primary-500" />
      </div>
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-[var(--text-muted)] font-medium">{title}</h3>
        <div className="p-2 bg-primary-50 dark:bg-primary-900/30 rounded-lg text-primary-500">
          <Icon size={20} />
        </div>
      </div>
      <div className="text-3xl font-bold text-[var(--text-main)] mb-2">
        ${amount.toLocaleString('en-US', { minimumFractionDigits: 2 })}
      </div>
      <div className="flex items-center text-sm">
        <span className={`flex items-center font-medium ${trendUp ? 'text-red-500' : 'text-green-500'}`}>
          {trendUp ? <ArrowUpRight size={16} className="mr-1" /> : <ArrowDownRight size={16} className="mr-1" />}
          {trend}
        </span>
        <span className="text-[var(--text-muted)] ml-2">vs last month</span>
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
              amount={12500.00} 
              icon={DollarSign} 
              trend="+2.5%" 
              trendUp={false} 
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
                      tickFormatter={(value) => `$${value}`}
                    />
                    <Tooltip 
                      contentStyle={{ backgroundColor: 'var(--bg-card)', borderColor: 'var(--border-color)', borderRadius: '8px' }}
                      itemStyle={{ color: 'var(--text-main)' }}
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
                      <p className="font-bold text-[var(--text-main)]">-${exp.amount.toFixed(2)}</p>
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
