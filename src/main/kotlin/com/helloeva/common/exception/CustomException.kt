package com.helloeva.common.exception

class CustomException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)
