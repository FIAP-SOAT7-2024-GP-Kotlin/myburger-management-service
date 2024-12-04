package io.github.soat7.myburguercontrol.config

import io.github.soat7.myburguercontrol.domain.usecase.CustomerUseCase
import io.github.soat7.myburguercontrol.domain.usecase.ProductUseCase
import io.github.soat7.myburguercontrol.domain.usecase.TokenUseCase
import io.github.soat7.myburguercontrol.external.db.customer.CustomerGateway
import io.github.soat7.myburguercontrol.external.db.product.ProductGateway
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration @Autowired constructor(
    @Value("\${jwt.key}")
    private val jwtKey: String,
) {

    @Bean
    fun customerUseCase(customerGateway: CustomerGateway) = CustomerUseCase(
        customerGateway = customerGateway,
    )

    @Bean
    fun productService(productGateway: ProductGateway) = ProductUseCase(
        productGateway = productGateway,
    )

    @Bean
    fun tokenUseCase() = TokenUseCase(jwtKey)
}
