import React, { useState } from 'react';
import axios from '../axios';
import styles from './chatSearchInput.module.css';

export default function ChatSearchInput({
  message,
  setMessages
}: {
  message: { user: string; bot: string }[];
  setMessages: (messages: { user: string; bot: string }[]) => void;
}) {
  const [query, setQuery] = useState('');

  const sendMessage = async () => {
    if (!query.trim()) return;

    const userMsg = { user: query, bot: '...' };
    setMessages([...message, userMsg]);

    // simulate bot or call API
    const response = await axios.post('/api/chat', { query });
    const botReply = response.data.answer || 'No response';

    setMessages([...message, { user: query, bot: botReply }]);
    setQuery('');
  };

  return (
    <div className={styles.searchInput}>
      <input
        type="text"
        value={query}
        onChange={e => setQuery(e.target.value)}
        placeholder="Ask something..."
      />
      <button onClick={sendMessage}>Send</button>
    </div>
  );
}
