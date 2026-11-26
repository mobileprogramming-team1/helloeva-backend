package com.helloeva.common.config

import com.helloeva.common.security.ErrorResponseWriter
import com.helloeva.common.security.JwtAuthenticationEntryPoint
import com.helloeva.common.security.JwtAuthenticationFilter
import com.helloeva.common.security.JwtTokenProvider
import com.helloeva.domain.user.infrastructure.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val userRepository: UserRepository,
    private val errorResponseWriter: ErrorResponseWriter,
    private val authenticationEntryPoint: JwtAuthenticationEntryPoint,
) {

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer =
        WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers(*PUBLIC_PATHS)
        }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .logout { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { it.anyRequest().authenticated() }
            .exceptionHandling { it.authenticationEntryPoint(authenticationEntryPoint) }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider, userRepository, errorResponseWriter),
                UsernamePasswordAuthenticationFilter::class.java,
            )

        return http.build()
    }

    companion object {
        private val PUBLIC_PATHS = arrayOf(
            "/auth/login",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error",
        )
    }
}
