import React, { createContext, useContext, useState, useEffect } from 'react';

const CurrencyContext = createContext();

export const useCurrency = () => useContext(CurrencyContext);

export const CurrencyProvider = ({ children }) => {
  const [currency, setCurrency] = useState(() => {
    return localStorage.getItem('currency') || 'USD';
  });

  useEffect(() => {
    localStorage.setItem('currency', currency);
  }, [currency]);

  // Using standard Intl formatter
  const formatCurrency = (amount) => {
    try {
      return new Intl.NumberFormat(navigator.language, {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      }).format(amount);
    } catch (e) {
      // Fallback if browser doesn't support the currency code properly
      return `${currency} ${Number(amount).toFixed(2)}`;
    }
  };

  const currencies = [
    { code: 'USD', name: 'US Dollar', symbol: '$' },
    { code: 'INR', name: 'Indian Rupee', symbol: '₹' },
    { code: 'EUR', name: 'Euro', symbol: '€' },
    { code: 'GBP', name: 'British Pound', symbol: '£' },
    { code: 'JPY', name: 'Japanese Yen', symbol: '¥' },
    { code: 'CNY', name: 'Chinese Yuan', symbol: '¥' },
    { code: 'RUB', name: 'Russian Ruble', symbol: '₽' }
  ];

  const currentSymbol = currencies.find(c => c.code === currency)?.symbol || '$';

  return (
    <CurrencyContext.Provider value={{ currency, setCurrency, formatCurrency, currencies, currentSymbol }}>
      {children}
    </CurrencyContext.Provider>
  );
};
