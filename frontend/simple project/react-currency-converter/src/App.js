import React, { useState, useEffect } from "react";
import "./App.css";

function App() {
  const [amount, setAmount] = useState(1);
  const [fromCurrency, setFromCurrency] = useState("USD");
  const [toCurrency, setToCurrency] = useState("INR");
  const [exchangeRate, setExchangeRate] = useState(null);
  const [currencies, setCurrencies] = useState([]);

  // Fetch currency list + rates
  useEffect(() => {
    fetch(`https://api.exchangerate-api.com/v4/latest/${fromCurrency}`)
      .then((res) => res.json())
      .then((data) => {
        setCurrencies(Object.keys(data.rates));
        setExchangeRate(data.rates[toCurrency]);
      });
  }, [fromCurrency, toCurrency]);

  const convertedAmount = (amount * exchangeRate).toFixed(2);

  return (
    <div className="container">
      <h1>Currency Converter 💱</h1>

      <div className="box">
        <input
          type="number"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />

        <select value={fromCurrency} onChange={(e) => setFromCurrency(e.target.value)}>
          {currencies.map((cur) => (
            <option key={cur}>{cur}</option>
          ))}
        </select>

        <span>→</span>

        <select value={toCurrency} onChange={(e) => setToCurrency(e.target.value)}>
          {currencies.map((cur) => (
            <option key={cur}>{cur}</option>
          ))}
        </select>
      </div>

      <h2>
        {amount} {fromCurrency} = {convertedAmount} {toCurrency}
      </h2>
    </div>
  );
}

export default App;