# Claude Backend

실시간 한국투자증권 API 연동 기반 주식 거래 및 소셜 기능을 제공하는 Spring Boot 백엔드 애플리케이션입니다.

## 🚀 주요 기능

- **Google OAuth2 로그인**: JWT 토큰 기반 인증
- **실시간 주식 데이터**: 한국투자증권 API 연동
- **주식 거래**: 매수/매도 기능
- **사용자 커스터마이징**: 캐릭터, 배경 변경
- **친구 시스템**: 사용자 간 친구 추가/삭제
- **상점 시스템**: 도토리로 아이템 구매
- **실시간 채팅**: WebSocket + STOMP + Redis Pub/Sub 기반 실시간 채팅

## 🛠 기술 스택

- **Framework**: Spring Boot 3.2.x
- **Language**: Java 17
- **Database**: H2 (개발), MySQL (운영)
- **Security**: Spring Security, OAuth2, JWT
- **API Documentation**: Swagger/OpenAPI 3
- **Build Tool**: Gradle
- **Cloud**: AWS S3 (파일 저장)
- **WebSocket**: Spring WebSocket + STOMP
- **Redis**: Pub/Sub

## 📋 API 문서

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## 🔐 인증 시스템

### OAuth2 로그인 플로우

1. **로그인 시작**: `GET /oauth2/authorization/google`
2. **Google OAuth2 인증**: Google 로그인 페이지로 리디렉션
3. **인증 성공**: 프론트엔드로 리디렉션하면서 토큰 전달
   ```
   http://15.164.70.242/oauth-success?status=success&access_token=xxx&refresh_token=yyy&user_id=zzz
   ```

### 토큰 관리

- **Access Token**: 7일 유효 (Authorization 헤더 사용)
- **Refresh Token**: 21일 유효 (토큰 갱신용)

### API 인증

```bash
# Authorization 헤더에 Bearer 토큰 포함
curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     http://localhost:8080/api/v1/users/me
```

### 토큰 갱신

```bash
# Refresh Token으로 새로운 Access Token 발급
curl -X POST "http://localhost:8080/api/v1/auth/refresh" \
     -d "refreshToken=YOUR_REFRESH_TOKEN"
```

## 🏃‍♂️ 실행 방법

### 1. 환경 설정

```bash
# application-local.yml 설정
spring:
  profiles:
    active: local
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
```

### 2. 애플리케이션 실행

```bash
# Gradle로 실행
./gradlew bootRun

# 또는 JAR 파일로 실행
./gradlew build
java -jar build/libs/claude-backend-0.0.1-SNAPSHOT.jar
```

### 3. 테스트 페이지 접속

- **테스트 페이지**: `http://localhost:8080/`
- **H2 콘솔**: `http://localhost:8080/h2-console`

## 📊 주요 API 엔드포인트

### 인증 관련
- `GET /oauth2/authorization/google` - Google OAuth2 로그인
- `POST /api/v1/auth/refresh` - 토큰 갱신
- `POST /api/v1/auth/verify` - 토큰 검증

### 사용자 관련
- `GET /api/v1/users/me` - 현재 사용자 정보
- `PUT /api/v1/users/{id}` - 사용자 정보 수정
- `GET /api/v1/users/search` - 사용자 검색

### 주식 관련
- `GET /api/v1/stocks` - 주식 목록 조회
- `GET /api/v1/stocks/{code}` - 주식 상세 정보
- `POST /api/v1/trades` - 주식 거래 (매수/매도)
- `GET /api/v1/trades/portfolio` - 포트폴리오 조회

### 상점 관련
- `POST /api/v1/shop/draw` - 뽑기 (캐릭터/배경)
- `GET /api/v1/shop/characters` - 보유 캐릭터 목록
- `GET /api/v1/shop/backgrounds` - 보유 배경 목록

## 🔧 개발 환경 설정

### 필수 요구사항
- Java 17+
- Gradle 8.0+

### IDE 설정
- IntelliJ IDEA 또는 Eclipse
- Spring Boot DevTools 활성화
- H2 Database 플러그인 (선택사항)

### 로그 확인
```bash
# 애플리케이션 로그
tail -f logs/claude-backend.log

# H2 데이터베이스 로그
tail -f logs/h2.log
```

## 🧪 테스트

### 단위 테스트
```bash
./gradlew test
```

### 통합 테스트
```bash
./gradlew integrationTest
```

### API 테스트
1. 테스트 페이지 접속: `http://localhost:8080/`
2. Google OAuth2 로그인 테스트
3. 토큰 갱신 테스트
4. API 호출 테스트

## 📁 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── com/example/claude_backend/
│   │       ├── application/     # 애플리케이션 서비스
│   │       ├── domain/          # 도메인 모델
│   │       ├── infrastructure/  # 인프라스트럭처
│   │       └── presentation/    # 프레젠테이션 계층
│   └── resources/
│       ├── application.yml      # 설정 파일
│       └── static/              # 정적 리소스
└── test/                        # 테스트 코드
```

## 🔒 보안 설정

### CORS 설정
- 모든 Origin 허용 (개발 환경)
- Authorization 헤더 허용
- Credentials 허용

### JWT 설정
- 토큰 시크릿: 환경변수로 관리
- Access Token: 7일
- Refresh Token: 21일

## 🚀 배포

### Docker 배포
```bash
# Docker 이미지 빌드
docker build -t claude-backend .

# 컨테이너 실행
docker run -p 8080:8080 claude-backend
```

### AWS 배포
1. EC2 인스턴스 생성
2. Java 17 설치
3. 애플리케이션 배포
4. Nginx 리버스 프록시 설정

## 📝 변경 이력

### v1.0.0 (2025-01-20)
- ✅ Google OAuth2 로그인 구현
- ✅ JWT 토큰 기반 인증
- ✅ 실시간 주식 데이터 연동
- ✅ 사용자 커스터마이징 기능
- ✅ 친구 시스템
- ✅ 상점 시스템
- ✅ 실시간 채팅 기능

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 문의

- **이메일**: support@claude-backend.com
- **이슈**: GitHub Issues
- **문서**: [Wiki](https://github.com/your-repo/wiki)

---

**Claude Backend** - 실시간 주식 거래 플랫폼의 백엔드 시스템
