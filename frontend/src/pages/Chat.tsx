import React, { useState } from 'react';
import ChatSidebar from '../components/ChatSidebar';
import ChatWindow from '../components/ChatWindow';
import '../App.css';
import ChatSearchInput from '../components/ChatSearchInput';

const Chat: React.FC = () => {
  const [query, setQuery] = useState('');
  const [messages, setMessages] = useState<{ user: string; bot: string }[]>([]);
  
  const [sessionId, setSessionId] = useState<number | null>(null);

  const handleLogout = () => {
    localStorage.removeItem('token');
    window.location.href = '/login';
  };

  

  const filteredMessages = messages.filter(
    (m) => m.user.includes(query) || m.bot.includes(query)
  );

  return (
    <div className="flex h-screen font-sans">
      {/* Main Chat Area */}
      <div className="flex flex-col flex-1">
        {/* Top Nav */}
        <div className="bg-white border-b px-4 py-3 flex justify-between items-center">
          <h1 className="text-xl font-semibold">MarketMate Chat</h1>
          <button
            onClick={handleLogout}
            className="bg-red-500 text-white px-4 py-1 rounded hover:bg-red-600"
          >
            Logout
          </button>
        </div>
        <ChatSidebar onSelect={setSessionId} />
        {sessionId && <ChatWindow sessionId={sessionId} />}

        {/* Input Bar */}
        <ChatSearchInput message={messages} setMessages={setMessages} />
      </div>
    </div>
  );
};

export default Chat;
