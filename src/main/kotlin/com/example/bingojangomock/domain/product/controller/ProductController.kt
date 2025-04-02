package com.example.bingojangomock.domain.product.controller

import com.example.bingojangomock.domain.product.service.RecommendProductService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: RecommendProductService
) {
    @GetMapping("/recommend")
    fun recommend() {
        val userId = 100L // TODO

        val body = productService.recommendProduct(userId = userId)
    }
}