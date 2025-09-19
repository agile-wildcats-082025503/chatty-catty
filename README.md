# chatty-catty
AI chat tool for SWFE 503, Fall 2025

![Alt text](doc/resources/media/chatty-catty-logo.jpg)

## Introduction

This is an application for serving AI responses to questions related to the University of Arizona's SWFE degrees. 

## Requirements

1. [Git Client](http://git-scm.com)
2. [Java Development Kit](https://www.oracle.com/java/technologies/downloads/)
3. Development environment (choose one):
   1. [Visual Studio Code](https://code.visualstudio.com/download)
   2. [JetBrains IntelliJ](https://www.jetbrains.com/idea/download)
   3. [Eclipse](https://www.eclipse.org/downloads/)
4. Dependency intallers
   1. [Maven](https://maven.apache.org/download.cgi?) (Java)
   2. [npm](https://doc.npmjs.com/downloading-and-installing-node-js-and-npm) (Node)
5. [Docker](https://www.docker.com/products/docker-desktop/)

## Development instructions

1. Install the requirements above.
2. Download this codebase using the green button on the top right above that says `[<> Code]`
3. Create a myconfig.properties file in the project root.
   1. Add an OpenAI api-key with this line: `spring.ai.openai.api-key=Insert-Your-Key-here`
   2. Don't check myconfig into git. Some IDEs will suggest to add it, but the .gitignore file will filter it out.
4. Run the following at the command line:
```bash
# Clone this repository
$ gh repo clone agile-wildcats-082025503/chatty-catty

# Go into the repository
cd chatty-catty

# Install dependencies
mvn clean install

# Run the app
mvn spring-boot:run
```
## Testing It Out
As of this writing, the app only tells jokes. Open a browser to the following url to see what it has to say:
http://localhost:8080/ai/chat/string

## Git Commands
Switch to a branch before making any changes
```
git checkout existing-branch
git checkout -b new-branch-name
```
Pull the latest code to an existing branch
```
git checkout main
git pull origin main
git checkout your-branch
git merge main
```
Check in changes:
```
git add .
git commit -m "Description of changes"
git push origin your-branch
```
After checking in changes, it will display a URL to open to get the code changes reviewed and approved.

---

# 🐱 ChattyCatty – Java + Spring RAG System

[![Java CI with Maven](https://github.com/agile-wildcats-082025503/chatty-catty/actions/workflows/tests.yml/badge.svg)](https://github.com/agile-wildcats-082025503/chatty-catty/actions/workflows/tests.yml)

[![codecov](https://codecov.io/gh/your-org/chattycatty/branch/main/graph/badge.svg)](https://codecov.io/gh/your-org/chattycatty)

ChattyCatty is a **Retrieval-Augmented Generation (RAG)** stack built with **Java + Spring Boot + React + PostgreSQL (pgvector)**.  
It comes with a **Makefile-driven developer workflow** for ingestion, reseeding, QA, and demos.

---

## 🚀 Quick Start

1. Start dev stack:
   ```bash
   make dev
   ```
2. Bootstrap default admin (username=`admin`, password=`admin`):
   ```bash
   make create-admin
   ```

3. Login as admin and get JWT:
   ```bash
   make login-admin
   ```
   Copy the JWT and export it:
   ```bash
   export ADMIN_JWT=eyJhbGciOiJIUzI1...
   ```

4. Ingest docs (requires admin JWT):
   ```bash
   make reseed-logs
   ```

5. Run QA checks:
   ```bash
   make qa-full
   ```

---

## 📂 Developer Workflow
| Command	       | Description                          |
|----------------|--------------------------------------|
| `make dev`	    |Start backend + frontend + tail logs |
| `make clean-dev`	 |Stop containers, wipe volumes/logs   |
| `make rebuild-dev`	|Reset everything & restart stack     |
| `make reseed`	|Trigger reseed and poll until done |
| `make reseed-logs`	 |Reseed with live logs & status|
| `make reseed-dev`	 |Rebuild + reseed in one go|

## ✅ Quality Assurance

|Command	|Description|
|-----------|-----------|
|`make qa`	|Quick health check (DB, backend, frontend)|
|`make qa-full`	|Full QA including RAG pipeline test|

## 🎤 Demo Modes

|Command	|Mode|
|-----------|----|
|`make demo`	|Full cycle → rebuild + reseed + QA|
|`make demo-fast`	|Fast path → reseed + QA only|
|`make demo-auto`	|Auto → picks fastest mode depending on running containers|

Example:

   ```bash
   make demo-auto
   ```
Output:
```
===============================================
🐱  CHATTYCATTY DEMO-AUTO MODE - FAST PATH     
===============================================

⚡ Containers already running → running demo-fast
✅ RAG pipeline responded successfully
🎉 Demo environment is ready!
```
## 📊 Monitoring

|Command	|Description|
|-----------|-----------|
|`make status`	|Show current ingestion job status|
|`make logs-seed`	|Tail only seed logs|
|`make health`	|Live dashboard: backend + seed status|

## 🔑 Environment Setup
Set your admin key before running ingestion commands:
   ```bash
    export ADMIN_API_KEY=your-secret-key
   ```
## 🏗️ Tech Stack
* Backend: Java 21 + Spring Boot 3 + JPA
* Database: PostgreSQL + pgvector
* Frontend: React + Axios
* Ingestion: PDF/TXT/Markdown parsing + OpenAI embeddings
* Dev Tools: Docker Compose + Makefile

## 🎉 Demo-Ready
ChattyCatty is developer-friendly and presentation-ready:

* ASCII art banners in demo/dev commands
* One-command demo setup (make demo or make demo-auto)
* Built-in QA + monitoring

🐱 Happy hacking with ChattyCatty!