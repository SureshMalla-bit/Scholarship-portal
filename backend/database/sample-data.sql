-- Sample scholarships (the same ones the backend adds on first start, so you normally do not need to run this).
-- Amounts follow the real schemes; deadlines are sample dates. Check the official sites before presenting them.
-- The admin account is created by the backend (admin@scholarship.com / admin123) because its password must be a BCrypt hash.

USE scholarship_db;

INSERT INTO scholarship (name, provider, level, amount, deadline, gender, categories, description) VALUES
('Central Sector Scholarship for College and University Students', 'Ministry of Education (NSP)', 'UG',
 'Rs 12,000 per year (UG), Rs 20,000 per year (PG)', '2026-11-30', 'All', 'All',
 'For students above the 80th percentile in Class 12 with low family income.'),
('AICTE Pragati Scholarship for Girls', 'AICTE', 'UG', 'Rs 50,000 per year', '2026-12-31', 'Female', 'All',
 'For girl students admitted to an AICTE-approved technical degree or diploma course.'),
('AICTE Saksham Scholarship', 'AICTE', 'UG', 'Rs 50,000 per year', '2026-12-31', 'All', 'All',
 'For specially-abled students in AICTE-approved technical courses.'),
('Post-Matric Scholarship for SC Students', 'Government of India and States', 'Any',
 'Course fees plus maintenance allowance', '2026-12-15', 'All', 'SC',
 'For SC students studying after Class 10, subject to a family income limit.'),
('Telangana ePASS Post-Matric Scholarship', 'Government of Telangana', 'Any',
 'Fee reimbursement plus maintenance allowance', '2026-12-15', 'All', 'SC,ST,BC,EBC,Minority',
 'For eligible students of Telangana from SC, ST, BC, EBC and minority communities.'),
('Prime Minister''s Scholarship Scheme', 'Ministry of Defence', 'UG',
 'Rs 2,500 per month (boys), Rs 3,000 per month (girls)', '2026-11-30', 'All', 'All',
 'For children of ex-servicemen in professional degree courses.'),
('INSPIRE Scholarship (SHE)', 'Department of Science and Technology', 'UG', 'Rs 80,000 per year', '2026-11-30', 'All', 'All',
 'For top-ranked students studying basic and natural sciences.'),
('Kotak Kanya Scholarship', 'Kotak Education Foundation', 'UG', 'Rs 1.5 lakh per year', '2027-01-31', 'Female', 'All',
 'For girl students in professional degree courses from low-income families.'),
('Reliance Foundation Undergraduate Scholarship', 'Reliance Foundation', 'UG',
 'Up to Rs 2 lakh for the full degree', '2026-12-31', 'All', 'All',
 'For first-year undergraduate students with low family income.');
