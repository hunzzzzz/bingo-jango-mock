package com.example.food.model

import com.example.food.model.property.TimePeriod
import com.example.food.utility.TimePeriodCalculator.calculate
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Entity
@Table(name = "food_histories")
class FoodHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_history_id")
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "food_id", nullable = false)
    val food: Food,

    @Enumerated(EnumType.STRING)
    @Column(name = "time_period", nullable = false)
    val timePeriod: TimePeriod = calculate(hour = now().hour),

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "consumed_at", nullable = false)
    val consumedAt: LocalDateTime = now()
)