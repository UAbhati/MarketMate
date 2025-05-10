import { useState } from "react";
import { sendMessage } from "../services/chatService";
import { ChatMessage, useChatContext } from "../context/ChatContext";
import { toast } from "react-toastify";
import { useParams } from "react-router-dom";

export default function ChatSearchInput() {
  const { setMessages, model, tier } = useChatContext();
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [useRealLLM, setUseRealLLM] = useState(true);
  const { sessionId } = useParams();

  const handleSend = async (e?: React.FormEvent) => {
    e?.preventDefault();
    console.log("🚀 Send clicked:", message);
    if (!message.trim() || !sessionId) return;
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
    <div className="p-3 border-t border-gray-200 bg-white">
      <div className="flex items-center justify-between gap-2 mb-2">
        <label className="text-sm text-gray-700">
          <input
            type="checkbox"
            checked={useRealLLM}
            onChange={() => setUseRealLLM(!useRealLLM)}
            className="mr-1"
          />
          Use Real LLM (OpenRouter)
        </label>
        {loading && <span className="text-sm text-gray-500">Generating response…</span>}
      </div>
      <div className="flex">
        <input
          type="text"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={handleKeyDown}
          disabled={loading}
          className="flex-1 border border-gray-300 rounded-l px-3 py-2"
          placeholder="Ask your financial question..."
        />
        <button
          type="button"
          onClick={handleSend}
          disabled={loading || !sessionId}
          className="bg-blue-600 text-white px-4 py-2 mx-2 rounded-r"
        >
          {loading ? '...' : 'Send'}
        </button>
      </div>
    </div>
  );
}
