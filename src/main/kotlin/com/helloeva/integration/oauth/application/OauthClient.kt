package com.helloeva.integration.oauth.application

interface OauthClient {
    fun getUserInfo(accessToken: String): OauthUserInfo
}
