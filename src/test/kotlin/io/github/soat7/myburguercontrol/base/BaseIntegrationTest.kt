package io.github.soat7.myburguercontrol.base

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.soat7.myburguercontrol.Application
import io.github.soat7.myburguercontrol.adapters.mapper.toPersistence
import io.github.soat7.myburguercontrol.container.MockServerContainer
import io.github.soat7.myburguercontrol.container.PostgresContainer
import io.github.soat7.myburguercontrol.domain.entities.Customer
import io.github.soat7.myburguercontrol.domain.entities.enum.UserRole
import io.github.soat7.myburguercontrol.external.db.customer.entity.CustomerEntity
import io.github.soat7.myburguercontrol.external.db.customer.repository.CustomerJpaRepository
import io.github.soat7.myburguercontrol.external.db.product.entity.ProductEntity
import io.github.soat7.myburguercontrol.external.db.product.repository.ProductJpaRepository
import io.github.soat7.myburguercontrol.fixtures.ProductFixtures
import io.github.soat7.myburguercontrol.util.JwtTokenUtil
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import java.time.LocalDateTime
import java.util.UUID

private val log = KotlinLogging.logger { }

@ActiveProfiles("test")
@SpringBootTest(
    classes = [Application::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@ExtendWith(PostgresContainer::class, MockServerContainer::class)
class BaseIntegrationTest {

    @Autowired
    protected lateinit var restTemplate: TestRestTemplate

    @Autowired
    protected lateinit var productJpaRepository: ProductJpaRepository

    @Autowired
    protected lateinit var customerJpaRepository: CustomerJpaRepository

    @Autowired
    protected lateinit var jwtTokenUtil: JwtTokenUtil

    protected lateinit var authenticationHeader: MultiValueMap<String, String>

    @BeforeEach
    fun setUpAuthentication() {
        authenticationHeader = buildAuthentication()
    }

    protected fun insertProducts(): List<ProductEntity> {
        productJpaRepository.save(ProductFixtures.mockProductEntity())
        productJpaRepository.save(ProductFixtures.mockProductEntity())

        return productJpaRepository.findAll()
    }

    protected fun insertCustomerData(customer: Customer): CustomerEntity {
        return customerJpaRepository.save(customer.toPersistence())
    }

    protected fun buildAuthentication(): MultiValueMap<String, String> {
        val cpf = "15666127055"
        val password = UUID.randomUUID().toString()
        val userRole = UserRole.ADMIN
        val ldt = LocalDateTime.now()

        val token = jwtTokenUtil.generateToken(cpf, ldt)

        val header: MultiValueMap<String, String> = LinkedMultiValueMap()
        log.info { "######### token: $token" }
        header.add("Authorization", "Bearer $token")
        header.add("Content-Type", MediaType.APPLICATION_JSON_VALUE)

        return header
    }
}
