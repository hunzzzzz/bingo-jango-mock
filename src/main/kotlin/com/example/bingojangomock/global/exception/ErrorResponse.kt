package com.example.bingojangomock.global.exception

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ErrorResponse(
    val statusCode: HttpStatus,
    val message: String,
    val time: LocalDateTime = LocalDateTime.now()
)
