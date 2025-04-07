package com.example.payment.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.script.RedisScript
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class QueueService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    companion object {
        const val WAITING_KEY = "payment:%d:wait"
    }

    fun getRank(productId: Long, userId: Long): Long {
        val waitingKey = WAITING_KEY.format(productId)
        val rank = redisTemplate.opsForZSet().rank(waitingKey, userId.toString()) ?: -1L

        return rank + 1
    }

    fun register(productId: Long, userId: Long): Long {
        val waitingKey = WAITING_KEY.format(productId)

        val script = """
            local waiting_key = KEYS[1]
            local user_id = ARGV[1]
            local timestamp = tonumber(ARGV[2])
            
            -- 이미 대기열에 등록된 유저인지 확인
            if redis.call('ZSCORE', waiting_key, user_id) ~= false then
                return -1
            end
            
            -- 대기열에 추가
            redis.call('ZADD', waiting_key, timestamp, user_id)
            
            -- 순위 조회
            local rank = redis.call('ZRANK', waiting_key, user_id)
            
            return rank + 1
        """.trimIndent()

        val result = redisTemplate.execute(
            RedisScript.of(script, Long::class.java),
            listOf(waitingKey),
            userId.toString(),
            Instant.now().epochSecond.toDouble()
        )

        if (result == -1L) throw RuntimeException("이미 대기열에 등록된 사용자입니다.")
        else return result
    }
}