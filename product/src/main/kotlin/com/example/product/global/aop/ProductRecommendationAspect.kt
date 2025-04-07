package com.example.product.global.aop

import com.example.product.domain.dto.response.ProductRecommendationResponse
import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Aspect
@Component
class ProductRecommendationAspect(
    private val objectMapper: ObjectMapper,
    private val redisTemplate: RedisTemplate<String, String>
) {
    /**
     *  [1] Redis에 캐싱된 '상품 추천' 값이 있는지 확인한다.
     *  [2-1] 캐싱된 값이 없다면, 서비스 로직 수행 후 해당 추천 값을 1시간 동안 캐싱한다.
     *  [2-2] 캐싱된 값이 존재하면, 서비스 로직을 수행하지 않고, 캐싱된 값을 바로 리턴한다.
     */

    @Around(value = "@annotation(com.example.product.global.aop.ProductRecommendation)")
    fun aspect(joinPoint: ProceedingJoinPoint): Any {
        val userId = (joinPoint.args[0] as Long?) ?: return joinPoint.proceed()

        val recommendKey = "user:$userId:recommend"
        val cachedRecommendation = redisTemplate.opsForValue().get(recommendKey) // [1]

        if (cachedRecommendation == null) {
            val result = joinPoint.proceed()

            redisTemplate.opsForValue().set(
                recommendKey,
                objectMapper.writeValueAsString(result),
                1, TimeUnit.HOURS
            ) // [2-1]

            return result
        } else
            return objectMapper.readValue(
                cachedRecommendation, ProductRecommendationResponse::class.java
            ) // [2-2]
    }
}