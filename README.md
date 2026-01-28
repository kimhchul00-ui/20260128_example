# Spring Boot Shopping Mall

Spring Boot + H2 + Thymeleaf 기반의 쇼핑몰 시스템입니다.

## 기술 스택

- Java 21
- Spring Boot 3.2.2
- Spring Security
- Spring Data JPA
- H2 Database
- Thymeleaf
- Bootstrap 5

## 프로젝트 구조

```
src/main/java/com/example/shop/
├── ShopApplication.java
├── config/                     # 설정 클래스
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   ├── DataInitializer.java
│   └── security/
├── controller/                 # 공통 컨트롤러
├── domain/                     # 도메인별 패키지
│   ├── member/                 # 회원 도메인
│   ├── product/                # 상품 도메인
│   ├── cart/                   # 장바구니 도메인
│   ├── order/                  # 주문 도메인
│   └── delivery/               # 배송 도메인
└── admin/                      # 관리자 기능
    ├── member/
    ├── product/
    ├── order/
    └── delivery/
```

## 기능

### 고객 서비스 (프론트)
- 회원가입, 로그인, 회원정보 관리
- 상품 목록, 검색, 상세 보기
- 장바구니 담기, 수량 변경, 삭제
- 주문서 작성, 결제수단 선택, 주문 생성
- 주문 내역 조회, 주문 취소

### 관리자 (백오피스)
- 대시보드 (통계)
- 회원 관리 (조회, 수정)
- 상품 관리 (등록, 수정, 삭제)
- 주문 관리 (조회, 상태 변경)
- 배송 관리 (조회, 상태 변경)

## 실행 방법

### 로컬 환경

```bash
# Maven Wrapper 사용
./mvnw spring-boot:run

# 또는 Maven 직접 사용
mvn spring-boot:run
```

### 접속 정보

- 애플리케이션: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:shopdb`
  - Username: `sa`
  - Password: (빈값)

### 테스트 계정

| 역할 | 이메일 | 비밀번호 |
|------|--------|----------|
| 관리자 | admin@shop.com | admin123 |
| 사용자 | user1@example.com | 1234 |
| 사용자 | user2@example.com | 1234 |
| 사용자 | user3@example.com | 1234 |

## 환경별 설정

- `local`: 인메모리 H2 DB, 샘플 데이터 자동 생성
- `dev`: 파일 기반 H2 DB
- `prod`: 외부 DB 연결 (환경변수로 설정)

### 프로파일 변경

```bash
# dev 환경으로 실행
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## 결제수단

주문 시 다음 결제수단 중 선택 가능 (기본값: 랜덤 선택):
- 신용카드
- 계좌이체
- 가상계좌
- 카카오페이
- 네이버페이

## 라이선스

MIT License
