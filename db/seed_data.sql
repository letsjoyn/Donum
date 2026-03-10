-- =====================================================================
-- Donum - Realistic Seed Data for Demo & Presentation
-- Real-world Indian disaster relief scenarios, 2025-2026
-- =====================================================================
USE ngo_db;

-- 1. Organizations
INSERT INTO organizations (org_name, type, country, address, phone, email, website) VALUES
('Donum Foundation',            'NGO',               'India',         '42 MG Road, Bengaluru, Karnataka 560001',           '+91-80-12345678',  'contact@donum.org',       'https://donum.org'),
('Goonj',                       'NGO',               'India',         'J-93, Sarita Vihar, New Delhi 110076',              '+91-11-26972351',  'mail@goonj.org',          'https://goonj.org'),
('CRY India',                   'NGO',               'India',         '632 Anna Salai, Chennai, Tamil Nadu 600006',        '+91-44-28297625',  'contact@cry.org',         'https://cry.org'),
('TechCorp CSR Initiative',     'Corporate Sponsor',  'India',         'Cyber City, Gurugram, Haryana 122002',              '+91-124-9876543',  'csr@techcorp.com',        'https://techcorp.com/csr'),
('NDRF - National Disaster Response Force', 'Government', 'India',    'NDRF Bhawan, Sector 29, Aundh, Pune 411007',        '+91-20-27115038',  'control@ndrf.gov.in',     NULL),
('Akshaya Patra Foundation',    'NGO',               'India',         'No. 72, 3rd Cross, Rajajinagar, Bengaluru 560010',  '+91-80-30143400',  'infodesk@akshayapatra.org','https://akshayapatra.org');

-- 2. Users (Passwords are BCrypt hashed)
-- admin123 -> $2a$10$VyeNG5boD5hxljAn7l4DJ.7Gk7z7xyg93hON9sKvZlx0HFW4k.4Eu
-- donor123 -> $2a$10$wDzfGgmqRXfw5Oj1cq4Ml.t/IY4JKdgSWOv0wQusmIQaWM/g12IMy
-- vol123   -> $2a$10$m4Nosno.Bp1l.nBZCupIhu6gSmbTci3wSB54TYkIRpSyAGlLzZ7Ru
INSERT INTO users (org_id, username, password_hash, email, role, full_name, phone) VALUES
(1, 'admin',    '$2a$10$VyeNG5boD5hxljAn7l4DJ.7Gk7z7xyg93hON9sKvZlx0HFW4k.4Eu', 'admin@donum.org',          'Admin',     'Priya Sharma',        '+91-9876543210'),
(1, 'donor1',   '$2a$10$wDzfGgmqRXfw5Oj1cq4Ml.t/IY4JKdgSWOv0wQusmIQaWM/g12IMy', 'rajesh.k@gmail.com',       'Donor',     'Rajesh Kumar',        '+91-9876501234'),
(1, 'donor2',   '$2a$10$FnBEHGV2QMUTl6ByCKv9tOa.1qgIKVT4DeilAwnoZMUnWgDai8YUG', 'anita.m@yahoo.com',        'Donor',     'Anita Menon',         '+91-9988776655'),
(1, 'vol1',     '$2a$10$m4Nosno.Bp1l.nBZCupIhu6gSmbTci3wSB54TYkIRpSyAGlLzZ7Ru', 'suresh.v@donum.org',       'Volunteer', 'Suresh Verma',        '+91-8765432109'),
(1, 'vol2',     '$2a$10$PD1qok8W4LgYvSbtcFT1PeRJDAKJq0hat02/mas5pmmKbORBv2/XO', 'meera.d@donum.org',        'Volunteer', 'Meera Deshpande',     '+91-7654321098'),
(2, 'donor3',   '$2a$10$wDzfGgmqRXfw5Oj1cq4Ml.t/IY4JKdgSWOv0wQusmIQaWM/g12IMy', 'vikram.s@outlook.com',     'Donor',     'Vikram Singh',        '+91-9123456789'),
(3, 'donor4',   '$2a$10$wDzfGgmqRXfw5Oj1cq4Ml.t/IY4JKdgSWOv0wQusmIQaWM/g12IMy', 'deepa.r@gmail.com',        'Donor',     'Deepa Raghavan',      '+91-9234567890'),
(1, 'vol3',     '$2a$10$m4Nosno.Bp1l.nBZCupIhu6gSmbTci3wSB54TYkIRpSyAGlLzZ7Ru', 'arjun.p@donum.org',        'Volunteer', 'Arjun Patel',         '+91-8123456780'),
(1, 'donor5',   '$2a$10$wDzfGgmqRXfw5Oj1cq4Ml.t/IY4JKdgSWOv0wQusmIQaWM/g12IMy', 'nisha.g@gmail.com',        'Donor',     'Nisha Gupta',         '+91-9345678901'),
(6, 'donor6',   '$2a$10$wDzfGgmqRXfw5Oj1cq4Ml.t/IY4JKdgSWOv0wQusmIQaWM/g12IMy', 'ravi.m@hotmail.com',       'Donor',     'Ravi Malhotra',       '+91-9456789012');

