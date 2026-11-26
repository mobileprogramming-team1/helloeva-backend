package com.helloeva.domain.survey.domain

interface SurveyOption {
    val name: String
    val label: String
}

enum class JobOption(override val label: String) : SurveyOption {
    OFFICE_WORKER("사업/회사 근무"),
    WORK_FROM_HOME("재택근무/재택사업"),
    TEACHER("교사/교육자"),
    NO_WORK_EXPERIENCE("일 경험 없음"),
}

enum class StudentOption(override val label: String) : SurveyOption {
    YES("예"),
    NO("아니요"),
}

enum class RecentCourseOption(override val label: String) : SurveyOption {
    DEGREE_PROGRAM("학위 과정 수업"),
    CONTINUING_EDUCATION("전문 기술 향상을 위한 평생학습"),
    LANGUAGE_CLASS("어학 수업"),
    OVER_FIVE_YEARS_AGO("수강 후 5년 이상 지남"),
}

enum class ResidenceOption(override val label: String) : SurveyOption {
    ALONE("개인 주택·아파트에 혼자 거주"),
    WITH_ROOMMATES("친구·룸메이트와 주택·아파트에 거주"),
    WITH_FAMILY("가족(배우자/자녀/기타 가족)과 주택·아파트에 거주"),
    DORMITORY("학교 기숙사"),
    MILITARY_BASE("군대 막사·군 시설"),
}

enum class LeisureOption(override val label: String) : SurveyOption {
    MOVIES("영화 보기"),
    CLUBS("클럽/나이트클럽 가기"),
    PERFORMANCES("공연 보기"),
    CONCERTS("콘서트 보기"),
    MUSEUMS("박물관 가기"),
    PARKS("공원 가기"),
    CAMPING("캠핑하기"),
    BEACH("해변 가기"),
    WATCHING_SPORTS("스포츠 관람"),
    GAMES("게임하기"),
    SNS_POSTING("SNS에 글 올리기"),
    TEXTING_FRIENDS("친구들과 문자 대화하기"),
    HOME_IMPROVEMENT("주거 개선"),
    BARS("술집/바에 가기"),
    CAFES("카페/커피 전문점 가기"),
    SHOPPING("쇼핑하기"),
    WATCHING_TV("TV 시청하기"),
    REALITY_SHOWS("리얼리티 쇼 시청하기"),
    NEWS("뉴스 보기/듣기"),
    COOKING_SHOWS("요리 관련 프로그램 시청하기"),
    CHESS("체스하기"),
    BILLIARDS("당구"),
    TEST_PREP_COURSES("시험 대비 과정 수강하기"),
    JOB_HUNTING("구직 활동하기"),
    VOLUNTEERING("자원봉사하기"),
    DRIVING("드라이브하기"),
}

enum class HobbyOption(override val label: String) : SurveyOption {
    READING_TO_CHILDREN("아이에게 책 읽어주기"),
    LISTENING_TO_MUSIC("음악 감상하기"),
    PLAYING_INSTRUMENTS("악기 연주하기"),
    SINGING("혼자 노래 부르기/합창하기"),
    DANCING("춤추기"),
    WRITING("글쓰기(편지, 단문, 시 등)"),
    DRAWING("그림 그리기"),
    COOKING("요리하기"),
    RAISING_PETS("반려동물 키우기"),
    INVESTING("주식 투자하기"),
    READING_NEWSPAPERS("신문 읽기"),
    READING_TRAVEL_MEDIA("여행 관련 잡지·블로그 읽기"),
    READING("독서"),
    PHOTOGRAPHY("사진 촬영하기"),
}

enum class SportOption(override val label: String) : SurveyOption {
    BASKETBALL("농구"),
    BASEBALL("야구/소프트볼"),
    SOCCER("축구"),
    AMERICAN_FOOTBALL("미식축구"),
    HOCKEY("하키"),
    CRICKET("크리켓"),
    GOLF("골프"),
    VOLLEYBALL("배구"),
    TENNIS("테니스"),
    BADMINTON("배드민턴"),
    TABLE_TENNIS("탁구"),
    SWIMMING("수영"),
    CYCLING("자전거"),
    SKIING("스키/스노보드"),
    ICE_SKATING("아이스 스케이트"),
    JOGGING("조깅"),
    WALKING("걷기"),
    YOGA("요가"),
    HIKING("하이킹/트레킹"),
    FISHING("낚시"),
    GYM("헬스"),
    TAEKWONDO("태권도"),
    EXERCISE_CLASSES("운동 수업 수강하기"),
    NONE("운동을 전혀 하지 않음"),
}

enum class TravelOption(override val label: String) : SurveyOption {
    STAYCATION("집에서 보내는 휴가"),
    DOMESTIC_BUSINESS_TRIP("국내 출장"),
    OVERSEAS_BUSINESS_TRIP("해외 출장"),
    DOMESTIC_TRAVEL("국내 여행"),
    OVERSEAS_TRAVEL("해외 여행"),
}
