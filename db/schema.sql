-- =====================================================================
-- Donum NGO Relief & Distribution Platform - Enterprise Schema v2.0
-- Multi-Tenant Architecture | Triggers | Stored Procedures | Indexes
-- =====================================================================

DROP DATABASE IF EXISTS ngo_db;
CREATE DATABASE ngo_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ngo_db;

-- =========================
-- 1. ORGANIZATIONS (Multi-Tenant Root)
-- =========================
CREATE TABLE organizations (
    org_id       INT AUTO_INCREMENT PRIMARY KEY,
    org_name     VARCHAR(150) NOT NULL,
    type         ENUM('NGO', 'Corporate Sponsor', 'Government') NOT NULL,
    country      VARCHAR(100),
    address      TEXT,
    phone        VARCHAR(20),
    email        VARCHAR(100),
    website      VARCHAR(255),
    logo_url     VARCHAR(255),
    is_active    BOOLEAN DEFAULT TRUE,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_org_type (type),
    INDEX idx_org_country (country)
) ENGINE=InnoDB;

-- =========================
-- 2. USERS (With BCrypt Hashing & Org Link)
-- =========================
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    org_id        INT,
    username      VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email         VARCHAR(100) UNIQUE NOT NULL,
    role          ENUM('Donor', 'Admin', 'Volunteer') NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    phone         VARCHAR(20),
    address       TEXT,
    avatar_url    VARCHAR(255),
    is_active     BOOLEAN DEFAULT TRUE,
    last_login    TIMESTAMP NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (org_id) REFERENCES organizations(org_id) ON DELETE SET NULL,
    INDEX idx_user_role (role),
    INDEX idx_user_org (org_id)
) ENGINE=InnoDB;

-- =========================
-- 3. CAMPAIGNS (Targeted Relief Drives)
-- =========================
CREATE TABLE campaigns (
    campaign_id    INT AUTO_INCREMENT PRIMARY KEY,
    org_id         INT NOT NULL,
    name           VARCHAR(200) NOT NULL,
    description    TEXT,
    target_amount  DECIMAL(15,2) DEFAULT 0,
    raised_amount  DECIMAL(15,2) DEFAULT 0,
    start_date     DATE NOT NULL,
    end_date       DATE NOT NULL,
    status         ENUM('Planning', 'Active', 'Completed', 'Cancelled') DEFAULT 'Planning',
    image_url      VARCHAR(255),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (org_id) REFERENCES organizations(org_id) ON DELETE CASCADE,
    INDEX idx_campaign_status (status),
    INDEX idx_campaign_dates (start_date, end_date)
) ENGINE=InnoDB;

-- =========================
-- 4. WAREHOUSES (Logistics & Storage)
-- =========================
CREATE TABLE warehouses (
    warehouse_id  INT AUTO_INCREMENT PRIMARY KEY,
    org_id        INT NOT NULL,
    name          VARCHAR(150) NOT NULL,
    location      VARCHAR(255) NOT NULL,
    capacity      DECIMAL(15,2) DEFAULT 0,
    manager_name  VARCHAR(100),
    phone         VARCHAR(20),
    is_active     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (org_id) REFERENCES organizations(org_id) ON DELETE CASCADE,
    INDEX idx_warehouse_org (org_id)
) ENGINE=InnoDB;

-- =========================
-- 5. INVENTORY (Warehouse-linked Stock)
-- =========================
CREATE TABLE inventory (
    item_id       INT AUTO_INCREMENT PRIMARY KEY,
    warehouse_id  INT,
    item_name     VARCHAR(100) NOT NULL,
    category      VARCHAR(50),
    quantity       DECIMAL(10,2) DEFAULT 0,
    unit          VARCHAR(20),
    min_threshold DECIMAL(10,2) DEFAULT 10,
    last_updated  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id) ON DELETE SET NULL,
    UNIQUE KEY uq_item_warehouse (item_name, warehouse_id),
    INDEX idx_inv_category (category)
) ENGINE=InnoDB;

