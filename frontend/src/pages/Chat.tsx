import React, { useState } from 'react';
import ChatSidebar from '../components/ChatSidebar';
import ChatWindow from '../components/ChatWindow';
import ChatSearchInput from '../components/ChatSearchInput';
import styles from './chat.module.css';
import Header from '../components/Header';

const Chat: React.FC = () => {
  const [query, setQuery] = useState('');
  const [messages, setMessages] = useState<{ user: string; bot: string }[]>([]);
  const [sessionId, setSessionId] = useState<number | null>(null);

  const handleLogout = () => {
    localStorage.removeItem('token');
    window.location.href = '/login';
  };

  return (
    <div className={styles.chatContainer}>
      <div className={styles.chatMain}>
        <Header onLogout={handleLogout}/>
        <div className={styles.chatBody}>
          <ChatSidebar onSelect={setSessionId} />
          <div className={styles.chatContent}>
            {sessionId && <ChatWindow sessionId={sessionId} />}
          </div>
        </div>
        <ChatSearchInput message={messages} setMessages={setMessages} />
      </div>
    </div>
  );
};

export default Chat;
