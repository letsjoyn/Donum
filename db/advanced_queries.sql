-- =====================================================================
-- ADVANCED SQL QUERIES - Donum Platform v2.0
-- Complex Joins | Subqueries | Window Functions | CTEs | Analytics
-- =====================================================================
USE ngo_db;

-- ---------------------------------------------------------------
-- 1. CTE + Window Function: Donor Leaderboard with Running Totals
-- ---------------------------------------------------------------
WITH DonorStats AS (
    SELECT
        u.user_id,
        u.full_name,
        u.email,
        COUNT(d.donation_id) AS donation_count,
        SUM(d.amount_or_quantity) AS total_contributed,
        MAX(d.donation_date) AS last_donation,
        RANK() OVER (ORDER BY SUM(d.amount_or_quantity) DESC) AS donor_rank
    FROM users u
    JOIN donations d ON u.user_id = d.donor_id
    WHERE u.role = 'Donor'
    GROUP BY u.user_id, u.full_name, u.email
)
SELECT * FROM DonorStats
ORDER BY donor_rank;

-- ---------------------------------------------------------------
-- 2. Complex Join + Aggregation: Campaign Impact Analysis
-- ---------------------------------------------------------------
SELECT
    c.name AS campaign_name,
    o.org_name AS organization,
    c.target_amount,
    c.raised_amount,
    ROUND((c.raised_amount / NULLIF(c.target_amount, 0)) * 100, 1) AS funding_pct,
    COUNT(DISTINCT d.donation_id) AS total_donations,
    COUNT(DISTINCT d.donor_id) AS unique_donors,
    COUNT(DISTINCT r.requirement_id) AS requirements_addressed,
    SUM(CASE WHEN r.status = 'Fulfilled' THEN 1 ELSE 0 END) AS requirements_fulfilled,
    COALESCE(SUM(dl.quantity_distributed), 0) AS total_goods_distributed
FROM campaigns c
JOIN organizations o ON c.org_id = o.org_id
LEFT JOIN donations d ON c.campaign_id = d.campaign_id
LEFT JOIN requirements r ON c.campaign_id = r.campaign_id
LEFT JOIN distribution_log dl ON r.requirement_id = dl.requirement_id
GROUP BY c.campaign_id, c.name, o.org_name, c.target_amount, c.raised_amount
ORDER BY funding_pct DESC;

-- ---------------------------------------------------------------
-- 3. Correlated Subquery: Inventory Depletion Risk Assessment
-- ---------------------------------------------------------------
SELECT
    i.item_name,
    w.name AS warehouse,
    w.location,
    i.quantity AS current_stock,
    i.min_threshold,
    (
        SELECT COALESCE(SUM(r.quantity_needed - r.quantity_fulfilled), 0)
        FROM requirements r
        WHERE r.item_name = i.item_name AND r.status != 'Fulfilled'
    ) AS pending_demand,
    CASE
        WHEN i.quantity <= 0 THEN 'OUT OF STOCK'
        WHEN i.quantity < i.min_threshold THEN 'CRITICAL'
        WHEN i.quantity < (
            SELECT COALESCE(SUM(r.quantity_needed - r.quantity_fulfilled), 0)
            FROM requirements r
            WHERE r.item_name = i.item_name AND r.status != 'Fulfilled'
        ) THEN 'INSUFFICIENT'
        ELSE 'ADEQUATE'
    END AS risk_status
FROM inventory i
JOIN warehouses w ON i.warehouse_id = w.warehouse_id
ORDER BY
    FIELD(risk_status, 'OUT OF STOCK', 'CRITICAL', 'INSUFFICIENT', 'ADEQUATE'),
    i.quantity ASC;

-- ---------------------------------------------------------------
-- 4. Subquery + Date Functions: Monthly Donation Trends (Last 6 Months)
-- ---------------------------------------------------------------
SELECT
    DATE_FORMAT(months.month_start, '%b %Y') AS month,
    COALESCE(cash.total, 0) AS cash_donations,
    COALESCE(kind.total, 0) AS kind_donations,
    COALESCE(cash.total, 0) + COALESCE(kind.total, 0) AS grand_total,
    COALESCE(cash.cnt, 0) + COALESCE(kind.cnt, 0) AS number_of_donations
