package com.example.bingojangomock.global.exception

import com.example.bingojangomock.global.exception.custom.FoodNotFoundException
import com.example.bingojangomock.global.exception.custom.NotEnoughFoodInStockException
import com.example.bingojangomock.global.exception.custom.ProductNotFoundException
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

    @ExceptionHandler(value = [ProductNotFoundException::class])
    fun handleProductNotFoundException(e: ProductNotFoundException): ErrorResponse {
        return ErrorResponse(
            statusCode = HttpStatus.BAD_REQUEST,
            message = e.message ?: ""
        )
    }
}