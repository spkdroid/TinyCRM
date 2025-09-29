<!-- Unified README (auto‑consolidated) -->
<p align="center">
  <br>
  <img src="./img/logo.png" alt="TinyCRM Logo" width="200" />
  <br><br>
  <b>TinyCRM</b><br>
  A containerized, full‑stack customer support & ticketing platform built with Spring Boot, Vue 3 (Element Plus) and MySQL.
  <br><br>
  <a href="#quick-start">Quick Start</a> •
  <a href="#features">Features</a> •
  <a href="#architecture">Architecture</a> •
  <a href="#authentication">Auth</a> •
  <a href="#api-overview">API</a> •
  <a href="#development">Dev</a> •
  <a href="#docker">Docker</a> •
  <a href="#troubleshooting">Help</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
  <img src="https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white" />
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
</p>

---

## 1. Overview
TinyCRM is a lean, production‑oriented CRM / support ticket module designed as a reference architecture for a typical modern SaaS helpdesk. It ships with:
- A secure Spring Boot API (tickets, users, comments, sessions)
- A Vue 3 + Element Plus single‑page UI
- Session‑based authentication with role support
- Dockerized deployment (frontend, backend, database)
- Opinionated but clean structure you can extend

This repository consolidates all prior scattered READMEs and internal docs into a single, human‑friendly guide.

---

## 2. Features <a id="features"></a>
### Core
| Area | Highlights |
|------|------------|
| Tickets | Create, view, update, delete; status & priority tracking |
| Comments | Threaded collaboration on tickets |
| Users | Registration, login, roles (ADMIN, SUPPORT, USER) |
| Dashboard | Stats, recent activity, quick actions |
| Sessions | Server persisted session tokens with validation |

### UX / UI
- Element Plus component library & icons
- Responsive layout (mobile → desktop)
- Soft animations & stateful feedback (notifications, loaders)
- Demo accounts pre-seeded

### Architecture & Ops
- Docker Compose (3 services)
- MySQL 8 w/ persistent volume
- Multi-stage builds (smaller images)
- CORS hardened & configurable
- Clean service/controller separation

### Enterprise-Oriented Enhancements
- Structured DTOs & services
- Patch endpoints for partial updates
- Ready for extension (reporting, SLAs, file attachments)

---

## 3. Architecture <a id="architecture"></a>
```
┌──────────────┐    ┌────────────────────────┐    ┌───────────────┐
│  Frontend    │ ↔  │  REST API (Spring Boot)│ ↔  │   MySQL 8     │
│  Vue + Nginx │    │  JPA / Services / DTOs │    │  Persistent   │
│  :3000       │    │  :8080                 │    │  :3306        │
└──────────────┘    └────────────────────────┘    └───────────────┘
```

### Tech Stack
Frontend: Vue 3, Element Plus, Vue Router, Axios  
Backend: Spring Boot, Spring Data JPA, Hibernate, Gradle  
Database: MySQL 8.x  
Infra: Docker, Docker Compose  

### Directory Glance
```
TinyCRM/
├── frontend/          # Vue SPA (Element Plus)
├── tinyOS/            # Spring Boot service
├── db/                # SQL init scripts
├── docker-compose.yml # Orchestration
└── img/               # Branding
```

---

## 4. Quick Start <a id="quick-start"></a>
### Fastest (Docker)
```bash
git clone https://github.com/spkdroid/TinyCRM.git
cd TinyCRM
docker-compose up -d --build
```
Then visit:
- Frontend: http://localhost:3000
- API:      http://localhost:8080

### Manual (Dev Mode)
Backend:
```bash
cd tinyOS
./gradlew bootRun
```
Frontend:
```bash
cd frontend
npm install
npm run serve
```
MySQL (if local): ensure a DB named `support_module` and user creds match `application.properties`.

---

## 5. Authentication <a id="authentication"></a>
---

## 9. Deep Dive: Docker & Enterprise Enhancements <a id="docker-deep-dive"></a>
This section consolidates what used to live in `DOCKER_README.md` and `ENTERPRISE_IMPROVEMENTS.md`.

