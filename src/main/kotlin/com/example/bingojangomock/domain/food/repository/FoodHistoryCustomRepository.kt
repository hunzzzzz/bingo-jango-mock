package com.example.bingojangomock.domain.food.repository

import com.example.bingojangomock.global.model.Category
import com.example.bingojangomock.global.model.TimePeriod
import org.springframework.stereotype.Repository

@Repository
interface FoodHistoryCustomRepository {
    fun getMostConsumedCategory(
        userId: Long,
        timePeriod: TimePeriod
    ): Category?
}