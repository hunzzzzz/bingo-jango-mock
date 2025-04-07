package com.example.food.controller

import com.example.food.service.FoodService
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
        val userId = 100L // TODO
        val body = foodService.eat(userId = userId, foodId = foodId, count = count)

        return ResponseEntity.ok(body)
    }
}