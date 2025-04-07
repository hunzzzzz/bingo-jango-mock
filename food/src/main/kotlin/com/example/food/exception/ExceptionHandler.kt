package com.example.food.exception

import com.example.food.exception.custom.FoodNotFoundException
import com.example.food.exception.custom.NotEnoughFoodInStockException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(value = [NotEnoughFoodInStockException::class])
    fun handleNotEnoughFoodInStockException(e: NotEnoughFoodInStockException): ErrorResponse {
        return ErrorResponse(
            statusCode = HttpStatus.BAD_REQUEST,
            message = e.message ?: ""
        )
    }

    @ExceptionHandler(value = [FoodNotFoundException::class])
    fun handleFootNotFoundException(e: FoodNotFoundException): ErrorResponse {
        return ErrorResponse(
            statusCode = HttpStatus.BAD_REQUEST,
            message = e.message ?: ""
        )
    }
}