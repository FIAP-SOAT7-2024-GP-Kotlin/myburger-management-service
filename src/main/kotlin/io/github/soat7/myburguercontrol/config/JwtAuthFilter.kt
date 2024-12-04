package io.github.soat7.myburguercontrol.config

import io.github.soat7.myburguercontrol.adapters.gateway.UserGateway
import io.github.soat7.myburguercontrol.domain.usecase.TokenUseCase
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val tokenUseCase: TokenUseCase,
    private val userGateway: UserGateway,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader: String? = request.getHeader("Authorization")

        if (authHeader.doesNotContainBearerToken()) {
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = authHeader!!.extractTokenValue()
        val claims = tokenUseCase.decode(jwtToken)
        val cpf = claims.subject

        val user = userGateway.findUserByCpf(cpf) ?: throw AuthenticationCredentialsNotFoundException("Invalid user")

        val auth =
            UsernamePasswordAuthenticationToken.authenticated(cpf, user, listOf(SimpleGrantedAuthority(user.role.name)))
        auth.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }

    private fun String?.doesNotContainBearerToken() =
        this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() =
        this.substringAfter("Bearer ")

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }
}
