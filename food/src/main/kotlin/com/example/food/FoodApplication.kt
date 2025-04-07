package com.example.food

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FoodApplication

fun main(args: Array<String>) {
	runApplication<FoodApplication>(*args)
}