### 9.1 Container Architecture Rationale
| Service | Purpose | Build Strategy | Notable Features |
|---------|---------|---------------|------------------|
| frontend | Serves SPA via Nginx | Multi-stage: Node build → Nginx Alpine | Compression, cache headers, healthcheck endpoint |
| backend  | Spring Boot API | Gradle build → JRE runtime | Layered jar, JVM ergonomics, CORS & session mgmt |
| database | MySQL 8 | Official image | Persistent volume, init SQL, health checks |

Networking: All services share a default bridge network. Backend refers to DB host as `database`. Frontend talks to backend via `http://backend:8080` internally or `http://localhost:8080` locally.

### 9.2 Recommended Dev vs Prod Modes
| Aspect | Dev | Prod |
|--------|-----|------|
| Backend run | `bootRun` hot reload | Built layered jar | 
| Frontend | `npm run serve` proxy to backend | Pre-built static assets in Nginx |
| Logging | Verbose (debug if needed) | Info / Warn | 
| Volumes | Mount source code | Immutable image layers |
| JVM | Debug port (5005) | Tuned memory flags |

### 9.3 Image Optimization Highlights
- Multi-stage builds keep runtime images small.
- Unused build caches cleared with `--no-daemon` where appropriate.
- Layer ordering: dependencies before sources for better incremental rebuilds.

### 9.4 Enterprise UI / UX Enhancements Recap
From the former enterprise improvements document:
| Area | Upgrade |
|------|---------|
| Design System | Element Plus integration with icon pack |
| Dashboard | Stats cards, recent tickets, quick actions |
| Ticketing | Priority tags, status chips, improved forms |
| Navigation | Collapsible sidebar, breadcrumbs, profile menu |
| Accessibility | Semantic structure, larger tap areas, color contrast |

### 9.5 Backend Enhancements Recap
- Added PATCH endpoints for partial ticket updates.
- Consolidated CORS config into centralized `WebConfig`.
- Session entity + repository for server invalidation.
- Data initializer seeds admin/support accounts.

### 9.6 Security & Hardening Notes
| Concern | Current Mitigation | Next Step |
|---------|-------------------|-----------|
| CORS | Explicit whitelist origins | Externalize to env in prod |
| Sessions | Server-side token with expiry | Rotate / refresh mechanism |
| Passwords | Stored hashed (ensure using encoder) | Add argon2/bcrypt config file |
| Headers | Nginx can set security headers | Add CSP & strict transport in reverse proxy |
| Input Validation | Basic DTO constraints | Add bean validation annotations |

### 9.7 Scaling Considerations
| Layer | Option | Comment |
|-------|--------|---------|
| DB | Read replicas | For analytical/report queries |
| API | Horizontal pods | Behind load balancer / ingress |
| Cache | Redis | Session + hot ticket lists |
| Observability | Prometheus + Grafana | JVM & DB dashboards |
| Queue | Kafka / RabbitMQ | For async ticket events & notifications |

### 9.8 Operational Playbook (Cheat Sheet)
```bash
# Full rebuild (clean images)
docker-compose build --no-cache

# Tail only backend logs
docker-compose logs -f backend

# Enter MySQL shell
docker exec -it tinycrm-database mysql -u app_user -papp_password support_module

# Snapshot database
docker exec tinycrm-database mysqldump -u app_user -papp_password support_module > backup_$(date +%Y%m%d).sql

# Restore snapshot (destructive)
mysql -u app_user -papp_password support_module < backup_20250101.sql

# Clean everything (including volumes!)
docker-compose down -v
```

### 9.9 Future Enterprise Candidates
- SSO (OAuth2 / OpenID Connect).
- Fine-grained RBAC (permissions matrix).
- Attachments storage abstraction (S3 / MinIO).
- SLA timers & automated escalations.
- Full audit trail (entity change history).
- Metrics endpoint (`/actuator/prometheus`).

---

## 10. Troubleshooting <a id="troubleshooting"></a>

Flow:
1. User logs in → `/api/auth/login`
2. Backend creates `UserSession` (token, expiry)
3. Token returned to frontend & stored (localStorage) + sent via Axios headers
4. Guarded routes validate via `/api/auth/me`
5. Logout → session invalidated server‑side

