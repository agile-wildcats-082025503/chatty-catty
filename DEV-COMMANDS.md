# 🐱 ChattyCatty Developer Commands

This project comes with a **Makefile-powered developer workflow** that creates VMs for RAG ingestion, QA, and demos.  

Below is a full cheatsheet of all available commands.

---

## Git Commands
The code is in git, so here are some commands to assist with development, demo, and testing.

### Getting Started
If you don't have anything on your system, clone from the main branch:
   ```bash
   gh repo clone agile-wildcats-082025503/chatty-catty
   # Then witch to an existing branch using:
   git checkout <existing-branch-name>
   ```
Or you can clone an existing branch to get started there:
   ```bash
   git clone --branch <existing-branch-name> https://github.com/agile-wildcats-082025503/chatty-catty.git
   ```

### Working with branches

Before making any changes to the code, create a branch
  ```bash
  git checkout -b <new-branch-name>
  ```

Pull the latest code to an existing branch to get the latest or prepare for checkin.
   ```bash
   git checkout main
   git pull origin main
   git checkout <your-branch>
   git merge main
   ```
Check in changes:
   ```bash
   git add .
   git commit -am "Description of changes"
   git push origin <your-branch>
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

### 🛠 Development in Node / React

Updating the UI is done within the frontend directory.

When making changes, it can be faster to simply run npm commands directly like
`npm install` or `npm run build` to see if changes build.

### 🛠 Development in Java / MVN

Updating the backend is done within the top-level directory - the Java files are located under src.

When making changes, using commands directly can speed diagnosis like
`mvn compile`.


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
  Setting up a connection in a local dev env (these should mirror what's in the `.env` file):
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

### Environment file `.env` 
Create a .env file in the top-level directory of the project.
   ```
   POSTGRES_INSTANCE=ragdb
   POSTGRES_SERVER=localhost:5432
   POSTGRES_SERVER_SPRING=postgres:5432
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=postgres
   SPRING_PROFILES_ACTIVE=dev
   OLLAMA_URL=http://host.docker.internal:11434
   ```
Adjust these settings as needed for your environment.

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

---

## Troubleshooting

* Errors with missing table, delete the ragdb container from Docker and then run `make clean dev`
* Ensure the correct branch is active `git branch`.
* Pull the related logs into a file and share over Discord.
    * Example (Windows): `discord logs chatty-catty-app > diagnoseme.log`
* Error `Makefile:#: *** missing separator. Stop.` The makefile uses spaces for indent instead of tabs.