FROM (
    SELECT DATE_SUB(CURRENT_DATE(), INTERVAL n MONTH) AS month_start
    FROM (SELECT 0 n UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) nums
) months
LEFT JOIN (
    SELECT DATE_FORMAT(donation_date, '%Y-%m-01') AS md, SUM(amount_or_quantity) AS total, COUNT(*) AS cnt
    FROM donations WHERE type = 'Cash'
    GROUP BY md
) cash ON DATE_FORMAT(months.month_start, '%Y-%m-01') = cash.md
LEFT JOIN (
    SELECT DATE_FORMAT(donation_date, '%Y-%m-01') AS md, SUM(amount_or_quantity) AS total, COUNT(*) AS cnt
    FROM donations WHERE type = 'Kind'
    GROUP BY md
) kind ON DATE_FORMAT(months.month_start, '%Y-%m-01') = kind.md
ORDER BY months.month_start;

-- ---------------------------------------------------------------
-- 5. Multi-Table Join: Volunteer Performance Dashboard
-- ---------------------------------------------------------------
SELECT
    u.full_name AS volunteer_name,
    o.org_name AS organization,
    COUNT(dl.log_id) AS total_deliveries,
    SUM(dl.quantity_distributed) AS total_quantity_delivered,
    COUNT(DISTINCT dl.requirement_id) AS requirements_served,
    COUNT(DISTINCT DATE(dl.distributed_at)) AS active_days,
    MIN(dl.distributed_at) AS first_delivery,
    MAX(dl.distributed_at) AS last_delivery,
    ROUND(SUM(dl.quantity_distributed) / NULLIF(COUNT(dl.log_id), 0), 2) AS avg_per_delivery
FROM users u
LEFT JOIN organizations o ON u.org_id = o.org_id
LEFT JOIN distribution_log dl ON u.user_id = dl.volunteer_id
WHERE u.role = 'Volunteer'
GROUP BY u.user_id, u.full_name, o.org_name
ORDER BY total_quantity_delivered DESC;

-- ---------------------------------------------------------------
-- 6. Analytical: Organization-wise Contribution Summary
-- ---------------------------------------------------------------
SELECT
    COALESCE(o.org_name, 'Individual Donors') AS source,
    o.type AS org_type,
    COUNT(DISTINCT u.user_id) AS member_count,
    COUNT(d.donation_id) AS total_donations,
    SUM(CASE WHEN d.type = 'Cash' THEN d.amount_or_quantity ELSE 0 END) AS cash_contributed,
    SUM(CASE WHEN d.type = 'Kind' THEN d.amount_or_quantity ELSE 0 END) AS goods_contributed,
    COUNT(DISTINCT d.campaign_id) AS campaigns_supported
FROM users u
LEFT JOIN organizations o ON u.org_id = o.org_id
LEFT JOIN donations d ON u.user_id = d.donor_id
WHERE u.role = 'Donor'
GROUP BY o.org_id, o.org_name, o.type
ORDER BY cash_contributed DESC;

-- ---------------------------------------------------------------
-- 7. Requirement Fulfillment Gap Analysis
-- ---------------------------------------------------------------
SELECT
    r.location,
    r.item_name,
    r.quantity_needed,
    r.quantity_fulfilled,
    (r.quantity_needed - r.quantity_fulfilled) AS gap,
    ROUND((r.quantity_fulfilled / NULLIF(r.quantity_needed, 0)) * 100, 1) AS fulfillment_pct,
    r.urgency,
    c.name AS campaign_name,
    COALESCE(inv.available_stock, 0) AS available_in_inventory
FROM requirements r
LEFT JOIN campaigns c ON r.campaign_id = c.campaign_id
LEFT JOIN (
    SELECT item_name, SUM(quantity) AS available_stock
    FROM inventory
    GROUP BY item_name
) inv ON r.item_name = inv.item_name
WHERE r.status != 'Fulfilled'
ORDER BY FIELD(r.urgency, 'Critical', 'High', 'Medium', 'Low'), fulfillment_pct ASC;
