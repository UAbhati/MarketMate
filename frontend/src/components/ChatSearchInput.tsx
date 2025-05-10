import { useState } from "react";
import { sendMessage } from "../services/chatService";
import { ChatMessage, useChatContext } from "../context/ChatContext";
import { toast } from "react-toastify";
import { useParams } from "react-router-dom";
import styles from "../pages/chat.module.css";

export default function ChatSearchInput() {
  const { setMessages, model, tier } = useChatContext();
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [useRealLLM, setUseRealLLM] = useState(true);
  const { sessionId } = useParams();

  const handleSend = async (e?: React.FormEvent) => {
    e?.preventDefault();
    if (!message.trim() || !sessionId) return;
    const userMessage: ChatMessage = {
      role: "user",
      content: message,
    };
  
    setMessages((prev) => [...prev, userMessage]);
    setLoading(true);

    try {
      const res = await sendMessage(
        sessionId,
        message,
        model,
        tier,
        useRealLLM
      );
      setMessages((prev) => [...prev, res]);
      setMessage("");
    } catch (err: any) {
      if (err?.response?.status === 422) {
        toast.warning("⚠️ Please ask only financial-market-related questions.");
      } else {
        toast.error("Something went wrong. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") handleSend();
  };

  return (
    <div className={styles.searchInputWrapper}>
      <div className={styles.searchInputHeader}>
        <label className={styles.checkboxLabel}>
          <input
            type="checkbox"
            checked={useRealLLM}
            onChange={() => setUseRealLLM(!useRealLLM)}
            className="mr-1"
          />
          Use Real LLM (OpenRouter)
        </label>
        {loading && <span className={styles.responseStatus}>Generating response…</span>}
      </div>
      <form className={styles.searchInputBox} onSubmit={handleSend}>
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={handleKeyDown}
          disabled={loading}
          className={styles.textInput}
          placeholder="Ask your financial question..."
        />
        <button
          type="submit"
          disabled={loading || !sessionId}
          className={styles.sendButton}
        >
          {loading ? "..." : "Send"}
        </button>
      </form>
    </div>
  );
}
