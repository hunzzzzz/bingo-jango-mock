package com.example.bingojangomock.domain.product.service

import com.example.bingojangomock.domain.food.repository.FoodHistoryRepository
import com.example.bingojangomock.domain.product.dto.response.ProductRecommendationResponse
import com.example.bingojangomock.domain.product.dto.response.ProductResponse
import com.example.bingojangomock.domain.product.repository.ProductRepository
import com.example.bingojangomock.global.model.Category
import com.example.bingojangomock.global.model.TimePeriod
import com.example.bingojangomock.global.utility.TimePeriodCalculator
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RecommendProductService(
    private val foodHistoryRepository: FoodHistoryRepository,
    private val productRepository: ProductRepository
) {
    private fun getMostConsumedCategoryInWeek(userId: Long, timePeriod: TimePeriod): Category? {
        return foodHistoryRepository.getMostConsumedCategoryInWeek(
            userId = userId, timePeriod = timePeriod
        )
    }

    private fun getPopularProductAmongUsers(): ProductResponse {
        return productRepository.getPopularProductsAmongUsers().random()
    }

    private fun getRandomProductByCategory(category: Category): ProductResponse {
        return productRepository.getProductsWithUserPattern(category = category).random()
    }

    /**
     * [1] 사용자의 최근 1주 간, '현재 시간대'에 가장 많이 소비한 카테고리를 찾는다.
     * [2] 사용자의 소비 데이터가 없는 경우, 해당 카테고리에 포함된 상품 중 다른 사용자들이 많이 구매한 상품 1개를 추천한다.
     * [3] 사용자의 소비 데이터가 존재하면, 카테고리에 포함된 상품 1개를 추천한다.
     */

    fun recommendProduct(userId: Long): ProductRecommendationResponse {
        val now = LocalDateTime.now()
        val timePeriod = TimePeriodCalculator.calculate(hour = now.hour)

        val mostConsumedCategory = getMostConsumedCategoryInWeek(userId = userId, timePeriod = timePeriod) // [1]
            ?: return ProductRecommendationResponse(
                message = "추천에 실패했어요. 대신 인기 상품을 추천드려요!",
                product = getPopularProductAmongUsers()
            ) // [2]

        return ProductRecommendationResponse(
            message = "지난 사용자의 패턴을 분석해본 결과, ${timePeriod}에 ${mostConsumedCategory}을/를 많이 소비하셨네요!",
            product = getRandomProductByCategory(category = mostConsumedCategory)
        ) // [3]
    }
}