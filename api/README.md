# API 명세

API 명세는 **[openapi.yaml](./openapi.yaml) 하나**입니다. 백엔드가 작성해 프론트(모바일)에 공유하고, 코드보다 먼저 바꿉니다.

## 작성과 공유 흐름

```
피그마 화면·기능 문서 → openapi.yaml 작성 → main 반영 → 문서 링크 공유
                → (선택) 프론트는 Mock 서버로 먼저 개발 → 백엔드 구현
```

| 단계 | 방법 |
|---|---|
| 작성 | 백엔드가 `openapi.yaml`을 작성·수정합니다 |
| 보기·테스트 | GitHub Pages 문서. Authorize에 액세스 토큰을 넣고 Try it out으로 개발 서버를 바로 호출합니다 |
| 먼저 개발 | `npx @stoplight/prism-cli mock api/openapi.yaml` → example 값을 돌려주는 가짜 서버 |
| 구현 확인 | GitHub Pages 문서에서 개발 서버를 호출해 봅니다. 명세와 다르면 구현을 고칩니다 |
| 변경 알림 | 명세가 바뀌면 팀 채널에 바뀐 엔드포인트와 Breaking Change 여부를 공유 |

## 작성 규칙

### 경로
- 복수형 명사, kebab-case: `/conversations/{conversationId}/questions/{questionNumber}/answer`
- `/api`, `/v1` 접두사 없음
- 내 리소스는 `/users/me/...`

### 인증
- `Authorization: Bearer {accessToken}`
- 토큰은 액세스 토큰 하나뿐 (유효 기간 30일, 리프레시 토큰·재발급 없음)
- 인증이 필요 없는 API는 `security: []`로 표시

### 요청·응답
- **엔드포인트마다 전용 Request/Response 스키마**를 둡니다 (`{Action}{Resource}Request` / `{Action}{Resource}Response`). 다른 API의 응답 스키마를 재사용하지 않습니다
  - 공유해도 되는 것: enum(`Grade`, `ConversationStatus` 등), `ErrorResponse`, 응답 안에 들어가는 작은 값 객체(`Answer`, `AreaScores` 등)
  - 응답에는 그 화면·동작에 필요한 필드만 넣습니다
- 요청 값 검증은 스키마에 명시합니다: 필수 여부(`required`), enum, 최소·최대 개수·길이
- 필드 이름은 camelCase
- 날짜·시간은 ISO-8601 문자열 (`2026-11-18T10:00:00+09:00`)
- enum은 대문자 상수 (`IN_PROGRESS`, `IM3`). 값 목록은 [용어집](../docs/glossary.md)과 맞춥니다
- 비어 있을 수 있는 필드는 `type: [string, 'null']`처럼 명시합니다 (OpenAPI 3.1 문법). 목록은 null 대신 `[]`
- 모든 스키마에 `example`을 적습니다 (Mock 서버와 문서에 그대로 쓰입니다)

### 상태 코드
| 코드 | 언제 |
|---|---|
| 200 | 조회·변경 성공 (본문 있음). 로그인은 신규 가입이어도 200 (`isNewUser`로 구분) |
| 201 | 생성 성공 |
| 204 | 성공, 본문 없음 (로그아웃 등) |
| 400 | 입력값 오류 |
| 401 | 인증 없음·만료 |
| 403 | 남의 리소스 |
| 404 | 없음 |
| 409 | 현재 상태에서 할 수 없음 (이미 답한 문항 등) |
| 502 | 카카오 로그인 서버 오류 (서버가 대신 처리할 수 없는 경우만) |

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
- 페이지 나누기 없이 전체를 줍니다: `{ totalCount, items }`. 데이터가 많아지면 그때 커서 방식으로 바꿉니다

### 오래 걸리는 작업 (AI 처리)
- 상태 관리·폴링 없이 동기로 처리합니다. 요청 안에서 끝까지 처리하고 결과를 바로 응답합니다
- 응답까지 수 초~수십 초 걸릴 수 있으므로 앱 요청 타임아웃은 60초로 합니다
- AI 호출이 실패하면 서버가 다시 시도합니다. 실패 상태나 재시도 API는 두지 않습니다

### 파일 (음성)
- 업로드는 `multipart/form-data`로 직접 받습니다
- 응답에는 재생 가능한 URL을 줍니다 (만료 없음)

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
