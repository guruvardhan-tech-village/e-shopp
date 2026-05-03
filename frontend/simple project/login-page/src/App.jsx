import { useState } from 'react';
import './App.css';

function App() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (username.trim()) {
      setIsLoggedIn(true);
    }
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    setUsername('');
    setPassword('');
  };

  return (
    <div className="container">
      {isLoggedIn ? (
        <div className="card welcome-card">
          <div className="success-icon">✨</div>
          <h1>Welcome, <span className="highlight">{username}</span>!</h1>
          <p>We're glad to have you back.</p>
          <button className="btn outline" onClick={handleLogout}>Logout</button>
        </div>
      ) : (
        <div className="card login-card">
          <h2>Welcome Back</h2>
          <p className="subtitle">Please enter your details to sign in.</p>
          
          <form onSubmit={handleSubmit}>
            <div className="input-group">
              <label htmlFor="username">Username</label>
              <input
                type="text"
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Enter your username"
                required
              />
            </div>
            
            <div className="input-group">
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                required
              />
            </div>
            
            <button type="submit" className="btn primary">Sign In</button>
          </form>
        </div>
      )}
    </div>
  );
}

export default App;
