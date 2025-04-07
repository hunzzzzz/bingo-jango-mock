package com.example.food.repository

import com.example.food.model.property.Category
import com.example.food.model.property.TimePeriod
import org.springframework.stereotype.Repository

@Repository
interface FoodHistoryCustomRepository {
    fun getMostConsumedCategory(
        userId: Long,
        timePeriod: TimePeriod
    ): Category?
}