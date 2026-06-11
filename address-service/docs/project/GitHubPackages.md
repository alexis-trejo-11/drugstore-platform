# GitHub Packages — Shared Library (`shared-kernel`)

Address Service consumes platform-wide Java utilities from **`shared-kernel`**, published as a Maven artifact to **GitHub Packages** instead of copying source into each microservice or relying on `mavenLocal()` only.

## Overview

| Item | Value |
|------|-------|
| **Artifact** | `io.github.alexisTrejo11:shared-kernel:2.0.0` |
| **Registry** | `https://maven.pkg.github.com/alexisTrejo11/drugstore-platform` |
| **Source module** | `libs/shared-kernel` in the monorepo |
| **Java package** | `libs_kernel.*` |

### What `shared-kernel` provides (examples used here)

- **API responses:** `ResponseWrapper`, `Error`, `Result`
- **Exceptions:** `ResourceNotFoundException`, `ValidationException`, `RateLimitExceededException`, etc.
- **Security / JWT:** `JwtProperties`, `TokenValidationResponse`, DTOs
- **Audit logging:** `AuditEvent`, audit logger helpers
- **Pagination / mappers:** shared kernel page and mapper contracts

Keeping this logic in one published library avoids duplicating cross-cutting code across auth, address, cart, and other services.

---

## Prerequisites

1. A GitHub account with access to the **`drugstore-platform`** repository.
2. A **Personal Access Token (classic)** or fine-grained token with:
   - **`read:packages`** — required to download dependencies (address-service build).
   - **`write:packages`** — required only when **publishing** from `libs/shared-kernel`.

---

## Local development (Gradle)

### 1. Configure credentials

Copy `.env.example` to `.env` and set:

```env
GITHUB_ACTOR=your_github_username
GITHUB_TOKEN=ghp_your_token_with_read_packages
```

Gradle loads these **before** dependency resolution (see `build.gradle` `loadDotEnv` / `envOrDotEnv`). Shell exports also work and take precedence over `.env` when both are set.

Alternative (CI or one-off):

```bash
export GITHUB_ACTOR=your_github_username
export GITHUB_TOKEN=ghp_...
./gradlew build
```

Or Gradle properties:

```bash
./gradlew build -PGITHUB_ACTOR=your_user -PGITHUB_TOKEN=ghp_...
```

### 2. Declare the repository and dependency

In `build.gradle`:

```groovy
repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/alexisTrejo11/drugstore-platform")
        credentials {
            username = envOrDotEnv("GITHUB_ACTOR", "alexisTrejo11")
            password = envOrDotEnv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    implementation 'io.github.alexisTrejo11:shared-kernel:2.0.0'
}
```

### 3. Build and run

```bash
./gradlew build
./gradlew bootRun
```

Verify resolution:

```bash
./gradlew dependencies --configuration compileClasspath | grep shared-kernel
```

Expected line:

```text
+--- io.github.alexisTrejo11:shared-kernel:2.0.0
```

---

## Docker / Docker Compose

The image build runs Gradle **inside** the container. `.env` is not copied into the image; credentials are passed as **build args** from Compose (which reads `.env` for variable substitution).

**`Dockerfile` (builder stage):**

```dockerfile
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN
ENV GITHUB_ACTOR=$GITHUB_ACTOR
ENV GITHUB_TOKEN=$GITHUB_TOKEN
```

**`docker-compose.yml`:**

```yaml
address-service:
  build:
    args:
      GITHUB_ACTOR: ${GITHUB_ACTOR:-alexisTrejo11}
      GITHUB_TOKEN: ${GITHUB_TOKEN:?Set GITHUB_TOKEN in .env for GitHub Packages}
```

Ensure `.env` contains `GITHUB_TOKEN` before:

```bash
docker compose up --build
```

---

## Publishing a new `shared-kernel` version

From the monorepo root, in `libs/shared-kernel`:

```bash
export GITHUB_ACTOR=your_github_username
export GITHUB_TOKEN=ghp_...   # needs write:packages

./gradlew publish
```

Then bump the version in address-service `build.gradle`:

```groovy
implementation 'io.github.alexisTrejo11:shared-kernel:2.0.1'  // example
```

Publish configuration lives in `libs/shared-kernel/build.gradle` (`maven-publish` → GitHub Packages URL above).

---

## Troubleshooting

| Symptom | Likely cause | Fix |
|---------|----------------|-----|
| `401 Unauthorized` from `maven.pkg.github.com` | Missing/invalid token or no repo access | Regenerate PAT with `read:packages`; confirm access to `drugstore-platform` |
| Works locally, fails in `docker compose build` | Token not passed into build | Add `GITHUB_TOKEN` to `.env`; rebuild with `--no-cache` if needed |
| `Could not resolve shared-kernel` offline | No network or wrong URL | URL must be `drugstore-platform` (not typos like `drugstore-plattform`) |
| Empty credentials in Gradle | `.env` only loaded in `bootRun` (old setup) | Use current `build.gradle` that loads `.env` at configuration time |

**Security:** Never commit `.env` or tokens. Add `.env` to `.gitignore`. Rotate tokens if leaked.

---

## Related files

| Path | Role |
|------|------|
| `build.gradle` | GitHub Packages repo + `shared-kernel` dependency + `.env` loader |
| `.env.example` | Template for `GITHUB_ACTOR` / `GITHUB_TOKEN` |
| `Dockerfile` | Build-args → env for Gradle in image build |
| `docker-compose.yml` | Forwards credentials into `docker compose build` |
| `libs/shared-kernel/build.gradle` | Publish `shared-kernel` to GitHub Packages |

---

## See also

- [Project Features](ProjectFeature.md) — feature **#13** summary (raw markdown)
- [Project Features (Obsidian source)](obsidian/ProjectFeature.md) — structured feature entry `github-packages-shared-kernel`
- [README](../../README.md) — run locally and Docker stack
