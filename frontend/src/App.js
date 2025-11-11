import React, { useContext, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
// Structural imports
import Header from './Header';
import NavBar from './NavBar';
import Footer from './Footer';
// Routes listed below
import ChatBot from './ChatBot';
import Upload from './Upload';
import About from './About';
import NotFound from './NotFound';

export default function App() {

  return (
    <Router>
      <div className="app">
        <Header />
        <main>
          <div className="left">
            <NavBar />
          </div>
          <div className="right">
            <Routes>
              <Route path="/" element={<ChatBot />} />
              <Route path="/Upload" element={<Upload />} />
              <Route path="/About" element={<About />} />
              <Route path="*" element={<NotFound />} />
            </Routes>
          </div>
        </main>
        <Footer />
      </div>
    </Router>
  );
}
