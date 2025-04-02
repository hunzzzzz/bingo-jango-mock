package com.example.bingojangomock.domain.product.repository

import com.example.bingojangomock.domain.product.dto.response.ProductResponse
import com.example.bingojangomock.global.model.Category
import org.springframework.stereotype.Repository

@Repository
interface ProductCustomRepository {
    fun getPopularProductsAmongUsers(): List<ProductResponse>

    fun getProductsWithUserPattern(category: Category): List<ProductResponse>
}