-- =========================
-- 6. DONATIONS (Cash & In-Kind with Campaign link)
-- =========================
CREATE TABLE donations (
    donation_id        INT AUTO_INCREMENT PRIMARY KEY,
    donor_id           INT NOT NULL,
    campaign_id        INT,
    type               ENUM('Cash', 'Kind') NOT NULL,
    item_name          VARCHAR(100),
    amount_or_quantity DECIMAL(15,2) NOT NULL,
    status             ENUM('Received', 'Allocated', 'Delivered') DEFAULT 'Received',
    notes              TEXT,
    donation_date      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donor_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (campaign_id) REFERENCES campaigns(campaign_id) ON DELETE SET NULL,
    INDEX idx_donation_donor (donor_id),
    INDEX idx_donation_campaign (campaign_id),
    INDEX idx_donation_date (donation_date)
) ENGINE=InnoDB;

-- =========================
-- 7. TRANSACTIONS (Payment Gateway Audit Trail)
-- =========================
CREATE TABLE transactions (
    transaction_id   VARCHAR(100) PRIMARY KEY,
    donation_id      INT NOT NULL,
    payment_method   ENUM('Stripe', 'PayPal', 'Bank Transfer', 'Cash', 'UPI', 'Other') NOT NULL,
    amount           DECIMAL(15,2) NOT NULL,
    currency         VARCHAR(3) DEFAULT 'INR',
    status           ENUM('Success', 'Failed', 'Pending', 'Refunded') DEFAULT 'Pending',
    gateway_response TEXT,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donation_id) REFERENCES donations(donation_id) ON DELETE CASCADE,
    INDEX idx_txn_status (status)
) ENGINE=InnoDB;

-- =========================
-- 8. REQUIREMENTS (Field Needs with Campaign link)
-- =========================
CREATE TABLE requirements (
    requirement_id      INT AUTO_INCREMENT PRIMARY KEY,
    campaign_id         INT,
    location            VARCHAR(255) NOT NULL,
    item_name           VARCHAR(100) NOT NULL,
    quantity_needed     DECIMAL(10,2) NOT NULL,
    quantity_fulfilled  DECIMAL(10,2) DEFAULT 0,
    urgency             ENUM('Low', 'Medium', 'High', 'Critical') DEFAULT 'Medium',
    status              ENUM('Pending', 'Partially Met', 'Fulfilled') DEFAULT 'Pending',
    description         TEXT,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (campaign_id) REFERENCES campaigns(campaign_id) ON DELETE SET NULL,
    INDEX idx_req_urgency (urgency),
    INDEX idx_req_status (status),
    INDEX idx_req_campaign (campaign_id)
) ENGINE=InnoDB;

