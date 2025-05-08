import React, { useState } from 'react';
import styles from './header.module.css';

export default function Header({ onLogout }: { onLogout: () => void }) {
  const [model, setModel] = useState('GPT-4');
  const [tier, setTier] = useState('Free');

  return (
    <div className={styles.header}>
      <h1>MarketMate Chat</h1>
      <div className={styles.controls}>
        <select value={model} onChange={(e) => setModel(e.target.value)}>
          <option value="GPT-4">GPT-4</option>
          <option value="GPT-3.5">GPT-3.5</option>
        </select>
        <select value={tier} onChange={(e) => setTier(e.target.value)}>
          <option value="Free">Free</option>
          <option value="Premium">Premium</option>
        </select>
        <button onClick={onLogout}>Logout</button>
      </div>
    </div>
  );
}
