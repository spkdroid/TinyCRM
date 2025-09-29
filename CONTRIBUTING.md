# Contributing to TinyCRM

Thanks for your interest in improving TinyCRM! This guide helps you get productive quickly and submit high‑quality contributions.

## 🧭 Philosophy
Keep it: clean, documented, minimal, extensible. Avoid adding technology for its own sake. Prefer clarity over cleverness.

## 🗂 Project Structure (Quick Reminder)
```
frontend/   # Vue 3 + Element Plus SPA
tinyOS/     # Spring Boot backend
 db/        # SQL init scripts
```

## ✅ Before You Start
- Ensure Java 17+ and Node 18+ installed
- Run the stack once: `docker-compose up -d --build`
- Read the root `README.md` (architecture + conventions)

## 🐛 Reporting Issues
Use GitHub Issues. Include:
- Summary + expected vs actual behavior
- Steps to reproduce
- Environment (OS, Docker version, Java/Node versions)
- Logs/screenshots if relevant

Security issues: follow `SECURITY.md` (don’t open a public issue).

## 🔀 Branching Strategy
| Branch | Purpose |
|--------|---------|
| main   | Stable, deployable |
| feat/* | New feature branches |
| fix/*  | Bug fixes |
| chore/*| Tooling / infra / docs |

Example: `feat/ticket-export`, `fix/login-session-expiry`.

## 💬 Commit Message Convention
Format (inspired by Conventional Commits):
```
<type>(scope): short imperative summary
```
Types: feat, fix, docs, style, refactor, perf, test, chore, ci.
Examples:
```
feat(auth): add session expiry validation
fix(tickets): correct NPE when description null
chore(docker): slim backend image layers
```

## 🧪 Testing (Current State)
Formal automated tests are minimal. When adding logic:
- Prefer adding a focused unit test in `tinyOS/src/test/java/...`
- For frontend logic-heavy utilities, create tests (if test framework added later)
- Provide manual test steps in PR description if no automated tests

## 🗃 Database Changes
If you introduce schema changes:
- Modify `db/init.sql` (if safe) or propose migration strategy (Flyway/Liquibase future)
- Keep backward compatibility where possible

## 🧱 Backend Code Style
- Follow standard Spring Boot layering (controller → service → repository)
- Avoid business logic in controllers
- Use constructor injection (preferred) when adding new beans
- Return meaningful HTTP status codes
- Validate input (add Bean Validation annotations when introduced)

## 🎨 Frontend Code Style
- Composition API for new components
- Keep components small; extract reusable UI pieces
- Use Axios instance (central config) for API calls
- Prefix global styles or scope them
- Avoid storing derived/computed data in state

## 🔐 Security Awareness
- Never log passwords or raw tokens
- Sanitize/escape any user-generated output in new UI components
- Avoid adding libraries with unclear maintenance status

## 🚀 Performance Considerations
- Defer heavy library additions until justified
- Use lazy loading for any large new route/component
- Keep payload sizes lean (avoid over-fetching)

## 🧩 Adding Dependencies
- Justify in PR description (why needed)
- Prefer well-maintained, widely adopted packages

## 📄 Pull Request Checklist
Before marking Ready for Review:
- [ ] Branch up to date with `main`
- [ ] Build passes locally (backend + frontend)
- [ ] No obvious console or server errors
- [ ] No secret / credential leakage
- [ ] Added/updated docs where needed
- [ ] Tested critical paths manually
- [ ] Squashed/focused commits OR clean logical history

## 🔍 Code Review Guidelines
Reviewers should look for:
- Clarity & simplicity
- Separation of concerns
- Security implications
- Regression risk
- Adherence to style and conventions

## 🗑 Deprecated / Dead Code
Remove it; don’t comment it out. If temporary, leave a `TODO(<github-username>):` explaining intent.

## 🛣 Roadmap Ideas (Open for Contribution)
- Attachments service
- Role-based permissions matrix
- SLA engine & escalations
- Metrics (Prometheus) + dashboards
- Full test suite (JUnit + frontend testing)
- Export/reporting module

## 🤝 Community
This project is currently stewarded by the original author. If adoption grows, governance guidelines will be formalized.

## 🙏 Thanks
Every improvement—typo fix, refactor, feature—is appreciated. Your contributions help shape a solid, educational reference.
