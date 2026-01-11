# Kubernetes Helm Deployment Monorepo

This repository contains a monorepo setup for a microservice-based application, designed for streamlined CI/CD using Jenkins, Docker, and Kubernetes Helm deployments.

---

##  Project Structure

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
└── bookstore-frontend/
    ├── Jenkinsfile      
    ├── Dockerfile  
    └── package.json 
```

* **Back-end services:** `authors-service`, `books-service`, `api-gateway` (Java/Spring Boot, Maven)
* **Front-end service:** `bookstore-frontend` (Angular, NodeJS)

Each service has its own `Jenkinsfile`, `Dockerfile`, and build configuration.

---

##  CI/CD Architecture

We use **Jenkins Multibranch Pipelines** to manage independent pipelines per service. Each service builds, tests, and pushes Docker images separately.

**Jenkins Dashboard Overview:**  

The dashboard shows all pipeline jobs per service.

[Jenkins Dashboard](docs/screenshots/jenkins-dashboard.png)

### Pipeline Jobs

| Service            | Jenkinsfile Path                 |
| ------------------ | -------------------------------- |
| authors-service    | `authors-service/Jenkinsfile`    |
| books-service      | `books-service/Jenkinsfile`      |
| api-gateway        | `api-gateway/Jenkinsfile`        |
| bookstore-frontend | `bookstore-frontend/Jenkinsfile` |

### Jenkins Pipeline Overview

**Back-end services:**

1. Checkout code
2. Maven build: `mvn clean package -DskipTests`
3. Docker build and push
4. Post-build actions & workspace cleanup

**Front-end service (`bookstore-frontend`):**

1. Checkout code
2. Angular build:

   ```bash
   npm ci
   npm run build -- --configuration production
   ```
3. Docker build and push
4. Post-build actions & cleanup

**NodeJS Tool:** NodeJS 24 (configured in Jenkins)

---

##  Credentials

The Jenkins pipelines require the following credentials:

| Credential ID      | Type              | Scope  | Username / Token    | Purpose                                                                                   |
| ------------------ | ----------------- | ------ | ------------------- | ----------------------------------------------------------------------------------------- |
| `dockerlogin`      | Username/Password | Global | Docker Hub username | Authenticate with Docker Hub to build and push images using a **Docker Hub access token** |
| `github-api-token` | Username/Password | Global | `x-access-token`    | Authenticate with GitHub using a **fine-grained Personal Access Token (PAT)**             |

[Jenkins Credentials](docs/screenshots/jenkins-credentials.png)

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

##  Jenkins Configuration

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

##  Jenkins Multibranch Pipeline Configuration

**Branch Sources:**

* **Type:** GitHub
* **Credentials:** `x-access-token/******` (“GitHub PAT for API access”)
* **Repository HTTPS URL:**

  ```
  https://github.com/rouisskhawla/kubernetes-helm-deployment.git
  ```
* **Validate:** Enabled to confirm repository access

**Build Configuration:**

* **Mode:** By Jenkinsfile
* **Script Path:** Specify the path to the service’s Jenkinsfile, e.g., `api-gateway/Jenkinsfile`
* **Scan Repository Triggers:**

  * Scan triggered by GitHub webhook events

**Pipeline Job Configuration:**  

[Pipeline Configuration](docs/screenshots/pipeline-config.png)


**Environment / Service Variables (for Jenkinsfile):**

Each service pipeline defines its own **environment variables**:

* `SERVICE_DIR` – the folder of the service in the repository (e.g., `authors-service`)
* `IMAGE_NAME` – Docker image name for the service (e.g., `username/authors-service`)

> These variables are used to detect changes in the service directory and to tag Docker images consistently.

**Execute Pipeline Only When a Service Changes:**

* Build, Docker Build, and Docker Push stages are wrapped with a condition:
  
  `when { changeset "SERVICE_DIR/**" }`
   
* This ensures that the pipeline **runs only if files in that service’s directory are modified**.

**Example behavior:**

* If only `authors-service/` files change, **only the `authors-service` pipeline runs**.

This setup **prevents unnecessary builds** and keeps CI/CD efficient by isolating each service.

**Multibranch Pipeline Notes:**

* Discover all branches in the repository
* Detect pull requests from origin
* Scan repository automatically using GitHub webhook triggers

---

## GitHub Webhook

**Webhook URL:**

```
https://ngrok-jenkins/github-webhook/
```

* Content type: `application/json`
* Trigger: Push events
* SSL verification: Enabled

**GitHub Webhook Settings:**  

Webhook triggers builds on push events.

[GitHub Webhook Settings](docs/screenshots/webhook-github-settings.png)


---

##  Example Workflow

* Updating a back-end service triggers only that service’s build. Other services are skipped.
* Updating `bookstore-frontend` triggers only its build.

Example:

```
✅ authors-service → Build #42 (SUCCESS)
⏭️ books-service → Skipped
⏭️ api-gateway → Skipped
⏭️ bookstore-frontend → Skipped
```

**Pipeline Run Example:**  

This shows a successful pipeline run for api-gateway service.

* Executed stages screenshot: [Pipeline Run](docs/screenshots/pipeline-run.png)

* Executed stages log: [pipeline-logs-executed.log](docs/logs/pipeline-logs-executed.log)  

**Skipped Pipeline Example:**  

Stages are skipped when no changes are made in the api-gateway service directory.

* Skipped stages: [Pipeline Skipped](docs/screenshots/pipeline-skipped.png)
* Skipped stages log: [pipeline-logs-skipped.log](docs/logs/pipeline-logs-skipped.log)

---

## ☸️ Kubernetes Deployment

*(To be added later – Helm charts and deployment instructions)*

