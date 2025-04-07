package com.example.product.domain.dto.response

import com.example.product.domain.model.property.Category

data class ProductResponse(
    val productId: Long,
    val category: Category,
    val name: String,
    val description: String,
    val price: Int,
    val totalQuantity: Int,
    val quantity: Int
)