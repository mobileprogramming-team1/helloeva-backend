package com.helloeva.domain.survey.presentation

import com.helloeva.domain.survey.domain.SelectionType
import com.helloeva.domain.survey.domain.SurveyStep

data class BackgroundSurveyResponse(
    val minTotalSelections: Int,
    val steps: List<Step>,
) {
    data class Step(
        val key: String,
        val stepNumber: Int,
        val title: String,
        val question: String,
        val selectionType: SelectionType,
        val minSelections: Int,
        val countsTowardTotal: Boolean,
        val options: List<Option>,
    )

    data class Option(
        val code: String,
        val label: String,
    )

    companion object {
        fun create(): BackgroundSurveyResponse =
            BackgroundSurveyResponse(
                minTotalSelections = SurveyStep.MIN_TOTAL_SELECTIONS,
                steps = SurveyStep.entries.map { step ->
                    Step(
                        key = step.key,
                        stepNumber = step.stepNumber,
                        title = step.title,
                        question = step.question,
                        selectionType = step.selectionType,
                        minSelections = step.minSelections,
                        countsTowardTotal = step.countsTowardTotal,
                        options = step.options.map { Option(code = it.name, label = it.label) },
                    )
                },
            )
    }
}
