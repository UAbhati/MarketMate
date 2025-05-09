import { useState } from 'react';
import axios from '../axios';
import styles from '../pages/chat.module.css';
import { Message } from '../pages/Chat';

interface Props {
  sessionId: number | null;
  onMessageAdded: (message: Message) => void;
  model: string;
  tier: string;
}

export default function ChatSearchInput({ sessionId, onMessageAdded, model, tier }: Props) {
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(false);

  const sendMessage = async () => {
    if (!query.trim() || !sessionId) return;

    onMessageAdded({ role: 'user', content: query });
    setLoading(true);

    try {
      const response = await axios.post(`/api/sessions/${sessionId}`, {
        message: query,
        model,
        tier,
      });
      const aiReply = response.data.content || 'No response';
      onMessageAdded({ role: 'assistant', content: aiReply });
    } catch (err) {
      onMessageAdded({ role: 'assistant', content: '⚠️ Failed to get response.' });
    } finally {
      setQuery('');
      setLoading(false);
    }
  };

  return (
    <div className={styles.searchInput}>
      <input
        type="text"
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        placeholder="Ask something..."
      />
      <button onClick={sendMessage} disabled={loading || !sessionId}>
        {loading ? '...' : 'Send'}
      </button>
    </div>
  );
}
