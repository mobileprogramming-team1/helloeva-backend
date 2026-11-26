package com.helloeva.domain.survey.domain

data class BackgroundSurvey(
    val job: JobOption,
    val student: StudentOption,
    val recentCourse: RecentCourseOption,
    val residence: ResidenceOption,
    val leisures: List<LeisureOption>,
    val hobbies: List<HobbyOption>,
    val sports: List<SportOption>,
    val travels: List<TravelOption>,
)
