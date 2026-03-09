-- =====================================================================
-- Donum - Realistic Seed Data for Demo & Presentation
-- =====================================================================
USE ngo_db;

-- 1. Organizations

INSERT INTO organizations (org_name, type, country, address, phone, email, website) VALUES
('Donum Foundation', 'NGO', 'India', '42 MG Road, Bengaluru, Karnataka 560001', '+91-80-12345678', 'contact@donum.org', 'https://donum.org'),
('Global Relief Network', 'NGO', 'United States', '1200 18th St NW, Washington DC 20036', '+1-202-555-0150', 'info@globalrelief.org', 'https://globalrelief.org'),
('TechCorp CSR Initiative', 'Corporate Sponsor', 'India', 'Cyber City, Gurugram, Haryana 122002', '+91-124-9876543', 'csr@techcorp.com', 'https://techcorp.com/csr'),
('Ministry of Social Welfare', 'Government', 'India', 'Shastri Bhawan, New Delhi 110001', '+91-11-23384444', 'welfare@gov.in', NULL);

-- 2. Users (Passwords are BCrypt hashed. All demo passwords = the text before @)
-- admin123 -> $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- donor123 -> $2a$10$dDJ3SW6p7b2GOIBkKmFGbOKEKsQYM1OMSHhvONJP.51VEgCp9HhoO
-- vol123   -> $2a$10$rI9kTTk5Z0MbDOkYnzN7kOuM1TdNlPEpOdoZqMvqwCN6vqLDEgP1e
INSERT INTO users (org_id, username, password_hash, email, role, full_name, phone) VALUES
(1, 'admin',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@donum.org',     'Admin',     'Priya Sharma',     '+91-9876543210'),
(1, 'donor1',    '$2a$10$dDJ3SW6p7b2GOIBkKmFGbOKEKsQYM1OMSHhvONJP.51VEgCp9HhoO', 'rajesh.k@gmail.com',     'Donor',     'Rajesh Kumar',     '+91-9876501234'),
(1, 'donor2',    '$2a$10$dDJ3SW6p7b2GOIBkKmFGbOKEKsQYM1OMSHhvONJP.51VEgCp9HhoO', 'anita.m@yahoo.com',      'Donor',     'Anita Menon',      '+91-9988776655'),
(1, 'vol1',      '$2a$10$rI9kTTk5Z0MbDOkYnzN7kOuM1TdNlPEpOdoZqMvqwCN6vqLDEgP1e', 'suresh.v@donum.org',  'Volunteer', 'Suresh Verma',     '+91-8765432109'),
(1, 'vol2',      '$2a$10$rI9kTTk5Z0MbDOkYnzN7kOuM1TdNlPEpOdoZqMvqwCN6vqLDEgP1e', 'meera.d@donum.org',   'Volunteer', 'Meera Deshpande',  '+91-7654321098'),
(2, 'globaladmin','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@globalrelief.org', 'Admin',     'John Mitchell',    '+1-202-555-0151'),
(NULL, 'donor_public', '$2a$10$dDJ3SW6p7b2GOIBkKmFGbOKEKsQYM1OMSHhvONJP.51VEgCp9HhoO', 'sarah.j@email.com', 'Donor', 'Sarah Johnson', '+1-555-0199');

-- 3. Campaigns
INSERT INTO campaigns (org_id, name, description, target_amount, raised_amount, start_date, end_date, status) VALUES
(1, 'Kerala Flood Relief 2026',    'Emergency relief for communities affected by the 2026 Kerala floods. Providing food, shelter, and medical supplies.', 5000000.00, 1250000.00, '2026-01-15', '2026-06-30', 'Active'),
(1, 'Rural Education Drive',       'Providing educational materials and infrastructure to 50 rural schools across Karnataka.', 2000000.00, 800000.00, '2026-02-01', '2026-12-31', 'Active'),
(2, 'Turkey Earthquake Response',  'Multi-phase response to the 2026 Turkey earthquake. Focus on shelter, clean water, and trauma care.', 10000000.00, 4500000.00, '2026-01-20', '2026-07-31', 'Active'),
(3, 'Digital Literacy Mission',    'TechCorp-sponsored program to set up 100 digital classrooms in underserved communities.', 3000000.00, 3000000.00, '2025-06-01', '2026-03-31', 'Completed');

-- 4. Warehouses
INSERT INTO warehouses (org_id, name, location, capacity, manager_name, phone) VALUES
(1, 'Bengaluru Central Hub', 'Peenya Industrial Area, Bengaluru', 50000.00, 'Vikram Rao', '+91-9876500001'),
(1, 'Kochi Emergency Store', 'Willingdon Island, Kochi', 30000.00, 'Joseph Thomas', '+91-9876500002'),
(2, 'DC Distribution Center', '1500 K St NW, Washington DC', 80000.00, 'Emily Carter', '+1-202-555-0160'),
(2, 'Istanbul Field Depot', 'Kadikoy District, Istanbul', 40000.00, 'Ahmet Yilmaz', '+90-212-555-0100');

-- 5. Inventory
INSERT INTO inventory (warehouse_id, item_name, category, quantity, unit, min_threshold) VALUES
(1, 'Rice',              'Food',        2500.00,  'kg',     500),
(1, 'Blankets',          'Shelter',     1200.00,  'units',  200),
(1, 'Medical Kits',      'Medical',      350.00,  'units',  100),
(1, 'Water Purifiers',   'Water',        180.00,  'units',   50),
(1, 'Tarpaulin Sheets',  'Shelter',      600.00,  'units',  150),
(2, 'Rice',              'Food',        1800.00,  'kg',     400),
(2, 'Blankets',          'Shelter',      800.00,  'units',  150),
(2, 'Medical Kits',      'Medical',      200.00,  'units',   80),
(2, 'Cooking Stoves',    'Equipment',    120.00,  'units',   30),
(3, 'MRE Packs',         'Food',       5000.00,  'units',  1000),
(3, 'Tents',             'Shelter',     1500.00,  'units',  300),
(3, 'Water Bottles',     'Water',      10000.00,  'units',  2000),
(4, 'Tents',             'Shelter',      900.00,  'units',  200),
(4, 'Medical Kits',      'Medical',      500.00,  'units',  100),
(4, 'Blankets',          'Shelter',     2000.00,  'units',  400);

-- 6. Requirements
INSERT INTO requirements (campaign_id, location, item_name, quantity_needed, quantity_fulfilled, urgency, status, description) VALUES
(1, 'Wayanad, Kerala',        'Rice',            5000.00,  1200.00, 'Critical', 'Partially Met', 'Immediate food supply for 2000 displaced families'),
(1, 'Wayanad, Kerala',        'Medical Kits',     200.00,    80.00, 'Critical', 'Partially Met', 'Urgent medical supplies for field hospital'),
(1, 'Alappuzha, Kerala',      'Blankets',        1500.00,   500.00, 'High',     'Partially Met', 'Shelter materials for relief camps'),
(1, 'Idukki, Kerala',         'Water Purifiers',  300.00,    50.00, 'Critical', 'Partially Met', 'Clean water access for hill communities'),
(2, 'Chikkaballapur, Karnataka', 'Textbooks',    2000.00,     0.00, 'Medium',   'Pending',       'Textbooks for primary school students'),
(2, 'Raichur, Karnataka',     'Laptops',          100.00,     0.00, 'Low',      'Pending',       'Laptops for digital classroom setup'),
(3, 'Hatay, Turkey',          'Tents',            500.00,   200.00, 'Critical', 'Partially Met', 'Emergency shelter for earthquake survivors'),
(3, 'Gaziantep, Turkey',      'Medical Kits',     300.00,   100.00, 'High',     'Partially Met', 'Trauma care medical supplies');

-- 7. Donations
INSERT INTO donations (donor_id, campaign_id, type, item_name, amount_or_quantity, status, notes) VALUES
(2, 1, 'Cash',  NULL,          50000.00, 'Received',  'Monthly contribution to Kerala relief'),
(2, 1, 'Kind', 'Rice',          200.00, 'Delivered',  'Sourced from local wholesale market'),
(3, 1, 'Cash',  NULL,         100000.00, 'Allocated', 'Corporate matching donation'),
(3, 2, 'Kind', 'Textbooks',     500.00, 'Received',  'Donated through school book drive'),
(7, 3, 'Cash',  NULL,          250000.00, 'Received',  'International donor contribution'),
(2, 2, 'Cash',  NULL,          25000.00, 'Received',  'For rural education support'),
(3, 1, 'Kind', 'Blankets',      100.00, 'Delivered',  'Winter blankets for relief camps');

-- 8. Transactions
INSERT INTO transactions (transaction_id, donation_id, payment_method, amount, currency, status) VALUES
('TXN-UA-20260215-001', 1, 'UPI',           50000.00, 'INR', 'Success'),
('TXN-UA-20260220-002', 3, 'Bank Transfer', 100000.00, 'INR', 'Success'),
('TXN-GR-20260218-001', 5, 'Stripe',        250000.00, 'USD', 'Success'),
('TXN-UA-20260301-003', 6, 'PayPal',         25000.00, 'INR', 'Success');

-- 9. Distribution Log
INSERT INTO distribution_log (donation_id, requirement_id, volunteer_id, quantity_distributed, notes) VALUES
(2, 1, 4, 200.00, 'Delivered to Wayanad relief camp - Batch 1'),
(7, 3, 5, 100.00, 'Distributed at Alappuzha temporary shelters'),
(NULL, 2, 4, 80.00, 'Medical kits from central warehouse to Wayanad field hospital'),
(NULL, 7, 5, 200.00, 'Tents shipped to Hatay from Istanbul depot');

-- 10. Audit Log
INSERT INTO audit_log (user_id, action, entity_type, entity_id, details, ip_address) VALUES
(1, 'LOGIN',      'USER',        1,    'Admin login successful', '192.168.1.100'),
(2, 'DONATION',   'DONATION',    1,    'Cash donation of INR 50,000 to Kerala Relief', '10.0.0.55'),
(4, 'DISTRIBUTE', 'DISTRIBUTION', 1,   'Distributed 200kg Rice to Wayanad', '192.168.1.101'),
(1, 'CREATE',     'CAMPAIGN',    1,    'Created Kerala Flood Relief 2026 campaign', '192.168.1.100');
