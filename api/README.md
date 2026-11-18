# API 명세

API 명세는 **[openapi.yaml](./openapi.yaml) 하나**입니다. 백엔드가 작성해 프론트(모바일)에 공유하고, 코드보다 먼저 바꿉니다.

## 작성과 공유 흐름

```
피그마 화면·스펙 → openapi.yaml 작성 → main 반영 → 문서 링크 공유
                → (선택) 프론트는 Mock 서버로 먼저 개발 → 백엔드 구현
```

| 단계 | 방법 |
|---|---|
| 작성 | 백엔드가 `openapi.yaml`을 작성·수정합니다 |
| 보기 | 웹 문서 링크 *(공유 방법 설정 예정)*. 그 전에는 [editor.swagger.io](https://editor.swagger.io)에 파일을 붙여넣어 봅니다 |
| 먼저 개발 | `npx @stoplight/prism-cli mock api/openapi.yaml` → example 값을 돌려주는 가짜 서버 |
| 구현 확인 | 서버의 Swagger UI (`/swagger-ui.html`). 명세와 다르면 구현을 고칩니다 |
| 변경 알림 | 명세가 바뀌면 팀 채널에 바뀐 엔드포인트와 Breaking Change 여부를 공유 |

## 작성 규칙

### 경로
- 복수형 명사, kebab-case: `/conversations/{conversationId}/questions/{questionNumber}/answer`
- `/api`, `/v1` 접두사 없음
- 내 리소스는 `/users/me/...`

### 인증
- `Authorization: Bearer {accessToken}`
- 토큰 재발급만 `Refresh-Token: {refreshToken}` 헤더 사용
- 인증이 필요 없는 API는 `security: []`로 표시

### 요청·응답
- 필드 이름은 camelCase
- 날짜·시간은 ISO-8601 문자열 (`2026-11-18T10:00:00+09:00`)
- enum은 대문자 상수 (`IN_PROGRESS`, `IM3`). 값 목록은 [용어집](../specs/000-overview/glossary.md)과 맞춥니다
- 비어 있을 수 있는 필드는 `type: [string, 'null']`처럼 명시합니다 (OpenAPI 3.1 문법). 목록은 null 대신 `[]`
- 모든 스키마에 `example`을 적습니다 (Mock 서버와 문서에 그대로 쓰입니다)

### 상태 코드
| 코드 | 언제 |
|---|---|
| 200 | 조회·변경 성공 (본문 있음) |
| 201 | 생성 성공 |
| 204 | 성공, 본문 없음 (로그아웃 등) |
| 400 | 입력값 오류 |
| 401 | 인증 없음·만료 |
| 403 | 남의 리소스 |
| 404 | 없음 |
| 409 | 현재 상태에서 할 수 없음 (이미 답한 문항 등) |

### 에러 응답
모든 에러는 같은 모양입니다. `code`로 분기하고 `message`는 화면에 보여줄 수 있는 한국어입니다.

```json
{
  "status": 400,
  "code": "INVALID_INPUT_VALUE",
  "message": "적절하지 않은 값입니다.",
  "errors": [{ "field": "targetGrade", "message": "필수 값입니다." }]
}
```

에러 코드를 추가하면 `openapi.yaml`의 `ErrorCode` 목록도 함께 고칩니다.

### 목록
- 커서 기반: 요청 `?cursor=&size=20`, 응답 `{ items, nextCursor, hasNext, totalCount }` *(003에서 확정)*

### 오래 걸리는 작업 (AI 피드백 등)
- 요청은 바로 응답하고, 결과 리소스에 `status: PENDING | COMPLETED | FAILED`를 둡니다
- 앱은 `COMPLETED`/`FAILED`가 될 때까지 조회 API를 폴링합니다 (권장 간격 2초)

### 파일 (음성)
- 업로드 방식은 004 plan에서 확정합니다 (multipart vs Presigned URL)
- 응답에는 재생 가능한 URL을 줍니다. 만료가 있다면 `expiresAt`을 함께 줍니다

### 호환성

| 변경 | 판단 |
|---|---|
| 엔드포인트·선택 필드·enum 값 추가 | 안전 |
| 필수 요청 필드 추가 | Breaking |
| 필드 삭제·이름 변경·타입 변경 | Breaking |
| enum 값 삭제 | Breaking |
| 응답 필드를 null 가능으로 변경 | Breaking (클라이언트 파싱이 깨질 수 있음) |

Breaking Change는 프론트에 미리 알리고 배포 순서를 맞춥니다.

## 파일 구성

처음에는 `openapi.yaml` 한 파일로 시작합니다. 커지면 아래처럼 나누고 Redocly CLI로 합칩니다.

```
api/
├── openapi.yaml          # 진입점
├── paths/                # 기능별 경로
└── components/schemas/   # 공통 스키마
```
