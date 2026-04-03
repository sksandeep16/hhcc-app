-- =============================================
-- DML Script — Dummy Data (jdbc:sqlite:demo)
-- =============================================

-- ── CARE SERVICES ──────────────────────────────────────────────────────────
INSERT OR IGNORE INTO care_services (id, name, icon, description, image_url, sort_order) VALUES
(1, 'Family care & support',          '👨‍👩‍👧',
 'Personalized care plans for every family member, including children, seniors, and those with special needs. Our caregivers provide companionship, daily assistance, and emotional support in the comfort of your home.',
 NULL, 1),
(2, 'Pet sitting & walking',          '🐕',
 'Professional pet sitters ensure your pets are happy and healthy while you''re away. We offer daily walks, feeding, playtime, and medication administration for dogs, cats, and other small animals.',
 NULL, 2),
(3, 'Pet medical support',            '🐾',
 'Our trained staff can assist with pet medication, post-surgery care, and regular health monitoring. We work closely with veterinarians to ensure your pets receive the best possible care at home.',
 NULL, 3),
(4, 'Home visits & wellness checks',  '🏠',
 'Regular home visits for wellness checks, safety assessments, and peace of mind. We help with light housekeeping, meal preparation, and ensure a safe environment for your loved ones and pets.',
 NULL, 4);

-- ── SERVICE BULLETS ────────────────────────────────────────────────────────
INSERT OR IGNORE INTO service_bullets (id, service_id, bullet_text, sort_order) VALUES
-- Family care & support
(1,  1, 'Daily routines: help with meals, hygiene, and medication reminders', 1),
(2,  1, 'Emotional support: friendly conversation, games, and activities', 2),
(3,  1, 'Custom care: plans tailored to each family''s unique needs', 3),
(4,  1, 'Example: "Our caregiver helped Mrs. Smith''s father regain independence after surgery with daily encouragement and gentle exercise."', 4),
-- Pet sitting & walking
(5,  2, 'Pet profiles: we learn your pet''s habits, likes, and needs', 1),
(6,  2, 'Exercise: regular walks and playtime for physical and mental health', 2),
(7,  2, 'Safety: secure home entry, GPS walk tracking, and daily updates', 3),
(8,  2, 'Example: "Max the golden retriever gets his favourite park walk and a photo update sent to his family every day."', 4),
-- Pet medical support
(9,  3, 'Medication: oral, topical, and injectable meds administered safely', 1),
(10, 3, 'Recovery: gentle wound care and post-surgery monitoring', 2),
(11, 3, 'Vet coordination: we update your vet and follow their instructions', 3),
(12, 3, 'Example: "After surgery, Bella the cat received daily wound checks and her family got regular progress reports."', 4),
-- Home visits & wellness checks
(13, 4, 'Safety: home safety checks, fall prevention, and emergency readiness', 1),
(14, 4, 'Meals: light meal prep and nutrition reminders', 2),
(15, 4, 'Peace of mind: regular updates to families and loved ones', 3),
(16, 4, 'Example: "Our team''s weekly visits helped the Lee family''s grandmother stay safe and independent at home."', 4);

-- ── USERS ─────────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO users (id, username, email, password, role) VALUES
(1,  'john_doe',      'john.doe@example.com',       'pass1234',  'USER'),
(2,  'jane_smith',    'jane.smith@example.com',      'pass1234',  'USER'),
(3,  'robert_brown',  'robert.brown@example.com',    'pass1234',  'USER'),
(4,  'emily_clark',   'emily.clark@example.com',     'pass1234',  'USER'),
(5,  'admin_user',    'admin@example.com',           'admin1234', 'ADMIN'),
(6,  'michael_lee',   'michael.lee@example.com',     'pass1234',  'USER'),
(7,  'sarah_jones',   'sarah.jones@example.com',     'pass1234',  'USER'),
(8,  'david_wilson',  'david.wilson@example.com',    'pass1234',  'USER'),
(9,  'lisa_martin',   'lisa.martin@example.com',     'pass1234',  'USER'),
(10, 'chris_taylor',  'chris.taylor@example.com',    'pass1234',  'USER');

-- ── REGISTRATION ──────────────────────────────────────────────────────────
INSERT OR IGNORE INTO registration (id, user_id, registered_at, status) VALUES
(1,  1,  '2026-01-10 09:15:00',  'ACTIVE'),
(2,  2,  '2026-01-15 11:30:00',  'ACTIVE'),
(3,  3,  '2026-02-03 14:45:00',  'ACTIVE'),
(4,  4,  '2026-02-20 08:00:00',  'PENDING'),
(5,  5,  '2026-03-01 10:00:00',  'ACTIVE'),
(6,  6,  '2026-03-05 08:30:00',  'ACTIVE'),
(7,  7,  '2026-03-08 11:00:00',  'ACTIVE'),
(8,  8,  '2026-03-12 14:00:00',  'PENDING'),
(9,  9,  '2026-03-18 09:45:00',  'ACTIVE'),
(10, 10, '2026-03-22 10:30:00',  'ACTIVE');

