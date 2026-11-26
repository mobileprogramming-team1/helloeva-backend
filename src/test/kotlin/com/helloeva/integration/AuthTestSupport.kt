package com.helloeva.integration

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import tools.jackson.databind.json.JsonMapper

fun MockMvc.loginAndGetToken(jsonMapper: JsonMapper, kakaoAccessToken: String): String {
    val body = post("/auth/login") {
        contentType = MediaType.APPLICATION_JSON
        content = """{"kakaoAccessToken": "$kakaoAccessToken"}"""
    }.andReturn().response.contentAsString
    return jsonMapper.readTree(body)["accessToken"].asString()
}
