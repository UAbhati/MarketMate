import React, { createContext, useContext, useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export type ChatMessage = {
    id?: number;
    role: "user" | "assistant" | "system" | "function";
    content: string;
    createdAt?: string;
};

export type Session = {
  id: string;
};

export type ChatContextType = {
  currentSession: Session | null;
  setCurrentSession: (session: Session | null) => void;
  messages: ChatMessage[];
  setMessages: React.Dispatch<React.SetStateAction<ChatMessage[]>>;
  model: string;
  setModel: (model: string) => void;
  tier: string;
  setTier: (tier: string) => void;
};

const defaultContext: ChatContextType = {
  currentSession: null,
  setCurrentSession: () => {},
  messages: [],
  setMessages: () => {},
  model: "gpt-4o",
  setModel: () => {},
  tier: "FREE",
  setTier: () => {},
};

export const ChatContext = createContext<ChatContextType>(defaultContext);

export const useChatContext = () => useContext(ChatContext);

export const ChatContextProvider = ({ children }: { children: React.ReactNode }) => {
  const { sessionId } = useParams();
  const [currentSession, setCurrentSession] = useState<Session | null>(null);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [model, setModel] = useState("gpt-3.5");
  const [tier, setTier] = useState("FREE");

  useEffect(() => {
    if (sessionId) {
      setCurrentSession({ id: sessionId });
    }
  }, [sessionId]);

  return (
    <ChatContext.Provider
      value={{
        currentSession,
        setCurrentSession,
        messages,
        setMessages,
        model,
        setModel,
        tier,
        setTier,
      }}
    >
      {children}
    </ChatContext.Provider>
  );
};
