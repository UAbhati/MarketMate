import React from 'react';
import ChatSidebar from '../components/ChatSidebar';
import ChatWindow from '../components/ChatWindow';
import ChatSearchInput from '../components/ChatSearchInput';
import styles from './chat.module.css';
import Header from '../components/Header';
import { useParams } from 'react-router-dom';
import { useChatContext } from '../context/ChatContext';

const Chat: React.FC = () => {
  const { sessionId } = useParams();
  const {
    setModel,
    setTier,
    messages,
    setMessages
  } = useChatContext();

  const handleLogout = () => {
    localStorage.removeItem('token');
    window.location.href = '/login';
  };

  const onModelOrTierChange = (model: string, tier:string) => {
    setModel(model);
    setTier(tier);
  }

  return (
    <div className={styles.chatContainer}>
      <Header
        onLogout={handleLogout}
        onChange={onModelOrTierChange}
      />
      <div className={styles.chatMainArea}>
        <ChatSidebar />
        <div className={styles.chatWindow}>
          {sessionId &&
            <ChatWindow
              sessionId={sessionId}
              messages={messages}
              setMessages={setMessages}
            />
          }
          {sessionId && <ChatSearchInput />}
        </div>
      </div>
    </div>
  );
};

export default Chat;
