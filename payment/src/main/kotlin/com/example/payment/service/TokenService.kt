package com.example.payment.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class TokenService(
    @Value("\${jwt.secret.key}")
    private val secretKey: String,

    @Value("\${jwt.issuer}")
    private val issuer: String
) {
    private val key: SecretKey =
        Base64.getDecoder().decode(secretKey).let { Keys.hmacShaKeyFor(it) }

    private fun getInfosFromToken(token: String): Claims {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload
    }

    fun generateToken(productId: Long, userId: Long): String {
        return Jwts.builder().let {
            it.subject(userId.toString())
            it.claims(
                Jwts.claims().add(
                    mapOf("productId" to productId)
                ).build()
            )
            it.expiration(Date(Date().time + 1000 * 60 * 10)) // 10분
            it.issuedAt(Date())
            it.issuer(issuer)
            it.signWith(key)
            it.compact()
        }
    }

    fun validateToken(productId: Long, userId: Long, token: String): Boolean {
        val claims = getInfosFromToken(token = token)
        val userIdFromClaims = claims.subject.toLongOrNull()
        val productIdFromClaims = claims["productId"] as Long?

        return userId == userIdFromClaims && productId == productIdFromClaims
    }
}