import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css';
import { ChatContextProvider } from "./context/ChatContext";

const rootElement = document.getElementById('root');
if (rootElement) {
  ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
      <ChatContextProvider>
        <App />
      </ChatContextProvider>
    </React.StrictMode>
  );
}
