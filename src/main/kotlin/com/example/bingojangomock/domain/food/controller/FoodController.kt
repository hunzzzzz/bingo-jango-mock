package com.example.bingojangomock.domain.food.controller

import com.example.bingojangomock.domain.food.service.FoodService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/foods/{foodId}")
class FoodController(
    private val foodService: FoodService
) {
    @PutMapping("/eat")
    fun eat(
        @PathVariable foodId: Long,
        @RequestParam count: Int
    ): ResponseEntity<Unit> {
        val body = foodService.eat(foodId = foodId, count = count)

        return ResponseEntity.ok(body)
    }
}