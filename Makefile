PROJECT_NAME=chatty-catty-app
COMPOSE=docker-compose
DOCKER=docker
ifneq (,$(wildcard .env))
    include .env
    export
endif
ifeq ($(OS),Windows_NT)
    BROWSER := powershell.exe -Command Start-Process -FilePath 'chrome.exe' -ArgumentList
else
    UNAME_S := $(shell uname -s)
    ifeq ($(UNAME_S),Linux)
        BROWSER := xdg-open
    else ifeq ($(UNAME_S),Darwin) # macOS
        BROWSER := open
    endif
endif

run:
	@echo "🚀 Starting CHATTYCATTY stack..."
	$(COMPOSE) --profile ${SPRING_PROFILES_ACTIVE} up --build -d
	@echo "⏳ Waiting for app to become healthy..."
	@until [ "$$(docker inspect --format='{{json .State.Health.Status}}' chatty-catty-app)" = "\"healthy\"" ]; do \
		echo "   → Waiting..."; \
		sleep 5; \
	done
	@echo "✅ App is healthy! Seeding documents..."
	$(MAKE) seed

# Stop services
stop:
	$(COMPOSE) down

# Restart services
restart: stop run

# View logs
logs:
	$(COMPOSE) logs -f $(PROJECT_NAME)

# Database logs
dblogs:
	$(COMPOSE) logs -f postgres

# Open database shell
psql:
	$(DOCKER) exec -it ragdb psql -U postgres -d ragdb

# Clean volumes (⚠️ wipes all data)
clean:
	$(COMPOSE) down -v

# Rebuild without cache
rebuild:
	$(COMPOSE) build --no-cache

# Trigger backend seed job
seed:
	@echo "🚀 Triggering seed endpoint..."
	@curl -s -X POST "http://localhost:8080/admin/seed?docsDir=docs" \
		&& echo "\n✅ Seed job requested!"

# Check current seed job status
status:
	@curl -s -H http://localhost:8080/admin/seed/status | jq .

# Tail only seed-related logs from the chatty-catty-app container
logs-seed:
	@echo "📜 Tailing seed-related logs from chatty-catty-app (Ctrl+C to stop)..."
	@$(DOCKER) logs -f chatty-catty-app 2>&1 | grep --line-buffered -i "seed"

