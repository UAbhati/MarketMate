import { useEffect, useState } from 'react';
import api from '../axios';
import styles from '../pages/chat.module.css';
import { useNavigate, useParams  } from 'react-router-dom';

interface Session {
  id: string;
  title: string;
}

export default function ChatSidebar() {
  const [sessions, setSessions] = useState<Session[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(false);
  const { sessionId } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchSessions = async () => {
      setLoading(true);
      try {
        const res = await api.get('/api/sessions', {
          headers: { 'Cache-Control': 'no-cache' }
        });
        console.log("SESSION RESPONSE:", res.data);
        setSessions(res.data);
      } catch (err) {
        setError(true);
        console.error('Failed to fetch sessions', err);
      } finally {
        setLoading(false); // ✅ move loading=false here to handle both success & error
      }
    };
    fetchSessions();
  }, []);

  const handleSelect = (id: string) => {
    navigate(`/chat/${id}`)
  };

  if (error) return <div className={styles.sidebar}>❌ Error loading sessions</div>;
  if (loading) return <div className={styles.sidebar}>⏳ Loading sessions...</div>;

  return (
    <div className={styles.sidebar}>
      <button
        onClick={() => navigate('/new-chat')}
        className="w-full bg-blue-600 text-white py-2 px-4 rounded hover:bg-blue-700 mb-4"
      >
        + New Chat
      </button>
      <h3>Chat History</h3>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {Array.isArray(sessions) ? (
          sessions.map(s => (
            <li key={s.id}>
              <button
                className={`${styles['session-button']} ${String(s.id) === sessionId ? styles.active : ''}`}
                onClick={() => handleSelect(s.id)}
              >
                {s.title}
              </button>
            </li>
          ))
        ) : (
          <li>No sessions available</li>
        )}
      </ul>
    </div>
  );
}
