# Spring Security REST API 테스트 가이드

이 문서는 IntelliJ IDEA의 HTTP Client 기능을 사용하여 Spring Security REST API를 테스트하는 방법에 대한 가이드입니다.

## IntelliJ HTTP Client란?

IntelliJ IDEA에는 HTTP 요청을 쉽게 만들고 실행할 수 있는 강력한 HTTP Client 기능이 내장되어 있습니다. 이 기능을 사용하면 Postman과 같은 별도의 도구 없이도 API를 테스트할 수 있습니다.

## 테스트 파일 구조

`/api` 폴더에는 다음과 같은 테스트 파일들이 있습니다:

1. `1_auth.http` - 인증 관련 API 테스트
2. `2_user.http` - 사용자 관련 API 테스트
3. `3_admin.http` - 관리자 권한이 필요한 API 테스트
4. `4_scenarios.http` - 전체 시나리오 테스트

## 사용 방법

### 기본 사용법

1. `.http` 파일을 엽니다.
2. 테스트하려는 요청 옆의 실행 버튼(▶️)을 클릭합니다.
3. 요청이 실행되고 오른쪽 패널에 응답이 표시됩니다.

![HTTP Client 실행 예시](/api/images/run-http-request.png)

### 환경 설정

API 호스트, 포트 및 토큰과 같은 공통 변수를 설정하려면:

1. 각 파일 상단에 있는 환경 변수를 확인하세요:
```
### 환경 변수
@host = http://localhost
@port = 8080
@baseUrl = {{host}}:{{port}}
```

2. 이 변수들은 요청 URL에서 참조할 수 있습니다:
```
GET {{baseUrl}}/api/me
Authorization: Bearer {{token}}
```

### 응답에서 값 추출하기

API 응답에서 값을 추출하여 다음 요청에서 사용할 수 있습니다. 로그인 응답에서 토큰을 추출하는 예시:

```
### 로그인 요청
POST {{baseUrl}}/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}

> {%
    client.global.set("token", response.body.token);
%}
```

이 코드는 응답 본문에서 토큰을 추출하여 `token`이라는 전역 변수에 저장합니다. 이후 요청에서 이 변수를 사용할 수 있습니다.

### 테스트 시나리오 실행

1. 애플리케이션이 실행 중인지 확인합니다.
2. `4_scenarios.http` 파일을 열고 맨 위에 있는 전체 테스트 시나리오 옆의 실행 버튼을 클릭합니다.
3. 모든 요청이 순차적으로 실행되고 각 응답이 표시됩니다.

## 개별 테스트 파일 설명

### 1. 인증 테스트 (`1_auth.http`)

- **로그인**: 사용자 자격 증명으로 JWT 토큰 얻기
- **잘못된 로그인**: 잘못된 자격 증명으로 로그인 시도
- **토큰 정보 확인**: 현재 인증된 사용자 정보 조회

### 2. 사용자 API 테스트 (`2_user.http`)

- **현재 사용자 정보**: 현재 로그인한 사용자의 정보 조회
- **특정 사용자 조회**: username으로 특정 사용자 정보 조회
- **인증 없이 접근**: 토큰 없이 보호된 API 접근 시도

### 3. 관리자 API 테스트 (`3_admin.http`)

- **모든 사용자 목록**: 관리자 권한으로 모든 사용자 목록 조회
- **관리자 대시보드**: 관리자 대시보드 데이터 조회
- **일반 사용자로 관리자 API 접근**: 일반 사용자 권한으로 관리자 API 접근 시도

### 4. 전체 시나리오 테스트 (`4_scenarios.http`)

1. 관리자 로그인
2. 관리자 정보 확인
3. 모든 사용자 목록 조회
4. 로그아웃(토큰 제거)
5. 일반 사용자 로그인
6. 일반 사용자 정보 확인
7. 관리자 API 접근 시도(실패 예상)

## 팁 & 트릭

1. **응답 비교**: IntelliJ는 이전 응답과 현재 응답을 비교할 수 있는 기능을 제공합니다.
2. **변수 보기**: 우측 HTTP Client 탭에서 모든 설정된 변수를 확인할 수 있습니다.
3. **요청 히스토리**: 이전에 실행한 요청들의 히스토리를 볼 수 있습니다.
4. **응답 형식화**: JSON 응답은 자동으로 형식화되어 표시됩니다.
5. **환경 전환**: 여러 환경(개발, 테스트, 프로덕션)을 설정하고 쉽게 전환할 수 있습니다.

## 문제 해결

1. **401 Unauthorized**: 토큰이 만료되었거나 유효하지 않습니다. 다시 로그인하세요.
2. **403 Forbidden**: 요청한 리소스에 대한 권한이 없습니다. 관리자 권한이 필요할 수 있습니다.
3. **404 Not Found**: URL이 올바른지 확인하세요.
4. **500 Server Error**: 서버 로그를 확인하여 오류 원인을 파악하세요.

---

자세한 HTTP Client 사용법은 [IntelliJ IDEA HTTP Client 공식 문서](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html)를 참조하세요.
