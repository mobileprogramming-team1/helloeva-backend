# Hello Eva 백엔드 개발 규칙

코드를 쓸 때 따르는 규칙입니다. 기능 요구사항은 `docs/features/`, API는 `api/openapi.yaml`에 있습니다.

## 1. 기준 문서

| 문서 | 내용 |
|---|---|
| `docs/product.md` | 제품 개요, 화면 흐름, 기능 목록 |
| `docs/glossary.md` | 용어집. 코드·API 이름도 이 표를 따릅니다 |
| `docs/features/NNN-*.md` | 기능별 요구사항(FR)과 인수 조건(AC) |
| `api/openapi.yaml` | 프론트와 공유하는 API 명세. 작성 규칙은 `api/README.md` |

- 구현이 `api/openapi.yaml`과 다르면 구현을 고칩니다. 명세를 바꿔야 하면 명세를 먼저 고치고 프론트에 알립니다.
- 요구사항이 바뀌면 해당 `docs/features/` 문서도 함께 고칩니다.

## 2. API

- 경로에 `/api` 접두사를 붙이지 않습니다. 리소스는 복수형 명사를 씁니다 (`/conversations`, `/users/me`).
- 에러 응답 형식은 `{ status, code, message, errors? }` 입니다. `code`는 `ErrorCode` enum 이름과 같고, `api/openapi.yaml`의 `ErrorCode` 목록과 일치시킵니다.
- 응답 필드는 camelCase, 날짜·시간은 ISO-8601 (`2026-11-18T10:00:00+09:00`) 입니다.
- 엔드포인트마다 전용 Request/Response DTO를 둡니다. 다른 엔드포인트의 응답 DTO를 재사용하지 않습니다.

## 3. 아키텍처

```
com.helloeva
├── common/        # 공통 관심사 (config, exception, security, log, domain)
├── domain/        # 비즈니스 도메인. 도메인마다 아래 4개 레이어
│   └── <domain>/
│       ├── presentation/    # Controller, Request/Response DTO
│       ├── application/     # Service, Reader/Creator/Updater/Deleter, Command
│       ├── domain/          # Entity, Enum, 도메인 규칙
│       └── infrastructure/  # Repository
└── integration/   # 외부 연동 (oauth, ai, storage ...). 인터페이스 + 구현체
```

- Service는 흐름만 조합하고, 실제 조회·변경은 `XxxReader`, `XxxCreator`, `XxxUpdater`, `XxxDeleter`에 둡니다.
- 도메인 규칙(상태 전이, 검증, 소유자 확인)은 엔티티 메서드에 둡니다.
- 외부 서비스(AI, Kakao, 스토리지)는 `integration/`의 인터페이스 뒤에 숨깁니다. 도메인은 구현체를 모릅니다.
- AI 작업(질문 생성, 음성 인식·합성, 평가)은 요청 안에서 동기로 처리합니다. 상태 관리·비동기 처리·폴링은 두지 않습니다 (전공 프로젝트 규모).
- 외부 API 호출은 DB 트랜잭션 밖에서 하고, 결과 저장만 짧은 트랜잭션으로 묶습니다.

## 4. 데이터

- 스키마 변경은 Flyway 마이그레이션으로만 합니다 (`ddl-auto: validate`). 파일명: `V{n}__{설명}.sql`.
- 회원 탈퇴는 사용자 상태만 '탈퇴'로 바꾸고 데이터는 남깁니다 (001).

## 5. 테스트

- 기능 문서의 인수 조건(AC)마다 테스트를 하나 이상 만들고, 테스트 이름에 AC 번호를 적습니다. 예: ``fun `AC-7 탈퇴한 사용자는 로그인 시 새 계정이 생성된다`()``
- 단위 테스트: `src/test/kotlin/com/helloeva/unit/...` (Docker 불필요)
- 통합 테스트: `src/test/kotlin/com/helloeva/integration/...` (Testcontainers로 MySQL 실행)
- 외부 API(AI, Kakao)는 테스트에서 가짜 구현체(`Fake*`, `src/test`)로 바꿉니다.
- main에 커밋하기 전에 로컬에서 전체 테스트를 통과시킵니다.

## 6. 형상 관리

- PR과 이슈는 쓰지 않고 main에 바로 커밋합니다.
- 커밋 메시지는 `<type>: <요약>` + 개조식 본문입니다 (Conventional Commits 기본 타입).
- 커밋에 AI 작성 표시(`Co-Authored-By` 등)를 붙이지 않습니다.

## 7. 보안과 설정

- 비밀 값(키, 비밀번호, 토큰)은 절대 커밋하지 않습니다. 설정 파일에는 `${ENV_VAR}` 자리표시자만 둡니다.
- 필요한 환경변수는 `.env.example`에 이름만 적습니다.
- 인증은 JWT 액세스 토큰 하나만 씁니다 (유효 기간 30일, 리프레시 토큰 없음). 서버에 토큰을 저장하지 않습니다. 컨트롤러에서는 `@LoginUser userId: Long`으로 사용자를 받습니다.

## 8. 기술 스택

버전은 Gradle version catalog(`gradle/libs.versions.toml`)에서 한곳으로 관리합니다.

| 영역 | 선택 | 버전 |
|---|---|---|
| 언어/런타임 | Kotlin, JDK | Kotlin 2.3.x, JDK 25 (LTS) |
| 프레임워크 | Spring Boot (Spring Framework 7, Jackson 3) | 4.1.x |
| 데이터 | MySQL, Spring Data JPA, Flyway | MySQL 8.4 |
| 인증 | Spring Security + JWT (jjwt), Kakao 로그인 | jjwt 0.13.x |
| 문서 | `api/openapi.yaml` (명세), springdoc (Swagger UI) | springdoc 3.x |
| 테스트 | JUnit 5, Testcontainers | Boot 관리 버전 |
| 빌드/CI | Gradle (version catalog), GitHub Actions | Gradle 9.x |

- Redis는 쓰지 않습니다.
- Boot 4는 Jackson 3를 씁니다. Jackson 패키지가 `com.fasterxml.jackson` → `tools.jackson`으로 바뀌었으니, 예전 Boot 3 코드나 예제를 그대로 가져오지 않습니다 (애노테이션 `com.fasterxml.jackson.annotation.*`은 그대로).
- 로컬 Docker 이미지 태그는 `latest`를 쓰지 않고 위 버전으로 고정합니다.
- AI(음성 인식·음성 합성·평가), 파일 저장소 제공자는 정해지면 이 표에 추가합니다.
