-- ============================================================================
-- CreatorConnect — Seed Data
-- Passwords below are BCrypt hashes of the plaintext "Password123!" so you
-- can log in immediately after seeding. Generated with BCryptPasswordEncoder
-- strength 12 (matches SecurityConfig).
-- ============================================================================
USE creatorconnect_db;

-- 1 admin, 3 companies, 5 creators
INSERT INTO users (email, password, full_name, phone, role, status, email_verified, created_at, updated_at) VALUES
('admin@creatorconnect.app', '$2a$12$5x1nJ4qk1p1Z6d2u1hZoUuKq0m0X1M0R2yq1WqoZ1a1F1R1a1XyD6', 'Platform Admin', '9999999999', 'ADMIN', 'ACTIVE', TRUE, NOW(), NOW()),
('brand@novabeauty.com', '$2a$12$5x1nJ4qk1p1Z6d2u1hZoUuKq0m0X1M0R2yq1WqoZ1a1F1R1a1XyD6', 'Nova Beauty Marketing Team', '9876543210', 'COMPANY', 'ACTIVE', TRUE, NOW(), NOW()),
('hello@urbanfit.co', '$2a$12$5x1nJ4qk1p1Z6d2u1hZoUuKq0m0X1M0R2yq1WqoZ1a1F1R1a1XyD6', 'UrbanFit Marketing', '9876500001', 'COMPANY', 'ACTIVE', TRUE, NOW(), NOW()),
('partnerships@byteeats.in', '$2a$12$5x1nJ4qk1p1Z6d2u1hZoUuKq0m0X1M0R2yq1WqoZ1a1F1R1a1XyD6', 'ByteEats Partnerships', '9876500002', 'COMPANY', 'ACTIVE', TRUE, NOW(), NOW()),
('ananya.creates@gmail.com', '$2a$12$5x1nJ4qk1p1Z6d2u1hZoUuKq0m0X1M0R2yq1WqoZ1a1F1R1a1XyD6', 'Ananya Rao', '9123400001', 'CREATOR', 'ACTIVE', TRUE, NOW(), NOW()),
('rahulfit.official@gmail.com', '$2a$12$5x1nJ4qk1p1Z6d2u1hZoUuKq0m0X1M0R2yq1WqoZ1a1F1R1a1XyD6', 'Rahul Mehta', '9123400002', 'CREATOR', 'ACTIVE', TRUE, NOW(), NOW()),
('thefoodiediary@gmail.com', '$2a$12$5x1nJ4qk1p1Z6d2u1hZoUuKq0m0X1M0R2yq1WqoZ1a1F1R1a1XyD6', 'Priya Nair', '9123400003', 'CREATOR', 'ACTIVE', TRUE, NOW(), NOW()),
('techwithkabir@gmail.com', '$2a$12$5x1nJ4qk1p1Z6d2u1hZoUuKq0m0X1M0R2yq1WqoZ1a1F1R1a1XyD6', 'Kabir Sethi', '9123400004', 'CREATOR', 'ACTIVE', TRUE, NOW(), NOW()),
('wanderlust.mira@gmail.com', '$2a$12$5x1nJ4qk1p1Z6d2u1hZoUuKq0m0X1M0R2yq1WqoZ1a1F1R1a1XyD6', 'Mira Kapoor', '9123400005', 'CREATOR', 'ACTIVE', TRUE, NOW(), NOW());

-- Companies (linked to the 3 company users above, ids 2,3,4)
INSERT INTO companies (user_id, company_name, logo_url, website, industry, description, location, is_verified, total_campaigns, total_spending, average_rating, created_at, updated_at) VALUES
(2, 'Nova Beauty', 'https://placehold.co/120x120?text=Nova', 'https://novabeauty.example.com', 'Beauty & Cosmetics', 'Clean beauty brand redefining skincare for Gen Z.', 'Mumbai, India', TRUE, 0, 0, 0, NOW(), NOW()),
(3, 'UrbanFit', 'https://placehold.co/120x120?text=UrbanFit', 'https://urbanfit.example.com', 'Fitness & Apparel', 'Performance activewear designed for city athletes.', 'Bengaluru, India', TRUE, 0, 0, 0, NOW(), NOW()),
(4, 'ByteEats', 'https://placehold.co/120x120?text=ByteEats', 'https://byteeats.example.com', 'Food Delivery', 'On-demand food delivery app expanding across tier-2 cities.', 'Chennai, India', FALSE, 0, 0, 0, NOW(), NOW());

