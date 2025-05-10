import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../axios';

const Register: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);
    try {
      console.log("Calling:", api.defaults.baseURL + '/auth/login');
      await api.post('/api/auth/register', { email, password });
      setSuccess('Registered successfully! Redirecting...');
      setTimeout(() => navigate('/login'), 1000);
    } catch (err: any) {
      console.log(err.message);
      setError('Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <form onSubmit={handleSubmit} className="auth-form">
        <h2 className="text-xl font-bold text-center mb-4">Register</h2>
        {error && <p className="auth-error">{error}</p>}
        {success && <p className="auth-success">{success}</p>}
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="Email"
          className="auth-input"
          required
        />
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="Password"
          className="auth-input"
          required
        />
        <button type="submit" className="auth-button" disabled={loading}>
          {loading ? 'Registering...' : 'Register'}
        </button>
        <button
          type="button"
          onClick={() => navigate('/login')}
          className="auth-secondary-button"
        >
          Back to Login
        </button>
      </form>
    </div>
  );
};

export default Register;
