package com.example.bingojangomock.domain.product.service

import com.example.bingojangomock.domain.food.repository.FoodHistoryRepository
import com.example.bingojangomock.domain.product.dto.response.ProductRecommendationResponse
import com.example.bingojangomock.domain.product.dto.response.ProductResponse
import com.example.bingojangomock.domain.product.repository.ProductRepository
import com.example.bingojangomock.global.aop.ProductRecommendation
import com.example.bingojangomock.global.exception.custom.ProductNotFoundException
import com.example.bingojangomock.global.model.Category
import com.example.bingojangomock.global.model.TimePeriod
import com.example.bingojangomock.global.utility.TimePeriodCalculator
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RecommendProductService(
    private val foodHistoryRepository: FoodHistoryRepository,
    private val productRepository: ProductRepository,
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private fun getMostConsumedCategory(userId: Long, timePeriod: TimePeriod): Category? {
        val userConsumptionKey = "user:${userId}:${timePeriod.name.lowercase()}:consumption"

        return try {
            redisTemplate.opsForZSet()
                .reverseRange(userConsumptionKey, 0, 0)
                ?.firstOrNull()
                ?.let { Category.valueOf(it) }
                ?: foodHistoryRepository.getMostConsumedCategory(userId, timePeriod)
        } catch (e: Exception) {
            logger.error("[Redis 에러] 사용자 소비 패턴 조회 실패 - userId: $userId", e)
            foodHistoryRepository.getMostConsumedCategory(userId, timePeriod)
        }
    }

    private fun getPopularProductAmongUsers(): ProductResponse {
        return productRepository.getPopularProductsAmongUsers().randomOrNull()
            ?: throw ProductNotFoundException("해당 상품을 찾을 수 없습니다.")
    }

    private fun getRandomProductByCategory(category: Category): ProductResponse {
        return productRepository.getProductsWithUserPattern(category = category).randomOrNull()
            ?: throw ProductNotFoundException("해당 상품을 찾을 수 없습니다.")
    }

    /**
     *  [1] 사용자의 최근 1주 간, '현재 시간대'에 가장 많이 소비한 카테고리를 찾는다.
     *  [2-1] 사용자의 소비 데이터가 없는 경우, 해당 카테고리 상품 중 다른 사용자들이 많이 구매한 상품 1개를 추천한다.
     *  [2-2] 사용자의 소비 데이터가 존재하면, 카테고리에 포함된 상품 1개를 추천한다.
     */

    @ProductRecommendation
    fun recommendProduct(userId: Long): ProductRecommendationResponse {
        val now = LocalDateTime.now()
        val timePeriod = TimePeriodCalculator.calculate(hour = now.hour)

        val mostConsumedCategory = getMostConsumedCategory(
            userId = userId, timePeriod = timePeriod
        ) // [1]

        return if (mostConsumedCategory == null)
            ProductRecommendationResponse(
                timePeriod = null,
                category = null,
                product = getPopularProductAmongUsers()
            ) // [2-1]
        else ProductRecommendationResponse(
            timePeriod = timePeriod,
            category = mostConsumedCategory,
            product = getRandomProductByCategory(category = mostConsumedCategory)
        ) // [2-2]
    }
}