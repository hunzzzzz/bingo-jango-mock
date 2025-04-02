package com.example.bingojangomock.domain.food.service

import com.example.bingojangomock.domain.food.model.Food
import com.example.bingojangomock.domain.food.repository.FoodHistoryRepository
import com.example.bingojangomock.domain.food.repository.FoodRepository
import com.example.bingojangomock.global.model.Category
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
import kotlin.test.assertTrue

@SpringBootTest
class FoodServiceTest {
    var foodId: Long = 0L
    var userId: Long = 0L

    @Autowired
    lateinit var foodService: FoodService

    @Autowired
    lateinit var foodRepository: FoodRepository

    @Autowired
    lateinit var foodHistoryRepository: FoodHistoryRepository

    @BeforeEach
    fun init() {
        val food = Food(name = "사과", category = Category.FRUIT, quantity = 100)

        foodId = foodRepository.save(food).id!!
        userId = 100L
    }

    @AfterEach
    fun clean() {
        foodHistoryRepository.deleteAll()
        foodRepository.deleteAll()
    }

    @Test
    fun 음식_1개_소비() {
        // when
        foodService.eat(userId = userId, foodId = foodId, count = 1)

        // then
        val food = foodRepository.findByIdOrNull(id = foodId)!!
        assertEquals(99, food.quantity)
    }

    @Test
    fun 순차적으로_101개의_음식_소비_요청이_들어왔을때() {
        assertThrows<RuntimeException> {
            repeat(101) {
                foodService.eat(userId = 1, foodId = foodId, count = 1)
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
                foodService.eat(userId = userId, foodId = foodId, count = 1)
                latch.countDown() // 각 스레드가 작업을 완료하면, latch count 감소
            }
        }

        latch.await() // latch count가 0이 될 때까지 (= 모든 스레드의 작업이 완료될 때까지) 메인 스레드 대기

        // then
        val food = foodRepository.findByIdOrNull(id = foodId)!!
        assertEquals(0, food.quantity)
    }

    @Test
    fun 음식을_소비했을때_FoodHistory_객체가_추가되는지_확인() {
        // given
        foodService.eat(userId = userId, foodId = foodId, count = 1)

        // expected
        val food = foodRepository.findByIdOrNull(id = foodId)!!
        assertTrue { foodHistoryRepository.existsByFoodAndUserId(food = food, userId = userId) }
    }
}