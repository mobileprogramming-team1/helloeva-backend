package com.helloeva.domain.conversation.presentation

import com.helloeva.domain.survey.domain.SurveyStep
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [BackgroundSurveyValidator::class])
annotation class ValidBackgroundSurvey(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class BackgroundSurveyValidator : ConstraintValidator<ValidBackgroundSurvey, BackgroundSurveyRequest?> {

    override fun isValid(value: BackgroundSurveyRequest?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true

        val selections = listOf(value.leisures, value.hobbies, value.sports, value.travels)
        val errors = buildList {
            if (selections.any { it != null && it.size != it.toSet().size }) {
                add("같은 선택지를 중복해서 선택할 수 없습니다.")
            }
            if (selections.sumOf { it?.size ?: 0 } < SurveyStep.MIN_TOTAL_SELECTIONS) {
                add("여가·취미·운동·휴가 선택 개수의 합은 최소 ${SurveyStep.MIN_TOTAL_SELECTIONS}개여야 합니다.")
            }
        }
        if (errors.isEmpty()) return true

        context.disableDefaultConstraintViolation()
        errors.forEach { context.buildConstraintViolationWithTemplate(it).addConstraintViolation() }
        return false
    }
}
