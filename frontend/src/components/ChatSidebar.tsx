import React, { useEffect, useState } from 'react';
import axios from 'axios';
import styles from './chatSidebar.module.css';

interface Session {
  id: number;
  title: string;
}

export default function ChatSidebar({ onSelect }: { onSelect: (id: number) => void }) {
  const [sessions, setSessions] = useState<Session[]>([]);
  const [activeId, setActiveId] = useState<number | null>(null);

  useEffect(() => {
    axios.get('/api/chat/sessions', { params: { userId: 'demo_user' } })
      .then(res => setSessions(res.data))
      .catch(console.error);
  }, []);

  const handleSelect = (id: number) => {
    setActiveId(id);
    onSelect(id);
  };

  return (
    <div className={styles.sidebar}>
      <h3>Chat History</h3>
      <ul>
        {sessions.map(s => (
          <li key={s.id}>
            <button
              className={`${styles['session-button']} ${s.id === activeId ? styles.active : ''}`}
              onClick={() => handleSelect(s.id)}
            >
              {s.title}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}
