package com.helloeva.integration

import com.helloeva.integration.fake.FakeOauthClient
import com.helloeva.integration.oauth.application.OauthClient
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
}
