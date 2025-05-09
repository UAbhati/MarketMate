import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import Chat from './pages/Chat';
import StartChat from './pages/StartChat';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const App: React.FC = () => {
  const token = localStorage.getItem('token');

  return (
    <>
      <Router>
        <Routes>
          <Route path="/" element={<Navigate to="/login" />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/chat/:sessionId" element={token ? <Chat /> : <Navigate to="/login" />} />
          <Route path="/new-chat" element={<StartChat />} />
        </Routes>
      </Router>
      <ToastContainer position="top-right" autoClose={3000} aria-label="toast" />
    </>
  );
};

export default App;
