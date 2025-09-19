# 🐱 ChattyCatty Developer Commands

This project comes with a **Makefile-powered developer workflow** for RAG ingestion, QA, and demos.  
Below is a full cheatsheet of all available commands.

---

## 📂 Document Ingestion

- **`make seed`**  
  Trigger ingestion of all files in `docs/` via `/admin/seed` (requires `ADMIN_API_KEY`).

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

## 🛠 Development Workflow

- **`make dev`**  
  Start the stack (`backend + frontend`) and tail logs from both containers.

- **`make clean-dev`**  
  Stop stack, remove containers, networks, volumes, and logs.

- **`make rebuild-dev`**  
  Wipe environment and restart stack fresh.

---

## ✅ Quality Assurance

- **`make qa`**  
  Quick health check of:
    - Postgres (`ragdb`)
    - Backend (`rag-app` `/actuator/health`)
    - Frontend (`rag-frontend` `/index.html`)

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

## 📊 Monitoring

- **`make health`**  
  Live monitoring dashboard that refreshes every 5s:
    - Backend health (`/actuator/health`)
    - Seed status (`/admin/seed/status`)

---

## 🔑 Environment

- Ensure `ADMIN_API_KEY` is set before using ingestion-related commands:
```bash
  export ADMIN_API_KEY=your-secret-key
```
## 🔑 User Management

- **`make create-admin`**
  Ensure a default admin user exists in Postgres (`username=admin`, `password=admin`).
  Uses a precomputed **bcrypt("admin")** password hash. Skips if already exists.

- **`make login-admin`**
  Logs in as the default admin and prints out the JWT token.
  Example usage:
  ```bash
  make login-admin
  export ADMIN_JWT=eyJhbGciOiJIUzI1...
  ```
- **`make promote-user username=<user>`**  
  Add **ROLE_ADMIN** to a given user. Requires `ADMIN_JWT`.  
  Example:
  ```bash
  make promote-user username=alice
  ``` 
- **`make demote-user username=<user>`**  
  Remove **ROLE_ADMIN** from a given user. Requires `ADMIN_JWT`.  
  Example:
  ```bash
  make demote-user username=alice
  ``` 
- **`make list-users`**
  List all registered users and their roles. Requires ADMIN_JWT.
  Example:
  ```bash
  make list-users
  ```
- **`make delete-user username=<user>`**
  Delete a given user entirely. Requires ADMIN_JWT.
  Example:
  ```bash
  make delete-user username=alice
  ```
---


🚀 Common Flows

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

## Save It

Create the file:

```bash
echo "<paste above>" > DEV-COMMANDS.md