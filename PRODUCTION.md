# Production deployment

The supported topology is one application instance with PostgreSQL and persistent local uploads, behind an HTTPS reverse proxy. The backend and database have no published ports. The frontend listens only on the host loopback interface at port 8080. Do not expose the backend directly: request and connection limits live in Nginx.

## Configure and launch

1. Install Docker Engine with Compose on the deployment host.
2. Copy `.env.example` to `.env`. Set a unique database password, a randomly generated JWT secret of at least 32 bytes, your Gemini API key, and the exact HTTPS site origin. Never commit `.env`.
3. Run `docker compose build` and `docker compose up -d`.
4. Run `docker compose ps` and confirm all services are healthy.
5. Configure your HTTPS ingress to proxy to `127.0.0.1:8080`. Enable HTTPS redirects and HSTS there. Restrict ingress to the intended host name. Request limits use the immediate peer IP; if your ingress is another proxy, configure Nginx `set_real_ip_from` for only that trusted proxy and `real_ip_header` before relying on per-client limits. Never trust forwarded IP headers from arbitrary clients.
6. Complete a live smoke test: register, log in, upload a PDF/DOCX, parse and confirm the profile, generate a bank, revise an answer, bookmark a question, complete an interview, and reopen its results. Verify that a second account cannot access the first account's data.

The production profile validates the schema and applies versioned Flyway migrations. Swagger is disabled. JWTs expire after one hour; logging out removes the browser token but does not revoke already issued tokens. Tokens are currently stored in browser local storage; keep the supplied CSP and avoid third-party scripts.

## Existing databases

V1 is a clean-install schema, not an automatic upgrade of an old Hibernate-managed database. Back up existing data and compare its schema with V1 in a staging copy. Apply reviewed corrections, validate the mappings, then explicitly baseline the matching database at version 1 using Flyway tooling before deploying. Automatic baseline is deliberately disabled. Never enable `ddl-auto=update` in production or baseline an unreviewed schema.

## Validation

Frontend: `npm ci`, `npm run lint`, `npm run build` from `ai-interview-frontend`.

Backend: `./gradlew test bootJar` from `backend` (Windows: `gradlew.bat`). Integration tests use H2 and mock Gemini. Set `TEST_POSTGRES_URL`, `TEST_POSTGRES_USER`, and `TEST_POSTGRES_PASSWORD` to an isolated, disposable PostgreSQL database to exercise the production migration and schema validation. CI runs this PostgreSQL check, builds both containers, and checks Nginx configuration.

On an isolated/staging deployment, run `node scripts/smoke.mjs https://your-staging-host`. This creates one test account and verifies frontend routing, security headers, authentication, protected APIs, and request validation. It does not call Gemini or replace the live AI smoke test.

`SchemaExportTests` writes `backend/build/postgresql-schema.sql` for mapping review; it is not applied at runtime. Future schema changes must receive a new migration; never edit an already deployed migration.

## Operations and recovery

- Use `/actuator/health/liveness` and `/actuator/health/readiness` internally. Readiness includes the database. Only health is exposed through Actuator; health details are hidden. The public frontend health check is `/healthz`.
- Inspect `docker compose logs --tail=200 backend`. Logs must not be used to store resumes, prompts, answers, tokens, or API keys. Restrict operational log access.
- Back up PostgreSQL using `pg_dump` and the uploads volume together; encrypt backups and test restoration into an isolated environment. Configure retention appropriate for candidate personal data. Do not use `docker compose down -v` on a live installation.
- Before an upgrade, record image tags and make a recoverable database/uploads backup. For rollback, restore the matching database and uploads backup with the previous images; an application rollback alone cannot reverse a migration.
- Set alerts for failed readiness, HTTP 5xx/429 rates, AI latency/errors, disk space, database connections, and Gemini spending. Configure provider quotas and billing alerts before opening registration publicly.
- The app bounds simultaneous Gemini calls to four per backend process and rejects overload. Each authenticated account receives 60 AI requests per hour (configure `app.ai.hourly-limit`); attempts count even when the provider fails. Quotas are in memory and reset on restart. Add durable shared quotas before scaling or high-volume public use; IP limits alone do not prevent distributed registration abuse.
- Local uploads require persistent disk and this deployment supports one backend instance. Multiple instances require shared object storage and coordinated rate limits.

Release gate: passing CI, healthy containers on the deployment host, HTTPS verified, live Gemini smoke test passed, and backup restoration demonstrated. Those environment-dependent checks must be completed before launch.

## Local verification — 2026-10-13

- Frontend build and lint passed; dependency audit reported zero vulnerabilities after compatible updates.
- Backend `test bootJar` passed: ten tests passed and the optional PostgreSQL CI test was skipped locally.
- Both production Docker images built; Nginx configuration passed validation.
- An isolated PostgreSQL 17 stack applied V1 and V2 migrations, passed Hibernate schema validation, and reported all three containers healthy with backend readiness UP.
- `scripts/smoke.mjs` passed against that stack through the frontend proxy. Gemini used a placeholder key for this infrastructure verification; AI workflow integration tests use a mocked provider.
- CI configuration now includes actual PostgreSQL schema validation and a complete Compose startup/smoke check. The GitHub workflow itself has not yet been executed from this workspace.

Still required on the target host: real Gemini credentials and a successful live AI workflow, the production domain and HTTPS ingress, monitoring/billing alerts, and a demonstrated backup restore. Existing databases also require the staging schema review described above.
