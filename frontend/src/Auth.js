// frontend/src/Auth.js
import React, { useState } from "react";
import axios from "axios";

const API_BASE = "http://localhost:8080";

export default function Auth({ onLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isRegister, setIsRegister] = useState(false);
  const [error, setError] = useState("");

  async function submit() {
    try {
      const endpoint = isRegister ? "/auth/register" : "/auth/login";
      const body = isRegister
        ? { username, password, admin: false }
        : { username, password };
      const resp = await axios.post(API_BASE + endpoint, body);
      if (resp.data.token) {
        localStorage.setItem("chatty_token", resp.data.token);
        onLogin(resp.data);
      }
      setError("");
    } catch (err) {
      setError(err?.response?.data?.error || err.message);
    }
  }

  return (
    <div className="auth">
      <h2>{isRegister ? "Register" : "Login"}</h2>
      <input
        placeholder="Username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <button onClick={submit}>{isRegister ? "Register" : "Login"}</button>
      <div>
        <a href="#" onClick={() => setIsRegister(!isRegister)}>
          {isRegister ? "Have an account? Login" : "Need an account? Register"}
        </a>
      </div>
      {error && <div style={{ color: "red" }}>{error}</div>}
    </div>
  );
}