-- 3. Campaigns (Real-world Indian disaster and social campaigns)
INSERT INTO campaigns (org_id, name, description, target_amount, raised_amount, start_date, end_date, status) VALUES
(1, 'Assam Floods 2026 - Emergency Relief',
 'Devastating floods across Assam''s Brahmaputra basin have displaced over 1.8 million people across 28 districts. Donum Foundation is on the ground providing cooked meals, dry ration kits, water purification tablets, and temporary shelters. We need urgent support for families in Nagaon, Cachar, and Barpeta districts.',
 7500000.00, 2840000.00, '2026-02-01', '2026-08-31', 'Active'),

(1, 'Wayanad Landslide Rehabilitation',
 'The catastrophic landslides in Wayanad, Kerala have destroyed homes, roads, and livelihoods. Over 400 families need permanent rehabilitation including rebuilding homes, restoring farmland, and providing livelihood support. This campaign focuses on long-term recovery beyond immediate relief.',
 12000000.00, 5200000.00, '2025-09-01', '2026-09-30', 'Active'),

(2, 'Manipur Conflict - Humanitarian Aid',
 'Goonj is providing relief to internally displaced communities in Manipur. Over 60,000 people in relief camps across Imphal and Churachandpur need clothing, hygiene kits, sanitary pads, blankets, and daily essentials. Every contribution directly reaches families who''ve lost everything.',
 4000000.00, 1750000.00, '2025-10-15', '2026-06-30', 'Active'),

(3, 'Bihar Child Nutrition Mission',
 'CRY India is tackling severe acute malnutrition among children under 5 in rural Bihar. Covering 15 blocks across Madhubani, Supaul, and Kishanganj districts. Providing therapeutic food, nutrition supplements, growth monitoring, and training local Anganwadi workers.',
 3500000.00, 1200000.00, '2026-01-01', '2026-12-31', 'Active'),

(6, 'Mid-Day Meal Expansion - Odisha',
 'Akshaya Patra is expanding its mid-day meal program to 500 new government schools in Odisha''s tribal districts. Each meal costs just Rs 12 but transforms attendance and nutrition outcomes. Target: 75,000 children getting one hot meal every school day.',
 5000000.00, 3200000.00, '2025-07-01', '2026-06-30', 'Active'),

(4, 'Digital Classrooms for Rural India',
 'TechCorp CSR is setting up 200 smart classrooms across government schools in backward districts of Jharkhand, Chhattisgarh, and Odisha. Each classroom gets a projector, laptop, internet connection, and pre-loaded educational content in local languages.',
 8000000.00, 8000000.00, '2025-04-01', '2026-03-31', 'Completed'),

(1, 'Chennai Urban Flood Preparedness',
 'Pre-monsoon disaster preparedness for flood-prone areas in Chennai. Setting up emergency supply depots, training community disaster response teams, distributing emergency kits to 10,000 vulnerable households in low-lying areas of Adyar and Velachery.',
 2500000.00, 900000.00, '2026-02-15', '2026-10-31', 'Active'),

