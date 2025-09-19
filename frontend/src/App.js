// frontend/src/App.js
import React, { useState } from "react";
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
  );
}
