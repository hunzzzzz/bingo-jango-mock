package com.example.food.repository

import com.example.food.model.Food
import com.example.food.model.FoodHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FoodHistoryRepository : JpaRepository<FoodHistory, Long>, FoodHistoryCustomRepository {
    fun existsByFoodAndUserId(food: Food, userId: Long): Boolean
}