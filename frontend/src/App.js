// frontend/src/App.js
import React, { useState } from "react";
import Chat from "./Chat";
import Upload from "./Upload";
import Auth from "./Auth";

export default function App() {
  const [user, setUser] = useState(null);

  function handleLogin(data) {
    setUser({ username: data.username });
  }

  return (
    <div>
      <h1>🐱 ChattyCatty</h1>
      {!user ? (
        <Auth onLogin={handleLogin} />
      ) : (
        <>
          <p>Welcome, {user.username}! (<a href="#" onClick={() => {localStorage.removeItem("chatty_token"); setUser(null);}}>Logout</a>)</p>
          <Upload />
        </>
      )}
    </div>
      <div className="app">
        <header>
          <h1>RAG Assistant (Spring Boot)</h1>
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
  );
}
