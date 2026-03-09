# NGO Relief System - Enterprise Entity Relationship (ER) Diagram

To make this system usable by multiple real-world NGOs globally, we use a "Multi-Tenant" architecture. Here is the expanded blueprint:

```mermaid
erDiagram
    ORGANIZATIONS ||--o{ USERS : "employs / registers"
    ORGANIZATIONS ||--o{ CAMPAIGNS : "runs"
    ORGANIZATIONS ||--o{ WAREHOUSES : "owns"
    
    USERS ||--o{ DONATIONS : "makes"
    USERS ||--o{ DISTRIBUTION_LOG : "logs (as volunteer)"
    
    CAMPAIGNS ||--o{ DONATIONS : "receives"
    CAMPAIGNS ||--o{ REQUIREMENTS : "addresses"
    
    DONATIONS ||--o| TRANSACTIONS : "processed via"
    DONATIONS ||--o| DISTRIBUTION_LOG : "is allocated via"
    
    WAREHOUSES ||--o{ INVENTORY : "stores"
    
    REQUIREMENTS ||--o{ DISTRIBUTION_LOG : "receives"

    ORGANIZATIONS {
        int org_id PK
        string org_name
        string type "NGO, Corporate Sponsor"
        string country
    }
    
    CAMPAIGNS {
        int campaign_id PK
        int org_id FK
        string name
        decimal target_amount
        date end_date
    }

    USERS {
        int user_id PK
        int org_id FK "Null for individual donors"
        string username
        string password_hash "Encrypted for real-world security"
        string email
        string role "Donor, Admin, Volunteer"
    }
    
    TRANSACTIONS {
        string transaction_id PK "From Stripe/PayPal"
        int donation_id FK
        string payment_method
        string status "Success, Failed, Pending"
    }

    DONATIONS {
        int donation_id PK
        int donor_id FK
        int campaign_id FK "Optional"
        string type "Cash or Kind"
        decimal amount
        string status
    }
    
    REQUIREMENTS {
        int requirement_id PK
        int campaign_id FK
        string global_location "GPS Coordinates or City/Country"
        string item_name
        decimal quantity_needed
        string urgency
    }
    
    WAREHOUSES {
        int warehouse_id PK
        int org_id FK
        string location
        decimal capacity
    }
    
    INVENTORY {
        int item_id PK
        int warehouse_id FK
        string item_name
        decimal quantity
    }
    
    DISTRIBUTION_LOG {
        int log_id PK
        int donation_id FK
        int requirement_id FK
        int volunteer_id FK
        decimal quantity_distributed
        timestamp distributed_at
    }
```

### What makes this "Real-World" vs "College Project":
1. **Multi-Tenancy (`ORGANIZATIONS`):** The single biggest difference. A real system isn't just for *one* NGO. It allows hundreds of NGOs (like Red Cross, UNICEF) and Corporate Sponsors (like Google or Microsoft CSR divisions) to sign up, manage their own projects, and collaborate.
2. **Payment Gateways (`TRANSACTIONS`):** Real systems integrate with Stripe or PayPal. We don't just record a donation; we record the exact bank transaction ID for financial auditing.
3. **Logistics (`WAREHOUSES`):** Global NGOs don't have one single inventory pool. They have physical warehouses in different countries.
4. **Targeted Relief (`CAMPAIGNS`):** People rarely donate to a general fund anymore. They donate to specific causes (e.g., "Turkey Earthquake Relief 2026").
5. **Security:** Passwords are hashed, and financial records are tamper-proofed.
