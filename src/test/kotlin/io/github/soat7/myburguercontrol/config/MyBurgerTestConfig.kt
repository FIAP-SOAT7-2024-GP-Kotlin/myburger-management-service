package io.github.soat7.myburguercontrol.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter


@Configuration
class MyBurgerTestConfig {

    @Bean
    fun logFilter(): CommonsRequestLoggingFilter = CommonsRequestLoggingFilter().apply {
        this.setIncludeQueryString(true)
        this.setIncludePayload(true)
        this.setMaxPayloadLength(10000)
        this.setIncludeHeaders(false)
        this.setAfterMessagePrefix("REQUEST DATA: ")
    }
}
