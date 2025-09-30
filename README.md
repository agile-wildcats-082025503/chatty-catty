# 🐱 ChattyCatty – Java + Spring RAG System

[![Java CI with Maven](https://github.com/agile-wildcats-082025503/chatty-catty/actions/workflows/tests.yml/badge.svg)](https://github.com/agile-wildcats-082025503/chatty-catty/actions/workflows/tests.yml)

[![codecov](https://codecov.io/gh/your-org/chattycatty/branch/main/graph/badge.svg)](https://codecov.io/gh/your-org/chattycatty)

This is an AI chat tool from the Agile Wildcats team for SWFE 503, Fall 2025.

## Introduction

ChattyCatty is a **Retrieval-Augmented Generation (RAG)** stack built with **Java + Spring Boot + React + PostgreSQL (pgvector)**.

It comes with a **Makefile-driven developer workflow** for ingestion, reseeding, QA, and demos.
It runs in Virtual Machines (VMs) powered by Docker.

![UofA Women's Wildcat mascot saying Chatty Catty](frontend/public/chatty-catty-logo.jpg)

---

This is an application for serving AI responses to questions related to the University of Arizona's SWFE degrees. 

## Installation

### Requirements

1. [Git Client](http://git-scm.com)
2. [Java Development Kit](https://www.oracle.com/java/technologies/downloads/)
3. Development environment (choose one):
    1. [Visual Studio Code](https://code.visualstudio.com/download)
    2. [JetBrains IntelliJ](https://www.jetbrains.com/idea/download)
    3. [Eclipse](https://www.eclipse.org/downloads/)
4. [Maven](https://maven.apache.org/download.cgi?) (Java builder)
5. [Docker](https://www.docker.com/products/docker-desktop/)
    1. Requires [Windows Subsystem for Linux](https://learn.microsoft.com/en-us/windows/wsl/install)
        1. [Windows fix regarding WSL updates](https://stackoverflow.com/questions/76479583/docker-desktop-requires-a-newer-wsl-kernel-version)
        2. Ensure it's set to run on restart using Settings->General->Start Docker...
6. [Node.js and npm](https://www.geeksforgeeks.org/node-js/how-to-download-and-install-node-js-and-npm/)
   1. Check the "Install the necessary tools" checkbox when installing Node.js (ex: installs Chocolatey on Windows)
7. [Make](https://medium.com/@divyeshpal07/mastering-gnu-make-and-makefiles-the-developers-guide-22df3b97cc0d)
   1. Windows: `choco install make` - if choco isn't found, go back to install Node.js above and Change the install to check the "Necessary Tools" option.
   2. Linux: `sudo apt-get install build-essential` or [Install make](https://linuxvox.com/blog/install-make-linux/)
8. [Lombok setup for your IDE](https://projectlombok.org/setup/)
9. [Install Ollama](https://ollama.com/download) for the open source AI engine

### Building the System
1. Download this codebase using the green button on the top right above that says `[<> Code]`
2. Prepare the environment:
   1. Create a file in the project root called .env and fill it with the following info:
   ```
   POSTGRES_INSTANCE=ragdb
   POSTGRES_SERVER=localhost:5432
   POSTGRES_SERVER_SPRING=postgres:5432
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=postgres
   SPRING_PROFILES_ACTIVE=dev
   ```
   2. This should be done the first time and repeated whenever maven or npm dependencies are changed. 
   ```bash
   # Navigate to the project
   cd chatty-catty

   # Install dependencies
   mvn clean install
   cd frontend
   npm install
   cd ..
   ```
3. Execute the makefile command to spin up docker VMs for the DB, API, and frontend:
   ```bash
   make clean dev
   ```
   This command builds and starts the VMS:
   * ragdb: The database
   * chatty-catty-app - The API tier
   * chatty-catty-frontend - The UI tier
4. Ensure ollama is running before executing commands in the frontend.

## Troubleshooting

* If docker complains about mvnw, use `dos2unix mvnw` in the project root directory
* Errors with missing table, delete the ragdb container from Docker and then run `mvn clean install`

## Testing It Out
* Open a browser to the following url to view the frontend: http://localhost:3000
* Joke endpoint for verifying the backend has the right OPENAI API KEY: http://localhost:8080/chat/general?message=Tell%20me%20a%20joke
* View the REST API endpoints for direct API testing using [POSTMAN](https://learning.postman.com/docs/getting-started/overview/) or other integration:
  * View directly in browser http://localhost:8080/swagger-ui.html
  * Download the REST JSON API http://localhost:8080/v3/api-docs
  * Download as yaml file http://localhost:8080/v3/api-docs.yaml

NOTE: Add documents into the docs folder for automatic ingestion into RAG.

---

## 🚀 Quick Start

1. Start dev stack:
   ```bash
   make dev
   ```
2. Ingest docs (requires admin JWT):
   ```bash
   make reseed-logs
   ```
5. Run QA checks:
   ```bash
   make qa-full
   ```

---
### Developer Usage

See the [DEV_COMMANDS](DEV-COMMANDS.md) file for the full list and explanation of Makefile commands.

---

### 🏗️ Tech Stack
* Backend: Java 24 + Spring Boot 6 + JPA
* Database: PostgreSQL + pgvector
* Frontend: React + Axios
* Ingestion: PDF/TXT/Markdown parsing + OpenAI embeddings
* Dev Tools: Docker Compose + Makefile
* AI provider: Ollama
* The VMs are used for the system:
  * ragdb - The database
  * chatty-catty-frontend - The UI tier
  * chatty-catty-app - The API tier

### 🎉 Demo-Ready
ChattyCatty is developer-friendly and presentation-ready:

* ASCII art banners in demo/dev commands
* One-command demo setup (make demo or make demo-auto)
* Built-in QA + monitoring

---

## 🐱 Happy chatting with ChattyCatty!