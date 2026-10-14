# Nova Bank — Java 17 Microservices Banking Project

A portfolio/interview-ready banking application built with:

- Java 17 + Spring Boot 4.1.1
- REST APIs
- Microservices: Auth, Account, Transaction
- React 19.3 + Vite 8
- MySQL 8.4
- Docker + Docker Compose
- Kubernetes
- AWS EKS + ECR deployment structure

## Architecture

```text
                         +----------------------+
                         |   React 19.3 UI      |
                         |   Vite / Nginx        |
                         +----------+-----------+
                                    |
                          /api/auth, /api/accounts,
                          /api/transactions
                                    |
          +-------------------------+--------------------------+
          |                         |                          |
  +-------v-------+        +--------v--------+        +--------v---------+
  | Auth Service  |        | Account Service |        | Transaction Svc  |
  | :8081         |        | :8082           |        | :8083             |
  | JWT + BCrypt  |        | balances        |        | transfer/history  |
  +-------+-------+        +--------+--------+        +--------+---------+
          |                         |                          |
    bank_auth DB              bank_account DB            bank_transaction DB
          +---------------------------+--------------------------+
                                      |
                                MySQL 8.4

Cloud target: Docker images -> Amazon ECR -> Amazon EKS -> RDS for MySQL
```

## Core API endpoints

### Auth

```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "demo@novabank.com",
  "password": "password"
}
```

### Accounts

```http
GET  /api/accounts
POST /api/accounts
GET  /api/accounts/{accountNumber}
POST /api/accounts/{accountNumber}/debit
POST /api/accounts/{accountNumber}/credit
```

### Transactions

```http
GET  /api/transactions
POST /api/transactions/transfer
```

## Demo credentials

Username: `demo@novabank.com`

Password: `password`

The demo user is seeded automatically in the auth service and two sample accounts are seeded in the account service.

## Run with Docker Compose

Prerequisites: Docker Desktop.

```bash
docker compose up --build
```

Open:

```text
http://localhost:3000
```

Services:

```text
auth-service        http://localhost:8081
account-service     http://localhost:8082
transaction-service http://localhost:8083
mysql               localhost:3306
frontend            http://localhost:3000
```

Stop:

```bash
docker compose down
```

Remove database data too:

```bash
docker compose down -v
```

## Run backend directly

From `backend/`:

```bash
mvn clean package
```

Then run each service jar separately.

Default local ports are 8081, 8082 and 8083.

## Run React directly

From `frontend/`:

```bash
npm install
npm run dev
```

Open `http://localhost:5173`.

The Vite config proxies `/api/auth`, `/api/accounts`, and `/api/transactions` to the local Spring services.

## Kubernetes

```bash
kubectl apply -k k8s/
kubectl get pods -n nova-bank
kubectl get svc -n nova-bank
```

The included Kubernetes manifests use MySQL in the cluster to keep the learning setup simple. On AWS, prefer Amazon RDS for MySQL for a production database and store secrets outside plain Kubernetes YAML.

## AWS

See `aws/README.md` for EKS/ECR deployment steps and `aws/cluster.yaml` for an `eksctl` cluster definition.

## Interview talking points

1. Authentication uses stateless JWT tokens with BCrypt password hashing.
2. Each microservice owns its own database/schema boundary.
3. REST APIs are separated by business capability rather than one monolithic application.
4. Docker packages each service independently.
5. Kubernetes Deployments provide replicas, health probes and service discovery.
6. AWS target architecture uses ECR for images, EKS for workloads and RDS for MySQL.

## Important production improvements

This project is intentionally a portfolio/learning starter, not a real banking platform. Before real financial use, add proper OAuth2/OIDC, MFA, refresh-token rotation, audit trails, idempotency keys, transaction/outbox patterns, service-to-service authentication, encryption/key management, fraud/risk controls, rate limiting, observability, secrets management, HA database design and comprehensive automated tests.
