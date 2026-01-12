# Kubernetes Helm Deployment Monorepo

This repository contains a monorepo setup for a microservice-based application, designed for streamlined CI/CD using Jenkins, Docker, and Kubernetes Helm deployments.

---

## Project Structure

```
kubernetes-helm-deployment/
├── authors-service/
│   ├── Jenkinsfile      
│   ├── Dockerfile   
│   └── pom.xml    
├── books-service/
│   ├── Jenkinsfile
│   ├── Dockerfile
│   └── pom.xml
├── api-gateway/
│   ├── Jenkinsfile
│   ├── Dockerfile
│   └── pom.xml
├── bookstore-frontend/
│   ├── Jenkinsfile      
│   ├── Dockerfile  
│   └── package.json 
└── scripts/
    └── version.sh
```

* **Back-end services:** `authors-service`, `books-service`, `api-gateway` (Java/Spring Boot, Maven)
* **Front-end service:** `bookstore-frontend` (Angular, NodeJS)
* **Shared scripts:** `scripts/version.sh` – semantic versioning logic used by all pipelines

Each service has its own `Jenkinsfile`, `Dockerfile`, and build configuration.

---

## CI/CD Architecture

We use **Jenkins Multibranch Pipelines** to manage independent pipelines per service. Each service builds, tests, and pushes Docker images separately.

### Jenkins Dashboard

The dashboard shows all pipeline jobs per service.

![Jenkins Dashboard](docs/screenshots/jenkins-dashboard.png)

---

## Branch Strategy & Environments

The CI/CD setup is **branch-driven** and environment-aware.

| Branch | Purpose     | Docker Tag Format | Target               |
| ------ | ----------- | ----------------- | -------------------- |
| `dev`  | Development | `X.Y.Z-dev`       | Kubernetes Cluster A |
| `main` | Production  | `X.Y.Z`, `latest` | Kubernetes Cluster B |

**Key rules:**

* Pipelines run on **both `dev` and `main`**
* Images are built **only when the service directory changes**
* The same image versioning logic is shared across all services

---

## Semantic Versioning Strategy

Versioning is handled **outside Jenkinsfiles** via a shared script:

```
scripts/version.sh
```

### Version Format

```
MAJOR.MINOR.PATCH
```

### Increment Rules

| Change Type                 | Version Increment |
| --------------------------- | ----------------- |
| Bug fix / small change      | PATCH             |
| Backward-compatible feature | MINOR             |
| Breaking change             | MAJOR             |

### Branch Behavior

* **dev branch**

  * Automatically increments **PATCH**
  * Appends `-dev` suffix
  * Example:

    ```
    1.4.3-dev
    ```

* **main branch**

  * Uses the same base version
  * No suffix
  * Also publishes `latest`
  * Example:

    ```
    1.4.3
    latest
    ```

### Version Source of Truth

* The script queries **Docker Hub** for the latest image tag
* No build numbers
* No Git SHA tags
* Versions increase **only when the service actually changes**

---

## Jenkins Pipeline Overview

### Version Computation (All Services)

Each pipeline computes the version before building:

```bash
./scripts/version.sh <image-name> <branch>
```

The computed version is then used consistently for:

* Docker build
* Docker push
* Deployment (future Helm stages)

---

### Back-end services

1. Checkout code
2. Compute semantic version
3. Maven build: `mvn clean package -DskipTests`
4. Docker build using service directory as context
5. Docker push with computed tag
6. Post-build actions & workspace cleanup

---

### Front-end service (`bookstore-frontend`)

1. Compute semantic version

2. Checkout code

3. Angular build:

   ```bash
   npm ci
   npm run build -- --configuration production
   ```

4. Docker build and push

5. Post-build actions & cleanup

**NodeJS Tool:** NodeJS 24 (configured in Jenkins)

---

## Credentials

The Jenkins pipelines require the following credentials:

| Credential ID      | Type              | Scope  | Username / Token    | Purpose                                                                                   |
| ------------------ | ----------------- | ------ | ------------------- | ----------------------------------------------------------------------------------------- |
| `dockerlogin`      | Username/Password | Global | Docker Hub username | Authenticate with Docker Hub to build and push images using a **Docker Hub access token** |
| `github-api-token` | Username/Password | Global | `x-access-token`    | Authenticate with GitHub using a **fine-grained Personal Access Token (PAT)**             |

![Jenkins Credentials](docs/screenshots/jenkins-credentials.png)

### GitHub Fine-Grained PAT Details

* Generate a **fine-grained Personal Access Token (PAT)** in GitHub Developer settings
* **Repository access:** Select **“Only select repositories”** and choose **this project repository**
* Store it in Jenkins as **Username/Password credential**

  * **Username:** `x-access-token`
  * **Password:** GitHub PAT
  * Scope: **Global** – usable across all Jenkins jobs and nodes

**Required PAT Permissions:**

* `repo:status` – Read and write commit statuses
* `repo:contents` – Read repository contents
* `repo:metadata` – Read repository metadata

### Docker Hub Access Token Details

* Generate an **access token** in Docker Hub
* Store it in Jenkins as **Username/Password credential**

  * **Username:** Docker Hub username
  * **Password:** Docker Hub access token
  * Scope: **Global** – usable by all pipelines

---

## Jenkins Configuration

**Plugins Required:**

* GitHub Branch Source
* Pipeline
* Docker Pipeline
* Maven Integration
* NodeJS Plugin

**Tools Configured:**

* Maven 3.9.11
* JDK 17
* NodeJS 24

---

## Jenkins Multibranch Pipeline Configuration

**Build Configuration:**

* **Mode:** By Jenkinsfile

* **Script Path:**
  Example:

  ```
  api-gateway/Jenkinsfile
  ```

* **Branch Discovery:** All branches (`dev`, `main`)

* **Trigger:** GitHub webhook push events

---

### Execute Pipeline Only When a Service Changes

All build-related stages are guarded by:

```groovy
when {
  changeset "${SERVICE_DIR}/**"
}
```

This ensures:

* No version bump without real changes
* No unnecessary Docker images
* Clean, predictable version history

---

## GitHub Webhook

**Webhook URL:**

```
https://ngrok-jenkins/github-webhook/
```

* Content type: `application/json`
* Trigger: Push events
* SSL verification: Enabled

[GitHub Webhook Settings](docs/screenshots/webhook-github-settings.png)

---

## Example Workflow

**Change pushed to `authors-service` on `dev`:**

```
authors-service:1.2.4-dev
```

**Promoted to `main`:**

```
authors-service:1.2.4
authors-service:latest
```

Other services remain skipped.

---

## ☸️ Kubernetes Deployment

*(To be added later – Helm charts, environment-specific values, and automated deployments to Cluster A / Cluster B)*

