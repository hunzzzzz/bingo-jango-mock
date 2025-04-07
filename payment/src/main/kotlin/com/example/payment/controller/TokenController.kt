package com.example.payment.controller

import com.example.payment.service.TokenService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/products/{productId}/payment")
class TokenController(
    private val tokenService: TokenService
) {
    @GetMapping("/allowed")
    fun validateToken(
        @PathVariable productId: Long,
        @RequestParam userId: Long,
        @RequestParam token: String
    ): Boolean {
        return tokenService.validateToken(productId, userId, token)
    }

    @GetMapping("/token")
    fun createToken(
        @PathVariable productId: Long,
        @RequestParam userId: Long,
        httpServletResponse: HttpServletResponse
    ) {
        // 토큰을 생성한다.
        val token = tokenService.generateToken(productId, userId)

        // 쿠키에 토큰을 넣어준다.
        httpServletResponse.addCookie(
            Cookie("payment-${productId}", token).apply {
                maxAge = 60 * 10 // 10분
                path = "/"
                isHttpOnly = true
            }
        )
    }
}