-- ── FAMILY MEMBERS ────────────────────────────────────────────────────────
INSERT OR IGNORE INTO family_members (id, user_id, first_name, last_name, relationship, date_of_birth) VALUES
-- John Doe's family
(1,  1, 'Mary',     'Doe',      'Spouse',       '1990-04-22'),
(2,  1, 'Lucas',    'Doe',      'Child',        '2015-08-10'),
(3,  1, 'Henry',    'Doe',      'Parent',       '1960-11-05'),

-- Jane Smith's family
(4,  2, 'Mark',     'Smith',    'Spouse',       '1988-07-14'),
(5,  2, 'Olivia',   'Smith',    'Child',        '2018-03-29'),
(6,  2, 'Sophia',   'Smith',    'Sibling',      '1992-09-17'),

-- Robert Brown's family
(7,  3, 'Patricia', 'Brown',    'Parent',       '1958-02-28'),
(8,  3, 'Daniel',   'Brown',    'Sibling',      '1994-06-11'),

-- Emily Clark's family
(9,  4, 'James',    'Clark',    'Spouse',       '1985-12-01'),
(10, 4, 'Ella',     'Clark',    'Child',        '2019-05-20'),
(11, 4, 'Nora',     'Clark',    'Grandparent',  '1945-03-15'),

-- Admin's family
(12, 5, 'Linda',    'Admin',    'Spouse',       '1982-10-08'),

-- Michael Lee's family
(13, 6, 'Rachel',   'Lee',      'Spouse',       '1991-03-14'),
(14, 6, 'Ethan',    'Lee',      'Child',        '2016-07-22'),
(15, 6, 'Grace',    'Lee',      'Child',        '2019-11-03'),
(16, 6, 'Thomas',   'Lee',      'Parent',       '1962-05-18'),

-- Sarah Jones's family
(17, 7, 'Kevin',    'Jones',    'Spouse',       '1987-09-25'),
(18, 7, 'Lily',     'Jones',    'Child',        '2017-04-12'),
(19, 7, 'Megan',    'Jones',    'Sibling',      '1993-01-30'),

-- David Wilson's family
(20, 8, 'Carol',    'Wilson',   'Spouse',       '1989-06-07'),
(21, 8, 'Noah',     'Wilson',   'Child',        '2020-02-19'),
(22, 8, 'George',   'Wilson',   'Parent',       '1955-08-11'),
(23, 8, 'Alice',    'Wilson',   'Grandparent',  '1930-12-25'),

-- Lisa Martin's family
(24, 9, 'Brian',    'Martin',   'Spouse',       '1984-10-02'),
(25, 9, 'Chloe',    'Martin',   'Child',        '2015-06-15'),
(26, 9, 'Dylan',    'Martin',   'Child',        '2018-09-28'),

-- Chris Taylor's family
(27, 10, 'Amy',     'Taylor',   'Spouse',       '1992-04-05'),
(28, 10, 'Jack',    'Taylor',   'Child',        '2021-01-17'),
(29, 10, 'Paul',    'Taylor',   'Parent',       '1963-07-09'),
(30, 10, 'Susan',   'Taylor',   'Sibling',      '1995-03-22');

-- ── PETS ──────────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO pets (id, user_id, name, species, breed, date_of_birth, gender) VALUES
-- John Doe's pets
(1,  1, 'Buddy',    'Dog',    'Golden Retriever',  '2020-03-15', 'Male'),
(2,  1, 'Whiskers', 'Cat',    'Persian',           '2021-06-10', 'Female'),

-- Jane Smith's pets
(3,  2, 'Charlie',  'Dog',    'Beagle',            '2019-11-22', 'Male'),
(4,  2, 'Bella',    'Cat',    'Siamese',           '2022-01-08', 'Female'),
(5,  2, 'Tweety',   'Bird',   'Canary',            '2023-04-01', 'Male'),

-- Robert Brown's pets
(6,  3, 'Max',      'Dog',    'German Shepherd',   '2018-07-30', 'Male'),
(7,  3, 'Goldie',   'Fish',   'Goldfish',          '2024-02-14', 'Unknown'),

-- Emily Clark's pets
(8,  4, 'Luna',     'Cat',    'Maine Coon',        '2020-09-05', 'Female'),
(9,  4, 'Rocky',    'Dog',    'Bulldog',           '2021-12-20', 'Male'),

-- Admin's pets
(10, 5, 'Shadow',   'Dog',    'Labrador',          '2019-05-18', 'Male'),

-- Michael Lee's pets
(11, 6, 'Coco',     'Dog',    'Poodle',            '2021-03-10', 'Female'),
(12, 6, 'Milo',     'Cat',    'Ragdoll',           '2022-08-25', 'Male'),
(13, 6, 'Sunny',    'Bird',   'Parrot',            '2020-11-15', 'Male'),

-- Sarah Jones's pets
(14, 7, 'Daisy',    'Dog',    'Corgi',             '2021-07-04', 'Female'),
(15, 7, 'Oliver',   'Cat',    'British Shorthair', '2020-02-14', 'Male'),
(16, 7, 'Nibbles',  'Rabbit', 'Holland Lop',       '2023-06-01', 'Female'),

