# StockRoom SNS Backend

PostgreSQL 기반 모의 주식 거래 시스템 백엔드 프로젝트

##  주요 기능

- **OAuth2 로그인**: Google OAuth2를 통한 사용자 인증
- **JWT 인증**: JWT 토큰 기반 API 인증
- **사용자 관리**: 프로필, 커스터마이제이션, 친구 시스템
- **계좌 관리**: 잔액, 도토리 시스템
- **주식 거래**: 모의 주식 매수/매도
- **상점 시스템**: 캐릭터 뽑기 (도토리 사용)
- **커스터마이제이션**: 배경, 캐릭터 선택

##  API 버전 관리

### 현재 버전: v1
- 모든 API 엔드포인트는 `/api/v1/`로 시작
- 향후 v2, v3 버전 확장 예정

### 주요 API 엔드포인트

#### 인증
- `GET /api/v1/auth/error` - 인증 오류
- `GET /auth/success` - OAuth2 로그인 성공
- `GET /auth/token` - 토큰 정보

#### 사용자 관리
- `GET /api/v1/users/me` - 내 정보 조회
- `PATCH /api/v1/users/me` - 내 정보 수정
- `GET /api/v1/users/{userId}` - 사용자 조회
- `GET /api/v1/users/check-nickname` - 닉네임 중복 확인
- `GET /api/v1/users/search` - 사용자 검색
- `GET /api/v1/users/stocks` - 보유 주식 조회

#### 친구 시스템
- `GET /api/v1/users` - 전체 사용자 목록
- `GET /api/v1/friends` - 친구 목록 조회
- `POST /api/v1/friends` - 친구 추가

#### 계좌 관리
- `GET /api/v1/accounts` - 계좌 정보 조회
- `POST /api/v1/accounts` - 계좌 생성
- `POST /api/v1/accounts/deposit` - 입금
- `POST /api/v1/accounts/withdraw` - 출금
- `GET /api/v1/accounts/acorn` - 도토리 조회
- `POST /api/v1/accounts/acorn/add` - 도토리 증가
- `POST /api/v1/accounts/acorn/subtract` - 도토리 감소

#### 주식 거래
- `GET /api/v1/stocks` - 전체 주식 목록
- `GET /api/v1/stocks/{stockCode}` - 주식 상세 정보
- `GET /api/v1/stocks/search` - 주식 검색
- `GET /api/v1/stocks/sector/{sector}` - 섹터별 주식
- `POST /api/v1/trades` - 주식 거래
- `GET /api/v1/trades/history` - 거래 내역
- `GET /api/v1/trades/portfolio` - 포트폴리오

#### 상점 시스템
- `POST /api/v1/shop/draw` - 캐릭터 뽑기

#### 커스터마이제이션
- `GET /api/v1/user/customization` - 커스터마이제이션 조회
- `POST /api/v1/user/customization/background` - 배경 추가
- `POST /api/v1/user/customization/character` - 캐릭터 추가
- `PATCH /api/v1/user/customization/select` - 커스터마이제이션 선택

## 🛠 기술 스택

- **Java 21**
- **Spring Boot 3.2**
- **Spring Security + OAuth2**
- **Spring Data JPA**
- **PostgreSQL**
- **Gradle**
- **Swagger/OpenAPI 3.0**

## 📚 API 문서

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 🔧 개발 환경 설정

1. **Java 21 설치**
2. **PostgreSQL 설치 및 데이터베이스 생성**
3. **환경 변수 설정**
   - `DATABASE_URL`
   - `GOOGLE_CLIENT_ID`
   - `GOOGLE_CLIENT_SECRET`
   - `JWT_SECRET`

4. **애플리케이션 실행**
   ```bash
   ./gradlew bootRun
   ```

## 📝 주요 변경사항

### API 버전 통일 (v1.0.0)
- 모든 API 엔드포인트를 `/api/v1/`로 통일
- API 버전 관리 설정 클래스 추가
- Swagger 문서 업데이트
- 향후 v2, v3 확장을 고려한 구조 설계

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
