# Imara API Specification

Multi-tenant apartment billing management system. Each **company** manages buildings, apartments, service types, and bills. Users belong to a company and have roles (`COMPANY_OWNER` or `MAINTAINER`). **MAINTAINERs are apartment owners** who are promoted by the COMPANY_OWNER to manage a building.

---

## Table of Contents

1. [Domain Model & Flow](#domain-model--flow)
2. [API Overview](#api-overview)
3. [Authentication APIs](#authentication-apis)
4. [Company APIs](#company-apis)
5. [User APIs](#user-apis)
6. [Building APIs](#building-apis)
7. [Apartment APIs](#apartment-apis)
8. [Service Type APIs](#service-type-apis)
9. [Building Service Type APIs](#building-service-type-apis)
10. [Building Assignment APIs](#building-assignment-apis)
11. [Apartment Service Subscription APIs](#apartment-service-subscription-apis)
12. [User Apartment APIs](#user-apartment-apis)
13. [Bill APIs](#bill-apis)
14. [Implementation Checklist](#implementation-checklist)

---

## Domain Model & Flow

```
Company (tenant root)
├── Users (COMPANY_OWNER | MAINTAINER)
├── Buildings
│   ├── Apartments
│   ├── BuildingServiceTypes (which services + cost + period)
│   └── BuildingAssignments (Maintainer ↔ Building)
├── ServiceTypes (catalog: water, gas, etc.)
├── ApartmentServiceSubscriptions (optional services per apartment)
├── UserApartments (User ↔ Apartment - residents/tenants)
└── Bills (per apartment, service, period)
```

**Typical flow:**

1. Company signs up → creates first user (COMPANY_OWNER)
2. Company adds buildings, apartments, service types
3. For each building: configure BuildingServiceTypes (which services, cost, MONTHLY/YEARLY)
4. User registers (within the company) with basic info only – **apartment info stays null**
5. COMPANY_OWNER sends verification email to the user, then assigns apartment (creates UserApartment link with floor, apartment number, etc.)
6. COMPANY_OWNER assigns one of the building's apartment owners to be the MAINTAINER for that building (BuildingAssignment)
7. Optional: for optional services, apartments subscribe (ApartmentServiceSubscription)
8. Generate/issue bills, mark as PAID when done

---

## API Overview


| Module                  | Base Path                                                           | Status  |
| ----------------------- | ------------------------------------------------------------------- | ------- |
| Auth                    | `/api/auth`                                                         | Done ✓  |
| Companies               | `/api/companies`                                                    | Done ✓  |
| Users                   | `/api/companies/{companyId}/users`                                  | Pending |
| Buildings               | `/api/companies/{companyId}/buildings`                              | Done ✓  |
| Apartments              | `/api/companies/{companyId}/buildings/{buildingId}/apartments`      | Pending |
| Service Types           | `/api/companies/{companyId}/service-types`                          | Pending |
| Building Service Types  | `/api/companies/{companyId}/buildings/{buildingId}/service-types`   | Pending |
| Building Assignments    | `/api/companies/{companyId}/buildings/{buildingId}/assignments`     | Pending |
| Apartment Subscriptions | `/api/companies/{companyId}/apartments/{apartmentId}/subscriptions` | Pending |
| User Apartments         | `/api/companies/{companyId}/users/{userId}/apartments`              | Pending |
| Bills                   | `/api/companies/{companyId}/bills`                                  | Pending |


**Security:** All APIs except auth (login/register) require JWT. Users are scoped to their `companyId` from the token.

---

## Authentication APIs .

**Base:** `/api/auth`


| Method | Endpoint         | Description                                                          | Status  |
| ------ | ---------------- | -------------------------------------------------------------------- | ------- |
| POST   | `/login`         | Login with email + password, returns JWT                             | Done ✓  |
| GET    | `/me`            | Get current user info (requires auth)                                | Done ✓  |
| POST   | `/register`      | Sign up: create company + first user (COMPANY_OWNER)                 | Done ✓  |
| POST   | `/register/join` | Apartment owner joins existing company (invite link or company code) | Done ✓  |


**Register (company):** Create company + first user atomically. Request: `companyName`, `companyEmail`, `companyPhone`, `fullName`, `email`, `password`.

**Register (apartment owner):** Join existing company. Request: `companyId` or `inviteToken`, `fullName`, `email`, `password`. **No apartment info at registration** – apartment stays null. COMPANY_OWNER sends verification email, then assigns apartment info (UserApartment) and may promote to MAINTAINER.

---

## Company APIs 

**Base:** `/api/companies`  
**Auth:** JWT, user must belong to company (or be system admin if added later)


| Method | Endpoint       | Description                                                  | Status  |
| ------ | -------------- | ------------------------------------------------------------ | ------- |
| GET    | `/`            | List companies (filtered by user's company or all for admin) | Done ✓  |
| GET    | `/{companyId}` | Get company by ID                                            | Done ✓  |
| PUT    | `/{companyId}` | Update company                                               | Done ✓  |


**Note:** Company creation is via `/api/auth/register`. No separate POST for companies unless admin onboarding is needed.

---

## User APIs

**Base:** `/api/companies/{companyId}/users`  
**Auth:** COMPANY_OWNER or MAINTAINER of the company


| Method | Endpoint                      | Description                                       | Status  |
| ------ | ----------------------------- | ------------------------------------------------- | ------- |
| GET    | `/`                           | List users in company (filter: verified, pending) | Pending |
| GET    | `/{userId}`                   | Get user by ID                                    | Pending |
| POST   | `/`                           | Create user (invite)                              | Pending |
| POST   | `/{userId}/send-verification` | Send verification email to user (COMPANY_OWNER)   | Pending |
| PUT    | `/{userId}`                   | Update user                                       | Pending |
| DELETE | `/{userId}`                   | Soft delete user                                  | Pending |


---

## Building APIs

**Base:** `/api/companies/{companyId}/buildings`  
**Auth:** COMPANY_OWNER or MAINTAINER assigned to this building


| Method | Endpoint        | Description                       | Status  |
| ------ | --------------- | --------------------------------- | ------- |
| GET    | `/`             | List buildings in company         | Done ✓  |
| GET    | `/{buildingId}` | Get building by ID                | Done ✓  |
| POST   | `/`             | Create building                   | Done ✓  |
| PUT    | `/{buildingId}` | Update building                   | Done ✓  |
| DELETE | `/{buildingId}` | Delete (or soft-disable) building | Done ✓  |


---

## Apartment APIs

**Base:** `/api/companies/{companyId}/buildings/{buildingId}/apartments`  
**Auth:** COMPANY_OWNER or MAINTAINER of the building


| Method | Endpoint         | Description                        | Status  |
| ------ | ---------------- | ---------------------------------- | ------- |
| GET    | `/`              | List apartments in building        | Pending |
| GET    | `/{apartmentId}` | Get apartment by ID                | Pending |
| POST   | `/`              | Create apartment                   | Pending |
| PUT    | `/{apartmentId}` | Update apartment                   | Pending |
| DELETE | `/{apartmentId}` | Delete (or soft-disable) apartment | Pending |


---

## Service Type APIs

**Base:** `/api/companies/{companyId}/service-types`  
**Auth:** COMPANY_OWNER or MAINTAINER in company


| Method | Endpoint           | Description              | Status  |
| ------ | ------------------ | ------------------------ | ------- |
| GET    | `/`                | List service types       | Pending |
| GET    | `/{serviceTypeId}` | Get service type by ID   | Pending |
| POST   | `/`                | Create service type      | Pending |
| PUT    | `/{serviceTypeId}` | Update service type      | Pending |
| DELETE | `/{serviceTypeId}` | Soft delete service type | Pending |


---

## Building Service Type APIs

**Base:** `/api/companies/{companyId}/buildings/{buildingId}/service-types`  
**Auth:** COMPANY_OWNER or MAINTAINER of building

Links a service type to a building with cost and billing period. Required for bill generation.


| Method | Endpoint                   | Description                                               | Status  |
| ------ | -------------------------- | --------------------------------------------------------- | ------- |
| GET    | `/`                        | List building service types                               | Pending |
| GET    | `/{buildingServiceTypeId}` | Get by ID                                                 | Pending |
| POST   | `/`                        | Add service to building (cost, isOptional, billingPeriod) | Pending |
| PUT    | `/{buildingServiceTypeId}` | Update cost/period/optional                               | Pending |
| DELETE | `/{buildingServiceTypeId}` | Soft delete                                               | Pending |


---

## Building Assignment APIs

**Base:** `/api/companies/{companyId}/buildings/{buildingId}/assignments`  
**Auth:** COMPANY_OWNER

Assigns an apartment owner as MAINTAINER for a building. Only COMPANY_OWNER can promote a user (who must already be linked to an apartment in that building) to MAINTAINER.


| Method | Endpoint          | Description                   | Status  |
| ------ | ----------------- | ----------------------------- | ------- |
| GET    | `/`               | List assignments for building | Pending |
| POST   | `/`               | Assign user to building       | Pending |
| DELETE | `/{assignmentId}` | Remove assignment             | Pending |


---

## Apartment Service Subscription APIs

**Base:** `/api/companies/{companyId}/apartments/{apartmentId}/subscriptions`  
**Auth:** COMPANY_OWNER or MAINTAINER of the building

For **optional** building service types: which apartments have subscribed.


| Method | Endpoint            | Description                             | Status  |
| ------ | ------------------- | --------------------------------------- | ------- |
| GET    | `/`                 | List subscriptions for apartment        | Pending |
| POST   | `/`                 | Subscribe apartment to optional service | Pending |
| DELETE | `/{subscriptionId}` | Unsubscribe                             | Pending |


---

## User Apartment APIs

**Base:** `/api/companies/{companyId}/users/{userId}/apartments`  
**Auth:** COMPANY_OWNER or MAINTAINER

Links users to apartments (residents/tenants). **COMPANY_OWNER assigns apartment after verifying the user** (sends verification email, then links with floor/apartment info). Used for bill visibility and ownership.


| Method | Endpoint             | Description                                               | Status  |
| ------ | -------------------- | --------------------------------------------------------- | ------- |
| GET    | `/`                  | List apartments for user                                  | Pending |
| POST   | `/`                  | Link user to apartment (COMPANY_OWNER after verification) | Pending |
| DELETE | `/{userApartmentId}` | Unlink                                                    | Pending |


---

## Bill APIs

**Base:** `/api/companies/{companyId}/bills`  
**Auth:** COMPANY_OWNER or MAINTAINER; residents may see only their apartment's bills (optional)


| Method | Endpoint        | Description                                                     | Status  |
| ------ | --------------- | --------------------------------------------------------------- | ------- |
| GET    | `/`             | List bills (filter: apartmentId, buildingId, status, dateRange) | Pending |
| GET    | `/{billId}`     | Get bill by ID                                                  | Pending |
| POST   | `/`             | Create/issue bill                                               | Pending |
| PUT    | `/{billId}`     | Update bill (e.g. mark PAID)                                    | Pending |
| PATCH  | `/{billId}/pay` | Mark bill as paid                                               | Pending |


**Bill generation logic (backend):** For each apartment, for each building service type (required + subscribed optional), create bill for period if not exists.

---

## Implementation Checklist

Use this to track progress. Check off when done.

### Phase 1: Auth & Company Setup

- [x] `POST /api/auth/register` – create company + first COMPANY_OWNER user
- [x] `POST /api/auth/register/join` – user joins existing company (basic info only, apartment stays null)
- [x] Add `permitAll` for `/api/auth/register` and `/api/auth/register/join` in SecurityConfig
- [x] Create `RegisterRequest` DTO (company flow), `JoinCompanyRequest` DTO (apartment owner flow – no apartment info), `register()` and `joinCompany()` in IAuthService
- [ ] User verification: COMPANY_OWNER sends verification email to user
- [ ] COMPANY_OWNER assigns apartment (UserApartment) after verification

### Phase 2: Core CRUD (Company-scoped)

- [x] Companies: GET `/`, GET `/{id}`, PUT `/{id}`
- Users: full CRUD
- Buildings: full CRUD
- Apartments: full CRUD
- Service Types: full CRUD

### Phase 3: Building Configuration

- Building Service Types: full CRUD
- Building Assignments: list, create, delete

### Phase 4: Subscriptions & User-Apartment Links

- Apartment Service Subscriptions: list, create, delete
- User Apartments: list, create, delete

### Phase 5: Bills

- Bills: list (with filters), get, create, update
- `PATCH /bills/{id}/pay` for marking paid
- (Optional) Bill generation batch job

### Phase 6: Authorization

- Enforce company scope: user can only access their company's data
- MAINTAINER: restrict to assigned buildings only
- Resident: restrict bills to own apartments (if supported)

---

## Technical Notes

- **Base URL:** `http://localhost:8080` (or configured port)
- **Authorization header:** `Authorization: Bearer <jwt>`
- **Content-Type:** `application/json`
- **IDs:** UUID format
- **Tenant isolation:** All queries filter by `company_id` from JWT

