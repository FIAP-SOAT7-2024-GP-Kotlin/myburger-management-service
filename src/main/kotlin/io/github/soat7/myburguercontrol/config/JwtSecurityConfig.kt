package io.github.soat7.myburguercontrol.config

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.soat7.myburguercontrol.adapters.gateway.UserGateway
import io.github.soat7.myburguercontrol.domain.usecase.TokenUseCase
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.stereotype.Component

private val log = KotlinLogging.logger { }

@Component
class JwtAuthenticationManager(
    private val userGateway: UserGateway,
) : AuthenticationManager {
    override fun authenticate(authentication: Authentication?): Authentication = run {
        if (authentication !is UsernamePasswordAuthenticationToken) {
            throw BadCredentialsException("Invalid token")
        }
        val cpf = authentication.principal as String

        val user = userGateway.findUserByCpf(cpf) ?: throw AuthenticationCredentialsNotFoundException("Invalid user")

        val authWithAuthority = UsernamePasswordAuthenticationToken.authenticated(
            authentication.principal,
            authentication.credentials,
            listOf(SimpleGrantedAuthority(user.role.name))
        )
        authWithAuthority.details = user

        authWithAuthority
    }
}

@Component
class JwtAuthenticationConverter(
    private val tokenUseCase: TokenUseCase,
) : AuthenticationConverter {

    override fun convert(request: HttpServletRequest?): Authentication {
        val header = request?.getHeader("Authorization")?.let { header ->
            if (!header.startsWith("Bearer ")) {
                throw IllegalArgumentException("Invalid token")
            }
            header
        } ?: throw IllegalArgumentException("Invalid token")

        val token = header.substringAfter("Bearer ").substringBefore("<")
        val claims = tokenUseCase.decode(token)
        return UsernamePasswordAuthenticationToken.unauthenticated(claims.subject, token)
    }
}
