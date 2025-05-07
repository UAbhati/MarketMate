import React, { useEffect, useState } from 'react';
import axios from '../axios';

interface Session {
  id: number;
  title: string;
}

export default function ChatSidebar({ onSelect }: { onSelect: (id: number) => void }) {
  const [sessions, setSessions] = useState<Session[]>([]);

  useEffect(() => {
    axios.get('/api/chat/sessions', { params: { userId: 'demo_user' } })
      .then(res => setSessions(res.data))
      .catch(console.error);
  }, []);

  return (
    <div className="sidebar">
      <h3>Chat History</h3>
      <ul>
        {sessions.map(s => (
          <li key={s.id} onClick={() => onSelect(s.id)}>{s.title}</li>
        ))}
      </ul>
    </div>
  );
}
