import { Dispatch, SetStateAction, useState } from 'react';

interface searchInputProps {
  message: {
        user: string;
        bot: string;
    }[];
  setMessages: Dispatch<SetStateAction<{ user: string; bot: string; }[]>>;
}

export default function ChatSearchInput({ message, setMessages }: searchInputProps ) {
    const [input, setInput] = useState('');
    const handleSend = () => {
        if (!input.trim()) return;
        // Mock response for now
        const userMsg = input;
        const botReply = `This is a response to: "${input}"`;
        setMessages((prev) => [...prev, { user: userMsg, bot: botReply }]);
        setInput('');
    };

    return (
        <div className="p-4 border-t bg-white flex gap-2">
            <input
                type="text"
                placeholder="Ask something..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
                className="flex-1 px-4 py-2 border rounded"
                onKeyDown={(e) => e.key === 'Enter' && handleSend()}
            />
            <button
                onClick={handleSend}
                className="bg-blue-600 text-white px-4 rounded hover:bg-blue-700"
            >
                Send
            </button>
        </div>
    );
}
