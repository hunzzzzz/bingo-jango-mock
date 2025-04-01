package com.example.bingojangomock.domain.food.service

import com.example.bingojangomock.domain.food.model.Food
import com.example.bingojangomock.domain.food.repository.FoodRepository
import com.example.bingojangomock.global.exception.custom.FoodNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FoodService(
    private val foodRepository: FoodRepository
) {
    private fun get(foodId: Long): Food {
        return foodRepository.findFoodById(id = foodId)
            ?: throw FoodNotFoundException("존재하지 않는 음식입니다.")
    }

    @Transactional
    fun eat(foodId: Long, count: Int) {
        val food = get(foodId = foodId)

        food.updateQuantity(count = count)
    }
}