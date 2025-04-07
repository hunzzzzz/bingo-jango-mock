package com.example.product.domain.controller

import com.example.product.domain.dto.response.ProductRecommendationResponse
import com.example.product.domain.service.RecommendProductService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: RecommendProductService
) {
    @GetMapping("/test")
    fun test() = "Test"

    @GetMapping("/recommend")
    fun recommend(): ResponseEntity<ProductRecommendationResponse> {
        val userId = 100L // TODO
        val body = productService.recommendProduct(userId = userId)

        return ResponseEntity.ok(body)
    }
}