package io.github.soat7.myburguercontrol.external.webservice.user

import io.github.soat7.myburguercontrol.adapters.gateway.UserGateway
import io.github.soat7.myburguercontrol.domain.entities.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.util.UriComponentsBuilder

@Service
class UserWebGateway(
    @Value("\${myburger.service.user.base-url}")
    private val userServiceBaseUrl: String,

    private val restTemplate: RestTemplate,
) : UserGateway {
    override fun findUserByCpf(cpf: String): User? = restTemplate
        .getForObject(
            UriComponentsBuilder
                .fromUriString(userServiceBaseUrl)
                .queryParam("cpf", cpf)
                .build().toUri(),
        )
}
