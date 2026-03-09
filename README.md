# Donum — NGO Donation & Relief Distribution Platform

> A full-stack web and desktop application for managing donations, inventory, relief campaigns, and last-mile distribution for non-governmental organizations.

**Version:** 2.0  
**Stack:** Java 11 · Servlets 4.0 · JSP/JSTL · MySQL · Maven · Chart.js · Java Swing  
**Type:** College Major Project (4th Semester)

---

## Table of Contents

1. [Purpose & Problem Statement](#1-purpose--problem-statement)
2. [Similar Existing Platforms](#2-similar-existing-platforms)
3. [Feature Overview](#3-feature-overview)
4. [System Architecture](#4-system-architecture)
5. [Database Design](#5-database-design)
6. [Technology Stack](#6-technology-stack)
7. [Module-wise Breakdown](#7-module-wise-breakdown)
8. [Security Implementation](#8-security-implementation)
9. [AI Matching Algorithm](#9-ai-matching-algorithm)
10. [User Interface](#10-user-interface)
11. [Project Structure](#11-project-structure)
12. [Installation & Setup](#12-installation--setup)
13. [Demo Credentials](#13-demo-credentials)
14. [Advanced SQL Features](#14-advanced-sql-features)
15. [Desktop Application](#15-desktop-application)
16. [Future Scope](#16-future-scope)

---

## 1. Purpose & Problem Statement

### The Problem

When disasters strike — floods, earthquakes, pandemics — NGOs receive thousands of donations (cash and goods) from across the country. Managing this manually leads to:

- **Wastage:** Goods expire in warehouses while people in affected areas go without.
- **Duplication:** Multiple teams send the same item to the same location while other areas get nothing.
- **No accountability:** Donors can't track where their money went. Volunteers don't know which areas are most critical.
- **No receipts:** Donors need tax receipts (Section 80G in India) but NGOs generate them manually.
- **Disconnected systems:** Donations tracked in spreadsheets, inventory on paper, distributions via WhatsApp.

### The Solution — Donum

Donum is a centralized platform that digitizes the entire donation-to-distribution pipeline:

```
DONOR donates              ADMIN manages              VOLUNTEER delivers
(Cash or Goods)   ───▶    (Inventory, Campaigns)  ───▶  (Last-mile to locations)
       │                         │                            │
       ▼                         ▼                            ▼
  Auto PDF Receipt      AI Matching Algorithm        Auto Inventory Deduction
  Campaign Tracking     Real-time Analytics          Field Notes & History
```

**In one sentence:** Donum ensures the right supplies reach the right people at the right time, with full transparency and accountability.

---

## 2. Similar Existing Platforms

Donum is inspired by real-world systems used by large organizations:

| Platform | Organization | What It Does | How Donum Compares |
|----------|-------------|-------------|-------------------|
| **Humanitarian OpenStreetMap (HOT)** | OpenStreetMap | Maps disaster-affected areas for relief coordination | Donum focuses on supply chain, not mapping |
| **ReliefWeb** | UN OCHA | Aggregates humanitarian reports and data globally | Donum is operational (manages donations/inventory), not just informational |
| **Kiva** | Kiva.org | Crowdfunding platform for micro-loans to underserved communities | Similar donor-facing experience; Donum adds inventory + distribution |
| **GiveDirectly** | GiveDirectly.org | Direct cash transfers to extreme poor | Cash donation tracking similar; Donum also handles in-kind goods |
| **NetHope** | NetHope.org | Technology solutions for humanitarian organizations | Similar mission; Donum is an actual implementation |
| **Salesforce Nonprofit Cloud** | Salesforce | CRM for donor management, fundraising, program tracking | Enterprise-grade equivalent; Donum is open-source and self-hosted |
| **Odoo Humanitarian** | Odoo | ERP module for NGO operations (inventory, donations, volunteers) | Closest comparison — Donum is a simplified, focused version |
| **DHIS2** | University of Oslo | Health data management for developing countries | Different domain but similar architecture philosophy |

### What Makes Donum Different

1. **Purpose-built for relief operations** — Not a generic CRM adapted for NGOs
2. **AI-powered distribution** — Matching algorithm prioritizes by urgency and stock levels
3. **Database triggers** — Automatic inventory management (no manual stock updates)
4. **Dual-platform** — Web app for all users + Desktop app for admin operations
5. **Self-contained** — No external APIs, cloud services, or paid dependencies

---

## 3. Feature Overview

### 3.1 Donor Features
- Register and login securely
- Donate **Cash** (monetary) or **In-Kind** (goods like rice, blankets, medicines)
- Select a specific **Campaign** to donate to (e.g., "Kerala Flood Relief 2025")
- Add notes to donations
- View complete **donation history** with status tracking (Received → Allocated → Delivered)
- Download **branded PDF tax receipts** (Section 80G compliant)
- Browse **active campaigns** with progress bars showing how much has been raised

### 3.2 Admin Features
- View **5 real-time stat cards**: Total Donations, Cash Raised, Distributions, Pending Requirements, Low Stock Items
- View **4 interactive charts** (Chart.js):
  - **Donation Trends** — Line chart showing monthly cash + in-kind donations
  - **Donation Types** — Doughnut chart showing Cash vs Kind breakdown
  - **Urgency Distribution** — Bar chart of requirement urgency levels
  - **Campaign Progress** — Horizontal bar chart showing campaign completion %
- View **AI Distribution Plan** — Matching algorithm output showing what to send where
- Manage **inventory** with warehouse details and stock status (OK / Low / Critical)
- View and add **field requirements** with urgency levels and campaign links
- Monitor **recent distributions** by volunteers
- Full **audit trail** of all login events

### 3.3 Volunteer Features
- View **pending field requirements** sorted by urgency (Critical first)
- Log **distributions** — record what was delivered, where, and how much
- Add **field notes** (ground conditions, difficulties encountered)
- View **personal distribution history**
- Automatic **inventory deduction** via database trigger when distribution is logged

### 3.4 System Features
- **BCrypt password hashing** — Passwords never stored as plain text
- **Session-based authentication** with 30-minute timeout
- **Role-based access control** — Admin pages blocked for non-admin users
- **Security headers** — XSS protection, clickjacking prevention, cache control
- **Input validation** — Server-side validation on all forms
- **XSS prevention** — All user-provided data escaped with `fn:escapeXml()`
- **Connection pooling** — HikariCP with 15 max connections
- **Audit logging** — Login events recorded with IP address and timestamp

---

## 4. System Architecture

### 4.1 Architecture Pattern: MVC (Model-View-Controller)

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                              │
│                                                                  │
│   Browser (JSP + CSS + JavaScript + Chart.js)                   │
│   Desktop (Java Swing)                                           │
└─────────────────────┬────────────────────────────────────────────┘
                      │ HTTP (GET/POST) + AJAX (JSON)
┌─────────────────────▼────────────────────────────────────────────┐
│                     PRESENTATION LAYER                            │
│                                                                   │
│   ┌──────────────┐  ┌──────────────────────────────────────┐     │
│   │  AuthFilter   │  │           SERVLETS                   │     │
│   │  (Security)   │──▶  LoginServlet      LogoutServlet     │     │
│   │              │  │  RegisterServlet    DonationServlet   │     │
│   │  Intercepts  │  │  DistributeServlet  RequirementServlet│     │
│   │  ALL requests│  │  ReceiptServlet     DashboardApiServlet│    │
│   └──────────────┘  └──────────────┬───────────────────────┘     │
└────────────────────────────────────┼─────────────────────────────┘
                                     │
┌────────────────────────────────────▼─────────────────────────────┐
│                      BUSINESS LOGIC LAYER                         │
│                                                                   │
│   ┌─────────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│   │MatchingAlgorithm│  │ PasswordUtil │  │ Model Classes    │   │
│   │(AI Distribution)│  │ (BCrypt)     │  │ (User, Donation, │   │
│   └─────────────────┘  └──────────────┘  │  Campaign, etc.) │   │
│                                           └──────────────────┘   │
└────────────────────────────────────┬─────────────────────────────┘
                                     │
┌────────────────────────────────────▼─────────────────────────────┐
│                      DATA ACCESS LAYER (DAO)                      │
│                                                                   │
│   UserDAO  DonationDAO  InventoryDAO  RequirementDAO             │
│   DistributionDAO  CampaignDAO  DashboardDAO                     │
│                         │                                         │
│              ┌──────────▼──────────┐                              │
│              │  DBUtil (HikariCP)  │                              │
│              │  Connection Pool    │                              │
│              └──────────┬──────────┘                              │
└─────────────────────────┼────────────────────────────────────────┘
                          │ JDBC (SQL)
┌─────────────────────────▼────────────────────────────────────────┐
│                      DATABASE LAYER (MySQL)                       │
│                                                                   │
│   10 Tables · 3 Triggers · 3 Stored Procedures · 2 Views        │
│   Indexes · Foreign Key Constraints                               │
└──────────────────────────────────────────────────────────────────┘
```

### 4.2 Request Lifecycle

Every HTTP request follows this exact path:

```
1. Browser sends request (GET /admin-dashboard or POST /donate)
          │
2. Tomcat receives it
          │
3. AuthFilter.doFilter() intercepts
          │
          ├── Is URL public? (/login, /register, /css/*, /js/*)
          │     YES → Pass through
          │     NO  → Check session
          │              │
          │              ├── Session exists? (user logged in?)
          │              │     NO  → Redirect to /login
          │              │     YES → Check role permissions
          │              │              │
          │              │              ├── Admin page + non-admin user?
          │              │              │     YES → Redirect to their dashboard
          │              │              │     NO  → Pass through
          │
4. Servlet handles the request
          │
          ├── doGet()  → Loads data via DAOs → Forwards to JSP
          ├── doPost() → Validates input → Calls DAO → Redirects
          │
5. DAO executes SQL via DBUtil.getConnection()
          │
          ├── HikariCP provides pooled connection
          ├── SQL runs (may trigger MySQL triggers)
          ├── Connection returned to pool
          │
6. JSP renders HTML
          │
          ├── header.jspf (nav bar, CSS/JS includes)
          ├── Page content (tables, forms, charts)
          ├── footer.jspf (footer, app.js include)
          │
7. Browser renders the page
          ├── CSS styles the dark theme
          ├── app.js initializes Chart.js charts (if dashboard)
          ├── Charts fetch data from /api/dashboard/* (AJAX → JSON)
```

### 4.3 Chart Data Flow (AJAX/REST API)

The admin dashboard charts load data asynchronously after the page renders:

```
Browser (app.js)                    Server (DashboardApiServlet)
      │                                        │
      │  GET /api/dashboard/trends             │
      ├───────────────────────────────────────▶│
      │                                        │ DashboardDAO.getMonthlyDonationTrends()
      │                                        │ → SELECT from v_monthly_donations view
      │         JSON Response                  │
      │◀───────────────────────────────────────┤
      │  [{"month":"Jan","total_cash":15000}]  │
      │                                        │
      │  Chart.js renders line chart           │
      │                                        │
      │  GET /api/dashboard/donation-types     │
      ├───────────────────────────────────────▶│
      │                                        │ DashboardDAO.getDonationsByType()
      │◀───────────────────────────────────────┤
      │  Chart.js renders doughnut chart       │
      │                                        │
      │  (same for /urgency and /campaigns)    │
```

---

## 5. Database Design

### 5.1 Entity-Relationship Overview

The database contains **10 tables** with the following relationships:

```
organizations (1) ──── (N) users
organizations (1) ──── (N) campaigns
organizations (1) ──── (N) warehouses

users (1) ──── (N) donations        [as donor]
users (1) ──── (N) distribution_log [as volunteer]
users (1) ──── (N) audit_log        [login tracking]

campaigns (1) ──── (N) donations
campaigns (1) ──── (N) requirements

requirements (1) ──── (N) distribution_log

warehouses (1) ──── (N) inventory

donations (1) ──── (1) transactions
```

### 5.2 Table Schemas

#### `organizations` — Multi-tenancy support
| Column | Type | Description |
|--------|------|-------------|
| org_id | INT PK AUTO_INCREMENT | Unique identifier |
| org_name | VARCHAR(100) | Organization name |
| type | ENUM('NGO','Corporate Sponsor','Government') | Organization type |
| country | VARCHAR(50) | Country of operation |
| address | VARCHAR(255) | Physical address |
| phone | VARCHAR(20) | Contact phone |
| email | VARCHAR(100) | Contact email |
| website | VARCHAR(200) | Website URL |
| logo_url | VARCHAR(500) | Logo image URL |
| active | BOOLEAN DEFAULT TRUE | Active status |

#### `users` — All platform users
| Column | Type | Description |
|--------|------|-------------|
| user_id | INT PK AUTO_INCREMENT | Unique identifier |
| org_id | INT FK → organizations | Associated organization (nullable) |
| username | VARCHAR(50) UNIQUE | Login username |
| password_hash | VARCHAR(255) | BCrypt hashed password |
| email | VARCHAR(100) UNIQUE | Email address |
| role | ENUM('Admin','Donor','Volunteer') | User role |
| full_name | VARCHAR(100) | Display name |
| phone | VARCHAR(20) | Phone number |
| address | VARCHAR(255) | Physical address |
| avatar_url | VARCHAR(500) | Profile picture URL |
| active | BOOLEAN DEFAULT TRUE | Account status |
| last_login | TIMESTAMP | Last login time |
| created_at | TIMESTAMP | Registration time |

#### `campaigns` — Fundraising campaigns
| Column | Type | Description |
|--------|------|-------------|
| campaign_id | INT PK AUTO_INCREMENT | Unique identifier |
| org_id | INT FK → organizations | Owning organization |
| name | VARCHAR(100) | Campaign name |
| description | TEXT | Campaign details |
| target_amount | DECIMAL(15,2) | Fundraising goal |
| raised_amount | DECIMAL(15,2) DEFAULT 0 | Amount raised so far |
| start_date | DATE | Campaign start |
| end_date | DATE | Campaign end (nullable) |
| status | ENUM('Active','Completed','Paused') | Current status |

#### `warehouses` — Storage locations
| Column | Type | Description |
|--------|------|-------------|
| warehouse_id | INT PK AUTO_INCREMENT | Unique identifier |
| org_id | INT FK → organizations | Owning organization |
| name | VARCHAR(100) | Warehouse name |
| location | VARCHAR(200) | Address/location |
| capacity | INT | Total storage capacity |
| manager_name | VARCHAR(100) | Manager's name |
| phone | VARCHAR(20) | Contact phone |
| active | BOOLEAN DEFAULT TRUE | Operational status |

#### `inventory` — Current stock levels
| Column | Type | Description |
|--------|------|-------------|
| item_id | INT PK AUTO_INCREMENT | Unique identifier |
| warehouse_id | INT FK → warehouses | Storage location |
| item_name | VARCHAR(100) | Item name |
| category | VARCHAR(50) | Category (Food, Medical, Clothing, Shelter, Other) |
| quantity | DECIMAL(10,2) | Current stock |
| unit | VARCHAR(20) | Unit of measurement |
| min_threshold | DECIMAL(10,2) DEFAULT 10 | Low stock alert threshold |
| last_updated | TIMESTAMP | Last stock update |

#### `donations` — All donations received
| Column | Type | Description |
|--------|------|-------------|
| donation_id | INT PK AUTO_INCREMENT | Unique identifier |
| donor_id | INT FK → users | Who donated |
| campaign_id | INT FK → campaigns | Which campaign (nullable) |
| type | ENUM('Cash','Kind') | Donation type |
| amount_or_quantity | DECIMAL(12,2) | Amount (cash) or quantity (goods) |
| item_name | VARCHAR(100) | Item name (for Kind type) |
| status | ENUM('Received','Allocated','Delivered') | Tracking status |
| notes | TEXT | Donor's notes |
| donation_date | TIMESTAMP | When donated |

#### `transactions` — Payment records
| Column | Type | Description |
|--------|------|-------------|
| transaction_id | VARCHAR(36) PK | UUID transaction ID |
| donation_id | INT FK → donations | Linked donation |
| payment_method | VARCHAR(50) | UPI, Card, Bank Transfer, etc. |
| amount | DECIMAL(12,2) | Transaction amount |
| currency | VARCHAR(3) DEFAULT 'INR' | Currency code |
| status | ENUM('Success','Failed','Pending') | Payment status |
| gateway_response | TEXT | Payment gateway response |
| created_at | TIMESTAMP | Transaction time |

#### `requirements` — Field needs
| Column | Type | Description |
|--------|------|-------------|
| requirement_id | INT PK AUTO_INCREMENT | Unique identifier |
| campaign_id | INT FK → campaigns | Related campaign (nullable) |
| item_name | VARCHAR(100) | What is needed |
| quantity_needed | DECIMAL(10,2) | How much is needed |
| quantity_fulfilled | DECIMAL(10,2) DEFAULT 0 | How much has been delivered |
| location | VARCHAR(100) | Where it's needed |
| urgency | ENUM('Critical','High','Medium','Low') | Priority level |
| description | TEXT | Additional details |
| created_at | TIMESTAMP | When requirement was added |

#### `distribution_log` — Delivery records
| Column | Type | Description |
|--------|------|-------------|
| log_id | INT PK AUTO_INCREMENT | Unique identifier |
| requirement_id | INT FK → requirements | Which requirement was fulfilled |
| volunteer_id | INT FK → users | Who delivered |
| quantity | DECIMAL(10,2) | Amount delivered |
| notes | TEXT | Field observations |
| distributed_at | TIMESTAMP | Delivery time |

#### `audit_log` — Security audit trail
| Column | Type | Description |
|--------|------|-------------|
| log_id | INT PK AUTO_INCREMENT | Unique identifier |
| user_id | INT FK → users | Who performed the action |
| action | VARCHAR(100) | What was done |
| details | TEXT | Additional details |
| ip_address | VARCHAR(45) | Client IP address |
| created_at | TIMESTAMP | When it happened |

### 5.3 Triggers

| Trigger | Event | Action |
|---------|-------|--------|
| `trg_after_donation_insert` | AFTER INSERT on `donations` | If donation type is 'Kind', automatically inserts or updates the item in the `inventory` table |
| `trg_after_distribution_insert` | AFTER INSERT on `distribution_log` | Automatically deducts the distributed quantity from `inventory` and updates `quantity_fulfilled` in `requirements` |
| `trg_after_campaign_update` | AFTER UPDATE on `campaigns` | If `raised_amount >= target_amount`, automatically sets campaign status to 'Completed' |

### 5.4 Stored Procedures

| Procedure | Purpose | Called By |
|-----------|---------|-----------|
| `GenerateImpactReport()` | Returns total donations, total cash, total distributions, unique beneficiary locations, active campaigns | `DistributionDAO.getImpactReport()` |
| `InventoryRiskAssessment()` | Returns all items where `quantity < min_threshold`, showing item name, current stock, threshold, warehouse, and risk level | Callable from any DAO |
| `GetDashboardStats()` | Returns 5 key metrics in a single query: total donations count, total cash amount, total distributions, pending requirements, active campaigns | `DashboardDAO.getAdminStats()` |

### 5.5 Views

| View | Definition | Purpose |
|------|-----------|---------|
| `v_monthly_donations` | Aggregates donations by month, splitting into total_cash and total_kind | Powers the "Donation Trends" line chart |
| `v_campaign_progress` | Joins campaigns with organizations, calculates progress percentage | Powers the "Campaign Progress" chart |

---

## 6. Technology Stack

### 6.1 Backend

| Technology | Version | Purpose |
|-----------|---------|---------|
| **Java** | 11 (LTS) | Core programming language |
| **Java Servlets** | 4.0 (javax.servlet) | HTTP request handling — NO Spring Framework |
| **JSP** | 2.3 | Server-side HTML templating |
| **JSTL** | 1.2 | Tag library for JSP (loops, conditionals, formatting, XSS escaping) |
| **HikariCP** | 4.0.3 | High-performance JDBC connection pool (15 max connections) |
| **jBCrypt** | 0.4 | BCrypt password hashing (10 salt rounds) |
| **Gson** | 2.10.1 | Java to JSON serialization (for REST API responses) |
| **iText** | 5.5.13 | PDF generation (donation receipts) |
| **SLF4J** | 1.7.36 | Logging facade |
| **MySQL Connector/J** | 8.0.33 | JDBC driver for MySQL |

### 6.2 Frontend

| Technology | Version | Purpose |
|-----------|---------|---------|
| **HTML5/CSS3** | — | Page structure and styling |
| **JavaScript (ES6)** | — | Client-side interactivity |
| **Chart.js** | 4.4.1 (CDN) | Interactive charts (line, doughnut, bar) |
| **Font Awesome** | 6.5.1 (CDN) | Icon library |
| **Google Fonts (Inter)** | — | Typography |

### 6.3 Database

| Technology | Purpose |
|-----------|---------|
| **MySQL** | Relational database with triggers, stored procedures, views |

### 6.4 Build & Deploy

| Technology | Purpose |
|-----------|---------|
| **Maven** | Build tool, dependency management, WAR packaging |
| **Apache Tomcat** | Servlet container for deployment |

### 6.5 Desktop

| Technology | Purpose |
|-----------|---------|
| **Java Swing** | Native desktop GUI for admin operations |

---

## 7. Module-wise Breakdown

### 7.1 Model Layer (`com.ngo.model`)

Plain Java classes (POJOs) with getters, setters, and computed fields. Each maps to a database table.

| Class | Table | Key Computed Methods |
|-------|-------|---------------------|
| `User.java` | users | — |
| `Donation.java` | donations | — |
| `Requirement.java` | requirements | `getFulfillmentPercent()` — returns % of quantity fulfilled |
| `InventoryItem.java` | inventory | `getStockStatus()` — returns "Critical" if qty < threshold/2, "Low" if < threshold, else "OK" |
| `DistributionLog.java` | distribution_log | — |
| `Campaign.java` | campaigns | `getProgressPercent()` — returns fundraising progress as integer % |
| `Organization.java` | organizations | — |
| `Transaction.java` | transactions | — |
| `Warehouse.java` | warehouses | — |

### 7.2 DAO Layer (`com.ngo.dao`)

Each DAO handles all SQL operations for its domain. All use `DBUtil.getConnection()` for pooled connections and follow try-with-resources JDBC pattern.

| Class | Key Methods | SQL Features Used |
|-------|-------------|-------------------|
| `UserDAO` | `login()`, `registerUser()`, `updateLastLogin()`, `getUserById()`, `updateProfile()`, `changePassword()`, `usernameExists()`, `emailExists()` | BCrypt verification, prepared statements |
| `DonationDAO` | `addDonation()`, `getDonationsByDonor()`, `getAllDonations()`, `countAll()`, `getTotalCashDonations()` | Generated keys, multi-table JOINs |
| `InventoryDAO` | `getAllInventory()`, `getLowStockItems()`, `updateStock()`, `addItem()`, `countLowStock()` | JOINs with warehouses |
| `RequirementDAO` | `getPendingRequirements()`, `getAllRequirements()`, `addRequirement()`, `countPending()` | `FIELD()` function for custom urgency ordering, JOINs with campaigns |
| `DistributionDAO` | `logDistribution()`, `getRecentDistributions()`, `getDistributionsByVolunteer()`, `getImpactReport()`, `countAll()` | Stored procedure call (`CALL GenerateImpactReport()`), multi-table JOINs |
| `CampaignDAO` | `getActiveCampaigns()`, `getAllCampaigns()`, `getById()`, `addCampaign()`, `countActive()` | JOINs with organizations |
| `DashboardDAO` | `getAdminStats()`, `getMonthlyDonationTrends()`, `getCampaignProgress()`, `getDonationsByType()`, `getUrgencyDistribution()` | Stored procedure call, view queries, GROUP BY aggregations |

### 7.3 Servlet Layer (`com.ngo.servlet`)

| Servlet | URL Mapping | Methods | What It Does |
|---------|-------------|---------|-------------|
| `LoginServlet` | `/login` | GET: show form, POST: authenticate | Validates credentials via BCrypt, creates session with 30-min timeout, writes audit log, redirects based on role |
| `RegisterServlet` | `/register` | GET: show form, POST: create account | Validates: empty fields, password ≥ 6 chars, passwords match, email regex, username/email uniqueness. Hashes password with BCrypt |
| `DonationServlet` | `/donate` | POST: record donation | Validates type, amount range (0-100M), item name for Kind type. Supports campaign linking and notes |
| `DistributeServlet` | `/distribute` | POST: log distribution | Validates requirement ID, quantity range (0-100K). Triggers auto inventory deduction |
| `RequirementServlet` | `/requirements` | POST: add requirement | Admin-only. Validates item name, quantity, location, urgency. Links to campaign |
| `ReceiptServlet` | `/generateReceipt` | GET: generate PDF | Creates branded A4 PDF receipt with iText: colored header, details table, tax notice (Section 80G), footer |
| `DashboardApiServlet` | `/api/dashboard/*` | GET: return JSON | REST API with 5 endpoints: `/stats`, `/trends`, `/campaigns`, `/donation-types`, `/urgency`. Returns JSON via Gson |
| `LogoutServlet` | `/logout` | GET: end session | Invalidates session, sets Cache-Control/Pragma headers to prevent back-button access |

### 7.4 Security Layer (`com.ngo.filter`)

**`AuthFilter.java`** — A `@WebFilter("/*")` that intercepts every single HTTP request.

```
Request arrives
     │
     ├── Is URL in public whitelist?
     │   (/login, /register, /css/*, /js/*, /images/*, /error/*)
     │   YES → Allow through
     │   NO  → Check session
     │          │
     │          ├── No session → Redirect to /login
     │          │
     │          └── Session exists
     │                 │
     │                 ├── Admin page + non-admin user → Redirect to role dashboard
     │                 │
     │                 └── Allowed → Add security headers → Pass through
     │
Security Headers Added:
  X-Content-Type-Options: nosniff
  X-Frame-Options: DENY
  X-XSS-Protection: 1; mode=block
  Cache-Control: no-cache, no-store, must-revalidate
```

### 7.5 Utility Layer (`com.ngo.util`)

| Class | Purpose | Details |
|-------|---------|---------|
| `DBUtil` | Database connection management | HikariCP pool: 15 max connections, 5 min idle, 30s connection timeout, statement caching (250), leak detection (60s) |
| `PasswordUtil` | Password security | `hashPassword(plain)` → BCrypt hash with 10 rounds. `checkPassword(plain, hash)` → verification |
| `MatchingAlgorithm` | AI distribution planning | See [Section 9](#9-ai-matching-algorithm) for full details |

---

## 8. Security Implementation

### 8.1 Password Security

```
Registration flow:
  User enters "admin123"
      │
      ▼
  PasswordUtil.hashPassword("admin123")
      │
      ▼
  BCrypt generates: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
      │ (10 rounds of salted hashing — different hash every time even for same password)
      ▼
  Stored in database `password_hash` column
      │
Login flow:
  User enters "admin123"
      │
      ▼
  PasswordUtil.checkPassword("admin123", stored_hash)
      │
      ▼
  BCrypt internally: re-hashes with same salt, compares
      │
      ▼
  Returns true/false (password NEVER compared as plain text)
```

### 8.2 Session Security

- **30-minute timeout** (configured in both `web.xml` and `LoginServlet`)
- **HttpOnly cookies** — JavaScript cannot access session cookies (prevents XSS session theft)
- **Session invalidation on logout** — Prevents session reuse
- **Anti-cache headers on logout** — Prevents back-button showing authenticated pages

### 8.3 Input Validation (Server-Side)

Every servlet validates all inputs before processing:

| Check | Where | Example |
|-------|-------|---------|
| Null/empty | All servlets | `if (username == null \|\| username.trim().isEmpty())` |
| Length limits | Registration | Password ≥ 6 characters |
| Email format | Registration | Regex: `^[A-Za-z0-9+_.-]+@(.+)$` |
| Uniqueness | Registration | `UserDAO.usernameExists()`, `emailExists()` |
| Numeric range | Donations | Amount: 0 < amount ≤ 100,000,000 |
| Type checking | Donations | Kind type requires non-empty itemName |
| Password match | Registration | password equals confirmPassword |

### 8.4 XSS Prevention

All user-provided data displayed in JSPs is escaped:

```jsp
<!-- UNSAFE (vulnerable to XSS): -->
${user.fullName}

<!-- SAFE (used throughout Donum): -->
${fn:escapeXml(user.fullName)}
```

This converts `<script>alert('hack')</script>` into harmless `&lt;script&gt;alert('hack')&lt;/script&gt;`.

### 8.5 Audit Logging

Every login attempt is recorded:

```sql
INSERT INTO audit_log (user_id, action, details, ip_address)
VALUES (?, 'LOGIN', 'User logged in successfully', ?)
```

---

## 9. AI Matching Algorithm

The `MatchingAlgorithm.java` is the most technically impressive feature. It solves the problem: **"Given pending requirements and available inventory, what's the optimal distribution plan?"**

### 9.1 How It Works

```
INPUT:
  - List of pending requirements (what's needed, where, how urgently)
  - List of inventory items (what's available, where stored)

PROCESS:
  For each requirement:
    1. Find all inventory items matching the item name
    2. Aggregate available quantity across all warehouses
    3. Calculate a PRIORITY SCORE:

       Score = Urgency Points + Gap Ratio Points

       Urgency Points:
         Critical = 40 points
         High     = 30 points
         Medium   = 15 points
         Low      =  5 points

       Gap Ratio Points (0-30):
         = 30 × (quantity_needed - quantity_fulfilled) / quantity_needed
         (Higher gap = higher priority)

    4. Determine match type:
         FULL    → Available ≥ Needed (can fully satisfy)
         PARTIAL → Available > 0 but < Needed
         NONE    → Nothing available

    5. Calculate allocation:
         allocate = min(available, needed - already_fulfilled)

OUTPUT:
  Sorted list (highest score first) with columns:
  | Location | Item | Needed | Available | Allocate | Match | Score |
```

### 9.2 Example Output

| Location | Item | Needed | Available | Allocate | Match | Score |
|----------|------|--------|-----------|----------|-------|-------|
| Wayanad District | Rice | 500 | 800 | 500 | FULL | 67 |
| Chennai South | Blankets | 200 | 50 | 50 | PARTIAL | 58 |
| Kochi Relief Camp | Medicines | 100 | 100 | 100 | FULL | 45 |
| Mysuru Area | Tents | 30 | 0 | 0 | NONE | 35 |

---

## 10. User Interface

### 10.1 Design System

- **Theme:** Dark glassmorphism with translucent cards
- **Primary Color:** `#00d2ff` (cyan)
- **Secondary Color:** `#7c3aed` (purple)
- **Background:** `#0f172a` (dark navy)
- **Card Background:** `rgba(30, 41, 59, 0.8)` (translucent dark)
- **Font:** Inter (Google Fonts)
- **Border Radius:** 16px (cards), 10px (inputs/buttons)

### 10.2 Pages

| Page | URL | Description |
|------|-----|-------------|
| Login | `/login` | Centered auth card, demo credentials, success/error alerts |
| Register | `/register` | Registration form with role selector (Donor/Volunteer) |
| Admin Dashboard | `/admin-dashboard.jsp` | 5 stat cards, 4 charts, AI plan table, requirements, inventory, add-requirement form, recent distributions |
| Donor Dashboard | `/donor-dashboard.jsp` | Donation form with Cash/Kind toggle, campaign picker, history table with PDF receipts, campaign cards |
| Volunteer Dashboard | `/volunteer-dashboard.jsp` | Distribution form with notes, pending requirements, personal history |
| Campaigns | `/campaigns.jsp` | Campaign cards with progress bars, org names, status badges |
| 404 Error | `/error/404.jsp` | Branded "not found" page |
| 500 Error | `/error/500.jsp` | Branded "server error" page |

### 10.3 Responsive Design

The CSS includes responsive breakpoints at 768px (mobile):
- Navigation collapses to hamburger menu
- Grid layouts switch to single column
- Stats grid goes to 2-column
- Campaign cards stack vertically

---

## 11. Project Structure

```
donum/
│
├── pom.xml                              # Maven build config, all dependencies
├── README.md                            # This document
├── ER_Diagram.md                        # Entity-relationship diagram
│
├── db/
│   ├── schema.sql                       # 10 tables, triggers, procedures, views
│   ├── seed_data.sql                    # Demo data (4 orgs, 7 users, campaigns...)
│   └── advanced_queries.sql             # 7 complex queries (CTEs, window functions)
│
└── src/main/
    ├── java/com/ngo/
    │   ├── model/                       # 9 POJOs (data classes)
    │   │   ├── User.java
    │   │   ├── Donation.java
    │   │   ├── Requirement.java
    │   │   ├── InventoryItem.java
    │   │   ├── DistributionLog.java
    │   │   ├── Campaign.java
    │   │   ├── Organization.java
    │   │   ├── Transaction.java
    │   │   └── Warehouse.java
    │   │
    │   ├── dao/                          # 7 DAOs (database operations)
    │   │   ├── UserDAO.java
    │   │   ├── DonationDAO.java
    │   │   ├── InventoryDAO.java
    │   │   ├── RequirementDAO.java
    │   │   ├── DistributionDAO.java
    │   │   ├── CampaignDAO.java
    │   │   └── DashboardDAO.java
    │   │
    │   ├── servlet/                      # 8 servlets (HTTP handlers)
    │   │   ├── LoginServlet.java
    │   │   ├── RegisterServlet.java
    │   │   ├── DonationServlet.java
    │   │   ├── DistributeServlet.java
    │   │   ├── RequirementServlet.java
    │   │   ├── ReceiptServlet.java
    │   │   ├── DashboardApiServlet.java
    │   │   └── LogoutServlet.java
    │   │
    │   ├── filter/
    │   │   └── AuthFilter.java           # Security filter (auth + headers)
    │   │
    │   ├── util/
    │   │   ├── DBUtil.java               # HikariCP connection pool
    │   │   ├── PasswordUtil.java         # BCrypt hashing
    │   │   └── MatchingAlgorithm.java    # AI distribution planner
    │   │
    │   └── swing/                        # Desktop app
    │       ├── MainApp.java              # Login window
    │       └── AdminDashboard.java       # Admin panel (inventory + requirements)
    │
    └── webapp/
        ├── login.jsp                     # Login page
        ├── register.jsp                  # Registration page
        ├── admin-dashboard.jsp           # Admin dashboard
        ├── donor-dashboard.jsp           # Donor dashboard
        ├── volunteer-dashboard.jsp       # Volunteer dashboard
        ├── campaigns.jsp                 # Campaign listing
        │
        ├── css/
        │   └── style.css                 # 600+ line dark theme
        │
        ├── js/
        │   └── app.js                    # Chart.js + interactivity
        │
        ├── error/
        │   ├── 404.jsp                   # Not found page
        │   └── 500.jsp                   # Server error page
        │
        └── WEB-INF/
            ├── web.xml                   # Servlet config, session, error pages
            ├── header.jspf               # Shared nav bar template
            └── footer.jspf               # Shared footer template
```

---

## 12. Installation & Setup

### Prerequisites

- Java 11 or higher (JDK)
- Apache Maven 3.6+
- MySQL 8.0+
- Apache Tomcat 9.x or 10.x

### Step-by-Step

```bash
# 1. Clone/download the project
cd college-major-project-4sem

# 2. Create MySQL database
mysql -u root -p
> CREATE DATABASE ngo_db;
> USE ngo_db;
> SOURCE db/schema.sql;
> SOURCE db/seed_data.sql;
> EXIT;

# 3. Configure database credentials
#    Edit: src/main/java/com/ngo/util/DBUtil.java
#    Update JDBC_URL, USERNAME, PASSWORD to match your MySQL setup

# 4. Build with Maven
mvn clean package

# 5. Deploy to Tomcat
#    Copy target/ngo-donation-system-2.0.war to TOMCAT_HOME/webapps/

# 6. Start Tomcat
#    Windows: TOMCAT_HOME/bin/startup.bat
#    Linux:   TOMCAT_HOME/bin/startup.sh

# 7. Open browser
#    http://localhost:8080/ngo-donation-system-2.0/
```

---

## 13. Demo Credentials

| Role | Username | Password | What You Can Do |
|------|----------|----------|-----------------|
| Admin | `admin` | `admin123` | Full access — stats, charts, AI plan, inventory, requirements |
| Donor | `donor1` | `donor123` | Donate cash/goods, view history, download receipts |
| Volunteer | `vol1` | `vol123` | Log distributions, view requirements |

---

## 14. Advanced SQL Features

The project demonstrates these advanced SQL concepts (important for viva/evaluation):

### 14.1 Common Table Expressions (CTEs)
```sql
WITH donor_stats AS (
    SELECT donor_id, COUNT(*) as donation_count,
           SUM(CASE WHEN type='Cash' THEN amount_or_quantity ELSE 0 END) as total_cash
    FROM donations GROUP BY donor_id
)
SELECT u.full_name, ds.donation_count, ds.total_cash,
       RANK() OVER (ORDER BY ds.total_cash DESC) as leaderboard_rank
FROM donor_stats ds JOIN users u ON ds.donor_id = u.user_id;
```

### 14.2 Window Functions
```sql
RANK() OVER (ORDER BY total_cash DESC) as leaderboard_rank
```

### 14.3 Correlated Subqueries
```sql
SELECT *, (SELECT COUNT(*) FROM distribution_log dl
           WHERE dl.volunteer_id = u.user_id) as total_distributions
FROM users u WHERE u.role = 'Volunteer';
```

### 14.4 CASE Expressions
```sql
CASE
    WHEN i.quantity < i.min_threshold * 0.5 THEN 'CRITICAL'
    WHEN i.quantity < i.min_threshold THEN 'LOW'
    ELSE 'ADEQUATE'
END as risk_level
```

### 14.5 Multi-table JOINs
```sql
SELECT d.*, u.full_name, c.name as campaign_name
FROM donations d
JOIN users u ON d.donor_id = u.user_id
LEFT JOIN campaigns c ON d.campaign_id = c.campaign_id;
```

### 14.6 Aggregate Functions with GROUP BY
```sql
SELECT DATE_FORMAT(donation_date, '%Y-%m') as month,
       SUM(CASE WHEN type='Cash' THEN amount_or_quantity ELSE 0 END) as total_cash,
       SUM(CASE WHEN type='Kind' THEN amount_or_quantity ELSE 0 END) as total_kind
FROM donations GROUP BY month ORDER BY month;
```

### 14.7 FIELD() for Custom Ordering
```sql
ORDER BY FIELD(urgency, 'Critical', 'High', 'Medium', 'Low')
```

---

## 15. Desktop Application

A Java Swing desktop app provides admin access for environments where a browser isn't available.

### 15.1 MainApp (Login)
- Dark-themed login window matching the web app's color scheme
- Connects to the same MySQL database
- Only allows Admin role users to proceed

### 15.2 AdminDashboard 
- **Tabbed interface** with two tabs:
  - **Inventory Tab** — Table showing: ID, Item, Category, Qty, Unit, Warehouse, Status, Updated
  - **Requirements Tab** — Table showing: ID, Item, Location, Needed, Fulfilled, Urgency, Campaign
- **Refresh** button to reload data
- **Logout** button returns to login

---

## 16. Future Scope

Potential enhancements for scaling Donum to production:

| Area | Enhancement |
|------|-------------|
| **Authentication** | OAuth 2.0 (Google/GitHub login), JWT tokens |
| **Notifications** | Email/SMS alerts for low stock, new donations, distribution confirmations |
| **Maps** | Google Maps API integration for requirement locations and warehouse visualization |
| **Mobile App** | React Native or Flutter mobile app for field volunteers |
| **Payment Gateway** | Razorpay/Stripe integration for real online cash donations |
| **File Uploads** | Photo uploads for distribution proof, donor receipts |
| **Multi-language** | i18n support (Hindi, Tamil, Telugu, etc.) |
| **Reporting** | Exportable Excel/PDF reports with JasperReports |
| **Real-time** | WebSocket notifications for live updates |
| **Cloud Deploy** | Docker containerization, AWS/GCP deployment |
| **Testing** | JUnit tests for DAOs and servlets, Selenium for UI testing |
| **API** | Full RESTful API for third-party integrations |

---

## License

This is a college project created for educational purposes.

---

*Built with ❤️ for the 4th Semester Major Project*
