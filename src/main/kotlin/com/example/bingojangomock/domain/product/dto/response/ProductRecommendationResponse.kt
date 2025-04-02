package com.example.bingojangomock.domain.product.dto.response

import com.example.bingojangomock.global.model.Category
import com.example.bingojangomock.global.model.TimePeriod

data class ProductRecommendationResponse(
    val timePeriod: TimePeriod?,
    val category: Category?,
    val product: ProductResponse
)