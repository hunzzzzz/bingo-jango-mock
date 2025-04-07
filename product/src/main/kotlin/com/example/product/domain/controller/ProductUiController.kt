package com.example.product.domain.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/products/{productId}/order")
class ProductUiController(
    @Value("\${host.url.payment-service}")
    val paymentServiceUrl: String,
) {
    private val restTemplate: RestTemplate = RestTemplate()

    private fun extractTokenFromCookies(
        productId: Long,
        httpServletRequest: HttpServletRequest
    ): String? {
        return httpServletRequest.cookies?.firstOrNull {
            it.name == "payment-${productId}"
        }?.value
    }

    private fun validateToken(
        productId: Long,
        userId: Long,
        token: String
    ): Boolean {
        val uri = UriComponentsBuilder
            .fromUriString(paymentServiceUrl)
            .path("/products/${productId}/payment/allowed")
            .queryParam("userId", userId)
            .queryParam("token", token)
            .build()
            .toUri()

        return restTemplate.getForObject(uri, Boolean::class.java) ?: false
    }

    private fun redirectToPaymentPage(
        productId: Long,
        userId: Long,
        httpServletResponse: HttpServletResponse
    ) {
        val redirectUrl = UriComponentsBuilder
            .fromUriString(paymentServiceUrl)
            .path("/products/${productId}/payment")
            .queryParam("userId", userId)
            .build()
            .toUriString()

        httpServletResponse.sendRedirect(redirectUrl)
    }

    private fun redirectToWaitingPage(
        productId: Long,
        userId: Long,
        httpServletResponse: HttpServletResponse
    ) {
        val redirectUrl = UriComponentsBuilder
            .fromUriString(paymentServiceUrl)
            .path("/wait")
            .queryParam("productId", productId)
            .queryParam("userId", userId)
            .build()
            .toUriString()

        httpServletResponse.sendRedirect(redirectUrl)
    }

    @GetMapping("/check")
    fun checkToken(
        @PathVariable productId: Long,
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ) {
        val userId = 100L // TODO : 추후 변경
        var isAllowed = false

        // 1. 쿠키에서 토큰 값을 추출한다.
        val token = extractTokenFromCookies(productId, httpServletRequest)

        // 2. 토큰 값이 존재하면, payment 서버로 요청을 보내 토큰의 유효성 여부를 확인한다.
        if (token != null)
            isAllowed = validateToken(productId, userId, token)

        // 3-1. 토큰이 유효한 경우, 결제 페이지로 바로 진입한다.
        // 3-2. 유효한 토큰이 없는 경우, 대기 페이지로 이동한다.
        if (isAllowed)
            redirectToPaymentPage(productId, userId, httpServletResponse)
        else
            redirectToWaitingPage(productId, userId, httpServletResponse)
    }
}