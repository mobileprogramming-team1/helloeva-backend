package com.helloeva.domain.survey.presentation

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SurveyController {

    @GetMapping("/background-survey-steps")
    fun getBackgroundSurveySteps(): BackgroundSurveyResponse =
        BackgroundSurveyResponse.create()
}
