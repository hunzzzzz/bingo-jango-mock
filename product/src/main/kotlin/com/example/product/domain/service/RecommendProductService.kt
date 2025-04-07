package com.example.product.domain.service

import com.example.product.domain.dto.response.ProductRecommendationResponse
import com.example.product.domain.model.property.Category
import com.example.product.domain.model.property.TimePeriod
import com.example.product.domain.repository.ProductRepository
import com.example.product.global.aop.ProductRecommendation
import com.example.product.global.exception.InternalServiceException
import com.example.product.global.exception.ProductNotFoundException
import com.example.product.global.utility.TimePeriodCalculator.calculate
import com.example.product.domain.dto.response.ProductResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class RecommendProductService(
    @Value("\${host.url.food-service}")
    val foodServiceUrl: String,

    private val productRepository: ProductRepository,
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private fun getMostConsumedCategoryFromFoodService(userId: Long, timePeriod: TimePeriod): Category? {
        return WebClient.create(foodServiceUrl)
            .get()
            .uri("/foods/history/most-consumed?userId=${userId}&timePeriod=${timePeriod.name}")
            .retrieve()
            .onStatus({ it.isError }, { response ->
                logger.error("[서비스 에러] 서비스 간 통신 에러: product -> food")
                Mono.error(InternalServiceException("서비스 간 통신 에러: product -> food, ${response.statusCode()}"))
            })
            .bodyToMono(Category::class.java)
            .block()
    }

    private fun getMostConsumedCategory(userId: Long, timePeriod: TimePeriod): Category? {
        val userConsumptionKey = "user:${userId}:${timePeriod.name.lowercase()}:consumption"

        return try {
            redisTemplate.opsForZSet()
                .reverseRange(userConsumptionKey, 0, 0)
                ?.firstOrNull()
                ?.let { Category.valueOf(it) }
                ?: getMostConsumedCategoryFromFoodService(userId, timePeriod)
        } catch (e: Exception) {
            logger.error("[Redis 에러] 사용자 소비 패턴 조회 실패 - userId: $userId", e)
            getMostConsumedCategoryFromFoodService(userId, timePeriod)
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
        val timePeriod = calculate(hour = now.hour)

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