(1, 'Uttarakhand Earthquake Recovery',
 'Following the 5.8 magnitude earthquake in Uttarakhand''s Chamoli district, 2,300 homes were damaged and 180 families fully displaced. This campaign supports structural assessment, temporary housing, winterization kits, and trauma counseling for affected communities.',
 6000000.00, 4100000.00, '2025-11-20', '2026-05-31', 'Active');

-- 4. Warehouses
INSERT INTO warehouses (org_id, name, location, capacity, manager_name, phone) VALUES
(1, 'Bengaluru Central Hub',       'Peenya Industrial Area, Bengaluru',          50000.00,  'Vikram Rao',       '+91-9876500001'),
(1, 'Kochi Emergency Store',       'Willingdon Island, Kochi, Kerala',           30000.00,  'Joseph Thomas',    '+91-9876500002'),
(1, 'Guwahati Relief Center',      'Paltan Bazar, Guwahati, Assam',             35000.00,  'Bhaskar Deka',     '+91-9876500003'),
(2, 'Delhi NCR Warehouse',         'Okhla Phase-II, New Delhi',                  60000.00,  'Ramesh Tiwari',    '+91-9876500004'),
(1, 'Chennai Emergency Depot',     'Guindy Industrial Estate, Chennai',          25000.00,  'Lakshmi Narayan',  '+91-9876500005'),
(6, 'Bhubaneswar Kitchen Hub',     'Chandrasekharpur, Bhubaneswar, Odisha',      40000.00,  'Sunil Mohanty',    '+91-9876500006');

-- 5. Inventory (Realistic quantities across warehouses)
INSERT INTO inventory (warehouse_id, item_name, category, quantity, unit, min_threshold) VALUES
-- Bengaluru Hub
(1, 'Rice (25kg bags)',     'Food',        800.00,   'bags',    200),
(1, 'Ready-to-Eat Meals',  'Food',       3000.00,   'packets', 500),
(1, 'Blankets',            'Shelter',     1200.00,   'units',   200),
(1, 'First Aid Kits',      'Medical',      450.00,   'units',   100),
(1, 'Water Purifier Tablets','Water',     5000.00,   'strips',  1000),
(1, 'Tarpaulin Sheets',    'Shelter',      600.00,   'units',   150),
(1, 'Hygiene Kits',        'Sanitation',   800.00,   'units',   200),
-- Kochi Store
(2, 'Rice (25kg bags)',     'Food',        500.00,   'bags',    150),
(2, 'Blankets',            'Shelter',      400.00,   'units',   100),
(2, 'Medical Kits',        'Medical',      200.00,   'units',    80),
(2, 'Cooking Stoves',      'Equipment',    120.00,   'units',    30),
(2, 'Sanitary Pad Kits',   'Sanitation',   600.00,   'units',   150),
(2, 'Tents (4-person)',    'Shelter',      180.00,   'units',    50),
-- Guwahati Center
(3, 'Rice (25kg bags)',     'Food',       1200.00,   'bags',    300),
(3, 'Ready-to-Eat Meals',  'Food',       5000.00,   'packets', 800),
(3, 'Blankets',            'Shelter',     2000.00,   'units',   400),
(3, 'Water Purifier Tablets','Water',     8000.00,   'strips',  1500),
(3, 'Tarpaulin Sheets',    'Shelter',     1500.00,   'units',   300),
(3, 'First Aid Kits',      'Medical',      300.00,   'units',   100),
(3, 'Mosquito Nets',       'Sanitation',  1000.00,   'units',   200),
-- Delhi NCR Warehouse
(4, 'Blankets',            'Shelter',     3000.00,   'units',   500),
(4, 'Winterization Kits',  'Shelter',      800.00,   'units',   200),
(4, 'Medical Kits',        'Medical',      600.00,   'units',   150),
(4, 'Dry Ration Kits',     'Food',       2000.00,   'kits',    400),
(4, 'Hygiene Kits',        'Sanitation',  1500.00,   'units',   300),
-- Chennai Depot
(5, 'Rice (25kg bags)',     'Food',         80.00,   'bags',    200),
(5, 'Ready-to-Eat Meals',  'Food',        400.00,   'packets', 500),
(5, 'Emergency Rain Kits', 'Shelter',      300.00,   'units',   100),
(5, 'Water Purifier Tablets','Water',     2000.00,   'strips',  500),
(5, 'First Aid Kits',      'Medical',       60.00,   'units',   100),
-- Bhubaneswar Kitchen
(6, 'Rice (25kg bags)',     'Food',       2000.00,   'bags',    500),
(6, 'Dal (10kg bags)',      'Food',        800.00,   'bags',    200),
(6, 'Cooking Oil (5L)',     'Food',        500.00,   'cans',    100),
(6, 'Vegetables (mixed)',   'Food',        300.00,   'kg',      100);

