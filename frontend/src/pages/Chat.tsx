import React, { useState } from 'react';
import ChatSidebar from '../components/ChatSidebar';
import ChatWindow from '../components/ChatWindow';
import ChatSearchInput from '../components/ChatSearchInput';
import styles from './chat.module.css';
import Header from '../components/Header';
import { useParams } from 'react-router-dom';

export interface Message {
  id?: number;            // optional, for messages already saved to DB
  role: 'user' | 'assistant' | 'system'; // identifies who sent the message
  content: string;        // actual message text
  createdAt?: string;     // optional: if you want to show timestamps
}


const Chat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const { sessionId } = useParams();
  const [model, setModel] = useState('gpt-3.5');
  const [tier, setTier] = useState('FREE');

  const handleLogout = () => {
    localStorage.removeItem('token');
    window.location.href = '/login';
  };

  const handleMessageAdded = (msg: Message) => {
    setMessages(prev => [...prev, msg]);
  };

  return (
    <div className={styles.chatContainer}>
      <div className={styles.chatMain}>
        <Header onLogout={handleLogout} onChange={(m, t) => {
          setModel(m);
          setTier(t);
        }} />
        <div className="app">
          <ChatSidebar />
          <div className="chat-window">
            {sessionId && <ChatWindow sessionId={parseInt(sessionId)} />}
            {sessionId && <ChatSearchInput
              sessionId={parseInt(sessionId)}
              onMessageAdded={handleMessageAdded}
              model={model}
              tier={tier}
            />}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Chat;
