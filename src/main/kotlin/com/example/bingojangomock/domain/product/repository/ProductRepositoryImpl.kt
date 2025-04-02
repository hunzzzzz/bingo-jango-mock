package com.example.bingojangomock.domain.product.repository

import com.example.bingojangomock.domain.product.dto.response.ProductResponse
import com.example.bingojangomock.domain.product.model.ProductStatus
import com.example.bingojangomock.domain.product.model.QProduct
import com.example.bingojangomock.global.model.Category
import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory
) : ProductCustomRepository {
    private val product = QProduct.product

    private fun productResponseProjection() = Projections.constructor(
        ProductResponse::class.java,
        product.id,
        product.category,
        product.name,
        product.description,
        product.price,
        product.totalQuantity,
        product.quantity
    )

    override fun getPopularProductsAmongUsers(): List<ProductResponse> {
        return jpaQueryFactory.select(
            productResponseProjection()
        ).from(product)
            .where(product.status.eq(ProductStatus.ON_SALE))
            .orderBy(product.orderCount.desc())
            .limit(5)
            .fetch()
    }

    override fun getProductsWithUserPattern(category: Category): List<ProductResponse> {
        return jpaQueryFactory.select(
            productResponseProjection()
        ).from(product)
            .where(
                product.category.eq(category),
                product.status.eq(ProductStatus.ON_SALE)
            )
            .orderBy(product.orderCount.desc())
            .fetch()
    }
}