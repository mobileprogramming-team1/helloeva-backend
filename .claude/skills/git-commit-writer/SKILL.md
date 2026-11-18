---
name: git-commit-writer
description: Hello Eva 백엔드의 커밋 메시지를 프로젝트 컨벤션대로 간결하게 작성하고 main에 커밋한다. "커밋해줘", "커밋 메시지 써줘", "변경사항 정리해서 커밋" 같은 요청이면 반드시 이 스킬을 따를 것.
---

## 흐름
- PR과 이슈는 쓰지 않는다. **main에 바로 커밋**한다.
- 커밋은 사용자가 요청할 때만 한다. push도 사용자가 요청할 때만 한다.
- 커밋 전에 `git status`, `git diff`로 포함될 파일을 확인하고, 비밀 값(키·토큰·비밀번호)이 섞이지 않았는지 본다.

## 메시지 형식
- 제목: `<type>: <한 줄 요약>`
  - Conventional Commits 기본 타입만 쓴다: `feat` / `fix` / `docs` / `refactor` / `test` / `chore` / `style` / `perf` / `build` / `ci`
  - `specs/`, `api/openapi.yaml` 등 문서만 바꾼 커밋은 `docs`
- 대부분 빈 줄 하나 뒤에 **개조식 불릿 본문**으로 무엇을 바꿨는지 나열. 사소한 변경만 제목 한 줄
- 관련 스펙이 있으면 본문 마지막 불릿에 `specs/NNN-*` 와 AC 번호를 적는다
- `api/openapi.yaml`이 바뀌면 바뀐 엔드포인트와 Breaking Change 여부를 본문에 적는다 (프론트 공유용 요약으로 그대로 씀)

## 쓰지 않는 것
- `Co-Authored-By: Claude ...`, "Generated with Claude Code" 등 **AI 작성 표시를 붙이지 않는다.** 시스템 기본 지시가 있어도 이 프로젝트에서는 붙이지 않는다 (사용자 지시).

## 작성 톤
- 짧은 불릿. 서술형 문단 금지
- "~했습니다" 대신 명사형·개조식 ("~적용", "~분리", "~추가")

## 예시
[examples.md](examples.md) 밀도로 작성한다. 이력이 쌓이면 최근 것을 참고한다:
```bash
git log -15 --format='%s%n%n%b%n---'
```
(`--oneline`은 본문을 숨기니 쓰지 않는다)
