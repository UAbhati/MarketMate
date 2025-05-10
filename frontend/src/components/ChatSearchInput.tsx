import { useState } from 'react';
import api from '../axios';
import styles from '../pages/chat.module.css';
import { Message } from '../pages/Chat';

interface Props {
  sessionId: string | undefined;
  onMessageAdded: (message: Message) => void;
  model: string;
  tier: string;
}

export default function ChatSearchInput({ sessionId, onMessageAdded, model, tier }: Props) {
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(false);
  const [popoverMessage, setPopoverMessage] = useState<string | null>(null);

  const sendMessage = async () => {
    if (!query.trim() || !sessionId) return;

    onMessageAdded({ role: 'user', content: query });
    setLoading(true);

    try {
      const response = await api.post('/api/chat', null, {params: {
        sessionId,
        model,
        tier,
        message: query,
      }});
      const aiReply = response.data.content || 'No response';
      onMessageAdded({ role: 'assistant', content: aiReply });
    } catch (err: any) {

      if (err.response?.status === 422) {
        setPopoverMessage(
          "❌ Please ask financial-market-related questions.\n" +
          "💡 Try: 'Q1 results for TCS' or 'market value of Reliance today'."
        );
      } else {
        onMessageAdded({ role: 'assistant', content: "financial-market-related questions Only." });
      }
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
      {popoverMessage && (
        <div className="popover-error">
          <p>{popoverMessage}</p>
          <button onClick={() => setPopoverMessage(null)}>×</button>
        </div>
      )}
    </div>
  );
}