-- David Wilson's pets
(17, 8, 'Bear',     'Dog',    'Husky',             '2019-09-12', 'Male'),
(18, 8, 'Nemo',     'Fish',   'Clownfish',         '2023-01-20', 'Unknown'),

-- Lisa Martin's pets
(19, 9, 'Hazel',    'Cat',    'Scottish Fold',     '2021-05-30', 'Female'),
(20, 9, 'Rex',      'Dog',    'Rottweiler',        '2018-12-08', 'Male'),
(21, 9, 'Pebble',   'Turtle', 'Red-eared Slider',  '2022-03-15', 'Unknown'),

-- Chris Taylor's pets
(22, 10, 'Biscuit', 'Dog',    'Dachshund',         '2020-06-22', 'Male'),
(23, 10, 'Luna',    'Cat',    'Abyssinian',        '2022-10-10', 'Female');

-- ── FEEDBACK ──────────────────────────────────────────────────────────────
INSERT OR IGNORE INTO feedback (id, user_id, name, email, category, support_type, rating, message, status, created_at) VALUES
(1,  1, 'John Doe',     'john.doe@example.com',    'FEEDBACK', NULL,    5, 'Great service! Very easy to manage family profiles.', 'CLOSED',   '2026-01-12 10:00'),
(2,  2, 'Jane Smith',   'jane.smith@example.com',  'FEEDBACK', NULL,    4, 'Love the pet scheduling feature. Could add calendar view.', 'OPEN', '2026-01-18 14:30'),
(3,  3, 'Robert Brown', 'robert.brown@example.com','SUPPORT',  'EMAIL', NULL, 'Having trouble logging in after password reset.', 'IN_PROGRESS', '2026-02-05 09:15'),
(4,  4, 'Emily Clark',  'emily.clark@example.com', 'SUPPORT',  'PHONE', NULL, 'Need help scheduling a pickup for multiple members.', 'OPEN', '2026-02-22 11:00'),
(5,  NULL, 'Guest User','guest@mail.com',           'FEEDBACK', NULL,    3, 'Good app but registration flow could be simpler.', 'OPEN', '2026-03-01 08:45'),
(6,  1, 'John Doe',     'john.doe@example.com',    'SUPPORT',  'CHAT',  NULL, 'App crashed when adding second pet.', 'CLOSED', '2026-03-10 16:20'),
(7,  5, 'Admin User',   'admin@example.com',        'FEEDBACK', NULL,    5, 'Dashboard analytics are very helpful!', 'CLOSED', '2026-03-15 13:00'),
(8,  2, 'Jane Smith',   'jane.smith@example.com',  'SUPPORT',  'EMAIL', NULL, 'Drop-off time not saving correctly.', 'IN_PROGRESS', '2026-03-20 10:30'),
(9,  6, 'Michael Lee',  'michael.lee@example.com', 'FEEDBACK', NULL,    4, 'Pet scheduling is smooth and intuitive. Great job!', 'CLOSED', '2026-03-06 09:00'),
(10, 7, 'Sarah Jones',  'sarah.jones@example.com', 'SUPPORT',  'CHAT',  NULL, 'Cannot find where to add a second pet for my account.', 'OPEN', '2026-03-09 14:00'),
(11, 8, 'David Wilson', 'david.wilson@example.com','FEEDBACK', NULL,    3, 'App is good but needs a dark mode option.', 'OPEN', '2026-03-13 11:30'),
(12, 9, 'Lisa Martin',  'lisa.martin@example.com', 'SUPPORT',  'PHONE', NULL, 'Payment failed twice for the same booking.', 'IN_PROGRESS', '2026-03-19 10:00'),
(13, 10,'Chris Taylor', 'chris.taylor@example.com','FEEDBACK', NULL,    5, 'Absolutely love this app! Best scheduling tool I have used.', 'CLOSED', '2026-03-23 08:15'),
(14, 6, 'Michael Lee',  'michael.lee@example.com', 'SUPPORT',  'EMAIL', NULL, 'PDF receipt for payment not generating.', 'OPEN', '2026-03-25 13:45'),
(15, 7, 'Sarah Jones',  'sarah.jones@example.com', 'FEEDBACK', NULL,    4, 'Would love push notification reminders for pick-up times.', 'OPEN', '2026-03-26 09:30'),
(16, NULL,'Guest',      'visitor@test.com',         'FEEDBACK', NULL,    2, 'Registration page is a bit confusing.', 'OPEN', '2026-03-27 07:00'),
(17, 3, 'Robert Brown', 'robert.brown@example.com','FEEDBACK', NULL,    5, 'Issue resolved quickly by support team. Very happy!', 'CLOSED', '2026-03-27 15:00'),
(18, 9, 'Lisa Martin',  'lisa.martin@example.com', 'SUPPORT',  'CHAT',  NULL, 'Need to update my email address on the account.', 'OPEN', '2026-03-28 08:00');