-- =========================
-- 9. DISTRIBUTION LOG (Last-Mile Delivery Records)
-- =========================
CREATE TABLE distribution_log (
    log_id                INT AUTO_INCREMENT PRIMARY KEY,
    donation_id           INT,
    requirement_id        INT NOT NULL,
    volunteer_id          INT NOT NULL,
    quantity_distributed  DECIMAL(10,2) NOT NULL,
    notes                 TEXT,
    distributed_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (donation_id) REFERENCES donations(donation_id) ON DELETE SET NULL,
    FOREIGN KEY (requirement_id) REFERENCES requirements(requirement_id) ON DELETE CASCADE,
    FOREIGN KEY (volunteer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_dist_volunteer (volunteer_id),
    INDEX idx_dist_date (distributed_at)
) ENGINE=InnoDB;

-- =========================
-- 10. AUDIT LOG (Accountability & Compliance)
-- =========================
CREATE TABLE audit_log (
    audit_id    INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT,
    action      VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id   INT,
    details     TEXT,
    ip_address  VARCHAR(45),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_action (action),
    INDEX idx_audit_date (created_at)
) ENGINE=InnoDB;


-- =====================================================================
-- TRIGGERS
-- =====================================================================

-- Trigger 1: Auto-add In-Kind donations to inventory
DELIMITER //
CREATE TRIGGER trg_after_donation_insert
AFTER INSERT ON donations
FOR EACH ROW
BEGIN
    IF NEW.type = 'Kind' AND NEW.item_name IS NOT NULL THEN
        INSERT INTO inventory (item_name, quantity, unit, category)
        VALUES (NEW.item_name, NEW.amount_or_quantity, 'units', 'Donated Goods')
        ON DUPLICATE KEY UPDATE quantity = quantity + NEW.amount_or_quantity;
    END IF;

    -- If donation is linked to a campaign, update raised_amount for Cash donations
    IF NEW.campaign_id IS NOT NULL AND NEW.type = 'Cash' THEN
        UPDATE campaigns
        SET raised_amount = raised_amount + NEW.amount_or_quantity
        WHERE campaign_id = NEW.campaign_id;
    END IF;
END;
//
DELIMITER ;

-- Trigger 2: Auto-reduce inventory and update requirement on distribution
DELIMITER //
CREATE TRIGGER trg_after_distribution_insert
AFTER INSERT ON distribution_log
FOR EACH ROW
BEGIN
    DECLARE v_item_name VARCHAR(100);

    -- Get the item name from the requirement
    SELECT item_name INTO v_item_name
    FROM requirements WHERE requirement_id = NEW.requirement_id;

    -- Reduce inventory
    UPDATE inventory
    SET quantity = GREATEST(quantity - NEW.quantity_distributed, 0)
    WHERE item_name = v_item_name;

    -- Update requirement fulfillment
    UPDATE requirements
    SET quantity_fulfilled = quantity_fulfilled + NEW.quantity_distributed,
        status = CASE
            WHEN (quantity_fulfilled + NEW.quantity_distributed) >= quantity_needed THEN 'Fulfilled'
            WHEN (quantity_fulfilled + NEW.quantity_distributed) > 0 THEN 'Partially Met'
            ELSE status
        END
    WHERE requirement_id = NEW.requirement_id;
END;
//
DELIMITER ;

-- Trigger 3: Auto-complete campaign when target is met
DELIMITER //
CREATE TRIGGER trg_after_campaign_update
BEFORE UPDATE ON campaigns
FOR EACH ROW
BEGIN
    IF NEW.raised_amount >= NEW.target_amount AND NEW.target_amount > 0 AND OLD.status = 'Active' THEN
        SET NEW.status = 'Completed';
    END IF;
END;
//
DELIMITER ;


-- =====================================================================
-- STORED PROCEDURES
-- =====================================================================

-- Procedure 1: Generate Donor Impact Report
DELIMITER //
CREATE PROCEDURE GenerateImpactReport(IN p_donor_id INT)
BEGIN
    SELECT
        u.full_name,
        COUNT(d.donation_id) AS total_donations,
        SUM(CASE WHEN d.type = 'Cash' THEN d.amount_or_quantity ELSE 0 END) AS total_cash,
        SUM(CASE WHEN d.type = 'Kind' THEN d.amount_or_quantity ELSE 0 END) AS total_goods,
        GROUP_CONCAT(DISTINCT d.item_name ORDER BY d.item_name SEPARATOR ', ') AS items_donated,
        COUNT(DISTINCT dl.log_id) AS successful_distributions,
        COALESCE(SUM(dl.quantity_distributed), 0) AS total_quantity_delivered
    FROM users u
    LEFT JOIN donations d ON u.user_id = d.donor_id
    LEFT JOIN distribution_log dl ON d.donation_id = dl.donation_id
    WHERE u.user_id = p_donor_id
    GROUP BY u.user_id, u.full_name;
END;
//
DELIMITER ;

-- Procedure 2: Inventory Risk Assessment
DELIMITER //
CREATE PROCEDURE InventoryRiskAssessment()
BEGIN
    SELECT
        i.item_name,
        i.quantity AS current_stock,
        i.min_threshold,
        COALESCE(pr.total_pending, 0) AS total_pending_demand,
        CASE
            WHEN i.quantity <= 0 THEN 'OUT OF STOCK'
            WHEN i.quantity < i.min_threshold THEN 'CRITICAL'
            WHEN i.quantity < COALESCE(pr.total_pending, 0) THEN 'LOW STOCK'
            ELSE 'ADEQUATE'
        END AS risk_level
    FROM inventory i
    LEFT JOIN (
        SELECT item_name, SUM(quantity_needed - quantity_fulfilled) AS total_pending
        FROM requirements
        WHERE status != 'Fulfilled'
        GROUP BY item_name
    ) pr ON i.item_name = pr.item_name
    ORDER BY
        CASE
            WHEN i.quantity <= 0 THEN 1
            WHEN i.quantity < i.min_threshold THEN 2
            WHEN i.quantity < COALESCE(pr.total_pending, 0) THEN 3
            ELSE 4
        END;
END;
//
DELIMITER ;

-- Procedure 3: Dashboard Statistics
DELIMITER //
CREATE PROCEDURE GetDashboardStats()
BEGIN
    SELECT
        (SELECT COUNT(*) FROM users WHERE is_active = TRUE) AS total_users,
        (SELECT COUNT(*) FROM users WHERE role = 'Donor' AND is_active = TRUE) AS total_donors,
        (SELECT COUNT(*) FROM users WHERE role = 'Volunteer' AND is_active = TRUE) AS total_volunteers,
        (SELECT COUNT(*) FROM donations) AS total_donations,
        (SELECT COALESCE(SUM(amount_or_quantity), 0) FROM donations WHERE type = 'Cash') AS total_cash_raised,
        (SELECT COUNT(*) FROM campaigns WHERE status = 'Active') AS active_campaigns,
        (SELECT COUNT(*) FROM requirements WHERE status != 'Fulfilled') AS pending_requirements,
        (SELECT COUNT(*) FROM distribution_log) AS total_distributions,
        (SELECT COUNT(*) FROM inventory WHERE quantity < min_threshold) AS low_stock_items,
        (SELECT COUNT(*) FROM organizations WHERE is_active = TRUE) AS total_organizations;
END;
//
DELIMITER ;


-- =====================================================================
-- VIEWS (For Reporting & Analytics)
-- =====================================================================

-- View: Monthly donation trends
CREATE VIEW v_monthly_donations AS
SELECT
    DATE_FORMAT(donation_date, '%Y-%m') AS month,
    COUNT(*) AS donation_count,
    SUM(CASE WHEN type = 'Cash' THEN amount_or_quantity ELSE 0 END) AS cash_total,
    SUM(CASE WHEN type = 'Kind' THEN amount_or_quantity ELSE 0 END) AS goods_total
FROM donations
GROUP BY DATE_FORMAT(donation_date, '%Y-%m')
ORDER BY month DESC;

-- View: Campaign progress overview
CREATE VIEW v_campaign_progress AS
SELECT
    c.campaign_id, c.name, o.org_name,
    c.target_amount, c.raised_amount,
    ROUND((c.raised_amount / NULLIF(c.target_amount, 0)) * 100, 1) AS progress_pct,
    c.status, c.start_date, c.end_date,
    COUNT(DISTINCT d.donation_id) AS donation_count,
    COUNT(DISTINCT r.requirement_id) AS requirement_count
FROM campaigns c
JOIN organizations o ON c.org_id = o.org_id
LEFT JOIN donations d ON c.campaign_id = d.campaign_id
LEFT JOIN requirements r ON c.campaign_id = r.campaign_id
GROUP BY c.campaign_id;
        GROUP_CONCAT(DISTINCT d.item_name SEPARATOR ', ') as items_donated,
        SUM(CASE WHEN d.status = 'Delivered' THEN 1 ELSE 0 END) as successful_impacts
    FROM users u
    LEFT JOIN donations d ON u.user_id = d.donor_id
    WHERE u.user_id = p_donor_id
    GROUP BY u.user_id;
END;
//
DELIMITER ;

-- 9. View: Live Needs (Gap Analysis)
CREATE OR REPLACE VIEW live_needs AS
SELECT 
    r.location,
    r.item_name,
    r.quantity_needed,
    COALESCE(i.quantity, 0) as stock_available,
    (r.quantity_needed - COALESCE(i.quantity, 0)) as gap,
    r.urgency
FROM requirements r
LEFT JOIN inventory i ON r.item_name = i.item_name
WHERE r.status != 'Fulfilled';

-- Sample Data Initial Users
INSERT INTO users (username, password, email, role, full_name) VALUES 
('admin', 'admin123', 'admin@ngo.org', 'Admin', 'NGO Administrator'),
('donor1', 'donor123', 'donor@example.com', 'Donor', 'John Doe'),
('vol1', 'vol123', 'volunteer@ngo.org', 'Volunteer', 'Sam Smith');
