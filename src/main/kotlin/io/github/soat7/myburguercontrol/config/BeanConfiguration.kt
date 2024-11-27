package io.github.soat7.myburguercontrol.config

import io.github.soat7.myburguercontrol.domain.usecase.CustomerUseCase
import io.github.soat7.myburguercontrol.domain.usecase.ProductUseCase
import io.github.soat7.myburguercontrol.domain.usecase.TokenUseCase
import io.github.soat7.myburguercontrol.external.db.customer.CustomerGateway
import io.github.soat7.myburguercontrol.external.db.product.ProductGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BeanConfiguration {

    @Bean
    fun customerUseCase(
        customerGateway: CustomerGateway,
    ) = CustomerUseCase(
        customerGateway = customerGateway,
    )

    @Bean
    fun productService(
        productGateway: ProductGateway,
    ) = ProductUseCase(
        productGateway = productGateway,
    )

    @Bean
    fun tokenUseCase(
        jwtProperties: JwtProperties,
    ) = TokenUseCase(
        jwtProperties = jwtProperties,
    )
}
