package io.github.soat7.myburguercontrol.util

import io.github.soat7.myburguercontrol.config.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Component
class JwtTokenUtil(
    jwtProperties: JwtProperties,
) {
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.key.toByteArray())
    private val zoneId = ZoneId.systemDefault()

    fun generateToken(
        cpf: String,
        expirationDate: LocalDateTime,
    ) = Jwts.builder()
        .claims()
        .subject(cpf)
        .issuedAt(Date.from(Instant.now()))
        .expiration(Date.from(expirationDate.atZone(zoneId).toInstant()))
        .and()
        .signWith(secretKey)
        .compact()
}
