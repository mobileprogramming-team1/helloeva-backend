package com.helloeva.common.utils

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

private val SEOUL: ZoneId = ZoneId.of("Asia/Seoul")

fun LocalDateTime.toSeoulOffset(): OffsetDateTime = atZone(SEOUL).toOffsetDateTime()
