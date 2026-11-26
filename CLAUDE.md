# CLAUDE.md

## 프로젝트

Hello Eva — AI 시험관 Eva와 영어로 대화하며 OPIc 말하기를 연습하는 모바일 앱의 백엔드 (Kotlin + Spring Boot).

## 먼저 읽을 문서

| 문서 | 언제 |
|---|---|
| `docs/conventions.md` | 코드 작성 전. 아키텍처, 데이터, 테스트, 보안, 기술 스택 규칙 |
| `docs/features/NNN-*.md` | 해당 기능 구현 전. 요구사항(FR)과 인수 조건(AC) |
| `api/openapi.yaml` | API 구현 전. 경로, 요청·응답, 에러 코드의 기준 |
| `api/README.md` | API 명세를 고칠 때 |
| `docs/glossary.md` | 이름 지을 때. 코드·API 이름은 용어집을 따름 |
| `docs/product.md` | 화면 흐름, 기능 목록, 아직 정하지 않은 것 |

## 작업 규칙

- 요청받은 범위만 작업한다. 계획·문서를 요청받았을 때 코드나 설정을 미리 만들지 않는다.
- 구현이 `api/openapi.yaml`과 다르면 구현을 고친다. 명세가 틀렸다고 판단되면 고치기 전에 사용자에게 알린다.
- 비밀 값(키, 토큰, 비밀번호)을 어떤 파일에도 쓰지 않는다.
- PR·이슈는 쓰지 않고 main에 바로 커밋한다. 커밋·push는 사용자가 요청할 때만 한다 (`git-commit-writer` 스킬).
- 커밋 메시지에 `Co-Authored-By: Claude ...`, "Generated with Claude Code" 같은 AI 작성 표시를 붙이지 않는다.
- 디자인: 팀 Figma 와이어프레임. 링크는 저장소에 두지 않으니 필요하면 사용자에게 받는다. Figma MCP는 호출 한도가 작으니 필요한 프레임만 조회한다.

## Claude 도구

- 외부 연동(STT, TTS, LLM, 저장소, Kakao) 추가·교체 → `new-integration-pattern` 스킬
- 새 컴포넌트 작성·리팩토링 후 구조 검토 → `architecture-reviewer` 에이전트
- 커밋 → `git-commit-writer` 스킬
