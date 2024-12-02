package io.github.soat7.myburguercontrol.base

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.soat7.myburguercontrol.Application
import io.github.soat7.myburguercontrol.adapters.mapper.toPersistence
import io.github.soat7.myburguercontrol.config.MyBurgerControlConfig
import io.github.soat7.myburguercontrol.container.MockServerContainer
import io.github.soat7.myburguercontrol.container.PostgresContainer
import io.github.soat7.myburguercontrol.domain.entities.Customer
import io.github.soat7.myburguercontrol.domain.entities.enum.UserRole
import io.github.soat7.myburguercontrol.external.db.customer.entity.CustomerEntity
import io.github.soat7.myburguercontrol.external.db.customer.repository.CustomerJpaRepository
import io.github.soat7.myburguercontrol.external.db.product.repository.ProductJpaRepository
import io.github.soat7.myburguercontrol.util.JwtTokenUtil
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.mockserver.matchers.Times
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.web.bind.annotation.RequestMethod
import java.time.LocalDateTime
import java.util.UUID

private val log = KotlinLogging.logger { }

@ActiveProfiles("test")
@SpringBootTest(
    classes = [Application::class, MyBurgerControlConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BaseIntegrationTest {

    companion object {
        private val mockServer = MockServerContainer.mockserver
        private val mockServerClient = MockServerContainer.client()
        private val postgresql = PostgresContainer.postgresql

        @JvmStatic
        @DynamicPropertySource
        private fun configureSpringWitheContainer(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresql::getJdbcUrl)
            registry.add("spring.datasource.username", postgresql::getUsername)
            registry.add("spring.datasource.password", postgresql::getPassword)
            registry.add("myburger.service.user.base-url") {
                "${mockServer.endpoint}/api/v1/users"
            }
            registry.add("mock-server.url", mockServer::getEndpoint)
        }
    }

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Autowired
    protected lateinit var productJpaRepository: ProductJpaRepository

    @Autowired
    protected lateinit var customerJpaRepository: CustomerJpaRepository

    @Autowired
    protected lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @BeforeAll
    fun setUpAuthentication() {
        val token = buildAuthenticationToken()
        val interceptor = ClientHttpRequestInterceptor { req, body, exec ->
            req.headers.add("Authorization", "Bearer $token")
            req.headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            exec.execute(req, body)
        }
        restTemplate.restTemplate.interceptors = listOf(interceptor)
    }

    protected fun insertCustomerData(customer: Customer): CustomerEntity {
        return customerJpaRepository.save(customer.toPersistence())
    }

    protected fun buildAuthenticationToken(): String {
        val cpf = "15666127055"
        val userRole = UserRole.ADMIN
        val ldt = LocalDateTime.now()

        val token = jwtTokenUtil.generateToken(cpf, ldt.plusHours(1))

        registerMockUserCall(cpf, userRole)

        return token
    }

    private fun registerMockUserCall(cpf: String, userRole: UserRole) {
        val user = mapOf(
            "id" to UUID.randomUUID().toString(),
            "cpf" to cpf,
            "role" to userRole.toString(),
        )

        mockServerClient.`when`(
            request()
                .withPath("/api/v1/users")
                .withQueryStringParameter("cpf", cpf)
                .withMethod(RequestMethod.GET.name),
            Times.unlimited(),
        ).respond(
            response()
                .withStatusCode(HttpStatus.OK.value())
                .withBody(
                    objectMapper.writeValueAsString(user),
                    org.mockserver.model.MediaType.APPLICATION_JSON,
                ),
        )
    }
}
