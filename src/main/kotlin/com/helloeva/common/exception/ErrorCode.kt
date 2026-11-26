package com.helloeva.common.exception

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String,
) {
    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 생겼습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "적절하지 않은 값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "요청 값의 타입이 잘못되었습니다."),
    MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "적절하지 않은 HTTP 메소드입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리소스를 찾을 수 없습니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 카카오 토큰입니다."),
    KAKAO_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "카카오 서버와 통신 중 에러가 발생했습니다."),

    // Conversation
    CONVERSATION_NOT_FOUND(HttpStatus.NOT_FOUND, "대화를 찾을 수 없습니다."),
    NOT_CONVERSATION_OWNER(HttpStatus.FORBIDDEN, "해당 대화에 접근할 권한이 없습니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "문항을 찾을 수 없습니다."),
    QUESTION_NOT_CURRENT(HttpStatus.CONFLICT, "현재 진행 중인 문항이 아닙니다."),
    QUESTION_ALREADY_ANSWERED(HttpStatus.CONFLICT, "이미 답변한 문항입니다."),
    REPLAY_LIMIT_EXCEEDED(HttpStatus.CONFLICT, "다시 듣기 횟수를 모두 사용했습니다."),
    INVALID_AUDIO_FILE(HttpStatus.BAD_REQUEST, "지원하지 않는 음성 파일입니다."),

    // Feedback
    QUESTIONS_NOT_ALL_ANSWERED(HttpStatus.CONFLICT, "15문항에 모두 답해야 종합 피드백을 볼 수 있습니다."),
    OVERALL_FEEDBACK_NOT_REQUESTED(HttpStatus.CONFLICT, "아직 종합 피드백을 만들지 않았습니다."),
}
