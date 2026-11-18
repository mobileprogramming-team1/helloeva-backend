# 기능 목록 (현황판)

스펙을 추가하거나 상태가 바뀌면 이 표를 함께 수정합니다.

| # | 기능 | 화면 | spec | plan | 상태 |
|---|---|---|---|---|---|
| 001 | 카카오 로그인·인증 (로그인, 로그아웃, 탈퇴) | 스플래시, 설정 | [spec](../001-auth/spec.md) | — | Draft |
| 002 | 대화 생성 (목표 등급 + 배경 설문) | 대화 생성 1~2 | [spec](../002-conversation-creation/spec.md) | — | Draft |
| 003 | 대화 목록 | 홈 | [spec](../003-conversation-list/spec.md) | — | Draft |
| 004 | 대화 진행 (질문 듣기, 음성 답변) | 대화 상세 1~2, 5 | [spec](../004-conversation-session/spec.md) | — | Draft |
| 005 | 문항별 피드백 | 대화 상세 3~4 | [spec](../005-question-feedback/spec.md) | — | Draft |
| 006 | 종합 피드백 | 대화 상세 6~8 | [spec](../006-overall-feedback/spec.md) | — | Draft |
| 007 | 내 정보·설정 | 설정 | [spec](../007-user-settings/spec.md) | — | Draft |

## 권장 순서

1. **001**: 범위가 작고 표준적이라 spec → plan → API → 구현 사이클을 처음 돌려보기 좋습니다.
2. **002 → 003**: 데이터만 다루고 AI가 없습니다.
3. **004 → 005 → 006**: AI(질문 생성, 음성 인식, 평가)가 필요합니다. 기술 결정이 가장 많습니다.
4. **007**

## 전체에 걸친 열린 질문

- [ ] 질문 생성 방식: AI 생성 vs 문제은행 vs 혼합
- [ ] AI 제공자: 음성 합성(TTS), 음성 인식(STT), 평가(LLM)
- [ ] 음성 파일 저장소와 보관 기간
- [ ] 프론트 스택 (API 코드 생성 여부 결정용)
