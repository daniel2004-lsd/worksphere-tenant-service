# WorkSphere — Tenant Management Service (`worksphere-tenant-service`)

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal%20%2F%20Clean-blue.svg)]()
[![License](https://img.shields.io/badge/License-Proprietary-red.svg)]()

> **Autonomous Polyglot Microservice** responsible for multi-tenant organization provisioning, membership management, PostgreSQL Row-Level Security session context, and atomic domain event emission via the Transactional Outbox Pattern.

---

## 🏛️ Architecture & Clean Boundaries

* **Decoupled Database:** Operates against `tenant_db`. Schema definitions and database migrations are managed independently in [`worksphere-tenant-db`](https://github.com/daniel2004-lsd/worksphere-tenant-db). This service does not embed DDL scripts.
* **Multi-Tenant Isolation:** Enforces PostgreSQL Row-Level Security by binding the authenticated Keycloak `tenant_id` to the database connection via `SET LOCAL app.current_tenant_id = ?`.
* **Transactional Outbox:** Atomically registers events in `outbox_events` within the local transaction before asynchronous publication to RabbitMQ.

---

## 🌿 Branching Model

This repository follows the formal 4-tier Git Flow per governance standards:
* `develop` (Integration and active development)
* `qa` (Integration testing & RLS security audits)
* `staging` (Pre-production smoke testing)
* `main` (Production release baseline)