-- 6. Requirements (Covering all active campaigns)
INSERT INTO requirements (campaign_id, location, item_name, quantity_needed, quantity_fulfilled, urgency, status, description) VALUES
-- Assam Floods
(1, 'Nagaon, Assam',              'Rice (25kg bags)',       2000.00,   600.00, 'Critical',   'Partially Met', 'Food for 8,000 people across 12 relief camps in Nagaon district'),
(1, 'Nagaon, Assam',              'Ready-to-Eat Meals',   10000.00,  3000.00, 'Critical',   'Partially Met', 'Immediate hot meals for families in waterlogged areas'),
(1, 'Barpeta, Assam',             'Blankets',              3000.00,   800.00, 'High',       'Partially Met', 'Families in open shelters need protection from rain and cold'),
(1, 'Barpeta, Assam',             'Water Purifier Tablets',15000.00,  4000.00, 'Critical',   'Partially Met', 'All drinking water sources contaminated by floodwater'),
(1, 'Cachar, Assam',              'Tarpaulin Sheets',      2000.00,   500.00, 'High',       'Partially Met', 'Temporary roof covers for damaged homes'),
(1, 'Cachar, Assam',              'First Aid Kits',         500.00,   100.00, 'Critical',   'Partially Met', 'Waterborne disease outbreak, urgent medical supplies needed'),
(1, 'Nagaon, Assam',              'Mosquito Nets',         5000.00,     0.00, 'High',       'Pending',       'Malaria prevention in stagnant flood water areas'),
-- Wayanad Rehabilitation
(2, 'Meppadi, Wayanad',           'Tents (4-person)',       200.00,    80.00, 'Critical',   'Partially Met', 'Families still in temporary shelters 6 months after landslide'),
(2, 'Chooralmala, Wayanad',       'Medical Kits',           150.00,    50.00, 'High',       'Partially Met', 'Community health center restocking for long-term care'),
(2, 'Mundakkai, Wayanad',         'Blankets',               500.00,   200.00, 'Medium',     'Partially Met', 'For families in rehabilitation camps during monsoon season'),
-- Manipur Conflict
(3, 'Imphal, Manipur',            'Hygiene Kits',          2000.00,   500.00, 'High',       'Partially Met', 'Basic hygiene essentials for displaced families in relief camps'),
(3, 'Churachandpur, Manipur',     'Blankets',              1500.00,   300.00, 'High',       'Partially Met', 'Winter blankets for families in temporary shelters'),
(3, 'Imphal, Manipur',            'Sanitary Pad Kits',     3000.00,   600.00, 'Critical',   'Partially Met', 'Menstrual hygiene for women and girls in camps - severely lacking'),
-- Bihar Nutrition
(4, 'Madhubani, Bihar',           'Ready-to-Eat Meals',    8000.00,  2000.00, 'High',       'Partially Met', 'Therapeutic nutrition packets for malnourished children under 5'),
(4, 'Supaul, Bihar',              'First Aid Kits',         200.00,     0.00, 'Medium',     'Pending',       'For Anganwadi centers conducting nutrition monitoring'),
-- Odisha Mid-Day Meals
(5, 'Koraput, Odisha',            'Rice (25kg bags)',      1000.00,   500.00, 'High',       'Partially Met', 'Raw material for mid-day meals at 200 tribal schools'),
(5, 'Malkangiri, Odisha',         'Dal (10kg bags)',        400.00,   150.00, 'Medium',     'Partially Met', 'Dal supply for school kitchens in remote tribal areas'),
-- Chennai Flood Prep
(7, 'Velachery, Chennai',         'Emergency Rain Kits',   5000.00,   300.00, 'Medium',     'Partially Met', 'Rain gear, torches, dry food, emergency numbers for vulnerable households'),
(7, 'Adyar, Chennai',             'Water Purifier Tablets', 8000.00,  1000.00, 'Medium',     'Partially Met', 'Pre-positioned in community centers for monsoon season'),
-- Uttarakhand Earthquake
(8, 'Chamoli, Uttarakhand',       'Winterization Kits',     500.00,   200.00, 'Critical',   'Partially Met', 'Insulated tarps, thermal blankets, firewood for families facing sub-zero winter'),
(8, 'Chamoli, Uttarakhand',       'Tents (4-person)',       180.00,   100.00, 'Critical',   'Partially Met', 'Earthquake-resistant temporary shelters for displaced families'),
(8, 'Joshimath, Uttarakhand',     'Medical Kits',           100.00,    40.00, 'High',       'Partially Met', 'Trauma kits and medicines for community health workers');

