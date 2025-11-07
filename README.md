# 🐱 ChattyCatty – Java + Spring RAG System

This is an AI chat tool from the Agile Wildcats team for SWFE 503, Fall 2025.

## 💬 Introduction

This is an application for serving AI responses to questions related to the University of Arizona's SWFE degrees.

![UofA Women's Wildcat mascot saying Chatty Catty](frontend/public/chatty-catty-logo.jpg)

### Technical details
ChattyCatty is a **Retrieval-Augmented Generation (RAG)** stack built with **Java + Spring Cloud + React + PostgreSQL (pgvector)**.

It comes with a **Makefile-driven developer workflow** for ingestion, reseeding, QA, and demos.

It runs in containers powered by Docker.

---

## ⚙️ Installation

### ⚠️ Requirements

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

### 🔧 Building the System
1. Download this codebase using the green button on the top right above that says `[<> Code]`
   1. If needed, switch to the required branch `git checkout existing-branch` 
2. Prepare the environment:
   1. See the [DEV_COMMANDS](DEV-COMMANDS.md?id=🔑-Environment) for the most updated `.env` file definition.
3. Ensure the dependent services are running:
   1. Docker (Desktop)
   2. Ollama
4. Execute the makefile command to spin up docker containers for the DB, API, and frontend:
   ```bash
   # Navigate to the project
   cd chatty-catty
   # Use make to create and deploy docker containers and seed the chatbot with relevant RAG information.
   make clean run
   ```
5. The following container names are now available in docker:
   * `ragdb`: The Postgres database as defined in the .env file.
   * `chatty-catty-app`: The API tier, visible on localhost:8080 by default.
   * `chatty-catty-frontend`: The UI tier, listening on localhost:3000 by default.

## 🔬 Testing It Out
* Open a browser to the following url to view the frontend: http://localhost:3000
* Joke endpoint for verifying the backend has the right AI configuration: http://localhost:8080/chat/general?message=Tell%20me%20a%20joke
* View the REST API endpoints for direct API testing using [POSTMAN](https://learning.postman.com/docs/getting-started/overview/) or other integration:
  * View directly in browser http://localhost:8080/swagger-ui.html
  * Download the REST JSON API http://localhost:8080/v3/api-docs
  * Download as yaml file http://localhost:8080/v3/api-docs.yaml

NOTE: Add documents into the `docs` folder for automatic ingestion (seeding) into RAG.

---

## 🚀 Quick Start - Development

1. Update the `.env` file with the latest changes from [DEV_COMMANDS](DEV-COMMANDS.md?id=🔑-Environment).
2. Start the containers for the DB, frontend, and REST microservice(s):
   ```bash
   # Using the dev target tails the container logs which is useful during development.
   make dev
   # or use the run target instead  
   make run
   ```
3. Ingest docs. This is only necessary if dev mode was chosen above.
   ```bash
   # Use the make command to start this via command line and view the logs.
   make reseed-logs
   # Or use the [Start Seed] button in the web UI. It will report success or errors encountered. 
   ```
4. Run QA checks:
   ```bash
   # Test if the system is stable using:
   make qa-full
   # Run tests and report coverage (only works if step 1 has been executed first, separately):
   make coverage
   ```

---
### 👨‍💻 Developer Usage

See the [DEV_COMMANDS](DEV-COMMANDS.md) file for the full list and explanation of Makefile targets, which can often be combined into a single call.

---

### 🏗️ Tech Stack
* Backend: Java 24 + Spring Cloud 6 + JPA
* Database: PostgreSQL + pgvector
* Frontend: React + Axios
* Ingestion: PDF/TXT/Markdown parsing + AI embeddings to vector database
* Dev Tools: Docker Compose + Makefile
* AI provider: Ollama
* The containers are used for the system:
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