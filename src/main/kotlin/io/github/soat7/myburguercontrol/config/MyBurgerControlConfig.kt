package io.github.soat7.myburguercontrol.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.time.Duration

private val log = KotlinLogging.logger {}

@Configuration
class MyBurgerControlConfig(
    @Value("\${spring.rest-template.connect-timeout}") private val connectTimeout: Long,
    @Value("\${spring.rest-template.read-timeout}") private val readTimeout: Long,
) {

    @Bean
    fun objectMapperBuilder(): Jackson2ObjectMapperBuilder = Jackson2ObjectMapperBuilder()
        .modules(JavaTimeModule(), KotlinModule.Builder().enable(KotlinFeature.SingletonSupport).build())
        .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .serializationInclusion(JsonInclude.Include.NON_NULL)

    @Bean
    @Primary
    fun objectMapper() = objectMapperBuilder().build<ObjectMapper>()

    @Bean
    fun mappingJackson2HttpMessageConverter(): MappingJackson2HttpMessageConverter =
        MappingJackson2HttpMessageConverter(objectMapper())

    @Bean
    fun restTemplateBuilder(): RestTemplateBuilder = run {
        val connectionManager = PoolingHttpClientConnectionManager()
        connectionManager.maxTotal = 10
        connectionManager.defaultMaxPerRoute = 10

        RestTemplateBuilder()
            .connectTimeout(Duration.ofMillis(connectTimeout))
            .readTimeout(Duration.ofMillis(readTimeout))
            .requestFactoryBuilder {
                ClientHttpRequestFactoryBuilder.httpComponents()
                    .withHttpClientCustomizer {
                        it.setConnectionManager(connectionManager)
                    }
                    .build()
            }
    }

    @Bean(name = ["serviceUserRestTemplate"])
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate = builder.build()
}
