USE ngo_db;

SELECT user_id, username, full_name, email, role, is_active, created_at
FROM users
ORDER BY role, user_id;



SELECT * FROM users WHERE role = 'Donor';
SELECT * FROM users WHERE role = 'Volunteer';
SELECT * FROM users WHERE role = 'Admin';




SELECT d.donation_id, d.donation_date, u.full_name AS donor,
       d.type, d.item_name, d.amount_or_quantity, d.status,
       c.name AS campaign
FROM donations d
JOIN users u ON d.donor_id = u.user_id
LEFT JOIN campaigns c ON d.campaign_id = c.campaign_id
ORDER BY d.donation_date DESC;




SELECT i.item_id, i.item_name, i.quantity, i.unit, i.category,
       w.name AS warehouse, i.last_updated
FROM inventory i
LEFT JOIN warehouses w ON i.warehouse_id = w.warehouse_id
ORDER BY i.item_name;


SELECT requirement_id, location, item_name, quantity_needed,
       quantity_fulfilled, urgency, status, campaign_id
FROM requirements
ORDER BY FIELD(urgency, 'Critical', 'High', 'Medium', 'Low');


SELECT dl.log_id, dl.distributed_at, u.full_name AS volunteer,
       r.location, r.item_name, dl.quantity_distributed, dl.notes
FROM distribution_log dl
JOIN users u ON dl.volunteer_id = u.user_id
JOIN requirements r ON dl.requirement_id = r.requirement_id
ORDER BY dl.distributed_at DESC;



SELECT campaign_id, name, status, target_amount, raised_amount,
       ROUND(raised_amount / NULLIF(target_amount, 0) * 100, 1) AS progress_pct
FROM campaigns
ORDER BY campaign_id;



SELECT * FROM users ORDER BY created_at DESC LIMIT 10;


