# HHCC Platform Monorepo

Helping Hands Care Centers (HHCC) – Digital Platform

This repository contains the full-stack, cloud-native, modular monorepo for the HHCC platform, supporting customer web, mobile, admin, backend API, and infrastructure as code.

## Structure
- apps/web: Customer web app (Next.js)
- apps/mobile: Customer mobile app (React Native)
- apps/admin: Admin portal (React + Vite)
- services/api: Java Spring Boot backend
- api-contracts/openapi: OpenAPI contracts
- packages: Shared TypeScript libraries
- infra: Kubernetes, Terraform, and infra code
- docs: Architecture, product, and delivery docs

## Getting Started with pnpm/yarn

### 1. Install pnpm or yarn (if not already installed)

#### pnpm
```
npm install -g pnpm
```

#### yarn
```
npm install -g yarn
```

### 2. Install dependencies (from project root)

#### pnpm
```
pnpm install
```

#### yarn
```
yarn install
```

### 3. Run the web app (Next.js landing page)

#### pnpm
```
pnpm --filter @hhcc/web dev
```

#### yarn
```
yarn workspace @hhcc/web dev
```

> Replace `@hhcc/web` with the correct package name if different in your monorepo.

### 4. Run tests for all packages/apps

#### pnpm
```
pnpm test
```

#### yarn
```
yarn test
```

---

## Quick Start
See `docs/README.md` for onboarding and development instructions.
