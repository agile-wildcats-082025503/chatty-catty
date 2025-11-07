import React, { useState, useEffect } from 'react';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import { FaPaperPlane } from 'react-icons/fa';
import './ChatBot.css';
const API_BASE = "http://localhost:8080";

const Chatbot = () => {
    const [messages, setMessages] = useState([]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);
    const [count, setCount] = useState(0); // Used to refresh the UI while streaming

    const linkify = (inputText) => {
        var replacedText, replacePattern1, replacePattern2, replacePattern3;

        //URLs starting with http://, https://, or ftp://
        replacePattern1 = /(\b(https?|ftp):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/gim;
        replacedText = inputText.replace(replacePattern1, '[$1]($1)');

        //Change email addresses to mailto:: links.
        replacePattern3 = /(([a-zA-Z0-9\-\_\.])+@[a-zA-Z\_]+?(\.[a-zA-Z]{2,6})+)/gim;
        replacedText = replacedText.replace(replacePattern3, '[$1](mailto:$1)');

        return replacedText;
    }

    useEffect(() => {
        scrollToBottom();
    }, [messages, count]);

    const scrollToBottom = () => {
        const myDiv = document.getElementById("chatbox");
        myDiv.scrollTop = myDiv.scrollHeight;
    }

    const handleSend = async () => {
        if (input.trim() === '') return;

        const newMessage = { text: input, sender: 'user' };
        setMessages([...messages, newMessage]);
        setInput('');
        setLoading(true);
        const start = Date.now();
        var startRendering;

        var aiMessage = { text: '...', sender: 'ai' };
        setMessages([...messages, newMessage, aiMessage]);

        function handleClose() {
            eventSource.close();
            setLoading(false);
            console.log("⏱️ Rendering time: " + (Date.now() - startRendering) + "ms");
            setCount(Date.now());
        }

        const eventSource = new EventSource(`${API_BASE}/chat/contextual?q=${encodeURIComponent(input)}`);
        eventSource.onmessage = (event) => {
            if (aiMessage.text == '...') {
                // Thinking time has completed - blank out the message and log the time
                aiMessage.text = "";
                console.log("⏱️ Thinking time: " + (Date.now() - start) + "ms");
                startRendering = Date.now();
                setCount(Date.now());
            }
            if (!event || !event.data) return;
            let dataNoEvents = event.data.replace(/^{{{/g, "").replace(/}}}$/g, "");
            let answerChunk = dataNoEvents;
            const indexSources = dataNoEvents.indexOf('{"sources":{');
            if (indexSources != -1) {
                aiMessage.text += dataNoEvents.substring(0, indexSources) + '\n\n**📚 Sources (grouped)**\n';
                answerChunk = '';
                aiMessage.text = linkify(aiMessage.text);
                const sourcesMap = JSON.parse(dataNoEvents.substring(indexSources));
                for (let key in sourcesMap.sources) {
                    let val = sourcesMap.sources[key];
                    answerChunk += '* [' + key + '](' + `${API_BASE}/files/${encodeURIComponent(key)}` + ')\n';
                    for (const source of val) {
                        answerChunk += '  * (Chunk) Similarity: ' + source.similarity + '\n';
                    }
                    answerChunk += '\n';
                }
            }
            aiMessage.text += answerChunk;
            setCount(Date.now());
        };
        eventSource.onerror = (error) => {
            console.error("⚠️ Error fetching AI response (check app logs)", error);
            handleClose();
        };
        return () => {
            handleClose();
        };
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
            <div className="chatbox" id="chatbox">
                {messages.map((message, index) => (
                    <div key={index} className={`message-container ${message.sender}`}>
                        <p className="avatar">{message.sender === 'user' ? "🧑" : "🤖"}</p>
                        <div className={`message ${message.sender}`} id={`msg${messages.length}part${count}`}>
                            <ReactMarkdown>
                                {message.text}
                            </ReactMarkdown>
                        </div>
                    </div>
                ))}
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