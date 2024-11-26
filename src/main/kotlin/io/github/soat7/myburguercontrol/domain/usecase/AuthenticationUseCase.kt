package io.github.soat7.myburguercontrol.domain.usecase

import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.soat7.myburguercontrol.config.JwtProperties
import io.github.soat7.myburguercontrol.exception.ReasonCode.BAD_CREDENTIALS
import io.github.soat7.myburguercontrol.exception.ReasonCodeException
import io.github.soat7.myburguercontrol.external.webservice.auth.api.AuthenticationRequest
import io.github.soat7.myburguercontrol.external.webservice.auth.api.AuthenticationResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date
import java.util.EmptyStackException

private val log = KotlinLogging.logger { }

class AuthenticationUseCase(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsUseCase,
    private val tokenUseCase: TokenUseCase,
    private val jwtProperties: JwtProperties,
) {

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        try {
            authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request.cpf,
                    request.password,
                ),
            )
        } catch (e: Exception) {
            throw ReasonCodeException(BAD_CREDENTIALS, e)
        }

        val user = listOf(UserDetails::class)//userDetailsService.loadUserByUsername(request.cpf)

        val accessToken = tokenUseCase.generate(
            userDetails = user.get(0).objectInstance!!,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
        )

        return AuthenticationResponse(accessToken)
    }
}
