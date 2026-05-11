## Cursor Cloud specific instructions

### Project overview

Multi-tenant e-commerce platform (Wave 1 Starter Kit) built with Java 21, Spring Boot 3.5, and hexagonal architecture. Three bounded contexts: `tenant`, `iam`, `catalog`. All persistence is currently in-memory (`ConcurrentHashMap`); PostgreSQL/JPA are declared in the POM but excluded from auto-configuration. The frontend is a minimal Angular stub with placeholder scripts only.

### Prerequisites

- **Java 21** (pre-installed on Cloud Agent VMs)
- **Maven** must be installed (`sudo apt-get install -y maven`); the project has no Maven wrapper (`mvnw`)

### Running the backend

```bash
cd backend
mvn spring-boot:run
```

The server starts on port 8080. No external services (database, Docker, etc.) are needed.

### Key dev commands (from `backend/`)

| Command | Purpose |
|---|---|
| `mvn clean verify` | Full build: compile, unit/integration/BDD/ArchUnit tests, JaCoCo coverage (80% line min), Spotless format check |
| `mvn test` | Run tests only (faster, skips verify-phase plugins) |
| `mvn spotless:check` | Lint/format check (Google Java Format) |
| `mvn spotless:apply` | Auto-fix formatting |
| `mvn spring-boot:run` | Start dev server on port 8080 |

### Authentication (dev seed)

The in-memory user repository seeds a dev admin: `admin@local.dev` / `admin-secret` (PLATFORM_ADMIN role, tenant ID `11111111-2222-3333-4444-555555555555`). Use `POST /api/auth/login` to get a JWT, then pass it as `Authorization: Bearer <token>` header.

### API documentation

Swagger UI is available at `http://localhost:8080/swagger-ui/index.html` when the server is running. Public endpoints: `/api/auth/login`, `/actuator/**`, Swagger UI. All `/api/admin/**` endpoints require a valid JWT.

### Gotchas

- The project has **no Maven wrapper** (`mvnw`). System `mvn` must be on PATH.
- JPA/PostgreSQL are on the classpath but **excluded** from auto-configuration in `Wave1BackendApplication.java`. Do not remove the `exclude` attribute unless wiring real persistence.
- Spotless enforces Google Java Format; run `mvn spotless:apply` before committing to auto-fix formatting.
- JaCoCo coverage gate is 80% line coverage for `tenant.domain`, `tenant.application`, `iam.domain`, and `iam.application` packages.
