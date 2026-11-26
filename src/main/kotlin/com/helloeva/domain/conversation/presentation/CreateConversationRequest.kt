package com.helloeva.domain.conversation.presentation

import com.helloeva.domain.conversation.domain.Grade
import com.helloeva.domain.survey.domain.BackgroundSurvey
import com.helloeva.domain.survey.domain.HobbyOption
import com.helloeva.domain.survey.domain.JobOption
import com.helloeva.domain.survey.domain.LeisureOption
import com.helloeva.domain.survey.domain.RecentCourseOption
import com.helloeva.domain.survey.domain.ResidenceOption
import com.helloeva.domain.survey.domain.SportOption
import com.helloeva.domain.survey.domain.StudentOption
import com.helloeva.domain.survey.domain.TravelOption
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateConversationRequest(
    @field:NotNull(message = "필수 값입니다.")
    val targetGrade: Grade?,

    @field:NotNull(message = "필수 값입니다.")
    @field:Valid
    @field:ValidBackgroundSurvey
    val backgroundSurvey: BackgroundSurveyRequest?,
)

data class BackgroundSurveyRequest(
    @field:NotNull(message = "필수 값입니다.")
    val job: JobOption?,

    @field:NotNull(message = "필수 값입니다.")
    val student: StudentOption?,

    @field:NotNull(message = "필수 값입니다.")
    val recentCourse: RecentCourseOption?,

    @field:NotNull(message = "필수 값입니다.")
    val residence: ResidenceOption?,

    @field:NotNull(message = "필수 값입니다.")
    @field:Size(min = 2, message = "최소 2개를 선택해야 합니다.")
    val leisures: List<LeisureOption>?,

    @field:NotNull(message = "필수 값입니다.")
    @field:Size(min = 1, message = "최소 1개를 선택해야 합니다.")
    val hobbies: List<HobbyOption>?,

    @field:NotNull(message = "필수 값입니다.")
    @field:Size(min = 1, message = "최소 1개를 선택해야 합니다.")
    val sports: List<SportOption>?,

    @field:NotNull(message = "필수 값입니다.")
    @field:Size(min = 1, message = "최소 1개를 선택해야 합니다.")
    val travels: List<TravelOption>?,
) {
    fun toBackgroundSurvey(): BackgroundSurvey =
        BackgroundSurvey(
            job = job!!,
            student = student!!,
            recentCourse = recentCourse!!,
            residence = residence!!,
            leisures = leisures!!,
            hobbies = hobbies!!,
            sports = sports!!,
            travels = travels!!,
        )
}
