package com.example.bingojangomock.domain.product.dto.response

import com.example.bingojangomock.global.model.Category

data class ProductResponse(
    val productId: Long,
    val category: Category,
    val name: String,
    val description: String,
    val price: Int,
    val totalQuantity: Int,
    val quantity: Int
)