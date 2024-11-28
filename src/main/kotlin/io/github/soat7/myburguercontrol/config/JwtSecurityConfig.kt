package io.github.soat7.myburguercontrol.config

import io.github.soat7.myburguercontrol.adapters.gateway.UserGateway
import io.github.soat7.myburguercontrol.domain.usecase.TokenUseCase
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("jwt")
data class JwtProperties(
    val key: String,
    val accessTokenExpiration: Long,
    val refreshTokenExpiration: Long,
)

@Component
class JwtAuthenticationManager(
    private val userGateway: UserGateway,
) : AuthenticationManager {
    override fun authenticate(authentication: Authentication?): Authentication = run {
        if (authentication !is UsernamePasswordAuthenticationToken) {
            throw IllegalArgumentException("Invalid token")
        }
        val cpf = authentication.principal as String

        val user = userGateway.findUserByCpf(cpf) ?: throw IllegalArgumentException("Invalid user")

        authentication.details = user
        authentication.authorities += SimpleGrantedAuthority(user.role.name)

        authentication
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

        return tokenUseCase.decode(token)
    }
}
