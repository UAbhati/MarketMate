import React, { useEffect, useState } from 'react';
import axios from '../axios';

interface Message {
  id?: number;
  role: string;
  content: string;
}

export default function ChatWindow({ sessionId }: { sessionId: number }) {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');

  useEffect(() => {
    axios.get(`/api/chat/messages/${sessionId}`)
      .then(res => setMessages(res.data))
      .catch(console.error);
  }, [sessionId]);

  const sendMessage = async () => {
    const newMsg: Message = { role: 'user', content: input };
    await axios.post(`/api/chat/messages/${sessionId}`, newMsg);
    setMessages([...messages, newMsg]);
    setInput('');
  };

  return (
    <div className="chat-window">
      <div className="messages">
        {messages.map((m, i) => (
          <div key={i} className={m.role}>{m.content}</div>
        ))}
      </div>
      <div className="input">
        <input value={input} onChange={e => setInput(e.target.value)} placeholder="Type a message" />
        <button onClick={sendMessage}>Send</button>
      </div>
    </div>
  );
}
