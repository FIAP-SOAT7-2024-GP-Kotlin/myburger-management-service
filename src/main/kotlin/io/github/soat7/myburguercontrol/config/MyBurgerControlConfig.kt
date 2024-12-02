package io.github.soat7.myburguercontrol.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import java.time.Duration

@Configuration
@EnableWebMvc
@AutoConfiguration
class MyBurgerControlConfig {

    @Bean
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder = Jackson2ObjectMapperBuilder()
        .modules(JavaTimeModule(), KotlinModule.Builder().enable(KotlinFeature.SingletonSupport).build())
        .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .serializationInclusion(JsonInclude.Include.NON_NULL)

    @Bean
    @Primary
    fun objectMapper() = objectMapperBuilder().build<ObjectMapper>()

    @Bean
    fun restTemplate(
        @Value("\${spring.rest-template.connect-timeout}") connectTimeout: Long,
        @Value("\${spring.rest-template.read-timeout}") readTimeout: Long,
        builder: RestTemplateBuilder,
    ): RestTemplate = builder
        .requestFactoryBuilder(
            ClientHttpRequestFactoryBuilder.httpComponents().withHttpClientCustomizer {
                it.disableRedirectHandling()
            },
        )
        .connectTimeout(Duration.ofMillis(connectTimeout))
        .readTimeout(Duration.ofMillis(readTimeout))
        .build()
}
