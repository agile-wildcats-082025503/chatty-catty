PROJECT_NAME=rag-app
COMPOSE=docker-compose

run:
	@echo "🚀 Starting RAG stack..."
	$(COMPOSE) up --build -d
	@echo "⏳ Waiting for app to become healthy..."
	@until [ "$$(docker inspect --format='{{json .State.Health.Status}}' rag-app)" = "\"healthy\"" ]; do \
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
	docker exec -it ragdb psql -U postgres -d ragdb

# Clean volumes (⚠️ wipes all data)
clean:
	$(COMPOSE) down -v

# Rebuild without cache
rebuild:
	$(COMPOSE) build --no-cache

# Trigger backend seed job (requires ADMIN_API_KEY env var)
seed:
	@if [ -z "$$ADMIN_API_KEY" ]; then \
		echo "❌ ADMIN_API_KEY not set. Please export it or put it in .env"; \
		exit 1; \
	fi
	@echo "🚀 Triggering seed endpoint..."
	@curl -s -X POST "http://localhost:8080/admin/seed?docsDir=docs" \
		-H "X-API-KEY: $$ADMIN_API_KEY" \
		&& echo "\n✅ Seed job requested!"

# Check current seed job status
status:
	@if [ -z "$$ADMIN_API_KEY" ]; then \
		echo "❌ ADMIN_API_KEY not set. Please export it or put it in .env"; \
		exit 1; \
	fi
	@curl -s -H "X-API-KEY: $$ADMIN_API_KEY" http://localhost:8080/admin/seed/status | jq .

# Tail only seed-related logs from the rag-app container
logs-seed:
	@echo "📜 Tailing seed-related logs from rag-app (Ctrl+C to stop)..."
	@docker logs -f rag-app 2>&1 | grep --line-buffered -i "seed"

