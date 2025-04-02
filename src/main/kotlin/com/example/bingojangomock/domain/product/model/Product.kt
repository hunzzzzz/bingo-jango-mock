package com.example.bingojangomock.domain.product.model

import com.example.bingojangomock.global.model.BaseTime
import com.example.bingojangomock.global.model.Category
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
    val totalQuantity: Int,

    @Column(name = "quantity")
    val quantity: Int,

    @Column(name = "order_count")
    val orderCount: Int = 0
) : BaseTime()