package com.helloeva.domain.usage.infrastructure

import com.helloeva.domain.usage.domain.ApiUsage
import org.springframework.data.jpa.repository.JpaRepository

interface ApiUsageRepository : JpaRepository<ApiUsage, Long>
