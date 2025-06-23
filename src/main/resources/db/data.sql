/*-- StockRoomSNS Initial Data
-- 개발 및 테스트용 초기 데이터
-- Created: 2025-01-20

-- 테스트 사용자 생성 (개발 환경에서만 사용)
-- 주의: 프로덕션에서는 이 데이터를 사용하지 마세요!

-- 관리자 계정
INSERT INTO users (id, google_sub, email, nickname, status)
VALUES (
           'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid,
           'google_admin_test_sub',
           'admin@stockroom-sns.com',
           'admin',
           'ACTIVE'
       ) ON CONFLICT (email) DO NOTHING;

-- 관리자 프로필
INSERT INTO user_profiles (user_id, profile_image_url, bio, total_assets, room_level)
VALUES (
           'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid,
           'https://via.placeholder.com/200x200?text=Admin',
           '스톡룸SNS 관리자입니다.',
           100000000, -- 1억원
           99
       ) ON CONFLICT (user_id) DO NOTHING;

-- 관리자 권한
INSERT INTO user_roles (user_id, role_name)
VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid, 'ROLE_USER'),
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid, 'ROLE_ADMIN')
    ON CONFLICT (user_id, role_name) DO NOTHING;

-- 테스트 일반 사용자 1
INSERT INTO users (id, google_sub, email, nickname, status)
VALUES (
           'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'::uuid,
           'google_user1_test_sub',
           'testuser1@example.com',
           '주식초보자',
           'ACTIVE'
       ) ON CONFLICT (email) DO NOTHING;

-- 테스트 사용자 1 프로필
INSERT INTO user_profiles (user_id, profile_image_url, bio, total_assets, room_level)
VALUES (
           'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'::uuid,
           'https://via.placeholder.com/200x200?text=User1',
           '주식 투자를 시작한 지 얼마 안 된 초보자입니다. 잘 부탁드려요!',
           10000000, -- 1천만원
           1
       ) ON CONFLICT (user_id) DO NOTHING;

-- 테스트 사용자 1 권한
INSERT INTO user_roles (user_id, role_name)
VALUES
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'::uuid, 'ROLE_USER')
    ON CONFLICT (user_id, role_name) DO NOTHING;

-- 테스트 일반 사용자 2
INSERT INTO users (id, google_sub, email, nickname, status)
VALUES (
           'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'::uuid,
           'google_user2_test_sub',
           'testuser2@example.com',
           '투자고수',
           'ACTIVE'
       ) ON CONFLICT (email) DO NOTHING;

-- 테스트 사용자 2 프로필
INSERT INTO user_profiles (user_id, profile_image_url, bio, total_assets, room_level)
VALUES (
           'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'::uuid,
           'https://via.placeholder.com/200x200?text=User2',
           '10년차 투자자입니다. 모의투자로 실력을 키우고 있어요.',
           50000000, -- 5천만원
           15
       ) ON CONFLICT (user_id) DO NOTHING;

-- 테스트 사용자 2 권한
INSERT INTO user_roles (user_id, role_name)
VALUES
    ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'::uuid, 'ROLE_USER'),
    ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'::uuid, 'ROLE_PREMIUM')
    ON CONFLICT (user_id, role_name) DO NOTHING;

-- 시퀀스 리셋 (필요한 경우)
-- SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));

-- 통계 확인용 쿼리 (주석 처리)

SELECT
    'Total Users' as metric,
    COUNT(*) as count
FROM users
UNION ALL
SELECT
    'Active Users' as metric,
    COUNT(*) as count
FROM users
WHERE status = 'ACTIVE'
UNION ALL
SELECT
    'Admin Users' as metric,
    COUNT(DISTINCT user_id) as count
FROM user_roles
WHERE role_name = 'ROLE_ADMIN';

-- 테스트용 사용자 데이터 (이미 있다면 무시)
INSERT INTO users (id, google_sub, email, nickname, status, created_at, updated_at)
VALUES 
    ('550e8400-e29b-41d4-a716-446655440000', 'test_google_sub_1', 'test1@example.com', 'testuser1', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440001', 'test_google_sub_2', 'test2@example.com', 'testuser2', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (google_sub) DO NOTHING;

-- 테스트용 OAuth 토큰 데이터
INSERT INTO oauth_tokens (id, user_id, provider, access_token, refresh_token, expires_at, created_at, updated_at)
VALUES 
    ('660e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440000', 'GOOGLE', 'test_access_token_1', 'test_refresh_token_1', CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 'GOOGLE', 'test_access_token_2', 'test_refresh_token_2', CURRENT_TIMESTAMP + INTERVAL '1 day', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
*/