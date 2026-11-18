# 용어집

프론트, 백엔드, 기획이 같은 단어를 쓰기 위한 표입니다. API 경로와 코드 이름도 이 표를 따릅니다.

> 시험 1회의 단위는 **"대화"(`Conversation`)**로 통일합니다. 화면 상단의 "OPIc 모의고사"는 화면 제목일 뿐이고, 문서·API·코드에서는 "모의고사"라는 말을 쓰지 않습니다.

| 한국어 (화면) | 영어 (코드/API) | 뜻 |
|---|---|---|
| 대화 | Conversation | 목표 등급과 배경 설문으로 만든 시험 1회. 문항 15개로 구성. API 경로는 `/conversations` |
| N회차 | round | 사용자가 만든 대화의 순번 (1부터) |
| 문제, 문항 | Question | 대화 안의 질문 1개. 순서(1~15)가 있음 |
| Eva | Eva | AI 시험관. 질문을 읽어주고 피드백을 줌 |
| 답변 | Answer | 문항에 대한 사용자의 음성 답변과 그 텍스트(전사본) |
| 목표 등급 | targetGrade | 사용자가 목표로 하는 OPIc 등급 |
| 예상 등급 | estimatedGrade | AI가 평가한 답변 수준 |
| 배경 설문 | BackgroundSurvey | OPIc 시작 전 직업·거주·여가 등을 고르는 설문 |
| 문항별 피드백, 답변 분석 | QuestionFeedback | 문항 하나에 대한 평가 |
| 답변 디벨롭 | improvementTips | 목표 등급에 맞춰 답변을 개선하는 방법 (목록) |
| 이렇게 말해 보세요 | recommendedScript | 개선된 예시 답변 |
| 주요 단어 | keyExpressions | 예시 답변에 쓰인 핵심 표현 |
| 종합 피드백 | OverallFeedback | 대화 15문항 전체에 대한 평가 |
| 영역별 평가 | areaScores | 유창성(fluency)·어휘(vocabulary)·문법(grammar)·내용 구성(organization) 점수 (0~100) |
| 잘한 점 / 보완할 점 | strengths / weaknesses | 종합 피드백 항목 |
| 추천 학습 | recommendedStudies | 다음에 공부할 것 |
| 자주 쓴 표현 | frequentExpressions | 사용자가 반복해서 쓴 표현 |
| 진행중 / 완료 | IN_PROGRESS / COMPLETED | 대화 상태 (`ConversationStatus`) |

## OPIc 등급 (`Grade`)

낮은 순서: `NL` < `NM` < `NH` < `IL` < `IM1` < `IM2` < `IM3` < `IH` < `AL`

| 코드 | 이름 |
|---|---|
| AL | Advanced Low |
| IH | Intermediate High |
| IM3 | Intermediate Mid 3 |
| IM2 | Intermediate Mid 2 |
| IM1 | Intermediate Mid 1 |
| IL | Intermediate Low |
| NH | Novice High |
| NM | Novice Mid |
| NL | Novice Low |
