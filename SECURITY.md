# Security Policy

TinyCRM aims to provide a clean, extensible reference implementation. While not yet audited, we follow pragmatic security practices and welcome responsible disclosure.

## 🔒 Supported Branches
Currently the `main` (or default) branch is the only actively maintained line.

| Version / Branch | Status | Notes |
|------------------|--------|-------|
| main             | Active | Security & functional fixes accepted |

> If you fork and materially change auth/session layers, treat your fork as a distinct security surface.

## 🛡️ Reporting a Vulnerability
We appreciate coordinated disclosure.

1. Email: `security@spkdroid.com` (placeholder – replace with preferred contact)  
2. Use an encrypted channel if possible (PGP key to be published).  
3. Provide: description, impact, reproduction steps, affected endpoints, suggested fix if any.
4. Please allow up to 5 business days for initial acknowledgement.

Do NOT create a public issue for: auth bypass, data exposure, RCE, injection vectors, session fixation, or privilege escalation.

## ⏱️ Disclosure Timeline (Target)
| Phase | Target Window |
|-------|---------------|
| Acknowledge receipt | 1–5 business days |
| Triage / severity rating | 5–7 business days |
| Fix development | 5–15 business days (severity dependent) |
| Coordinated release | Immediately after fix validation |

Critical issues may be patched faster with out-of-band releases.

## ✅ Current Security Posture
| Area | Status | Notes |
|------|--------|-------|
| Authentication | Session token (server stored) | Consider refresh/rotation strategy |
| Password Storage | Should use BCrypt/Argon2 | Verify encoder present (spring config) |
| CORS | Explicit origins | Externalize for multi-env production |
| Input Validation | Basic (manual + DTO) | Add Bean Validation annotations (@NotBlank etc.) |
| Audit Logging | Not implemented | Add for updates/deletes |
| Rate Limiting | Not implemented | Consider gateway / filter level |
| CSRF | Session model (API style) | If cookies adopted, add CSRF tokens |
| Content Security Policy | Handled via reverse proxy | Add CSP header config in Nginx |

## 🚧 Hardening Roadmap
- Add validation annotations across DTOs.
- Enforce password complexity on registration.
- Introduce brute-force protection (incremental backoff or IP risk scoring).
- Add structured audit log (entity changes + actor + timestamp).
- Provide `/health` and `/metrics` segregation; protect metrics behind auth if sensitive.
- Integrate security scanning (Snyk/OWASP Dependency Check) into CI.
- Add integration tests for auth edge cases (expired session, replay attempt, tampered token).

## 🔐 Secure Development Guidelines
| Topic | Guidance |
|-------|----------|
| Secrets | Never commit; use env vars or secret manager |
| Logging | Avoid logging PII or credentials |
| Errors | Return generic messages; log specifics server-side |
| Dependencies | Keep frameworks patched; review monthly |
| SQL | Use JPA parameter binding (no string concatenation) |
| Serialization | Avoid Java native serialization; use JSON DTOs |

## 🧪 Suggested Security Tests
- Login with invalid credentials (rate limiting readiness)
- Access protected endpoint without token
- Use expired session token
- Attempt privilege escalation (USER calling admin-only endpoints)
- Inject scripts into comment / ticket fields (XSS sanitization check)
- Large payload submissions (DoS surface)

## 📨 Contact
For all security related matters: `security@spkdroid.com` (placeholder).

If this evolves into a production service, formalize a SECURITY.txt in the site root.
