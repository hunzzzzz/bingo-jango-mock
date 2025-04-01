package com.example.bingojangomock.domain.food.model

import com.example.bingojangomock.global.exception.custom.NotEnoughFoodInStockException
import jakarta.persistence.*

@Entity
@Table(name = "foods")
class Food(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id", nullable = false, unique = true)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    val category: FoodCategory,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "quantity", nullable = false)
    var quantity: Int
) {
    fun updateQuantity(count: Int) {
        if (this.quantity - count < 0)
            throw NotEnoughFoodInStockException("잔여 수량이 부족합니다.")

        this.quantity -= count
    }
}