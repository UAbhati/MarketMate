import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from '../axios';

const StartChat: React.FC = () => {
  const [title, setTitle] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleStart = async () => {
    if(title === '') setError("Add some title");
    setError('');
    setLoading(true);
    try {
      const fallback = `Chat ${Math.random().toString(36).substring(2, 8)}`;
      const sessionTitle = title.trim() || fallback;
      const res = await axios.post(`/api/sessions?title=${encodeURIComponent(sessionTitle)}`);
      navigate(`/chat/${res.data.id}`);
    } catch (err) {
      setError('Failed to create session. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-form">
        <h1 className="text-xl font-bold text-center mb-4">MarketMate</h1>
        <h2 className="text-xl font-bold text-center mb-4">Start a New Chat</h2>
        {error && <p className="auth-error">{error}</p>}
        <input
          type="text"
          placeholder="Session Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="auth-input"
          required
        />
        <button onClick={handleStart} className="auth-button" disabled={loading}>
          {loading ? 'Starting...' : 'Start Chat'}
        </button>
        <button
          onClick={() => navigate('/login')}
          className="auth-secondary-button"
        >
          Cancel
        </button>
      </div>
    </div>
  );
};

export default StartChat;
