# CLAUDE.md

## 프로젝트

Hello Eva — AI 시험관 Eva와 영어로 대화하며 OPIc 말하기를 연습하는 모바일 앱의 백엔드.

**스펙주도 개발**을 따른다. 현재 단계: 스펙 작성 (코드 없음).

## 작업 규칙

- 구현·API 작업 전에 `specs/constitution.md`와 해당 기능의 `specs/NNN-*/spec.md`를 먼저 읽는다.
- 스펙이 `Draft`인 기능은 plan·API·코드를 만들지 않는다. 승인 여부를 사용자에게 묻는다.
- 요구사항이 바뀌면 스펙을 먼저 고치고, 그다음 계약(`api/openapi.yaml`)과 코드를 고친다.
- 스펙과 코드가 다르면 스펙이 기준이다. 스펙이 틀렸다고 판단되면 고치지 말고 사용자에게 알린다.
- 요청받은 범위만 작업한다. 계획·문서를 요청받았을 때 코드나 설정을 미리 만들지 않는다.
- 용어는 `specs/000-overview/glossary.md`를 따른다.
- 비밀 값(키, 토큰, 비밀번호)을 어떤 파일에도 쓰지 않는다.
- PR·이슈는 쓰지 않고 main에 바로 커밋한다. 커밋·push는 사용자가 요청할 때만 한다 (`git-commit-writer` 스킬).
- 커밋 메시지에 `Co-Authored-By: Claude ...`, "Generated with Claude Code" 같은 AI 작성 표시를 붙이지 않는다.
- 디자인: 팀 Figma 와이어프레임. 링크는 저장소에 두지 않으니 필요하면 사용자에게 받는다. Figma MCP는 호출 한도가 작으니 필요한 프레임만 조회한다.

## 하네스: 스펙주도 개발

**목표:** 기능 스펙 → API 계약 → 작업 목록을 일관되게 만들고, 문서 간 불일치를 막는다.

**트리거:**
- 스펙 작성·수정, 열린 질문 반영 → `write-feature-spec` 스킬
- plan·API 명세(openapi) 작성 → `design-api-contract` 스킬
- 위 작업이 끝나면, 또는 스펙/계약 검토 요청 시 → `spec-qa` 에이전트 (`review-spec` 스킬)
- 단순 질문은 직접 응답 가능.

**변경 이력:**
| 날짜 | 변경 내용 | 대상 | 사유 |
|------|----------|------|------|
| 2026-11-18 | 초기 구성 | 전체 | - |
| 2026-11-18 | spec-writer·api-designer 에이전트, spec-driven-dev 오케스트레이터 삭제 / specs/README.md를 constitution에 병합 / API 규칙 중복 제거 | agents, skills, specs, api/README.md | 스킬과 내용 중복, 대화형 스펙 작업에 파이프라인은 과함 |
| 2026-11-18 | .github 템플릿 삭제, git-issue-pr-writer → git-commit-writer (커밋 전용), 커밋 AI 서명 끄기 | .github, skills, settings.json | PR·이슈 없이 main 직접 커밋 |
