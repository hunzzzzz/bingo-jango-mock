package com.example.product.domain.repository

import com.example.product.domain.dto.response.ProductResponse
import com.example.product.domain.model.property.Category
import org.springframework.stereotype.Repository

@Repository
interface ProductCustomRepository {
    fun getPopularProductsAmongUsers(): List<ProductResponse>

    fun getProductsWithUserPattern(category: Category): List<ProductResponse>
}