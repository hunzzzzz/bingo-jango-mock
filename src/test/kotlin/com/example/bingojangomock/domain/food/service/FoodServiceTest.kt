package com.example.bingojangomock.domain.food.service

import com.example.bingojangomock.domain.food.model.Food
import com.example.bingojangomock.domain.food.model.FoodCategory
import com.example.bingojangomock.domain.food.repository.FoodRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.assertEquals

@SpringBootTest
class FoodServiceTest {
    var foodId: Long = 0L

    @Autowired
    lateinit var foodRepository: FoodRepository

    @Autowired
    lateinit var foodService: FoodService

    @BeforeEach
    fun init() {
        val food = Food(name = "사과", category = FoodCategory.FRUIT, quantity = 100)

        foodId = foodRepository.save(food).id!!
    }

    @AfterEach
    fun clean() {
        foodRepository.deleteAll()
    }

    @Test
    fun 음식_1개_소비() {
        // when
        foodService.eat(foodId = foodId, count = 1)

        // then
        val food = foodRepository.findByIdOrNull(id = foodId)!!
        assertEquals(99, food.quantity)
    }

    @Test
    fun 순차적으로_101개의_음식_소비_요청이_들어왔을때() {
        assertThrows<RuntimeException> {
            repeat(101) {
                foodService.eat(foodId = foodId, count = 1)
            }
        }
    }

    @Test
    fun 동시에_100개의_음식_소비_요청이_들어왔을때() {
        /**
         * ExecutorService : 스레드 풀을 관리하는 객체
         * CountDownLatch: 여러 스레드의 작업이 완료될 때까지 대기하도록 하는 동기화 도구
         */
        // given
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(threadCount)
        val latch = CountDownLatch(threadCount) // latch count를 100으로 설정

        // when
        repeat(threadCount) {
            executorService.submit {
                foodService.eat(foodId = foodId, count = 1)
                latch.countDown() // 각 스레드가 작업을 완료하면, latch count 감소
            }
        }

        latch.await() // latch count가 0이 될 때까지 (= 모든 스레드의 작업이 완료될 때까지) 메인 스레드 대기

        // then
        val food = foodRepository.findByIdOrNull(id = foodId)!!
        assertEquals(0, food.quantity)
    }
}