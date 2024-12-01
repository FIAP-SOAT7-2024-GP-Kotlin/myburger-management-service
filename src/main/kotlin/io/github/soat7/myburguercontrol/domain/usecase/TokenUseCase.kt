package io.github.soat7.myburguercontrol.domain.usecase

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys

class TokenUseCase(
    private val jwtKey: String,
) {
    private val secretKey = Keys.hmacShaKeyFor(jwtKey.toByteArray())
    private val jwtParser = Jwts.parser().verifyWith(secretKey).build()

    fun decode(token: String) = run {
        jwtParser.parseSignedClaims(token).payload
//        UsernamePasswordAuthenticationToken.authenticated(payload.subject, token, emptyList())
    }
}
