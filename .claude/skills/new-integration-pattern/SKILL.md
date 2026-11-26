---
name: new-integration-pattern
description: Hello Eva의 외부 연동(음성 인식 STT, 음성 합성 TTS, 질문 생성, 답변·종합 평가 LLM, 음성 파일 저장소, Kakao)에 새 인터페이스나 제공자(구현체)를 추가하거나 기존 제공자를 교체할 때 사용. "~로 바꿔줘", "~API 붙여줘", "제공자 추가해줘", "STT/TTS/평가 모델 연동해줘" 같은 요청이면 이 스킬의 Strategy 패턴 컨벤션대로 구현한다. (코드 작성 단계부터 사용)
---

## 구조

외부 서비스는 `integration/` 아래 **역할 인터페이스 + 제공자 구현체**로 둔다. 도메인은 인터페이스만 안다.

```
integration/
├── speech/      application/SpeechToText, TextToSpeech     infrastructure/<제공자>SpeechToText ...
├── question/    application/QuestionGenerator              infrastructure/<제공자>QuestionGenerator
├── evaluation/  application/AnswerEvaluator, OverallEvaluator
├── storage/     application/FileStorage
└── oauth/       application/OauthClient                    infrastructure/KakaoOauthClient
```
(이름은 예시. 실제 이름은 구현할 때 정한다.)

## 새 제공자 추가/교체 절차
1. 해당 역할의 인터페이스가 `integration/<역할>/application/`에 있는지 확인한다. 있으면 새로 만들지 않는다.
2. 없을 때만 인터페이스를 만든다. **역할 하나에 인터페이스 하나** — STT와 TTS를 한 인터페이스에 묶지 않는다 (ISP).
3. 구현체를 `infrastructure/`에 `<제공자명><역할>` 이름으로 추가한다 (예: `GeminiAnswerEvaluator`).
4. HTTP 호출은 Spring `RestClient`(또는 해당 제공자 공식 SDK). 키와 URL은 `${ENV_VAR}`로 주입하고 `.env.example`에 이름을 추가한다.
5. 상위 컴포넌트는 **인터페이스 타입으로만** 의존한다. 구현체 클래스를 import하지 않는다.
6. 교체(완전 대체)인지 병행(`@Primary`/프로필로 선택)인지 먼저 정하고 등록한다.
7. 테스트용 가짜 구현체(`Fake<역할>`)를 `src/test`에 둔다. 통합 테스트가 실제 AI를 부르지 않게 하기 위해서다.
8. 선택한 제공자는 `docs/conventions.md`의 기술 스택 표에 추가한다.

## 하지 말 것
- 무관한 기능을 기존 인터페이스에 억지로 끼워 넣기
- 도메인 엔티티·Service에서 외부 SDK 타입을 직접 다루기
- 키·토큰을 설정 파일에 직접 쓰기
