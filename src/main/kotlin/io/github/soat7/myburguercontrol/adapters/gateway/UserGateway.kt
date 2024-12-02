package io.github.soat7.myburguercontrol.adapters.gateway

import io.github.soat7.myburguercontrol.domain.entities.User

interface UserGateway {
    fun findUserByCpf(cpf: String): User?
}
