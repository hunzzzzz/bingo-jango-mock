package com.example.product.global.utility

import com.example.product.domain.model.property.TimePeriod

object TimePeriodCalculator {
    fun calculate(hour: Int): TimePeriod = when (hour) {
        in 6..11 -> TimePeriod.MORNING
        in 11..16 -> TimePeriod.AFTERNOON
        in 16..21 -> TimePeriod.EVENING
        in 22..24, in 0..5 -> TimePeriod.NIGHT
        else -> TimePeriod.NIGHT
    }
}