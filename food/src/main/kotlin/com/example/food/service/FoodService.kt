package com.example.food.service

import com.example.food.model.Food
import com.example.food.model.FoodHistory
import com.example.food.repository.FoodHistoryRepository
import com.example.food.repository.FoodRepository
import com.example.food.exception.custom.FoodNotFoundException
import com.example.food.model.property.Category
import com.example.food.model.property.TimePeriod
import com.example.food.utility.TimePeriodCalculator.calculate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime.now

@Service
class FoodService(
    private val foodHistoryRepository: FoodHistoryRepository,
    private val foodRepository: FoodRepository,
    private val redisTemplate: RedisTemplate<String, String>
) {
    private fun get(foodId: Long): Food {
        return foodRepository.findFoodById(id = foodId)
            ?: throw FoodNotFoundException("존재하지 않는 음식입니다.")
    }

    private fun updateFoodQuantity(food: Food, count: Int) {
        food.updateQuantity(count = count)
    }

    private fun saveFoodHistoryEntity(userId: Long, food: Food) {
        val foodHistory = FoodHistory(
            food = food,
            userId = userId
        )

        foodHistoryRepository.save(foodHistory)
    }

    private fun incrementScoreInConsumptionZSet(userId: Long, category: Category, timePeriod: TimePeriod) {
        val userConsumptionKey = "user:${userId}:${timePeriod.name.lowercase()}:consumption"

        redisTemplate.opsForZSet().incrementScore(
            userConsumptionKey,
            category.name,
            1.0
        )
    }

    @Transactional
    fun eat(userId: Long, foodId: Long, count: Int) {
        val food = get(foodId = foodId)
        val timePeriod = calculate(hour = now().hour)

        updateFoodQuantity(food = food, count = count)
        saveFoodHistoryEntity(userId = userId, food = food)
        incrementScoreInConsumptionZSet(
            userId = userId,
            category = food.category,
            timePeriod = timePeriod
        )
    }
}