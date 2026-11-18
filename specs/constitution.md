# Hello Eva 백엔드 원칙 (Constitution)

모든 기능 스펙(`specs/NNN-*/`)과 구현은 이 문서를 따릅니다. 원칙을 바꾸려면 이 문서를 먼저 수정합니다.

## 1. 스펙이 기준이다

- 코드보다 스펙을 먼저 씁니다. 순서: `spec.md` → `plan.md` (+ `api/openapi.yaml`) → `tasks.md` → 구현.
- `spec.md`는 **무엇을, 왜** 만드는지만 씁니다. 기술 용어(테이블, 클래스명)는 `plan.md`에 씁니다.
- 구현 중 요구사항이 바뀌면 **스펙을 먼저 고치고** 코드를 고칩니다. 스펙과 코드가 다르면 스펙이 맞는 것으로 봅니다.
- 스펙의 상태는 `Draft → Approved → Implemented` 로 관리합니다. `Approved` 전에는 구현하지 않습니다.
  - `Draft`: 작성 중 / `Approved`: 기획·프론트 리뷰 완료, plan·구현 가능 / `Implemented`: 구현과 테스트 완료

### 문서 구성

| 경로 | 내용 |
|---|---|
| `specs/constitution.md` | 이 문서. 모든 기능에 적용되는 원칙 |
| `specs/000-overview/` | 제품 개요(`product.md`), 용어집(`glossary.md`), 기능 현황판(`features.md`) |
| `specs/NNN-기능명/` | 기능별 `spec.md` → `plan.md` → `tasks.md` |
| `specs/_templates/` | 새 기능 문서 템플릿 |
| `api/` | API 작성 규칙(`README.md`)과 계약서(`openapi.yaml`) |

## 2. API 계약

- 프론트(모바일)와 공유하는 API 명세는 **`api/openapi.yaml`** 하나입니다. 백엔드가 직접 작성해 공유합니다. 작성 규칙은 [api/README.md](../api/README.md)를 따릅니다.
- Swagger UI(springdoc)는 구현 확인용입니다. 계약과 다르면 구현을 고칩니다.
- 경로에 `/api` 접두사를 붙이지 않습니다. 리소스는 복수형 명사를 씁니다 (`/conversations`, `/users/me`).
- 모든 에러 응답 형식은 `{ status, code, message, errors? }` 입니다. `code`는 `ErrorCode` enum 이름과 같습니다.
- 응답 필드는 camelCase, 날짜/시간은 ISO-8601 (`2026-11-18T10:00:00+09:00`) 입니다.

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

- Service는 흐름만 조합하고, 실제 조회/변경은 `XxxReader`, `XxxCreator`, `XxxUpdater`, `XxxDeleter`에 둡니다.
- 도메인 규칙(상태 전이, 검증)은 엔티티 메서드에 둡니다.
- 외부 서비스(AI, Kakao, 스토리지)는 `integration/`의 인터페이스 뒤에 숨깁니다. 도메인은 구현체를 모릅니다.
- AI 작업(질문 생성, 음성 인식·합성, 평가)도 요청 안에서 동기로 처리합니다. 상태 관리·비동기 처리는 두지 않습니다 (전공 프로젝트 규모).

## 4. 데이터

- 스키마 변경은 **Flyway 마이그레이션으로만** 합니다 (`ddl-auto: validate`). 파일명: `V{n}__{설명}.sql`.
- 회원 탈퇴는 사용자 상태만 '탈퇴'로 바꾸고 데이터는 남깁니다 (001). 그 밖의 삭제 정책은 해당 기능 plan.md에 씁니다.

## 5. 테스트

- 스펙의 **인수 조건(AC) 하나당 테스트 하나 이상**을 만듭니다. 테스트 이름에 AC 번호를 적습니다. 예: ``fun `AC-3 탈퇴한 사용자는 로그인 시 새 계정이 생성된다`()``
- 단위 테스트: `src/test/kotlin/com/helloeva/unit/...` (Docker 불필요)
- 통합 테스트: `src/test/kotlin/com/helloeva/integration/...` (Testcontainers로 MySQL/Redis 실행)
- 외부 API(AI, Kakao)는 테스트에서 가짜 구현체로 바꿉니다.
- main에 커밋하기 전에 로컬에서 전체 테스트를 통과시킵니다. main에 push되면 CI가 테스트를 다시 실행합니다.

## 형상 관리

- PR과 이슈는 쓰지 않고 **main에 바로 커밋**합니다.
- 커밋 메시지는 `<type>: <요약>` + 개조식 본문입니다 (`feat`/`fix`/`docs`/`refactor`/`test`/`chore` 등 Conventional Commits 기본 타입, 문서 변경은 `docs`).
- 커밋에 AI 작성 표시(`Co-Authored-By` 등)를 붙이지 않습니다.

## 6. 보안과 설정

- 비밀 값(키, 비밀번호, 토큰)은 **절대 커밋하지 않습니다**. 설정 파일에는 `${ENV_VAR}` 자리표시자만 둡니다.
- 필요한 환경변수는 `.env.example`에 이름만 적습니다.
- 인증은 JWT 액세스 토큰 하나만 씁니다 (유효 기간 30일, 리프레시 토큰 없음). 컨트롤러에서는 `@LoginUser userId: Long`으로 사용자를 받습니다.

## 7. 기술 스택 (확정)

2026-11 기준 최신 안정 버전입니다. 버전은 Gradle version catalog(`gradle/libs.versions.toml`)에서 한곳으로 관리합니다.

| 영역 | 선택 | 버전 |
|---|---|---|
| 언어/런타임 | Kotlin, JDK | Kotlin 2.3.x, JDK 25 (LTS) |
| 프레임워크 | Spring Boot (Spring Framework 7, Jackson 3) | 4.1.x |
| 데이터 | MySQL, Redis, Spring Data JPA, Flyway | MySQL 8.4, Redis 7.4 |
| 인증 | Spring Security + JWT (jjwt), Kakao 로그인 | jjwt 0.13.x |
| 문서 | `api/openapi.yaml` (계약), springdoc (Swagger UI) | springdoc 3.x |
| 테스트 | JUnit 5, Testcontainers | Boot 관리 버전 |
| 빌드/CI | Gradle (version catalog), GitHub Actions | Gradle 9.x |

- Boot 4는 Jackson 3를 씁니다. Jackson 패키지가 `com.fasterxml.jackson` → `tools.jackson`으로 바뀌었으니, 예전 Boot 3 코드나 예제를 그대로 가져오지 않습니다 (애노테이션 `com.fasterxml.jackson.annotation.*`은 그대로).
- 로컬 Docker 이미지 태그는 `latest`를 쓰지 않고 위 버전으로 고정합니다.
- AI(음성 인식·음성 합성·평가), 파일 저장소, 푸시 알림 등은 해당 기능의 plan.md에서 정하고 이 표에 추가합니다.
