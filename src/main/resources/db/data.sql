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

-- ===== 새로 추가된 도메인 샘플 데이터 =====

-- 계좌 데이터 생성
INSERT INTO accounts (user_id, balance, acorn)
VALUES 
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'::uuid, 100000000, 10), -- 관리자: 1억원, 10개 도토리
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'::uuid, 10000000, 5),   -- 사용자1: 1천만원, 5개 도토리
    ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'::uuid, 50000000, 8)    -- 사용자2: 5천만원, 8개 도토리
ON CONFLICT (user_id) DO NOTHING;

-- 주식 데이터 생성 (대표적인 한국 주식들) - 프론트엔드에서 주식 정보를 전달하므로 주석 처리
-- INSERT INTO stocks (ticker, name, current_price, per, pbr, trade_date, trade_time) VALUES
-- ('005930', '삼성전자', 75000.00, 15.2, 1.8, CURRENT_DATE, CURRENT_TIME),
-- ('000660', 'SK하이닉스', 120000.00, 12.5, 2.1, CURRENT_DATE, CURRENT_TIME),
-- ('035420', 'NAVER', 180000.00, 25.3, 3.2, CURRENT_DATE, CURRENT_TIME),
-- ('051910', 'LG화학', 450000.00, 18.7, 2.5, CURRENT_DATE, CURRENT_TIME),
-- ('006400', '삼성SDI', 380000.00, 22.1, 2.8, CURRENT_DATE, CURRENT_TIME),
-- ('035720', '카카오', 55000.00, 30.5, 4.1, CURRENT_DATE, CURRENT_TIME),
-- ('207940', '삼성바이오로직스', 850000.00, 45.2, 6.8, CURRENT_DATE, CURRENT_TIME),
-- ('068270', '셀트리온', 180000.00, 28.9, 3.7, CURRENT_DATE, CURRENT_TIME),
-- ('323410', '카카오뱅크', 28000.00, 12.8, 1.9, CURRENT_DATE, CURRENT_TIME),
-- ('373220', 'LG에너지솔루션', 420000.00, 35.6, 4.2, CURRENT_DATE, CURRENT_TIME)
-- ON CONFLICT (ticker) DO NOTHING;

-- 사용자 보유 주식 데이터 생성 - 주식 데이터가 없으므로 주석 처리
-- INSERT INTO user_stocks (user_id, stock_id, quantity, average_price)
-- SELECT 
--     'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'::uuid, -- 주식초보자
--     s.id,
--     10, -- 10주씩 보유
--     s.current_price * 0.95 -- 평균 매수가 (현재가의 95%)
-- FROM stocks s 
-- WHERE s.ticker IN ('005930', '035420', '035720') -- 삼성전자, NAVER, 카카오
-- ON CONFLICT (user_id, stock_id) DO NOTHING;

-- INSERT INTO user_stocks (user_id, stock_id, quantity, average_price)
-- SELECT 
--     'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'::uuid, -- 투자고수
--     s.id,
--     CASE 
--         WHEN s.ticker = '005930' THEN 50  -- 삼성전자 50주
--         WHEN s.ticker = '000660' THEN 30  -- SK하이닉스 30주
--         WHEN s.ticker = '051910' THEN 20  -- LG화학 20주
--         WHEN s.ticker = '207940' THEN 10  -- 삼성바이오로직스 10주
--         ELSE 15
--     END,
--     s.current_price * 0.92 -- 평균 매수가 (현재가의 92%)
-- FROM stocks s 
-- WHERE s.ticker IN ('005930', '000660', '051910', '207940', '068270')
-- ON CONFLICT (user_id, stock_id) DO NOTHING;

-- 거래 데이터 생성 (최근 거래 내역) - 주식 데이터가 없으므로 주석 처리
-- INSERT INTO trades (user_id, account_id, stock_id, price, quantity, trade_type, status, timestamp)
-- SELECT 
--     a.user_id,
--     a.id,
--     s.id,
--     s.current_price,
--     us.quantity,
--     'BUY',
--     'COMPLETED',
--     NOW() - INTERVAL '1 day' * (ROW_NUMBER() OVER () % 30) -- 최근 30일 내 거래
-- FROM accounts a
-- JOIN user_stocks us ON a.user_id = us.user_id
-- JOIN stocks s ON us.stock_id = s.id
-- WHERE a.user_id IN ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'::uuid, 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'::uuid);

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

-- 뉴스 테스트 데이터 생성
INSERT INTO news (id, summary, created_at, updated_at)
VALUES 
    ('770e8400-e29b-41d4-a716-446655440000', '삼성전자, 반도체 시장 회복세로 실적 개선 전망', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP - INTERVAL '1 day'),
    ('770e8400-e29b-41d4-a716-446655440001', 'SK하이닉스, AI 메모리 수요 증가로 매출 성장 예상', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP - INTERVAL '2 days'),
    ('770e8400-e29b-41d4-a716-446655440002', 'NAVER, AI 기술 투자 확대로 미래 성장 동력 확보', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP - INTERVAL '3 days'),
    ('770e8400-e29b-41d4-a716-446655440003', '카카오, 금융 서비스 확장으로 새로운 수익원 발굴', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP - INTERVAL '4 days'),
    ('770e8400-e29b-41d4-a716-446655440004', 'LG화학, 전기차 배터리 시장 점유율 확대 전략 발표', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP - INTERVAL '5 days')
ON CONFLICT (id) DO NOTHING;

-- ===== 통계 확인용 쿼리 =====

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
WHERE role_name = 'ROLE_ADMIN'
UNION ALL
SELECT
    'Total Stocks' as metric,
    COUNT(*) as count
FROM stocks
UNION ALL
SELECT
    'Total Accounts' as metric,
    COUNT(*) as count
FROM accounts
UNION ALL
SELECT
    'Total Trades' as metric,
    COUNT(*) as count
FROM trades;