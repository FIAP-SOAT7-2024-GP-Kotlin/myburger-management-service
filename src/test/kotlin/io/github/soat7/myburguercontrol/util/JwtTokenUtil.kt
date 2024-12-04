package io.github.soat7.myburguercontrol.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Component
class JwtTokenUtil(
    @Value("\${jwt.key}")
    private val jwtKey: String,
) {
    private val secretKey = Keys.hmacShaKeyFor(jwtKey.toByteArray())
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
