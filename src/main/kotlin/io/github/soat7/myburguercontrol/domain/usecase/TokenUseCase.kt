package io.github.soat7.myburguercontrol.domain.usecase

import io.github.soat7.myburguercontrol.config.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class TokenUseCase(
    jwtProperties: JwtProperties,
) {
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())
    private val jwtParser = Jwts.parser().verifyWith(secretKey).build()

    fun decode(token: String) = run {
        val payload = jwtParser.parseSignedClaims(token).payload
        UsernamePasswordAuthenticationToken.authenticated(payload.subject, token, emptyList())
    }
}
