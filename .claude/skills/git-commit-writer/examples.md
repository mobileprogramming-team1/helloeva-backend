# 커밋 메시지 예시 (few-shot)

이 정도 밀도로 작성한다. 실제 이력이 쌓이면 이 파일을 최근 예시로 교체한다.
(아래 예시의 클래스·테이블 이름은 형식 예시일 뿐, 확정된 설계가 아니다.)

```
spec: 002 대화 생성 열린 질문 반영

- 설문은 완료 시 한 번에 제출하도록 FR-1 수정
- 진행중 대화가 있어도 새로 생성 가능 (AC-6 추가)
- features.md 상태 갱신
```

```
spec: 대화 API 명세 추가

- POST /conversations, GET /conversations 추가
- ConversationStatus, BackgroundSurvey 스키마 추가
- Breaking Change 없음
- specs/002, 003
```

```
feat: 대화 생성 API 구현

- 설문 선택 개수 검증 (단계별 최소, 누적 12개)
- 대화 생성 서비스·저장소 추가
- AC-1~5 통합 테스트
- specs/002-conversation-creation
```

사소한 변경은 제목만:
```
chore: openapi example 날짜 형식 수정
```
