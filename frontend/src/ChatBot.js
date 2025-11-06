import React, { useState } from 'react';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import { FaPaperPlane } from 'react-icons/fa';
import './ChatBot.css';
const API_BASE = "http://localhost:8080";

const Chatbot = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSend = async () => {
        if (input.trim() === '') return;

        const newMessage = { text: input, sender: 'user' };
        setMessages([...messages, newMessage]);
        setInput('');
        setLoading(true);

        try {
            const response = await axios.get(`${API_BASE}/chat/contextual?q=${encodeURIComponent(input)}`);
            const aiMessage = { text: response.data, sender: 'ai' };
            const indexSources = response.data.indexOf('{"sources":{');
            if (indexSources != -1) {
                aiMessage.text = response.data.substring(0, indexSources) + '\n\n**📚 Sources (grouped)**\n';
                const sourcesMap = JSON.parse(response.data.substring(indexSources));
                for (let key in sourcesMap.sources) {
                    let value = sourcesMap.sources[key];
                    aiMessage.text += '* [' + key + '](' + `${API_BASE}/files/${encodeURIComponent(key)}` + ')\n';
                    for (const source of value) {
                        aiMessage.text += '  * (Chunk) Similarity: ' + source.similarity + '\n';
                    }
                    aiMessage.text += '\n';
                }
            }
            setMessages([...messages, newMessage, aiMessage]);
        } catch (error) {
            console.error("Error fetching AI response", error);
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        setInput(e.target.value);
    };

    const handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            handleSend();
        }
    };

    return (
        <div className="chatbot-container">
            <div className="chatbox">
                {messages.map((message, index) => (
                    <div key={index} className={`message-container ${message.sender}`}>
                        <p className="avatar">{message.sender === 'user' ? "🧑" : "🤖"}</p>
                        <div className={`message ${message.sender}`}>
                            <ReactMarkdown>
                                {message.text}
                            </ReactMarkdown>
                        </div>
                    </div>
                ))}
                {loading && (
                    <div className="message-container ai">
                        <p className="avatar">🤖</p>
                        <div className="message ai">...</div>
                    </div>
                )}
            </div>
            <div className="input-container">
                <input
                    list="options"
                    autocomplete="question"
                    name="question"
                    type="text"
                    value={input}
                    onChange={handleInputChange}
                    onKeyPress={handleKeyPress}
                    placeholder="Type your message..."
                />
                <datalist id="options">
                    <option value="Required degree course work (including semesters courses are offered) - BS program "></option>
                    <option value="Required degree course prerequisites - BS program"></option>
                    <option value="Major admissions requirements BS program"></option>
                    <option value="Typical 4-year course planning guide (on campus or distance student)"></option>
                    <option value="Typical 4-year course planning guide (online campus)"></option>
                    <option value="Pre-approved undergraduate technical electives"></option>
                    <option value="Transfer credit applicability"></option>
                    <option value="Transfer credit process"></option>
                    <option value="Course registration process"></option>
                    <option value="Admission requirements - MS program"></option>
                    <option value="Required course work - MS program"></option>
                    <option value="Required course prerequisites - MS program"></option>
                    <option value="Possible technical electives - MS program"></option>
                    <option value="Information on specialization tracks - MS program"></option>
                    <option value="Thesis vs. non-thesis options - MS program"></option>
                    <option value="Admission requirements - PhD program"></option>
                    <option value="Required course work - PhD program"></option>
                    <option value="Potential minor options - PhD program"></option>
                    <option value="Required course prerequisites - PhD program"></option>
                    <option value="Possible technical electives - PhD program"></option>
                    <option value="Guidance on research focus areas and faculty advisors - PhD program"></option>
                    <option value="Qualifying exam requirements - PhD program"></option>
                    <option value="Dissertation requirements - PhD program"></option>
                    <option value="Funding opportunities - PhD program"></option>
                </datalist>
                <button onClick={handleSend}>
                    <FaPaperPlane />
                </button>
            </div>
        </div>
    );
};

export default Chatbot;