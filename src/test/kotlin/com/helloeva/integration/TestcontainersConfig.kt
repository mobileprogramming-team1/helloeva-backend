package com.helloeva.integration

import com.helloeva.integration.evaluation.application.AnswerEvaluator
import com.helloeva.integration.fake.FakeAnswerEvaluator
import com.helloeva.integration.fake.FakeFileStorage
import com.helloeva.integration.fake.FakeOauthClient
import com.helloeva.integration.fake.FakeQuestionGenerator
import com.helloeva.integration.fake.FakeSpeechToText
import com.helloeva.integration.fake.FakeTextToSpeech
import com.helloeva.integration.oauth.application.OauthClient
import com.helloeva.integration.question.application.QuestionGenerator
import com.helloeva.integration.speech.application.SpeechToText
import com.helloeva.integration.speech.application.TextToSpeech
import com.helloeva.integration.storage.application.FileStorage
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.testcontainers.mysql.MySQLContainer

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfig {

    @Bean
    @ServiceConnection
    fun mysqlContainer(): MySQLContainer = MySQLContainer("mysql:8.4")

    @Bean
    @Primary
    fun fakeOauthClient(): OauthClient = FakeOauthClient()

    @Bean
    @Primary
    fun fakeQuestionGenerator(): QuestionGenerator = FakeQuestionGenerator()

    @Bean
    @Primary
    fun fakeTextToSpeech(): TextToSpeech = FakeTextToSpeech()

    @Bean
    @Primary
    fun fakeFileStorage(): FileStorage = FakeFileStorage()

    @Bean
    @Primary
    fun fakeSpeechToText(): SpeechToText = FakeSpeechToText()

    @Bean
    @Primary
    fun fakeAnswerEvaluator(): AnswerEvaluator = FakeAnswerEvaluator()
}
