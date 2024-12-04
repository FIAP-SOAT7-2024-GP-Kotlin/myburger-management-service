package io.github.soat7.myburguercontrol.webservice

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.soat7.myburguercontrol.base.BaseIntegrationTest
import io.github.soat7.myburguercontrol.domain.entities.enum.ProductType
import io.github.soat7.myburguercontrol.external.webservice.common.PaginatedResponse
import io.github.soat7.myburguercontrol.external.webservice.product.api.ProductResponse
import io.github.soat7.myburguercontrol.fixtures.ProductFixtures
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.util.UUID

private val log = KotlinLogging.logger { }

class ProductIT : BaseIntegrationTest() {

    @Test
    @Transactional(Transactional.TxType.NEVER)
    fun `should successfully create a new product`() {
        val inputProductData = ProductFixtures.mockProductCreationRequest()

        val response = restTemplate.postForEntity<ProductResponse>("/products", inputProductData)

        log.info { "response = $response" }

        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        assertThat(response.body).isNotNull
        assertThat(response.body!!.type).isEqualTo(inputProductData.type.name)

        val savedProduct = productJpaRepository.findByIdOrNull(response.body!!.id)

        assertThat(savedProduct).isNotNull
        assertThat(savedProduct!!.description).isEqualTo(inputProductData.description)
        assertThat(savedProduct.price).isEqualTo(inputProductData.price)
    }

    @Test
    fun `should successfully find a product by id`() {
        val id = UUID.randomUUID()
        val product = productJpaRepository.save(ProductFixtures.mockProductEntity(id))

        val response = restTemplate.exchange<ProductResponse>(
            url = "/products/{id}",
            method = HttpMethod.GET,
            uriVariables = mapOf("id" to product.id),
        )

        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        assertThat(response.body).isNotNull

        val responseBody = response.body!!
        assertThat(responseBody.id).isEqualTo(product.id)
        assertThat(responseBody.description).isEqualTo(product.description)
        assertThat(responseBody.price).isEqualTo(product.price)
        assertThat(responseBody.type).isEqualTo(product.type)
    }

    @Test
    fun `should return NOT_FOUND when no product is found for the given Id`() {
        val randomId = UUID.randomUUID()

        val response = restTemplate.exchange<ProductResponse>(
            url = "/products/{id}",
            method = HttpMethod.GET,
            uriVariables = mapOf(
                "id" to randomId.toString(),
            ),
        )
        assertThat(response.statusCode.value()).isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    @Test
    fun `should return a Paginated response of product`() {
        insertRandomTypeProducts()

        val response = restTemplate.exchange<PaginatedResponse<ProductResponse>>(
            url = "/products",
            method = HttpMethod.GET,
        )

        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        assertThat(response.body).isNotNull
        assertThat(response.body!!.content).isNotEmpty
        assertThat(response.body!!.totalPages).isGreaterThanOrEqualTo(2)
    }

    @Test
    fun `should successfully find a product by type`() {
        insertRandomTypeProducts()
        val type = "DRINK"

        val response = restTemplate.exchange<List<ProductResponse>>(
            url = "/products/type?type={type}",
            method = HttpMethod.GET,
            uriVariables = mapOf("type" to type),
        )

        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        assertThat(response.body).isNotNull
        assertThat(response.body!!).allSatisfy { it.type == type }
    }

    @Test
    fun `should return an empty product page when no products are found by type`() {
        insertRandomTypeProducts()
        val type = "PIZZA"

        val response = restTemplate.exchange<List<ProductResponse>>(
            url = "/products/type?type={type}",
            method = HttpMethod.GET,
            uriVariables = mapOf("type" to type),
        )

        assertThat(response.statusCode.is2xxSuccessful).isTrue()
        assertThat(response.body).isNotNull
        assertThat(response.body!!).isEmpty()
    }

    @Test
    fun `should delete a product with given ID`() {
        val id = UUID.randomUUID()
        val product = productJpaRepository.save(ProductFixtures.mockProductEntity(id))

        val response = restTemplate.exchange<Void>(
            url = "/products/{id}",
            method = HttpMethod.DELETE,
            uriVariables = mapOf("id" to product.id),
        )

        assertThat(response.statusCode.is2xxSuccessful).isTrue()
    }

    @Test
    fun `should return NOT_FOUND when trying to delete a product with the given Id`() {
        val randomId = UUID.randomUUID()

        val response = restTemplate.exchange<Void>(
            url = "/products/{id}",
            method = HttpMethod.DELETE,
            uriVariables = mapOf(
                "id" to randomId.toString(),
            ),
        )

        assertThat(response.statusCode.value()).isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    private fun insertRandomTypeProducts() {
        productJpaRepository.save(ProductFixtures.mockProductEntity())
        productJpaRepository.save(ProductFixtures.mockProductEntity(type = ProductType.DRINK))
        productJpaRepository.save(ProductFixtures.mockProductEntity(type = ProductType.APPETIZER))
        productJpaRepository.save(ProductFixtures.mockProductEntity(type = ProductType.DESSERT))
        productJpaRepository.save(ProductFixtures.mockProductEntity(type = ProductType.OTHER))
    }
}
