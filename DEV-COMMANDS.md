# 🐱 ChattyCatty Developer Commands

This project comes with a **Makefile-powered developer workflow** that creates VMs for RAG ingestion, QA, and demos.  

Below is a full cheatsheet of all available commands.

---

## Git Commands
The code is in git, so here are some commands to assist with development, demo, and testing.

Pull the latest code to an existing branch to get the latest or prepare for checkin.
   ```bash
   git checkout main
   git pull origin main
   git checkout your-branch
   git merge main
   ```
Switch to a branch before making any changes.
   ```bash
   git checkout existing-branch
   git checkout -b new-branch-name
   ```
Check in changes:
   ```bash
   git add .
   git commit -m "Description of changes"
   git push origin your-branch
   ```
After checking in changes, it will display a URL to open to get the code changes reviewed and approved.

---

## 🛠 Development Workflow

- **`make dev`**  
  Start the stack (DB, API backend, and frontend) and tail logs from both containers.

- **`make clean-dev`**  
  Stop stack, remove containers, networks, volumes, and logs.

- **`make rebuild-dev`**  
  Wipe environment and restart stack fresh.

---

## 📂 Document Ingestion

- **`make seed`**  
  Trigger ingestion of all files in `docs/` via `/admin/seed`.

- **`make status`**  
  Show current ingestion job status as JSON.

- **`make logs-seed`**  
  Tail only **seed-related logs** from the backend container.

- **`make reseed`**  
  Trigger ingestion and **auto-poll status** until it finishes.

- **`make reseed-logs`**  
  Trigger ingestion, **poll status + tail logs live** until it finishes.

- **`make reseed-dev`**  
  Rebuild stack from scratch and reseed docs with logs.

---

## ✅ Quality Assurance

- **`make qa`**  
  Quick health check of:
    - Postgres (`ragdb`)
    - Backend (`chatty-catty-app` `/actuator/health`)
    - Frontend (`chatty-catty-frontend` `/index.html`)

- **`make qa-full`**  
  Runs `make qa` and also sends a test query to `/chat/formatted` to validate **RAG pipeline**.

---

## 🎤 Demo Modes

- **`make demo`**  
  Full cycle: rebuild stack → reseed docs → run QA.

- **`make demo-fast`**  
  Quick demo: reseed docs + QA (without restarting stack).

- **`make demo-auto`**  
  Automatically picks:
    - `demo-fast` if containers are already running
    - `demo` if containers are not running

---

## 📊 Monitoring and Diagnostics

- **`make health`**  
  Live monitoring dashboard that refreshes every 5s:
    - Backend health (`/actuator/health`)
    - Seed status (`/admin/seed/status`)

### Docker Commands
- **Docker diagnostics**
   ```bash
   docker ps -a
   docker logs <container_name>
   ```
- **Log into a Docker VM to run local commands**
   ```bash
   docker exec -it chatty-catty-app /bin/bash
   # Execute various commands...then exit using:
   exit 
   ```
- **Use [pgAdmin](https://www.pgadmin.org/) to connect to the database**
  Setting up a connection in a local dev env:
  1. Add Server
     2. General Tab
        1. Name: ragdb
     3. Connection Tab
        1. Host name/address: localhost
        2. Port: 5432
        3. Save password? Enable
        4. Password: postgres
           1. This might change - check src/main/resources/application.yml for the latest DB settings.
        5. Click Save
---

## 🔑 Environment

- Ensure `OPENAI_API_KEY` is set before using ingestion-related commands. This is done using the .env file but can also be done in the environment itself.
   ```bash
   # Set in the environment
   export OPENAI_API_KEY=your-secret-key

   # Or here's the format for setting in the .env file
   OPENAI_API_KEY=your-secret-key
   ```
---


## 🚀 Common Flows

Start fresh dev environment + logs:

`make dev`


Rebuild + reseed + QA (clean demo):

`make demo`


Fast reseed + QA (containers running):

`make demo-fast`


Full auto cycle (choose fastest demo mode automatically):

`make demo-auto`


Live monitor ingestion + backend health:

`make health`

