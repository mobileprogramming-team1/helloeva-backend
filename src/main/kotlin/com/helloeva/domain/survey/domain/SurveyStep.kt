package com.helloeva.domain.survey.domain

enum class SurveyStep(
    val key: String,
    val stepNumber: Int,
    val title: String,
    val question: String,
    val selectionType: SelectionType,
    val minSelections: Int,
    val countsTowardTotal: Boolean,
    val options: List<SurveyOption>,
) {
    JOB(
        "job", 1, "직업", "현재 귀하는 어느 분야에 종사하고 계십니까?",
        SelectionType.SINGLE, 1, false, JobOption.entries,
    ),
    STUDENT(
        "student", 2, "학생 여부", "현재 학생이십니까?",
        SelectionType.SINGLE, 1, false, StudentOption.entries,
    ),
    RECENT_COURSE(
        "recentCourse", 3, "최근 수강 강의", "최근에 어떤 강의를 수강하셨습니까?",
        SelectionType.SINGLE, 1, false, RecentCourseOption.entries,
    ),
    RESIDENCE(
        "residence", 4, "거주 형태", "현재 어디에 살고 계십니까?",
        SelectionType.SINGLE, 1, false, ResidenceOption.entries,
    ),
    LEISURES(
        "leisures", 5, "여가 활동", "여가 시간에 주로 무엇을 하십니까?",
        SelectionType.MULTIPLE, 2, true, LeisureOption.entries,
    ),
    HOBBIES(
        "hobbies", 6, "취미·관심사", "취미나 관심사는 무엇입니까?",
        SelectionType.MULTIPLE, 1, true, HobbyOption.entries,
    ),
    SPORTS(
        "sports", 7, "운동", "어떤 운동을 즐기십니까?",
        SelectionType.MULTIPLE, 1, true, SportOption.entries,
    ),
    TRAVELS(
        "travels", 8, "휴가·출장", "어떤 휴가나 출장을 다녀오셨습니까?",
        SelectionType.MULTIPLE, 1, true, TravelOption.entries,
    ),
    ;

    companion object {
        const val MIN_TOTAL_SELECTIONS = 12
    }
}

enum class SelectionType {
    SINGLE,
    MULTIPLE,
}
