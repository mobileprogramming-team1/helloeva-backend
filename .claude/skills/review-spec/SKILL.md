---
name: review-spec
description: Hello Eva 스펙 문서끼리, 그리고 스펙과 API 계약(openapi.yaml)이 서로 맞는지 교차 검증하는 체크리스트. spec-qa 에이전트가 사용하며, "스펙 검토해줘", "스펙이랑 API 맞는지 봐줘", "openapi 리뷰해줘", "용어 안 맞는 거 찾아줘" 같은 검토 요청에도 이 스킬을 따를 것.
---

# 스펙 교차 검증

"파일이 있는가"가 아니라 "두 문서가 같은 말을 하는가"를 본다. 각 항목은 양쪽 파일을 **동시에** 열어 비교한다.

## 범위: spec 단계

| # | 확인 | 비교 대상 |
|---|---|---|
| S1 | 템플릿 섹션이 모두 있다 | spec.md ↔ `_templates/spec.md` |
| S2 | AC가 Given/When/Then이고, Then이 관찰 가능한 결과다 | spec.md |
| S3 | 모든 FR이 하나 이상의 AC로 검증된다 | spec.md FR ↔ AC |
| S4 | 숫자 규칙(최소/최대/개수)마다 AC가 있다 | spec.md |
| S5 | 기술 구현 내용(테이블, 클래스, 경로)이 없다 | spec.md |
| S6 | 용어가 용어집과 같다 | spec.md ↔ `glossary.md` |
| S7 | 다른 스펙과 모순이 없다 (같은 대상의 상태·규칙) | spec.md ↔ 참조하는 다른 spec.md |
| S8 | 현황판의 링크·상태가 맞다 | spec.md ↔ `features.md` |
| S9 | 추측이 FR/AC에 섞여 있지 않다 (근거 없는 수치 등) | spec.md |

## 범위: api 단계 (spec 단계 항목 + 아래)

| # | 확인 | 비교 대상 |
|---|---|---|
| A1 | 모든 AC가 엔드포인트 응답으로 드러난다 | spec.md AC ↔ openapi.yaml |
| A2 | AC에 쓴 상태 코드·ErrorCode가 계약에 있다 | spec.md ↔ openapi.yaml `ErrorCode` |
| A3 | plan.md 에러 코드 표와 `ErrorCode` enum이 일치 | plan.md ↔ openapi.yaml |
| A4 | plan.md API 표와 openapi 경로가 일치 | plan.md ↔ openapi.yaml |
| A5 | 필드·enum 이름이 용어집 영어 이름과 같다 | openapi.yaml ↔ `glossary.md` |
| A6 | `api/README.md` 규칙 준수 (경로, 인증, 날짜, null 표기, 목록, AI 동기 처리) | openapi.yaml ↔ README |
| A7 | 모든 필드에 example이 있고 타입과 맞다 | openapi.yaml |
| A8 | 인증 불필요 API에 `security: []`, 나머지는 bearerAuth | openapi.yaml ↔ spec 시나리오 |
| A9 | Breaking Change가 표시되어 있다 | openapi.yaml git diff ↔ plan.md |
| A10 | YAML이 파싱된다 | `python3 -I -c "import yaml; yaml.safe_load(open('api/openapi.yaml'))"` |

## 리포트 형식

```markdown
# {번호} spec-qa 리포트 ({spec|api} 단계)

| # | 심각도 | 항목 | 문제 | 근거 |
|---|---|---|---|---|
| 1 | 차단 | A2 | AC-3의 409 응답이 계약에 없음 | specs/004.../spec.md:45, api/openapi.yaml:120 |

확인했고 문제 없는 항목: S1, S2, ...
```

- `차단`: 그대로 두면 구현이나 프론트 개발이 틀어짐
- `권장`: 품질 개선
- 이전 리포트가 있으면 각 지적에 `해결/미해결`을 붙인다.
