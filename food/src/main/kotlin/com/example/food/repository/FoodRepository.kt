package com.example.food.repository

import com.example.food.model.Food
import jakarta.persistence.LockModeType
import jakarta.persistence.QueryHint
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.QueryHints
import org.springframework.stereotype.Repository

@Repository
interface FoodRepository : JpaRepository<Food, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints(QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000"))
    fun findFoodById(id: Long): Food?
}