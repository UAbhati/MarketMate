import React, { useEffect, useRef, useState } from 'react';
import axios from '../axios';
import styles from '../pages/chat.module.css';

interface Message {
  id?: number;
  role: 'user' | 'assistant' | 'system';
  content: string;
  createdAt?: string;
}

export default function ChatWindow({ sessionId }: { sessionId: number }) {
  const [messages, setMessages] = useState<Message[]>([]);
  const bottomRef = useRef<HTMLDivElement | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (sessionId) {
      setLoading(true);
      axios.get(`/api/chat/messages/${sessionId}`)
        .then(res => setMessages(res.data))
        .catch(console.error)
        .finally(() => setLoading(false));
    }
  }, [sessionId]);

  // Scroll to bottom when messages change
  useEffect(() => {
    if (bottomRef.current) {
      bottomRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [messages]);

  return (
    <div className={styles.window}>
      {messages.length === 0 && (
        <div className={styles.welcome}>
          👋 Start typing your question to begin the chat!
        </div>
      )}
      {loading && (
        <div className={styles.loading}>
          Loading messages...
        </div>
      )}
      {messages.map((m, i) => (
        <div
          key={i}
          className={`${styles.message} ${
            m.role === 'user'
              ? styles['user-message']
              : m.role === 'assistant'
              ? styles['bot-message']
              : styles['system-message']
          }`}
        >
          <div>
            <strong>{m.role === 'user' ? 'You' : m.role === 'assistant' ? 'MarketMate' : 'System'}:</strong> {m.content}
          </div>
          {m.createdAt && (
            <div className={styles.timestamp}>
              {new Date(m.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
            </div>
          )}
        </div>
      ))}
      <div ref={bottomRef} />
    </div>
  );
}