# Trigger seed and auto-poll status until finished
reseed:
	@echo "🚀 Triggering reseed..."
	@curl -s -X POST "http://localhost:8080/admin/seed?docsDir=docs" \
		> /dev/null
	@echo "⏳ Polling status (Ctrl+C to stop)..."
	@while true; do \
		out=$$(curl -s -H http://localhost:8080/admin/seed/status); \
		state=$$(echo $$out | jq -r .state); \
		msg=$$(echo $$out | jq -r .message); \
		echo " → $$state : $$msg"; \
		if [ "$$state" = "completed" ] || [ "$$state" = "failed" ] || [ "$$state" = "idle" ]; then \
			break; \
		fi; \
		sleep 5; \
	done
	@echo "✅ Reseed finished."

# Trigger reseed and tail logs at the same time
reseed-logs:
	@echo "🚀 Triggering reseed with live logs..."
	@curl -s -X POST "http://localhost:8080/admin/seed?docsDir=docs" \
		> /dev/null
	@echo "⏳ Polling status + tailing logs (Ctrl+C to stop)..."
	@{ \
	  docker logs -f chatty-catty-app 2>&1 | grep --line-buffered -i "seed" & \
	  LOG_PID=$$!; \
	  while true; do \
	    out=$$(curl -s -H http://localhost:8080/admin/seed/status); \
	    state=$$(echo $$out | jq -r .state); \
	    msg=$$(echo $$out | jq -r .message); \
	    echo " → $$state : $$msg"; \
	    if [ "$$state" = "completed" ] || [ "$$state" = "failed" ] || [ "$$state" = "idle" ]; then \
	      kill $$LOG_PID; \
	      break; \
	    fi; \
	    sleep 5; \
	  done; \
	}
	@echo "✅ Reseed finished (with logs)."

# Start stack and follow both backend + frontend logs
dev:
	@echo ""
	@echo "==============================================="
	@echo "🐱  CHATTYCATTY DEV MODE - POWERED BY MAKEFILE "
	@echo "==============================================="
	@echo ""
	@echo "🚀 Starting stack in dev mode (backend + frontend logs)..."
	$(COMPOSE) --profile ${SPRING_PROFILES_ACTIVE} up --build -d
	@echo "📜 Tailing logs from chatty-catty-app (Spring Boot) and chatty-catty-frontend (React)..."
	@$(DOCKER) compose logs -f app frontend

# Stop stack and reset environment (containers, networks, volumes, logs)
clean-dev:
	@echo "🧹 Stopping and cleaning dev environment..."
	$(COMPOSE) down -v --remove-orphans --rmi local
	@echo "🗑  Removing dangling images (if any)..."
	$(DOCKER) image prune -f
	@echo "🗑  Removing old container logs..."
	$(DOCKER) ps -a -q --filter "name=chatty-catty-app" --filter "name=chatty-catty-frontend" | xargs -r docker rm -f
	@echo "✅ Clean dev environment ready."

# Full rebuild: clean everything and restart stack with logs
rebuild-dev: clean-dev dev
	@echo "🔄 Rebuilt dev environment and attached logs."

# Full cycle: rebuild stack and reseed docs with logs
reseed-dev: rebuild-dev
	@echo "📂 Rebuilding stack and reseeding docs..."
	$(MAKE) reseed-logs

# Quick QA check: DB + backend + frontend health
qa:
	@echo "🔍 Running QA checks..."
	@echo "📦 Checking Postgres..."
	@if docker exec ragdb pg_isready -U postgres > /dev/null; then \
		echo "✅ Postgres is ready"; \
	else \
		echo "❌ Postgres is not ready"; \
	fi
	@echo "⚙️  Checking backend (Spring Boot)..."
	@if curl -s http://localhost:8080/actuator/health | grep -q '"status":"UP"'; then \
		echo "✅ Backend is UP"; \
	else \
		echo "❌ Backend is DOWN"; \
	fi
	@echo "🌐 Checking frontend (React)..."
	@if curl -s -o /dev/null -w "%{http_code}" http://localhost:3000/index.html | grep -q "200"; then \
		echo "✅ Frontend is serving"; \
	else \
		echo "❌ Frontend not reachable"; \
	fi
	@echo "🔎 QA checks finished."

# Full QA: includes RAG chat endpoint check
qa-full: qa
	@echo "💬 Checking RAG chat pipeline..."
	@if curl -s -X POST http://localhost:8080/chat/formatted \
		-H "Content-Type: application/json" \
		-d '{"question":"What is this system about?","format":"text"}' | grep -qi "system"; then \
		echo "✅ RAG pipeline responded successfully"; \
	else \
		echo "❌ RAG pipeline did not respond correctly"; \
	fi
	@echo "🔎 Full QA finished."

# One-command demo: rebuild stack, reseed docs, and run full QA
demo: rebuild-dev reseed-logs qa-full
	@echo ""
	@echo "==============================================="
	@echo "🐱  CHATTYCATTY DEMO MODE - FULL CYCLE         "
	@echo "==============================================="
	@echo ""
	@echo "🎉 Demo environment is ready!"

# Fast demo: reseed docs and run QA without rebuilding containers
demo-fast: reseed-logs qa-full
	@echo ""
	@echo "==============================================="
	@echo "🐱  CHATTYCATTY DEMO MODE - FAST PATH          "
	@echo "==============================================="
	@echo ""
	@echo "⚡ Fast demo ready (no rebuild)!"

# Auto demo: picks demo-fast if containers are running, else full demo
demo-auto:
	@if docker ps --format '{{.Names}}' | grep -q '^chatty-catty-app$$'; then \
		echo ""; \
		echo "==============================================="; \
		echo "🐱  CHATTYCATTY DEMO-AUTO MODE - FAST PATH     "; \
		echo "==============================================="; \
		echo ""; \
		echo "⚡ Containers already running → running demo-fast"; \
		$(MAKE) demo-fast; \
	else \
		echo ""; \
		echo "==============================================="; \
		echo "🐱  CHATTYCATTY DEMO-AUTO MODE - FULL CYCLE    "; \
		echo "==============================================="; \
		echo ""; \
		echo "🧹 No running containers → running full demo"; \
		$(MAKE) demo; \
	fi

# Live health dashboard: backend + seed status
health:
	@echo "📊 Monitoring system health (Ctrl+C to stop)..."
	@while true; do \
		echo "---- $$(date) ----"; \
		curl -s http://localhost:8080/actuator/health | jq .; \
		curl -s -H http://localhost:8080/admin/seed/status | jq .; \
		echo ""; \
		sleep 5; \
	done

# Run tests with coverage and open HTML report
coverage: $(eval SPRING_PROFILES_ACTIVE=test)
coverage:
	@echo "🧪 Running tests with JaCoCo coverage..."
	@export OLLAMA_URL=$(OLLAMA_MVNW_URL) && ./mvnw verify
	@echo "👉 ***NOTE*** If there are errors above - ensure the containers are running by using 'make run' first. Or combine with 'make run coverage'"
	@echo "📊 Coverage report generated at target/site/jacoco/index.html"
	@echo "👉 ***NOTE*** If the below command fails to load the report, open target/site/jacoco/index.html manually"
	$(BROWSER) $(CURDIR)/target/site/jacoco/index.html