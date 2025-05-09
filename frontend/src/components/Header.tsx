import { useEffect, useState } from 'react';
import styles from '../pages/chat.module.css';

interface Props {
  onLogout: () => void;
  onChange: (model: string, tier: string) => void;
}

export default function Header({ onLogout, onChange }: Props) {
  const [model, setModel] = useState('gpt-3.5');
  const [tier, setTier] = useState('FREE');

  useEffect(() => {
    onChange(model, tier);
  }, [model, tier]);

  return (
    <div className={styles.header}>
      <h1>MarketMate Chat</h1>
      <div className={styles.controls}>
        <select value={model} onChange={(e) => setModel(e.target.value)}>
          <option value="gpt-3.5">GPT-3.5</option>
          <option value="gpt-4">GPT-4</option>
          <option value="gpt-4o">GPT-4o</option>
        </select>
        <select value={tier} onChange={(e) => setTier(e.target.value)}>
          <option value="FREE">Free</option>
          <option value="TIER_1">Tier-1</option>
          <option value="TIER_2">Tier-2</option>
          <option value="TIER_3">Tier-3</option>
        </select>
        <button onClick={onLogout}>Logout</button>
      </div>
    </div>
  );
}
