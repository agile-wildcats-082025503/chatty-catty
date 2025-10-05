// frontend/src/App.js
import React, { useState } from "react";
import Chat from "./Chat";
import Upload from "./Upload";

export default function App() {

  return (
    <div>
      <div className="app">
        <h1>🐱 ChattyCatty</h1>
        <header>
          <h1>Chatty Catty UofA Chatbot</h1>
          <p>Ask questions and upload docs to extend the knowledge base.</p>
        </header>

        <main>
          <div className="left">
            <Upload />
          </div>

          <div className="right">
            <Chat />
          </div>
        </main>

        <footer>
          <small>Frontend talking to backend at <code>http://localhost:8080</code></small>
        </footer>
      </div>
    </div>
  );
}
