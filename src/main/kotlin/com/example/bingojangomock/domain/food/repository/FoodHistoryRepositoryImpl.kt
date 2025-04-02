package com.example.bingojangomock.domain.food.repository

import com.example.bingojangomock.domain.food.model.QFoodHistory
import com.example.bingojangomock.global.model.Category
import com.example.bingojangomock.global.model.TimePeriod
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FoodHistoryRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : FoodHistoryCustomRepository {
    val foodHistory: QFoodHistory = QFoodHistory.foodHistory

    override fun getMostConsumedCategory(
        userId: Long,
        timePeriod: TimePeriod
    ): Category? {
        return jpaQueryFactory.select(foodHistory.food.category)
            .from(foodHistory)
            .where(
                foodHistory.userId.eq(userId),
                foodHistory.timePeriod.eq(timePeriod)
            )
            .groupBy(foodHistory.food.category)
            .orderBy(foodHistory.food.category.count().desc())
            .fetchFirst()
    }
}