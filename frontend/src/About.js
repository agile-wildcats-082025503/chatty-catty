import React, { useContext, useEffect } from 'react';
import SimpleTable from './SimpleTable';
import ReactMarkdown from 'react-markdown';

export default function About() {
  // From the Agile Wildcats powerpoint slides "MVP 1 Summary Notes from Team"
  const authors = [
    { id: 1, Name: 'Ariel A.', Role: 'Quality Manager' },
    { id: 2, Name: 'Robert D.', Role: 'Architect / Lead Developer' },
    { id: 3, Name: 'Justin L.', Role: 'Requirements Manager' },
    { id: 4, Name: 'Karri F.', Role: 'UI/UX Manager' },
    { id: 5, Name: 'Richard G.', Role: 'Scrum Master' },
  ];

  // Just grabbed a section of the README.md file here.
  const readmeSection = `
# 🐱 ChattyCatty – Java + Spring RAG System

This is an AI chat tool from the Agile Wildcats team for SWFE 503, Fall 2025.

## 💬 Introduction

This is an application for serving AI responses to questions related to the University of Arizona's SWFE degrees.

![UofA Women's Wildcat mascot saying Chatty Catty](chatty-catty-logo_400x400.jpg)

### 💻 Technical details
ChattyCatty is a **Retrieval-Augmented Generation (RAG)** stack built with **Java + Spring Cloud + React + PostgreSQL (pgvector)**.

It comes with a **Makefile-driven developer workflow** for ingestion, reseeding, QA, and demos.

It runs in containers powered by Docker.
`;

  return (
    <div class="about">
      <h1>
        About
      </h1>
      <ReactMarkdown>
        {readmeSection}
      </ReactMarkdown>
      <h2>
        Contributors
      </h2>
      <SimpleTable data={authors} />
    </div>
  );
}