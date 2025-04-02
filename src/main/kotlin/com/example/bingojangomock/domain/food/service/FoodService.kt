package com.example.bingojangomock.domain.food.service

import com.example.bingojangomock.domain.food.model.Food
import com.example.bingojangomock.domain.food.model.FoodHistory
import com.example.bingojangomock.domain.food.repository.FoodHistoryRepository
import com.example.bingojangomock.domain.food.repository.FoodRepository
import com.example.bingojangomock.global.exception.custom.FoodNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FoodService(
    private val foodHistoryRepository: FoodHistoryRepository,
    private val foodRepository: FoodRepository
) {
    private fun get(foodId: Long): Food {
        return foodRepository.findFoodById(id = foodId)
            ?: throw FoodNotFoundException("존재하지 않는 음식입니다.")
    }

    private fun addFoodHistory(userId: Long, food: Food) {
        val foodHistory = FoodHistory(
            food = food,
            userId = userId
        )

        foodHistoryRepository.save(foodHistory)
    }

    @Transactional
    fun eat(userId: Long, foodId: Long, count: Int) {
        val food = get(foodId = foodId)

        food.updateQuantity(count = count)
        addFoodHistory(userId = userId, food = food)
    }
}