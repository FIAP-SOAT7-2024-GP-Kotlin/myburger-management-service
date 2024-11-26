package io.github.soat7.myburguercontrol.domain.usecase

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import io.github.soat7.myburguercontrol.domain.entities.User as UserBusinessModel


class CustomUserDetailsUseCase(
//    private val userGateway: UserGateway,
) /*: UserDetailsService*/ {

//    override fun loadUserByUsername(cpf: String): UserDetails =
//        userGateway.findUserByCpf(cpf)
//            ?.mapToUserDetails()
//            ?: throw UsernameNotFoundException("Cpf not found: $cpf")
//
//    private fun UserBusinessModel.mapToUserDetails(): UserDetails =
//        User.builder()
//            .username(this.cpf)
//            .password(this.password)
//            .roles(this.role.name)
//            .build()
}
