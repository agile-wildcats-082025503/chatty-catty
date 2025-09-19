import React, { useState, useEffect } from "react";
import axios from "axios";

const API_BASE = "http://localhost:8080";
 
 export default function Upload() {
  const [files, setFiles] = useState([]);
  const [status, setStatus] = useState("");
  const [adminKey, setAdminKey] = useState(""); // kept for backward compatibility (optional)
  const [seedStatus, setSeedStatus] = useState(null);
  const [isPolling, setIsPolling] = useState(false);
  const [token, setToken] = useState(localStorage.getItem("chatty_token") || null);

  useEffect(() => {
    if (token) localStorage.setItem("chatty_token", token);
    else localStorage.removeItem("chatty_token");
  }, [token]);
 
   function onFilesChange(e) {
     setFiles(Array.from(e.target.files));
   }
 
   async function uploadFiles() {
     if (files.length === 0) {
       setStatus("No files selected.");
       return;
     }
     setStatus("Uploading...");
     try {
       const formData = new FormData();
       files.forEach((f) => formData.append("files", f));
      const headers = { "Content-Type": "multipart/form-data" };
      if (token) headers["Authorization"] = `Bearer ${token}`;
      else if (adminKey) headers["X-API-KEY"] = adminKey;

      const resp = await axios.post(`${API_BASE}/docs/uploadFiles`, formData, { headers });
       setStatus(resp.data || "Upload complete.");
     } catch (err) {
       console.error(err);
       setStatus("Upload failed: " + (err?.response?.data || err.message));
     }
   }
 
   async function startSeed() {
    // prefer JWT token; fallback to adminKey
    if (!token && !adminKey) {
      alert("Please login or enter admin API key first");
      return;
    }
    setSeedStatus({ state: "requested", message: "Requesting seed..." });
    try {
      const headers = {};
      if (token) headers["Authorization"] = `Bearer ${token}`;
      else headers["X-API-KEY"] = adminKey;
      await axios.post(`${API_BASE}/admin/seed?docsDir=docs`, null, { headers });
       pollSeedStatus();
     } catch (err) {
       setSeedStatus({ state: "error", message: err?.response?.data || err.message });
     }
   }
 
   function pollSeedStatus() {
     setIsPolling(true);
     const interval = setInterval(async () => {
       try {
        const headers = {};
        if (token) headers["Authorization"] = `Bearer ${token}`;
        else headers["X-API-KEY"] = adminKey;
        const resp = await axios.get(`${API_BASE}/admin/seed/status`, { headers });
         setSeedStatus(resp.data);
         if (!resp.data.running) {
           clearInterval(interval);
           setIsPolling(false);
         }
       } catch (err) {
         clearInterval(interval);
         setIsPolling(false);
         setSeedStatus({ state: "error", message: err?.response?.data || err.message });
       }
     }, 3000);
   }
 
   return (
     <div className="upload">
       <h2>Upload documents</h2>
       <input type="file" multiple onChange={onFilesChange} />
       <div className="buttons">
         <button onClick={uploadFiles}>Upload Selected</button>
       </div>
      <div style={{ marginTop: 8 }}>
        <label>Admin API Key (optional):</label>
        <input type="password" value={adminKey} onChange={e => setAdminKey(e.target.value)} placeholder="X-API-KEY" />
      </div>
      <div style={{ marginTop: 8 }}>
        <label>Auth Token:</label>
        <input type="text" value={token || ""} onChange={e => setToken(e.target.value)} placeholder="Paste JWT here or login via Auth" />
        <div style={{ fontSize: 12, color: "#666" }}>Or use the Auth panel to login and the token will be saved to localStorage.</div>
      </div>
       <div style={{ marginTop: 8 }}>
         <button onClick={startSeed} disabled={!adminKey || isPolling}>Start Seed</button>
         <div>
           {seedStatus && <div>Seed status: {seedStatus.state} — {seedStatus.message}</div>}
         </div>
       </div>
 
       <div className="status">{status}</div>
       <p className="hint">Supported: .pdf, .md, .txt</p>
     </div>
   );
 }
