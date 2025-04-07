package com.example.product.domain.model

import com.example.product.domain.model.property.BaseTime
import com.example.product.domain.model.property.Category
import jakarta.persistence.*

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    val status: ProductStatus,

    @Column(name = "name")
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    val category: Category,

    @Column(name = "description")
    val description: String,

    @Column(name = "price")
    val price: Int,

    @Column(name = "total_quantity")
    val totalQuantity: Int, // 총 수량

    @Column(name = "quantity")
    val quantity: Int, // 잔여 수량

    @Column(name = "order_count")
    val orderCount: Int = 0 // 주문 횟수
) : BaseTime()