-- Creators (linked to the 5 creator users above, ids 5-9)
INSERT INTO creators (user_id, profile_picture_url, bio, location, category, experience_years, follower_count, engagement_rate, instagram_handle, youtube_handle, portfolio_url, skills, is_verified, average_rating, completed_campaigns_count, success_rate, total_earnings, profile_completion_percent, created_at, updated_at) VALUES
(5, 'https://placehold.co/200x200?text=Ananya', 'Skincare & beauty content creator. Honest reviews, no filters.', 'Mumbai, India', 'Beauty', 4, 128000, 5.4, '@ananya.creates', NULL, 'https://ananyacreates.example.com', 'Photography, Video editing, Copywriting', TRUE, 4.8, 12, 92.3, 145000.00, 95, NOW(), NOW()),
(6, 'https://placehold.co/200x200?text=Rahul', 'Certified personal trainer sharing fitness journeys that stick.', 'Bengaluru, India', 'Fitness', 3, 84000, 6.1, '@rahulfit.official', '@RahulFitTV', 'https://rahulfit.example.com', 'Reels, Coaching, Nutrition planning', TRUE, 4.6, 8, 88.0, 96000.00, 90, NOW(), NOW()),
(7, 'https://placehold.co/200x200?text=Priya', 'Chennai-based food blogger. Street food to fine dining.', 'Chennai, India', 'Food', 5, 210000, 4.8, '@thefoodiediary', '@FoodieDiaryTV', 'https://foodiediary.example.com', 'Food styling, Storytelling, Reels', TRUE, 4.9, 20, 95.0, 310000.00, 100, NOW(), NOW()),
(8, 'https://placehold.co/200x200?text=Kabir', 'Tech reviews and gadget unboxings for the everyday buyer.', 'Delhi, India', 'Technology', 2, 56000, 3.9, NULL, '@TechWithKabir', 'https://techwithkabir.example.com', 'Scripting, Editing, Product reviews', FALSE, 4.2, 3, 75.0, 24000.00, 70, NOW(), NOW()),
(9, 'https://placehold.co/200x200?text=Mira', 'Travel storyteller documenting hidden gems across India.', 'Goa, India', 'Travel', 6, 175000, 5.9, '@wanderlust.mira', '@WanderlustMira', 'https://wanderlustmira.example.com', 'Cinematography, Drone footage, Blogging', TRUE, 4.7, 15, 90.0, 220000.00, 98, NOW(), NOW());

-- Campaigns
INSERT INTO campaigns (company_id, title, description, budget, min_followers, preferred_platform, category, deadline, banner_url, guidelines, deliverables, creators_required, status, view_count, created_at, updated_at) VALUES
(1, 'Glow Serum Launch — Reels Campaign', 'Introduce our new Vitamin C glow serum to your audience with an authentic before/after reel.', 15000.00, 20000, 'INSTAGRAM', 'Beauty', DATE_ADD(CURDATE(), INTERVAL 21 DAY), 'https://placehold.co/800x400?text=Glow+Serum', 'Tag @novabeauty, use #GlowWithNova, no third-party filters.', '1 Reel (30-60s) + 2 Story frames', 5, 'OPEN', 342, NOW(), NOW()),
(2, 'Marathon Training Gear — YouTube Review', 'Full training-cycle review of our new running shoe line for an upcoming city marathon.', 25000.00, 30000, 'YOUTUBE', 'Fitness', DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'https://placehold.co/800x400?text=UrbanFit+Shoes', 'Minimum 8-minute honest review video, include performance metrics.', '1 YouTube video + 1 Instagram carousel', 3, 'OPEN', 218, NOW(), NOW()),
(3, 'App Launch — Tier-2 City Push', 'Promote our food delivery app launch in your city with a fun, relatable ordering-experience video.', 8000.00, 15000, 'INSTAGRAM', 'Food', DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'https://placehold.co/800x400?text=ByteEats', 'Show the full ordering + delivery experience, keep it under 60s.', '1 Reel + 1 Story series', 8, 'OPEN', 501, NOW(), NOW()),
(1, 'Sunscreen Summer Edit', 'Summer skincare routine featuring our SPF 50 sunscreen as the hero product.', 10000.00, 10000, 'INSTAGRAM', 'Beauty', DATE_ADD(CURDATE(), INTERVAL 45 DAY), 'https://placehold.co/800x400?text=Sunscreen', 'Natural lighting, outdoor B-roll preferred.', '1 Reel', 4, 'OPEN', 97, NOW(), NOW());

-- Applications
INSERT INTO applications (campaign_id, creator_id, message, portfolio_link, expected_timeline, status, created_at, updated_at) VALUES
(1, 1, 'I love clean beauty brands — would be a natural fit for my audience!', 'https://ananyacreates.example.com/portfolio', '5 days after product delivery', 'ACCEPTED', NOW(), NOW()),
(1, 3, 'My audience skews beauty-adjacent (food + lifestyle) — happy to cross-promote.', 'https://foodiediary.example.com/portfolio', '1 week', 'PENDING', NOW(), NOW()),
(2, 2, 'I am training for the same marathon this quarter — authentic fit guaranteed.', 'https://rahulfit.example.com/portfolio', '3 weeks (full training cycle)', 'ACCEPTED', NOW(), NOW()),
(3, 5, 'Frequent traveller in tier-2 cities, can showcase the app during trips.', 'https://wanderlustmira.example.com/portfolio', '10 days', 'SHORTLISTED', NOW(), NOW());

-- Content submission for the accepted Nova Beauty application
INSERT INTO content_submissions (application_id, instagram_reel_link, comments, submission_date, status, created_at, updated_at) VALUES
(1, 'https://instagram.com/reel/example-glow-serum', 'Shot in natural light, added before/after split-screen as discussed.', NOW(), 'APPROVED', NOW(), NOW());

-- Payment for the completed/approved submission
INSERT INTO payments (application_id, creator_id, company_id, amount, status, invoice_number, paid_at, created_at, updated_at) VALUES
(1, 1, 1, 15000.00, 'PAID', 'CC-2026-000101', NOW(), NOW(), NOW());
