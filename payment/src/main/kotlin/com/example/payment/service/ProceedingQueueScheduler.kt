package com.example.payment.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class ProceedingQueueScheduler(
    private val redisTemplate: RedisTemplate<String, String>
) {
    companion object {
        const val BATCH_SIZE = 10
        const val WAITING_KEY = "payment:%d:wait"
        const val PROCEEDING_KEY = "payment:%d:proceed"
        const val STOCKS_KEY = "stocks"
    }

    private fun notifyOutOfStock(productId: Long) {

    }

    private fun allow(productId: Long) {
        val waitingKey = WAITING_KEY.format(productId)
        val proceedingKey = PROCEEDING_KEY.format(productId)
        val stocksKey = STOCKS_KEY

        val script = """
            local waiting_key = KEYS[1]
            local proceeding_key = KEYS[2]
            local stock_key = KEYS[3]
            
            local product_id = ARGV[1]
            local batch_size = tonumber(ARGV[2])
            local current_time = ARGV[3]
            
            -- 현재 재고 확인
            local remaining_stock = tonumber(redis.call('ZSCORE', stock_key, product_id)) or 0
            
            -- 처리 가능한 최대 인원 계산 (재고 vs 배치 크기 중 작은 값)
            local allowed_count = math.min(batch_size, remaining_stock)
            
            if allowed_count <= 0 then
                return -1
            end
            
            -- 대기열에서 사용자 추출
            local users = redis.call('ZPOPMIN', waiting_key, allowed_count)
            
            -- 추출된 사용자를 결제진행 큐에 추가
            for i = 1, #users, 2 do
                local user_id = users[i]
                redis.call('ZADD', proceeding_key, current_time, user_id)
            end
        """.trimIndent()

        val result = redisTemplate.execute(
            RedisScript.of(script, Long::class.java),
            listOf(waitingKey, proceedingKey, stocksKey),
            productId.toString(), BATCH_SIZE, Instant.now().epochSecond.toString()
        )
        if (result == -1L) notifyOutOfStock(productId)
    }

    @Scheduled(initialDelay = 5_000, fixedDelay = 3_000)
    fun allow() {
        val keys = redisTemplate.keys("payment:*:wait")

        keys.forEach { key ->
            if (key != null) {
                val productId = key.split(":")[1].toLongOrNull() ?: return@forEach

                val stocksKey = STOCKS_KEY
                val remainingStock = redisTemplate.opsForZSet().score(stocksKey, productId.toString())

                if (remainingStock != null && remainingStock > 0.0)
                    allow(productId) // 대기열(wait)에서 결제진행(proceed)큐로 이동
                else notifyOutOfStock(productId) // 재고 매진 시, WebSocket으로 프론트엔드 서버에 알림
            }
        }
    }
}