-- 7. Donations (More diverse, realistic amounts)
INSERT INTO donations (donor_id, campaign_id, type, item_name, amount_or_quantity, status, notes) VALUES
-- Assam Floods donations
(2, 1, 'Cash',  NULL,            75000.00, 'Received',  'Monthly recurring donation for Assam relief'),
(2, 1, 'Kind', 'Rice (25kg bags)',  50.00, 'Delivered',  'Sourced from Bengaluru wholesale market'),
(3, 1, 'Cash',  NULL,           200000.00, 'Allocated', 'Corporate CSR quarterly allocation'),
(7, 1, 'Cash',  NULL,            15000.00, 'Received',  'First-time donor via UPI'),
(8, 1, 'Kind', 'Blankets',        200.00, 'Received',  'Donated at Goonj collection center'),
(10,1, 'Cash',  NULL,            50000.00, 'Received',  'Birthday fundraiser proceeds'),
-- Wayanad donations
(3, 2, 'Cash',  NULL,           500000.00, 'Allocated', 'Annual CSR commitment to Kerala rehabilitation'),
(2, 2, 'Cash',  NULL,            25000.00, 'Received',  'Monthly support for Wayanad families'),
(9, 2, 'Cash',  NULL,           100000.00, 'Received',  'Donated after seeing news coverage'),
-- Manipur donations
(7, 3, 'Kind', 'Hygiene Kits',    100.00, 'Delivered',  'Collected from college drive in Delhi'),
(8, 3, 'Kind', 'Blankets',        150.00, 'Delivered',  'Winter blanket collection from Pune'),
(10,3, 'Cash',  NULL,            30000.00, 'Received',  'WhatsApp group fundraiser'),
-- Bihar Nutrition
(3, 4, 'Cash',  NULL,           300000.00, 'Received',  'CRY matched donation program'),
(9, 4, 'Cash',  NULL,            10000.00, 'Received',  'Small but regular monthly donor'),
-- Odisha Mid-Day Meals
(2, 5, 'Cash',  NULL,            50000.00, 'Received',  'Sponsor 100 children for a month'),
(7, 5, 'Kind', 'Rice (25kg bags)', 100.00, 'Delivered',  'Bulk purchase and delivery to Bhubaneswar'),
-- Chennai Prep
(10,7, 'Cash',  NULL,            20000.00, 'Received',  'Chennai local donor for monsoon prep'),
-- Uttarakhand
(3, 8, 'Cash',  NULL,           400000.00, 'Allocated', 'Emergency CSR fund release for earthquake'),
(9, 8, 'Kind', 'Winterization Kits', 50.00, 'Received',  'Purchased from local supplier in Dehradun'),
(2, 8, 'Cash',  NULL,            30000.00, 'Received',  'Earthquake response contribution');

