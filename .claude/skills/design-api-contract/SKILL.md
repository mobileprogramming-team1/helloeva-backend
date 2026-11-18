---
name: design-api-contract
description: Approved 된 Hello Eva 기능 스펙을 api/openapi.yaml(OpenAPI 3.1) 계약과 plan.md의 API·에러 코드 섹션으로 옮기는 방법. 엔드포인트 설계, 요청/응답 스키마, 에러 코드, 예시 값, Breaking Change 판단을 다룬다. "API 명세 짜줘", "openapi 작성/수정해줘", "plan 써줘"처럼 API 계약이나 plan.md를 다룰 때 반드시 이 스킬을 따를 것. 다 쓴 뒤에는 spec-qa 에이전트로 검토한다.
---

# API 계약 설계법

규칙의 원본은 `api/README.md`다. 이 스킬은 그 규칙을 적용하는 순서를 정한다. 둘이 다르면 README가 맞다.

## 절차

1. **스펙 확인**: 상태가 `Approved`인지. 아니면 멈춘다.
2. **AC 목록 뽑기**: AC마다 "어떤 요청에 어떤 응답이 나와야 하는가"를 한 줄로.
3. **엔드포인트 정하기**: 리소스 단위로 묶는다. 경로·인증·목록·비동기·파일 규칙은 `api/README.md`대로.
4. **스키마 작성**: `components/schemas`에 `{Action}{Resource}Request` / `{Resource}Response` 이름으로. 공통 enum(`Grade`, `ConversationStatus`, `GenerationStatus`)과 `ErrorResponse`는 `$ref`로 재사용.
5. **응답 정리**: 성공 코드(200/201/204) + AC에 나온 에러 응답. 공통 에러는 `components/responses`(`BadRequest`, `Unauthorized`, `Forbidden`, `NotFound`, `Conflict`)를 참조.
6. **에러 코드**: 새 코드는 `ErrorCode` enum + plan.md 에러 코드 표에 같이 추가. 이름은 `{대상}_{상황}` 대문자 (예: `QUESTION_ALREADY_ANSWERED`).
7. **예시**: 모든 스키마 필드에 `example`. Mock 서버와 문서에 그대로 나오므로 현실적인 값으로 (등급은 `IM3`, 날짜는 `+09:00`).
8. **plan.md 갱신**: API 변경 표(Method, Path, 설명, 관련 AC), 에러 코드 표.
9. **검사**: YAML 문법 확인 `python3 -I -c "import yaml; yaml.safe_load(open('api/openapi.yaml'))"`. 가능하면 `npx @redocly/cli lint api/openapi.yaml`.

## OpenAPI 문법 메모 (README에 없는 작성 디테일)

- 인증 불필요 API: 오퍼레이션에 `security: []`
- 파일 업로드: `multipart/form-data` + `type: string, contentMediaType: audio/m4a` (방식은 004 plan에서 확정)
- 각 오퍼레이션에 `operationId`(camelCase 동사+명사), `tags`, `summary`(한국어)

## Breaking Change

판단 기준은 `api/README.md`의 "호환성" 표. Breaking이면 plan.md에 명시하고 사용자에게 알린다.

## 출력: AC → 엔드포인트 매핑

```markdown
| AC | Method Path | 응답 |
|---|---|---|
| AC-1 | POST /conversations | 201 ConversationResponse |
| AC-2 | POST /conversations | 400 INVALID_INPUT_VALUE |
```
매핑이 안 되는 AC는 표 아래에 따로 적는다.