Demo Accounts: `admin / admin123`, `support / support123`

Registration Fields: firstName, lastName, username, email, password. New users get role `USER`.

---

## 6. API Overview <a id="api-overview"></a>
Only a subset shown here for brevity. See controllers for full details.

### Auth
```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me
```

### Users
```http
GET    /api/users
GET    /api/users/{id}
POST   /api/users        (admin)
PUT    /api/users/{id}
DELETE /api/users/{id}
```

### Tickets
```http
GET    /api/tickets
GET    /api/tickets/{id}
POST   /api/tickets
PUT    /api/tickets/{id}
PATCH  /api/tickets/{id}   # Partial status update
DELETE /api/tickets/{id}
GET    /api/tickets/user/{userId}
```

### Comments
```http
GET    /api/comments
GET    /api/comments/{id}
GET    /api/comments/ticket/{ticketId}
POST   /api/comments
PUT    /api/comments/{id}
DELETE /api/comments/{id}
```

---

## 7. Development <a id="development"></a>
### Backend
```bash
cd tinyOS
./gradlew bootRun            # Run
./gradlew test               # Tests (if/when added)
```
Debug: add `--debug-jvm` or attach to port 5005 if exposed via compose.

### Frontend
```bash
cd frontend
npm install
npm run serve    # Dev
npm run build    # Prod build
npm run lint     # Lint
```

### Code Layout
`tinyOS/src/main/java/com/spkd/tinycrm/tinyos/` contains:
- controller/ – REST endpoints
- service/ – Business logic
- repository/ – Spring Data interfaces
- entity/ – JPA entities
- dto/ – Request/response models
- config/ – CORS, data init, etc.

`frontend/src/` contains Vue components, router, assets.

---

## 8. Docker & Deployment <a id="docker"></a>
Services:
- frontend (Node build → Nginx) :3000
- backend (Spring Boot) :8080
- database (MySQL 8) :3306

Common commands:
```bash
docker-compose up -d --build
docker-compose logs -f backend
docker-compose restart frontend
docker-compose down -v   # Reset EVERYTHING (drops DB!)
```

Environment examples (compose or .env):
```
SPRING_DATASOURCE_URL=jdbc:mysql://database:3306/support_module
SPRING_DATASOURCE_USERNAME=app_user
SPRING_DATASOURCE_PASSWORD=app_password
CORS_ALLOWED_ORIGINS=http://localhost:3000
```

---

## 9. Troubleshooting <a id="troubleshooting"></a>
| Symptom | Try This |
|---------|----------|
| CORS errors | Confirm `CORS_ALLOWED_ORIGINS` matches frontend origin & restart backend |
| Login fails | Clear localStorage sessionToken & retry; check backend logs |
| DB init fails | Ensure volume not holding stale schema: `docker-compose down -v` |
| Port already used | macOS: `lsof -i :3000` then kill offending PID |
| 404 on API | Verify base URL `/api` prefix and proxy (if using dev server) |

Performance Tips:
- Keep images updated (rebuild periodically)
- Use indexed columns (add where query patterns emerge)
- Add caching (Spring Cache) if scaling read load

---

## 11. Extending
Ideas:
- File attachments (S3 / MinIO)
- Email notifications (ticket updates)
- SLA timers & escalation rules
- Role-based granular permissions
- Metrics endpoint + Grafana dashboards

---

## 12. License
Apache 2.0 — see `LICENSE` file.

---

## 13. Author
Built & maintained by **Ramkumar Velmurugan**  
[Portfolio](http://www.spkdroid.com/CV/) • [GitHub](https://github.com/spkdroid)

If this project helps you, a ⭐ star is the best thank‑you.

---

<p align="center">Made with ❤️ — Improve it and share back.</p>
 
---

### See Also
- [`CONTRIBUTING.md`](./CONTRIBUTING.md) – How to propose changes, coding standards, branching and PR checklist.
- [`SECURITY.md`](./SECURITY.md) – Reporting vulnerabilities, current posture, and hardening roadmap.

```
