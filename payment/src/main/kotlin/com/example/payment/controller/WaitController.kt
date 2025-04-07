package com.example.payment.controller

import com.example.payment.dto.RankResponse
import com.example.payment.service.QueueService
import com.example.payment.service.TokenService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class WaitController(
    private val tokenService: TokenService,
    private val queueService: QueueService
) {
    private fun extractTokenFromCookies(
        productId: Long,
        httpServletRequest: HttpServletRequest
    ): String? {
        return httpServletRequest.cookies?.firstOrNull {
            it.name == "payment-${productId}"
        }?.value
    }

    private fun redirectToPaymentPage(
        productId: Long,
        userId: Long,
        httpServletResponse: HttpServletResponse
    ) {
        val redirectUrl = "/products/${productId}/payment?userId=${userId}"

        httpServletResponse.sendRedirect(redirectUrl)
    }

    @GetMapping("/wait")
    fun wait(
        @RequestParam productId: Long,
        @RequestParam userId: Long,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ): ResponseEntity<RankResponse>? {
        // 대기 순위가 0번이 된 사용자는 (프론트엔드 서버에서) 토큰 발급 요청을 하고, 해당 페이지를 새로고침한다.
        // 토큰을 부여받은 유저는 결제 페이지로 진입한다.
        val token = extractTokenFromCookies(productId, httpServletRequest)
        if (token != null && tokenService.validateToken(productId, userId, token)) {
            redirectToPaymentPage(productId, userId, httpServletResponse)
            return null
        }

        // 대기열에 해당 유저를 등록한다.
        // 이미 대기열에 등록된 유저는, 기존 대기 순위를 조회한다.
        val rank = try {
            queueService.register(productId, userId)
        } catch (e: Exception) {
            queueService.getRank(productId, userId)
        }

        return ResponseEntity.ok(RankResponse(productId, userId, rank))
    }

    @GetMapping("/rank")
    fun getRank(
        @RequestParam productId: Long,
        @RequestParam userId: Long
    ): ResponseEntity<RankResponse> {
        // 현재 대기 순위를 조회한다.
        val rank = queueService.getRank(productId, userId)

        return ResponseEntity.ok(RankResponse(productId, userId, rank))
    }
}