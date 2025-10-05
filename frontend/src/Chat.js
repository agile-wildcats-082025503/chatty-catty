import React, { useState } from "react";
import axios from "axios";
import ReactMarkdown from "react-markdown";

const API_BASE = "http://localhost:8080";

export default function Chat() {
  const [q, setQ] = useState("");
  const [loading, setLoading] = useState(false);
  const [response, setResponse] = useState(null); // { answer, markdown, html, sources }

  async function ask() {
    if (!q.trim()) return;
    setLoading(true);
    setResponse(null);
    try {
      const url = `${API_BASE}/chat/contextual?q=${encodeURIComponent(q)}`;
      const resp = await axios.get(url);
      setResponse(resp.data);
    } catch (err) {
      console.error(err);
      alert("Error: " + err?.response?.data + ": " + err.message);
    } finally {
      setLoading(false);
    }
  }

  function renderBody() {
    if (!response) return null;
    return (
      <div className="answer">
        <h3>🤖 Answer</h3>
        <pre className="response">{response.answer}</pre>
      </div>
    );
  }

  return (
    <div className="chat">
      <h2>Ask the assistant</h2>

      <div className="controls">
        <input
          type="text"
          autocomplete="question"
          name="question"
          value={q}
          onChange={(e) => setQ(e.target.value)}
          placeholder="Type your question..."
          onKeyDown={(e) => {
            if (e.key === "Enter") ask();
          }}
        />
        <button onClick={ask} disabled={loading}>
          {loading ? "Thinking..." : "Ask"}
        </button>
      </div>

      <div className="result">{renderBody()}</div>

      {response?.sources && (
        <div className="sources">
          <h3>📚 Sources (grouped)</h3>
          {Object.entries(response.sources).map(([source, docs]) => (
            <details key={source} open>
              <summary>{source} — {docs.length} chunk(s)</summary>
              <ul>
                {docs.map((d, idx) => (
                  <li key={idx}>
                    <div className="chunk"><pre>{d.content}</pre></div>
                    <div className="sim">similarity: {d.similarity.toFixed(2)}</div>
                  </li>
                ))}
              </ul>
            </details>
          ))}
        </div>
      )}
    </div>
  );
}