# Trigger seed and auto-poll status until finished
reseed:
	@if [ -z "$$ADMIN_API_KEY" ]; then \
		echo "❌ ADMIN_API_KEY not set. Please export it or put it in .env"; \
		exit 1; \
	fi
	@echo "🚀 Triggering reseed..."
	@curl -s -X POST "http://localhost:8080/admin/seed?docsDir=docs" \
		-H "X-API-KEY: $$ADMIN_API_KEY" > /dev/null
	@echo "⏳ Polling status (Ctrl+C to stop)..."
	@while true; do \
		out=$$(curl -s -H "X-API-KEY: $$ADMIN_API_KEY" http://localhost:8080/admin/seed/status); \
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
	@if [ -z "$$ADMIN_API_KEY" ]; then \
		echo "❌ ADMIN_API_KEY not set. Please export it or put it in .env"; \
		exit 1; \
	fi
	@echo "🚀 Triggering reseed with live logs..."
	@curl -s -X POST "http://localhost:8080/admin/seed?docsDir=docs" \
		-H "X-API-KEY: $$ADMIN_API_KEY" > /dev/null
	@echo "⏳ Polling status + tailing logs (Ctrl+C to stop)..."
	@{ \
	  docker logs -f rag-app 2>&1 | grep --line-buffered -i "seed" & \
	  LOG_PID=$$!; \
	  while true; do \
	    out=$$(curl -s -H "X-API-KEY: $$ADMIN_API_KEY" http://localhost:8080/admin/seed/status); \
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
	$(COMPOSE) up --build -d
	@echo "📜 Tailing logs from rag-app (Spring Boot) and rag-frontend (React)..."
	@docker logs -f rag-app rag-frontend

# Stop stack and reset environment (containers, networks, volumes, logs)
clean-dev:
	@echo "🧹 Stopping and cleaning dev environment..."
	$(COMPOSE) down -v --remove-orphans --rmi local
	@echo "🗑  Removing dangling images (if any)..."
	docker image prune -f
	@echo "🗑  Removing old container logs..."
	docker ps -a -q --filter "name=rag-app" --filter "name=rag-frontend" | xargs -r docker rm -f
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
	@if docker ps --format '{{.Names}}' | grep -q '^rag-app$$'; then \
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
	@if [ -z "$$ADMIN_API_KEY" ]; then \
		echo "❌ ADMIN_API_KEY not set. Please export it or put it in .env"; \
		exit 1; \
	fi
	@echo "📊 Monitoring system health (Ctrl+C to stop)..."
	@while true; do \
		echo "---- $$(date) ----"; \
		curl -s http://localhost:8080/actuator/health | jq .; \
		curl -s -H "X-API-KEY: $$ADMIN_API_KEY" http://localhost:8080/admin/seed/status | jq .; \
		echo ""; \
		sleep 5; \
	done

# Create a default admin user in Postgres (username=admin, password=admin)
create-admin:
	@echo "👤 Creating default admin user in Postgres..."
	@docker exec -i ragdb psql -U postgres -d postgres <<'EOSQL'
	DO $$
	BEGIN
		IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin') THEN
			INSERT INTO users (username, password_hash) VALUES (
				'admin',
				'$$2a$$10$$7QjE2fC7UZY0uF6Z/8sX1OHkM1Y3Q7JmP.nQixuQ3hlUL3Q5X9wuy'  -- bcrypt("admin")
			);
			INSERT INTO users_roles (user_id, roles)
			SELECT id, 'ROLE_ADMIN' FROM users WHERE username = 'admin';
		END IF;
	END $$;
	EOSQL
	@echo "✅ Default admin user ensured (username=admin, password=admin)"

# Login as default admin (username=admin, password=admin) and print JWT
login-admin:
	@echo "🔑 Logging in as default admin..."
	@resp=$$(curl -s -X POST http://localhost:8080/auth/login \
		-H "Content-Type: application/json" \
		-d '{"username":"admin","password":"admin"}'); \
	token=$$(echo $$resp | jq -r '.token'); \
	if [ "$$token" = "null" ] || [ -z "$$token" ]; then \
		echo "❌ Failed to login. Response:"; \
		echo "$$resp"; \
		exit 1; \
	fi; \
	echo "✅ Got admin JWT:"; \
	echo ""; \
	echo "$$token"; \
	echo ""; \
	echo "💡 Export it with:"; \
	echo "   export ADMIN_JWT=$$token"

# Promote a user to admin role (requires ADMIN_JWT and username var)
promote-user:
	@if [ -z "$$username" ]; then \
		echo "❌ Please provide a username, e.g. make promote-user username=alice"; \
		exit 1; \
	fi
	@if [ -z "$$ADMIN_JWT" ]; then \
		echo "❌ ADMIN_JWT not set. Run 'make login-admin' and export the token"; \
		exit 1; \
	fi
	@echo "⬆️  Promoting user '$$username' to ROLE_ADMIN..."
	@curl -s -X POST http://localhost:8080/auth/promote/$$username \
		-H "Authorization: Bearer $$ADMIN_JWT" | jq .

# Demote a user (remove admin role) (requires ADMIN_JWT and username var)
demote-user:
	@if [ -z "$$username" ]; then \
		echo "❌ Please provide a username, e.g. make demote-user username=alice"; \
		exit 1; \
	fi
	@if [ -z "$$ADMIN_JWT" ]; then \
		echo "❌ ADMIN_JWT not set. Run 'make login-admin' and export the token"; \
		exit 1; \
	fi
	@echo "⬇️  Demoting user '$$username' (removing ROLE_ADMIN)..."
	@curl -s -X POST http://localhost:8080/auth/demote/$$username \
		-H "Authorization: Bearer $$ADMIN_JWT" | jq .

# List all users and their roles (requires ADMIN_JWT)
list-users:
	@if [ -z "$$ADMIN_JWT" ]; then \
		echo "❌ ADMIN_JWT not set. Run 'make login-admin' and export the token"; \
		exit 1; \
	fi
	@echo "📋 Listing all users..."
	@curl -s http://localhost:8080/auth/users \
		-H "Authorization: Bearer $$ADMIN_JWT" | jq .

# Delete a user (requires ADMIN_JWT and username var)
delete-user:
	@if [ -z "$$username" ]; then \
		echo "❌ Please provide a username, e.g. make delete-user username=alice"; \
		exit 1; \
	fi
	@if [ -z "$$ADMIN_JWT" ]; then \
		echo "❌ ADMIN_JWT not set. Run 'make login-admin' and export the token"; \
		exit 1; \
	fi
	@echo "🗑️  Deleting user '$$username'..."
	@curl -s -X POST http://localhost:8080/auth/delete/$$username \
		-H "Authorization: Bearer $$ADMIN_JWT" | jq .


# Run tests with coverage and open HTML report
coverage:
	@echo "🧪 Running tests with JaCoCo coverage..."
	./mvnw clean verify
	@echo "📊 Coverage report generated at target/site/jacoco/index.html"
	@if command -v xdg-open > /dev/null; then \
		xdg-open target/site/jacoco/index.html; \
	elif command -v open > /dev/null; then \
		open target/site/jacoco/index.html; \
	else \
		echo "👉 Open target/site/jacoco/index.html manually"; \
	fi