-- 8. Transactions (For all cash donations)
INSERT INTO transactions (transaction_id, donation_id, payment_method, amount, currency, status) VALUES
('TXN-DON-20260205-001', 1, 'UPI',           75000.00,  'INR', 'Success'),
('TXN-DON-20260210-002', 3, 'Bank Transfer', 200000.00, 'INR', 'Success'),
('TXN-DON-20260212-003', 4, 'UPI',           15000.00,  'INR', 'Success'),
('TXN-DON-20260215-004', 6, 'UPI',           50000.00,  'INR', 'Success'),
('TXN-DON-20260201-005', 7, 'Bank Transfer', 500000.00, 'INR', 'Success'),
('TXN-DON-20260218-006', 8, 'UPI',           25000.00,  'INR', 'Success'),
('TXN-DON-20260220-007', 9, 'UPI',          100000.00,  'INR', 'Success'),
('TXN-DON-20260225-008', 12, 'UPI',           30000.00, 'INR', 'Success'),
('TXN-DON-20260228-009', 13, 'Bank Transfer', 300000.00,'INR', 'Success'),
('TXN-DON-20260228-010', 14, 'UPI',           10000.00, 'INR', 'Success'),
('TXN-DON-20260301-011', 15, 'UPI',           50000.00, 'INR', 'Success'),
('TXN-DON-20260303-012', 17, 'UPI',           20000.00, 'INR', 'Success'),
('TXN-DON-20260115-013', 18, 'Bank Transfer', 400000.00,'INR', 'Success'),
('TXN-DON-20260120-014', 20, 'UPI',           30000.00, 'INR', 'Success');

-- 9. Distribution Log (Volunteer deliveries)
INSERT INTO distribution_log (donation_id, requirement_id, volunteer_id, quantity_distributed, notes) VALUES
(2,  1,  4,  50.00,  'Delivered 50 rice bags to Nagaon relief camp #3 via NDRF truck'),
(5,  3,  5, 200.00,  'Blankets distributed at Barpeta community hall to 200 families'),
(NULL, 6, 4, 100.00,  'Medical kits from Guwahati warehouse to Cachar PHC'),
(NULL, 4, 9, 2000.00, 'Water purification tablets delivered to 6 hand pump locations in Barpeta'),
(NULL, 8, 5,  80.00,  'Tents erected at Meppadi temporary settlement site'),
(10, 12, 9, 150.00,  'Blankets delivered to Churachandpur relief camp via army convoy'),
(11, 11, 9, 100.00,  'Hygiene kits distributed to women at Imphal relief camp #7'),
(16, 16, 4, 100.00,  'Rice bags delivered to Akshaya Patra Koraput kitchen facility'),
(19, 20, 5,  50.00,  'Winterization kits delivered to Chamoli block office for distribution');

-- 10. Audit Log
INSERT INTO audit_log (user_id, action, entity_type, entity_id, details, ip_address) VALUES
(1, 'LOGIN',      'USER',        1,    'Admin login successful',                                   '192.168.1.100'),
(2, 'DONATION',   'DONATION',    1,    'Cash donation of INR 75,000 to Assam Floods Relief',       '10.0.0.55'),
(4, 'DISTRIBUTE', 'DISTRIBUTION', 1,   'Distributed 50 rice bags to Nagaon relief camp',           '192.168.1.101'),
(1, 'CREATE',     'CAMPAIGN',    1,    'Created Assam Floods 2026 Emergency Relief campaign',      '192.168.1.100'),
(1, 'CREATE',     'CAMPAIGN',    2,    'Created Wayanad Landslide Rehabilitation campaign',        '192.168.1.100'),
(3, 'DONATION',   'DONATION',    3,    'Cash donation of INR 2,00,000 via Bank Transfer',          '10.0.0.60'),
(5, 'DISTRIBUTE', 'DISTRIBUTION', 5,   'Tents set up at Meppadi site',                            '192.168.1.102'),
(1, 'CREATE',     'REQUIREMENT', 1,    'Created critical rice requirement for Nagaon',             '192.168.1.100'),
(9, 'DISTRIBUTE', 'DISTRIBUTION', 6,   'Blankets to Churachandpur via army convoy',                '10.0.0.78'),
(1, 'LOGIN',      'USER',        1,    'Admin session — inventory review',                         '192.168.1.100');
