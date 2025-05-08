import React, { useEffect, useState } from 'react';
import axios from '../axios';
import styles from './chatWindow.module.css';

interface Message {
  id?: number;
  role: string;
  content: string;
}

export default function ChatWindow({ sessionId }: { sessionId: number }) {
  const [messages, setMessages] = useState<Message[]>([]);

  useEffect(() => {
    if (sessionId) {
      axios.get(`/api/chat/messages/${sessionId}`)
        .then(res => setMessages(res.data))
        .catch(console.error);
    }
  }, [sessionId]);

  if (!sessionId) {
    return <div className={styles.window}><p className={styles.welcome}>👋 Welcome to MarketMate! Start a new chat to begin.</p></div>;
  }

  return (
    <div className={styles.window}>
      {messages.length === 0 && (
        <div className={styles.welcome}>
          👋 Start typing your question to begin the chat!
        </div>
      )}
      {messages.map((m, i) => (
        <div
          key={i}
          className={`${styles.message} ${
            m.role === 'user' ? styles['user-message'] : styles['bot-message']
          }`}
        >
          {m.content}
        </div>
      ))}
    </div>
  );
}
