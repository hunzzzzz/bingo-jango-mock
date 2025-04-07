package com.example.product.domain.dto.response

import com.example.product.domain.model.property.Category
import com.example.product.domain.model.property.TimePeriod

data class ProductRecommendationResponse(
    val timePeriod: TimePeriod?,
    val category: Category?,
    val product: ProductResponse
)